package com.example.studentapi.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * KAFKA CONSUMER
 *
 * In a real microservices setup this would be a SEPARATE application
 * (e.g. email-service, analytics-service). It is included here in the
 * same app purely to demonstrate the full producer → broker → consumer
 * cycle without needing a second project.
 *
 * @KafkaListener works like this:
 *   1. Spring Kafka starts a background thread on app startup
 *   2. That thread continuously polls the broker for new messages
 *   3. When a message arrives it deserializes JSON → StudentEvent
 *   4. Spring calls the annotated method with the deserialized object
 *
 * groupId = "student-consumer-group"
 *   Kafka tracks which messages each consumer GROUP has already read (offset).
 *   If this app restarts, it continues from where it left off — no messages lost.
 *   Multiple instances of this app with the same groupId share the load
 *   (each message goes to only ONE instance in the group).
 */
@Component
public class StudentEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(StudentEventConsumer.class);

    /**
     * Listens to "student-events" topic.
     *
     * In production you'd have separate @KafkaListener methods (or classes) for:
     *   - Sending welcome emails
     *   - Updating analytics dashboards
     *   - Writing audit logs
     *
     * Each would be in its own consumer app with its own groupId so they
     * all receive every message independently.
     */
    @KafkaListener(
            topics = KafkaTopicConfig.STUDENT_EVENTS_TOPIC,
            groupId = "student-consumer-group"
    )
    public void consume(StudentEvent event) {
        log.info("============================================");
        log.info("Received Kafka event!");
        log.info("  Event Type : {}", event.getEventType());
        log.info("  Student ID : {}", event.getStudentId());
        log.info("  Name       : {}", event.getStudentName());
        log.info("  Email      : {}", event.getEmail());
        log.info("  Course     : {}", event.getCourse());
        log.info("  Occurred   : {}", event.getOccurredAt());
        log.info("============================================");

        // In a real consumer app, this is where you'd:
        // emailService.sendWelcomeEmail(event.getEmail());
        // analyticsService.trackNewStudent(event.getStudentId());
        // auditService.log(event);
    }
}
