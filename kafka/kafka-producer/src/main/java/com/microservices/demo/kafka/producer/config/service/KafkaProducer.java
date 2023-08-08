package com.microservices.demo.kafka.producer.config.service;

import java.io.Serializable;

import org.apache.avro.specific.SpecificRecord;
import org.apache.avro.specific.SpecificRecordBase;

public interface KafkaProducer<K extends Serializable, V extends SpecificRecordBase> {
	/*
	 * KafkaTemplate<K,V> has send method in spring-kafka library
	 * But we want custom configuration hence created this interface of type KafkaProducer<K extends Serializable, V extends SpecificRecordBase>
	 */
	void send(String topicName, K key, V message);

}
