package com.microservices.demo.kafka.producer.config.service.impl;


import javax.annotation.PreDestroy;

import org.apache.kafka.clients.Metadata;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.microservices.demo.kafka.avro.model.TwitterAvroModel;
import com.microservices.demo.kafka.producer.config.service.KafkaProducer;

@Service
public class TwitterKafkaProducer implements KafkaProducer<Long, TwitterAvroModel>{

	private final Logger LOG = LoggerFactory.getLogger(TwitterKafkaProducer.class);
	
	/*
	 * To use our custom send method from KafkaProducer interface we are injecting KafkaTemplate<Long, TwitterAvroModel> dependency
	 * KafkaTemplet accepts KafkaProducer so we are sending custom kafkaProducer to kafkaTemplet
	 */
	
	/*
	 * Send method will return list of listenablefuture since its async operation
	 * ListenableFuture: A ListenableFuture represents the result of an asynchronous computation
	 * We can register callback methods like onFailure / onSuccess  for handling events when response return while adding data
	 */
	private KafkaTemplate<Long, TwitterAvroModel> kafkaTemplt;
	
	/*
	 * 	KafkaTemplet is wrapper over kafka-producer using which we can send data into kafka
	 * 	kafka Producer's send method takes topic_Name, Key and value as parameters
	 * 	Key parameter is used to determine target partition on kafka topic of a message
	 * 	Send method in non blocking, it will not wait for results continuously 
	 */
	
	public TwitterKafkaProducer(KafkaTemplate<Long, TwitterAvroModel> templet)
	{
		this.kafkaTemplt = templet;
	}
	
	@Override
	public void send(String topicName, Long key, TwitterAvroModel message) {
		LOG.info("Sending message to Kafka topic"+topicName+", "+message);
		ListenableFuture<SendResult<Long, TwitterAvroModel>> kafkaResultFuture = kafkaTemplt.send(topicName,key, message);
		addCallBack(topicName, message, kafkaResultFuture);
	}
	
	@PreDestroy
	public void close()
	{
		if(kafkaTemplt!=null)
		{
			LOG.info("Closing kafka templet , hence not accepting any new data into kafka");
			kafkaTemplt.destroy();
		}
	}

	private void addCallBack(String topicName, TwitterAvroModel message,
			ListenableFuture<SendResult<Long, TwitterAvroModel>> kafkaResultFuture) {
		kafkaResultFuture.addCallback(new ListenableFutureCallback<SendResult<Long, TwitterAvroModel>>() {

			@Override
			public void onSuccess(SendResult<Long, TwitterAvroModel> result) {
				RecordMetadata data =  result.getRecordMetadata();
				LOG.debug("Received new data in kafka topic. Topic; Partition: Timestamp: at time:"
						+data.topic()
						+" "+data.partition()
						+" "+data.timestamp()
						+" "+System.nanoTime());
				
			}

			@Override
			public void onFailure(Throwable ex) {
				LOG.error("Error while sending data to kafka "+message.toString()+" "+topicName+" "+ex);
				
			}
			
		});
	}
	

}
