package com.quickbite.payment_service.config;

import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaErrorConfig {

    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<String, Object> kafka) {

        // where to send a message that keeps failing
        DeadLetterPublishingRecoverer recoverer =
                new DeadLetterPublishingRecoverer(kafka,
                        (record, ex) -> new TopicPartition(
                                record.topic() + ".DLT", -1));   // -1 = any partition

        // retry 2 more times, 1 second apart, then give up and send to DLT
        return new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 2L));
    }
}