package com.microservices.demo.kafka.producer.config;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import com.microservices.demo.config.KafkaConfigData;
import com.microservices.demo.config.KafkaProducerConfigData;

@Configuration
public class KafkaProducerConfig<K extends Serializable , V extends SpecificRecordBase> {
    private final KafkaConfigData kafkaConfigData;
    private final KafkaProducerConfigData kafkaProducerConfigData;

    public KafkaProducerConfig(KafkaConfigData configData,KafkaProducerConfigData producerConfigData )
    {
        this.kafkaConfigData = configData;
        this.kafkaProducerConfigData = producerConfigData;
    }
 
    
    /*
     * Using bean of type KafkaTemplet<K,V> (K&V are generic) from spring-kafka dependency to add data/message to kafka
	   To create KafkaTemplet bean we need to create DefaultkafkaProducerFactory bean
     */
    
    /*
     * Now we will creat bean of Map type which will contain configuration properties for kafka producer
     * 
     * It will return Map of priducerConfig type
     * as a key we will add parameters of ProducerConfig class from spring-kafka like batch size, linger time etc
     * 
     */
    
    @Bean
    public Map<String,Object> producerConfig()
    {
    	Map<String,Object> props = new HashMap<>();
    	props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfigData.getBootstrapServers());
    	props.put(kafkaConfigData.getSchemaRegistryUrlKey(), kafkaConfigData.getSchemaRegistryUrl());
    	props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, kafkaProducerConfigData.getKeySerializerClass());
    	props.put(ProducerConfig.BATCH_SIZE_CONFIG, kafkaProducerConfigData.getBatchSize()*kafkaProducerConfigData.getBatchSizeBoostFactor());
    	props.put(ProducerConfig.LINGER_MS_CONFIG, kafkaProducerConfigData.getLingerMs());
    	props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, kafkaProducerConfigData.getCompressionType());
    	props.put(ProducerConfig.ACKS_CONFIG, kafkaProducerConfigData.getAcks());
    	props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, kafkaProducerConfigData.getRequestTimeoutMs());
    	props.put(ProducerConfig.RETRIES_CONFIG, kafkaProducerConfigData.getRetryCount());
    	return props;
    }
    
    /*
     * Now will creat bean to construct producerFactory with producer config 
     */
    
    public ProducerFactory<K, V> producerFactory()
    {
    	return new DefaultKafkaProducerFactory<>(producerConfig());
    }
    
    /*
     * Now we will pass this producerFactory to kafkaTemplet
     */
    
    @Bean
    public KafkaTemplate<K, V> kafkaTemplet()
    {
    	return new KafkaTemplate<>(producerFactory());
    }
    
}
