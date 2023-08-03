package com.microservices.demo.twittertokafka.service.impl;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.microservices.demo.config.TwitterToKafkaConfigData;
import com.microservices.demo.twittertokafka.service.runner.StreamRunner;

import twitter4j.TwitterException;

@Component
@ConditionalOnProperty(name="twitter-to-kafka-service.enable-v2-tweets",havingValue="true",matchIfMissing=true)
public class TwitterV2KafkaStreamrunner implements StreamRunner{

	private static final Logger LOG = LoggerFactory.getLogger(TwitterV2KafkaStreamrunner.class);
	
	private final TwitterToKafkaConfigData twitterToKafkaConfigData;
	private final TwitterV2StreamHelper twitterV2StreamHelper;
	
	public  TwitterV2KafkaStreamrunner(TwitterToKafkaConfigData configData,
			TwitterV2StreamHelper v2Streamhelper)
	{
		this.twitterToKafkaConfigData = configData;
		this.twitterV2StreamHelper = v2Streamhelper;
	}
	
	@Override
	public void start()  {
		
		String bearerToken  = twitterToKafkaConfigData.getTwitterV2BearerToken();
		if(bearerToken!=null)
		{
			try {
				twitterV2StreamHelper.setupRules(bearerToken, getRules());
				twitterV2StreamHelper.connectStream(bearerToken);
			} catch (IOException| URISyntaxException | JSONException e) {
				LOG.info("Error streaming tweets",e);
				throw new RuntimeException("Error streaming tweets");
			}  
		}
		else
		{
			LOG.error("There was problem getting bearer token"+
		"Check if TWITTER_BEARER_TOKEN is set correctly in environment variable");
			throw new RuntimeException("There was problem getting bearer token"+
					"Check if TWITTER_BEARER_TOKEN is set correctly in environment variable");
		}
		
	}

	private Map<String, String> getRules() {
		List<String> keywords = twitterToKafkaConfigData.getTwitterKeywords();
		Map<String,String> rules = new HashMap<>();
		for(String words: keywords)
		{
			rules.put(words, "keyword: "+words);
		}
		LOG.info("Created filter for twitter stream for keywords {} "+keywords);
		return rules;
	}

}
