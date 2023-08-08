package com.microservices.demo.twittertokafka.service.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.microservices.demo.config.KafkaConfigData;
import com.microservices.demo.kafka.avro.model.TwitterAvroModel;
import com.microservices.demo.kafka.producer.config.service.KafkaProducer;
import com.microservices.demo.twittertokafka.service.transformer.TwitterStatusToAvroTransform;

import twitter4j.Status;
import twitter4j.StatusAdapter;

@Component
public class TwitterToKafkaStatusListener extends StatusAdapter
{
	private final Logger  LOG = LoggerFactory.getLogger(TwitterToKafkaStatusListener.class);
	
	private final KafkaConfigData kafkaConfigData;
	private final KafkaProducer<Long, TwitterAvroModel> kafkaProducer;
	private final TwitterStatusToAvroTransform twitterStatusToAvroTransform;
	
	public TwitterToKafkaStatusListener(KafkaConfigData configData,
			KafkaProducer<Long,TwitterAvroModel> producer,
			TwitterStatusToAvroTransform transform)
	{
		this.kafkaConfigData = configData;
		this.kafkaProducer = producer;
		this.twitterStatusToAvroTransform = transform;	
	}
	
	@Override
	public void onStatus(Status status)
	{
		LOG.info("Received status text , sending it to kafka topic " +status.getText()+" "+kafkaConfigData.getTopicName());
		TwitterAvroModel twitterAvroModel = twitterStatusToAvroTransform.getTwitterAvroModelFromStatus(status);
		
		// We are sending topic name , kafka partition key as user ID and value as entire avroModel
		// Passing kafka partition key as user ID means partitiojn / folder data as per user ID of avroModel object
		// That way tweets belongs to user will be added to same partition of kafka topic
		kafkaProducer.send(kafkaConfigData.getTopicName(), twitterAvroModel.getUserId(), twitterAvroModel);
	}
}
