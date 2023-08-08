package com.microservices.demo.twittertokafka.service.transformer;

import org.springframework.stereotype.Component;

import com.microservices.demo.kafka.avro.model.TwitterAvroModel;

import twitter4j.Status;

@Component
public class TwitterStatusToAvroTransform {

	public TwitterAvroModel getTwitterAvroModelFromStatus(Status status)
	{
		return TwitterAvroModel.newBuilder()
				.setId(status.getId())
				.setUserId(status.getUser().getId())
				.setText(status.getText())
				.setCreatedAt(status.getCreatedAt().getTime()).build();
	}
}
