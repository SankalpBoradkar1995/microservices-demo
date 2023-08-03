package com.microservices.demo.twittertokafka.service.exceptions;

public class TwitterToKafkaserviceException extends RuntimeException {
	
	public TwitterToKafkaserviceException()
	{
		super();
	}
	
	public TwitterToKafkaserviceException(String message)
	{
		super(message);
	}
	public TwitterToKafkaserviceException(String message, Throwable cause)
	{
		super(message,cause);
	}

}
