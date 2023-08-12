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
	private final TwitterStatusToAvroTransform avroTransform;
	
	public TwitterToKafkaStatusListener(KafkaConfigData configData, KafkaProducer<Long, TwitterAvroModel> kafkaproducer,TwitterStatusToAvroTransform transform)
	{
		this.kafkaConfigData = configData;
		this.kafkaProducer = kafkaproducer;
		this.avroTransform = transform;
	}
	
	@Override
	public void onStatus(Status status)
	{
		LOG.info("Received status text {}, sending data to kafka topic {} "+status.getText()+" "+kafkaConfigData.getTopicName());
		TwitterAvroModel avroModel = avroTransform.getTwitterAvroModelFromStatus(status);
		kafkaProducer.send(kafkaConfigData.getTopicName(), avroModel.getUserId(), avroModel);
	}
}
