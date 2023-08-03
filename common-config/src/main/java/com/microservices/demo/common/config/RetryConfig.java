package com.microservices.demo.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import com.microservices.demo.config.RetryConfigData;

@Configuration
public class RetryConfig {

	private final RetryConfigData retryConfigData;
	
	public RetryConfig(RetryConfigData configData)
	{
		this.retryConfigData = configData;
	}
	
	@Bean
	public RetryTemplate retryTemplate()
	{
		RetryTemplate retryTemplate = new RetryTemplate();
		// ExponentialBackOffPolicy to configure retry template to hold for every retry attempt
		// for specified configuration
		
		ExponentialBackOffPolicy exponentialBackOffPolicy = new ExponentialBackOffPolicy();
		exponentialBackOffPolicy.setInitialInterval(retryConfigData.getInitialIntervalMs());
		exponentialBackOffPolicy.setMaxInterval(retryConfigData.getMaxIntervalMs());
		exponentialBackOffPolicy.setMultiplier(retryConfigData.getMultiplier());
		
		retryTemplate.setBackOffPolicy(exponentialBackOffPolicy);
		
		/*
		 * SimpleRetry Policy to retry until max attempts are reached
		 */
		
		SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy();
		simpleRetryPolicy.setMaxAttempts(retryConfigData.getMaxAttempts());
		
		retryTemplate.setRetryPolicy(simpleRetryPolicy);
		
		return retryTemplate;
	}
	
}
