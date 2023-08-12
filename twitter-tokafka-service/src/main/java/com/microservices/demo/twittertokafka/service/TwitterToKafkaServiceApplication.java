package com.microservices.demo.twittertokafka.service;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.FileSystemResource;

import com.microservices.demo.config.TwitterToKafkaConfigData;
import com.microservices.demo.twittertokafka.service.init.StreamInitializer;
import com.microservices.demo.twittertokafka.service.runner.StreamRunner;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

@SpringBootApplication
@ComponentScan(basePackages="com.microservices.demo")

public class TwitterToKafkaServiceApplication implements CommandLineRunner {
	
	
	private final StreamRunner streamRunner;
	private final StreamInitializer streamInitializer;
	private static final Logger LOG = LoggerFactory.getLogger(TwitterToKafkaServiceApplication.class);
	
	public TwitterToKafkaServiceApplication(StreamInitializer init,
			StreamRunner streamrunner ) {
		this.streamInitializer = init;
		this.streamRunner = streamrunner;
	}
	
	public static void main(String args[])
	{
		SpringApplication.run(TwitterToKafkaServiceApplication.class, args);
		
		//System.out.println(new FileSystemResource("").getFile().getAbsolutePath());
	}

	/*
	 * Since this application will not be executed by client we need to implement type of runner for it
	 */
	@Override
	public void run(String... args) throws Exception {
		LOG.info("App started");
		streamInitializer.init();
		streamRunner.start();
	}

}
 