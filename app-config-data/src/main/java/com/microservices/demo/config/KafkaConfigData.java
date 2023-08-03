package com.microservices.demo.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix="kafka-config")
public class KafkaConfigData {

	private String bootstrapServers;
	private String schemaRegistryUrlKey;
	private String SchemaRegistryUrlValue;
	private String topicName;
	private List<String> topicNamesToCreate;
	private Integer numberOfpartitions;
	private Short replicationFactor;
}
