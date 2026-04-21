package com.example.studentapi.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * KAFKA PRODUCER
 *
 * @Component — marks this as a Spring bean so it can be injected
 * into StudentService via constructor injection.
 *
 * KafkaTemplate is the Spring abstraction over the Kafka Producer API.
 * It handles:
 *   - Serializing the StudentEvent object to JSON
 *   - Connecting to the broker
 *   - Sending the message to the correct topic
 *   - Async callbacks (success / failure)
 *
 * You never deal with raw Kafka Producer API — KafkaTemplate does it for you,
 * just like JdbcTemplate abstracts raw JDBC.
 */
@Component
public class StudentEventProducer {

    private static final Logger log = LoggerFactory.getLogger(StudentEventProducer.class);

    /**
     * KafkaTemplate<String, StudentEvent>
     *   String       → type of the message KEY (we use studentId as key)
     *   StudentEvent → type of the message VALUE (the actual payload)
     *
     * Spring auto-configures this bean using application.properties settings.
     * Jackson serializes StudentEvent → JSON string before sending to broker.
     */
    private final KafkaTemplate<String, StudentEvent> kafkaTemplate;

    public StudentEventProducer(KafkaTemplate<String, StudentEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Publishes a StudentEvent to the "student-events" topic.
     *
     * kafkaTemplate.send(topic, key, value)
     *   topic → which topic to write to
     *   key   → used by Kafka to decide which partition to write to
     *            (same key always goes to same partition — useful for ordering)
     *   value → the actual message (StudentEvent serialized to JSON)
     *
     * send() returns a CompletableFuture — the send is ASYNC.
     * This method returns immediately — it does NOT wait for the broker to confirm.
     * The callbacks (whenComplete) just log success or failure.
     */
    public void publishStudentEvent(StudentEvent event) {
        String key = String.valueOf(event.getStudentId());  // partition by studentId

        CompletableFuture<SendResult<String, StudentEvent>> future =
                kafkaTemplate.send(KafkaTopicConfig.STUDENT_EVENTS_TOPIC, key, event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                // Message successfully acknowledged by broker
                log.info("Published event: {} | topic={} | partition={} | offset={}",
                        event.getEventType(),
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            } else {
                // Broker unreachable or other error — log it but don't crash the app
                log.error("Failed to publish event: {} | reason: {}",
                        event.getEventType(), ex.getMessage());
            }
        });
    }
}
