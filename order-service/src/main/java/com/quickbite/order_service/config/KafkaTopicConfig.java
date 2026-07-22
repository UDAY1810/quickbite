package com.quickbite.order_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic orderEvents() {
        return TopicBuilder.name("order-events").partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic paymentEvents() {
        return TopicBuilder.name("payment-events").partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic deliveryEvents() {
        return TopicBuilder.name("delivery-events").partitions(3).replicas(1).build();
    }
}