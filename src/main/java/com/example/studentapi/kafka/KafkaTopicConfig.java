package com.example.studentapi.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * KAFKA TOPIC CONFIGURATION
 *
 * @Configuration tells Spring this class defines beans (objects Spring manages).
 *
 * Without this class you'd have to manually create the topic using Kafka CLI:
 *   kafka-topics.sh --create --topic student-events ...
 *
 * With this class, Spring Kafka auto-creates the topic on startup if it
 * doesn't already exist on the broker.
 *
 * Topic settings explained:
 *   name       → "student-events" — the channel name producers/consumers agree on
 *   partitions → how many parallel "lanes" the topic has (more = higher throughput)
 *   replicas   → how many broker copies (1 = dev/local, 3 = production for HA)
 */
@Configuration
public class KafkaTopicConfig {

    public static final String STUDENT_EVENTS_TOPIC = "student-events";

    /**
     * @Bean — Spring calls this method and registers the returned object
     * in its IoC container. KafkaAdminClient picks it up and creates
     * the topic on the broker automatically.
     */
    @Bean
    public NewTopic studentEventsTopic() {
        return TopicBuilder
                .name(STUDENT_EVENTS_TOPIC)
                .partitions(1)   // 1 partition is fine for local dev
                .replicas(1)     // 1 replica — use 3 in production
                .build();
    }
}
