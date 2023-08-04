package com.microservices.demo.kafka.admin.config.clietns;

import com.microservices.demo.config.KafkaConfigData;
import com.microservices.demo.config.RetryConfigData;
import com.microservices.demo.kafka.admin.exception.KafkaClientException;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicListing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Component
public class KafkaAdminClients {

	private final Logger LOG = LoggerFactory.getLogger(KafkaAdminClients.class);

	private final KafkaConfigData kafkaConfigData;
	private final RetryConfigData retryConfigData;

	// Taking admin client from the bean created in kafka admin config class of
	// kafka-admin package
	private final AdminClient adminClient;

	// Taking retry templet from the bean created in RetryConfig class of
	// common-config package
	private final RetryTemplate retryTemplate;
	
	//Taiking WebClient bean for making http requests from kafka-admin-config package
	// It is non thread blocking approch compared to RestTemplet
	private final WebClient webClient;

	public KafkaAdminClients(KafkaConfigData configData, RetryConfigData retryConfig, AdminClient adminClient,
			RetryTemplate retryTemplet,WebClient client) {
		this.kafkaConfigData = configData;
		this.retryConfigData = retryConfig;
		this.adminClient = adminClient;
		this.retryTemplate = retryTemplet;
		this.webClient = client;
	}

	public void createTopics() {
		CreateTopicsResult createTopicsResult;
		try {
			createTopicsResult = retryTemplate.execute(this::doCreateTopics);
			LOG.info("Create topic result {}", createTopicsResult.values().values());
		} catch (Throwable t) {
			throw new KafkaClientException("Reached max number of retry for creating kafka topic(s)!", t);
		}
		checkTopicsCreated();
	}

	private CreateTopicsResult doCreateTopics(RetryContext retryContext) {
		List<String> topicNames = kafkaConfigData.getTopicNamesToCreate();
		LOG.info("Creating {} topics(s), attempt {}", topicNames.size(), retryContext.getRetryCount());
		List<NewTopic> kafkaTopics = topicNames.stream().map(topic -> new NewTopic(topic.trim(),
				kafkaConfigData.getNumOfPartitions(), kafkaConfigData.getReplicationFactor()))
				.collect(Collectors.toList());
		return adminClient.createTopics(kafkaTopics);
	}

	private Collection<TopicListing> getTopics() {
		Collection<TopicListing> topics;
		try {
			topics = retryTemplate.execute(this::doGetTopics);
		} catch (Throwable e) {
			throw new KafkaClientException("Reached max number of retry for creating kafka topic(s)!", e);
		}
		return topics;

	}

	private Collection<TopicListing> doGetTopics(RetryContext retryContext)
			throws InterruptedException, ExecutionException {
		LOG.info("Reading topics, attempts" + kafkaConfigData.getTopicNamesToCreate().toArray(),
				retryContext.getRetryCount());
		Collection<TopicListing> topics = adminClient.listTopics().listings().get();
		if (topics != null) {
			topics.forEach(topic -> LOG.info("Topic with name" + topic.name()));
		}
		return topics;
	}

	public void checkTopicsCreated() {
		Collection<TopicListing> topics = getTopics();
		int retryCount = 1;
		Integer maxRetryCount = retryConfigData.getMaxAttempts();
		Integer multiplier = retryConfigData.getMultiplier().intValue();
		Long sleepTime = retryConfigData.getSleepTimeMs();

		for (String topic : kafkaConfigData.getTopicNamesToCreate()) {
			while (!isTopicCreated(topics, topic)) {
				checkMaxRetry(retryCount++, maxRetryCount);
				sleep(sleepTime);
				sleepTime = sleepTime * multiplier;
				topics = getTopics();
			}
		}

	}

	private void sleep(Long sleepTime) {
		try {
			Thread.sleep(sleepTime);
		} catch (InterruptedException e) {
			throw new KafkaClientException("Thread sleep error while waiting for new topic to " + "be created");
		}

	}

	private void checkMaxRetry(int retryCount, Integer maxRetryCount) {
		if (retryCount > maxRetryCount) {
			throw new KafkaClientException("Max retry count reached to read topics");
		}

	}

	private boolean isTopicCreated(Collection<TopicListing> topics, String topic) {
		if (topics == null) {
			return false;
		} else {
			return topics.stream().anyMatch(i -> i.name().equals(topic));
		}
	}
	
	public void checkSchemaRegistry()
	{
		int retryCount = 1;
		Integer maxRetryCount = retryConfigData.getMaxAttempts();
		Integer multiplier = retryConfigData.getMultiplier().intValue();
		Long sleepTime = retryConfigData.getSleepTimeMs();
		
		while(!getSchemaRegistryStatus().is2xxSuccessful())
		{
			checkMaxRetry(retryCount, maxRetryCount);
			sleep(sleepTime);
			sleepTime = sleepTime*multiplier;
		}
	}
	
	private HttpStatus getSchemaRegistryStatus() {
        try {
            return webClient
                    .method(HttpMethod.GET)
                    .uri(kafkaConfigData.getSchemaRegistryUrl())
                    .exchange()
                    .map(ClientResponse::statusCode)
                    .block();
        } catch (Exception e) {
            return HttpStatus.SERVICE_UNAVAILABLE;
        }
    }
}
