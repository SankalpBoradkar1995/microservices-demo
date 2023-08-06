package com.microservices.demo.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix="twitter-to-kafka-service")
public class TwitterToKafkaConfigData {

	private List<String> twitterKeywords;
	private String welcomeMessage;
	private String twitterV2BaseUrl;
	private String twitterV2RulesBaseUrl;
	private String twitterV2BearerToken;
	private Boolean enableMockTweets;
	private Long mockSleepMs;
	private Integer mockMinTweetLength;
	private Integer mockMaxTweetLength;
}
