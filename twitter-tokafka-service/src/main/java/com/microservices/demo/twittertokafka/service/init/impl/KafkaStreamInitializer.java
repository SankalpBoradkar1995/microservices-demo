package com.microservices.demo.twittertokafka.service.init.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.microservices.demo.config.KafkaConfigData;
import com.microservices.demo.kafka.admin.config.clietns.KafkaAdminClients;
import com.microservices.demo.twittertokafka.service.init.StreamInitializer;

@Component
public class KafkaStreamInitializer implements StreamInitializer{

	private final Logger LOG = LoggerFactory.getLogger(KafkaStreamInitializer.class);
	
	private final KafkaConfigData kafkaConfigData;
	private final KafkaAdminClients kafkaAdminClients;
	
	public KafkaStreamInitializer(KafkaConfigData configData, 
			KafkaAdminClients adminClient)
	{
		this.kafkaConfigData = configData;
		this.kafkaAdminClients = adminClient;
	}
	
	@Override
	public void init() {
		
		// create topics
		kafkaAdminClients.createTopics();
		
		//check schema registry is up
		kafkaAdminClients.checkSchemaRegistry();
		LOG.info("Topics with the name are ready for opeartion "+kafkaConfigData.getTopicNamesToCreate().toArray());
		
	}

}
