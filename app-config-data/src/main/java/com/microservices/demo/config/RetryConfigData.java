package com.microservices.demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;



//@Data
@Data
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix="retry-config")
public class RetryConfigData {

	private Long initialIntervalMs;
	private Long maxIntervalMs;
	private Integer maxAttempts;
	private Double multiplier;
	private Long sleepTimeMs;
	
}
