package com.microservices.demo.kafka.producer.config.service.impl;

import javax.annotation.PreDestroy;

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
	private final KafkaTemplate<Long, TwitterAvroModel> kafkaTemplet;
	
	
	public TwitterKafkaProducer(KafkaTemplate<Long, TwitterAvroModel> templet)
	{
		this.kafkaTemplet = templet;
	}
	
	/*
	 * Following send method is our custom send method from interface kafkaProducer of type KafkaProducer <K,V>
	 * where K would be of type Long and Value will be of TwitterAvroModel
	 */
	
	/*
	 * 
	 */
	
	@Override
    public void send(String topicName, Long key, TwitterAvroModel message) {
        LOG.info("Sending message='{}' to topic='{}'", message, topicName);
        ListenableFuture<SendResult<Long, TwitterAvroModel>> kafkaResultFuture =
        		kafkaTemplet.send(topicName, key, message);
        addCallback(topicName, message, kafkaResultFuture);
    }

    @PreDestroy
    public void close() {
        if (kafkaTemplet != null) {
            LOG.info("Closing kafka producer!");
            kafkaTemplet.destroy();
        }
    }

    private void addCallback(String topicName, TwitterAvroModel message,
                             ListenableFuture<SendResult<Long, TwitterAvroModel>> kafkaResultFuture) {
        kafkaResultFuture.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onFailure(Throwable throwable) {
                LOG.error("Error while sending message {} to topic {}", message.toString(), topicName, throwable);
            }

            @Override
            public void onSuccess(SendResult<Long, TwitterAvroModel> result) {
                    RecordMetadata metadata = result.getRecordMetadata();
                    LOG.debug("Received new metadata. Topic: {}; Partition {}; Offset {}; Timestamp {}, at time {}",
                            metadata.topic(),
                            metadata.partition(),
                            metadata.offset(),
                            metadata.timestamp(),
                            System.nanoTime());
            }
        });
    }

}
