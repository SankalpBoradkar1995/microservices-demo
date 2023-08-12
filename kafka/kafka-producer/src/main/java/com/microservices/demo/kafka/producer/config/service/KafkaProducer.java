package com.microservices.demo.kafka.producer.config.service;

import java.io.Serializable;

import org.apache.avro.specific.SpecificRecordBase;

public interface KafkaProducer<K extends Serializable, V extends SpecificRecordBase> {
	
	public void send(String topicName,K key, V message);

}
