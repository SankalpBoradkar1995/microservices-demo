package com.microservices.demo.twittertokafka.service.impl;

import java.util.Arrays;

import javax.annotation.PreDestroy;

import com.microservices.demo.config.TwitterToKafkaConfigData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;


import com.microservices.demo.twittertokafka.service.component.TwitterCreds;

import com.microservices.demo.twittertokafka.service.listener.TwitterToKafkaStatusListener;
import com.microservices.demo.twittertokafka.service.runner.StreamRunner;

import twitter4j.FilterQuery;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

@Component
//@ConditionalOnProperty creats bean for particular condition
//@ConditionalOnProperty(name="twitter-to-kafka-service.enable-v2-tweets",havingValue="false")

/*
 * With following annotation bean of this class will be created when v2 tweets and mock tweet set to false 
 * in yml file
 */
@ConditionalOnExpression("not ${twitter-to-kafka-service.enable-v2-tweets} &&"
		+ " not ${twitter-to-kafka-service.enable-mock-tweets}")
public class TwitterKafkaStreamRunner implements StreamRunner{
	
	private final Logger LOG = LoggerFactory.getLogger(TwitterKafkaStreamRunner.class);

	private final TwitterToKafkaConfigData twitterToKafkaConfigData;
	private final TwitterToKafkaStatusListener twitterToKafkaStatusListener;
	private final TwitterCreds twitterCreds;
	
	private TwitterStream twitterStream;
	
	public TwitterKafkaStreamRunner(TwitterToKafkaConfigData configData, 
			TwitterToKafkaStatusListener statusListener,
			TwitterCreds creds)
	{
		this.twitterToKafkaConfigData = configData;
		this.twitterToKafkaStatusListener = statusListener;
		this.twitterCreds = creds;
	}
	
	
	@Override
	public void start() throws TwitterException {
		twitterStream = new TwitterStreamFactory().getInstance();
		//twitterStream = new TwitterCreds().instance();
		twitterStream.addListener(twitterToKafkaStatusListener);
		addFilter();
	}
	
	//Predistroy is to delete twitter object before exiting the app / bean is destroyed
	@PreDestroy
	public void shutDown()
	{
		if(twitterStream!=null)
		{
			LOG.info("Closing twitter stream");
			twitterStream.shutdown();
			
		}
	}
	
	public void addFilter() {
		String keywords[] = twitterToKafkaConfigData.getTwitterKeywords().toArray(new String[0]);
		FilterQuery filterQuery = new FilterQuery(keywords);
		twitterStream.filter(filterQuery);
		LOG.info("Started filtering twitter stream for configured keywords"+Arrays.toString(keywords));
	}

}
