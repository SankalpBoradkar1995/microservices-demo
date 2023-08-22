# microservices-demo
# Using spring webflux to make http calls since its thread safe compared to RestTemplet
# Using Spring retry to check topics created or not
# Kafka config data maintained under application.yml file of twitter-to-kafka service
# Retry config data maintained under application.yml file of twitter-to-kafka service
# Spring retry mechanism is configured to check kafka and schema registry are up and topics are created or not
# Main components of retry mechanism are:
	#1. KafkaAdminConfig -> It has @EnableRetry added and bean method AdminClient
	#   Kafka admin client manage and inspects  brokers, topics and copnfiguration
	#2  RetryConfig -> It has Retrytemplet configured with values from application.yml file
# Retry mechanism is achieved via three main methods #checkTopicsCreated, #checkSchemaRegistry, #createTopics