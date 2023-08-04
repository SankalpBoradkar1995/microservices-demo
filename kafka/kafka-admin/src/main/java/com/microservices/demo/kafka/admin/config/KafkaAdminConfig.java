package com.microservices.demo.kafka.admin.config;

import java.util.Map;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;

import com.microservices.demo.config.KafkaConfigData;

@EnableRetry
@Configuration
public class KafkaAdminConfig {

	private final KafkaConfigData kafkaConfigData;
	
	public KafkaAdminConfig(KafkaConfigData configData)
	{
		this.kafkaConfigData = configData;
	}
	
	
	/*
	 * Kafka admin client manage and inspects  brokers, topics and copnfiguration
	 */
	@Bean
	public AdminClient adminClient()
	{
		return AdminClient.create(Map.of(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG,
				kafkaConfigData.getBootstrapServers()));
	}
}
