package com.microservices.demo.twittertokafka.service.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import twitter4j.Status;
import twitter4j.StatusAdapter;

@Component
public class TwitterToKafkaStatusListener extends StatusAdapter
{
	private final Logger  LOG = LoggerFactory.getLogger(TwitterToKafkaStatusListener.class);
	@Override
	public void onStatus(Status status)
	{
		LOG.info("Twitter status: " +status.getText());
	}
}
