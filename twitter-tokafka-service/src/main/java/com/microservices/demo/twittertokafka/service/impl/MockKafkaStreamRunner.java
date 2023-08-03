package com.microservices.demo.twittertokafka.service.impl;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.microservices.demo.config.TwitterToKafkaConfigData;
import com.microservices.demo.twittertokafka.service.exceptions.TwitterToKafkaserviceException;
import com.microservices.demo.twittertokafka.service.listener.TwitterToKafkaStatusListener;
import com.microservices.demo.twittertokafka.service.runner.StreamRunner;

import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterObjectFactory;

@Component
@ConditionalOnProperty(name="twitter-to-kafka-service.enable-mock-tweets",havingValue="true")
public class MockKafkaStreamRunner implements StreamRunner {

	private static Logger LOG = LoggerFactory.getLogger(MockKafkaStreamRunner.class);

	private final TwitterToKafkaConfigData twitterToKafkaConfigData;
	private final TwitterToKafkaStatusListener twitterToKafkaStatusListener;

	private static final Random RANDOM = new Random();

	private static final String[] WORDS = new String[] { "Lorem", "ipsum", "dolor", "sit", "amet", "consectetuer",
			"adipiscing", "elit", "Maecenas", "porttitor", "congue", "massa", "Fusce", "posuere", "magna", "sed",
			"pulvinar", "ultricies", "purus", "lectus", "malesuada", "libero" };

	private static final String tweetAsRawJson = "{" + "\"created_at\":\"{0}\"," + "\"id\":\"{1}\","
			+ "\"text\":\"{2}\"," + "\"user\":{\"id\":\"{3}\"}" + "}";

	private static final String TWITTER_STATUS_DATE_FORMAT = "EEE MMM dd HH:mm:ss zzz yyyy";

	public MockKafkaStreamRunner(TwitterToKafkaConfigData configData, TwitterToKafkaStatusListener statusListener) {
		this.twitterToKafkaConfigData = configData;
		this.twitterToKafkaStatusListener = statusListener;
	}

	@Override
	public void start() throws TwitterException {
		String keywords[] = twitterToKafkaConfigData.getTwitterKeywords().toArray(new String[0]);
		int minTweetLength = twitterToKafkaConfigData.getMockMinTweetLength();
		int maxTweetlength = twitterToKafkaConfigData.getMockMaxTweetLength();
		long sleepTimeMs = twitterToKafkaConfigData.getMockSleepMs();

		LOG.info("Starting mock filtering tweeter streams for the keywords: " + Arrays.toString(keywords));

		simulateTwitterStream(keywords, minTweetLength, maxTweetlength, sleepTimeMs);
	}

	private void simulateTwitterStream(String[] keywords, int minTweetLength, int maxTweetlength, long sleepTimeMs) {

		// We dont want to run infinite while loop on main thread
		// so will create separate thread to get it run

		/*
		 * Submit implements runnable interface runnable interface has one abstract
		 * method run which accepts no parameters and returns nothing
		 * 
		 * So we can pass implementation to the abstract method directly as lambda
		 */
		Executors.newSingleThreadExecutor().submit(() -> {
			try {
				while (true) {
					String formatteDtweetsAsRawJson = getFormattedTweets(keywords, minTweetLength, maxTweetlength);
					Status status = TwitterObjectFactory.createStatus(formatteDtweetsAsRawJson);
					twitterToKafkaStatusListener.onStatus(status);
					sleep(sleepTimeMs);
				}
			} catch (TwitterException e) {
				// TODO Auto-generated catch block
				LOG.error("Error creating twitter status", e);
			}
		});

	}

	private void sleep(long sleepTimeMs) {
		try {
			Thread.sleep(sleepTimeMs);
		} catch (InterruptedException e) {
			throw new TwitterToKafkaserviceException("Error while waiting for new status to be created", e);
		}

	}

	private String getFormattedTweets(String[] keywords, int minTweetLength, int maxTweetlength) {
		String params[] = new String[] {
				ZonedDateTime.now().format(DateTimeFormatter.ofPattern(TWITTER_STATUS_DATE_FORMAT, Locale.ENGLISH)),
				String.valueOf(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE)),
				getRandomTweetContent(keywords, minTweetLength, maxTweetlength),
				String.valueOf(ThreadLocalRandom.current().nextLong(Long.MAX_VALUE)),

		};

		return formatTweetsAsJsonWithParams(params);
	}

	private String formatTweetsAsJsonWithParams(String[] params) {
		String tweet = tweetAsRawJson;

		for (int i = 0; i < params.length; i++) {
			tweet = tweet.replace("{" + i + "}", params[i]);
		}
		return tweet;
	}

	private String getRandomTweetContent(String[] keywords, int minTweetLength, int maxTweetlength) {
		StringBuilder tweet = new StringBuilder();
		int tweetLength = RANDOM.nextInt(maxTweetlength - minTweetLength + 1) + minTweetLength;
		return constructRandomTweets(keywords, tweet, tweetLength);
	}

	private String constructRandomTweets(String[] keywords, StringBuilder tweet, int tweetLength) {
		for (int i = 0; i < tweetLength; i++) {
			tweet.append(WORDS[RANDOM.nextInt(WORDS.length)]).append(" ");
			if (i == tweetLength / 2) {
				tweet.append(keywords[RANDOM.nextInt(keywords.length)]).append(" ");
			}
		}
		return tweet.toString().trim();
	}

}
