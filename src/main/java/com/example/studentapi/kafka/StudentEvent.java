package com.example.studentapi.kafka;

import java.time.LocalDateTime;

/**
 * KAFKA MESSAGE PAYLOAD
 *
 * This is the object that gets serialized to JSON and sent to the Kafka topic.
 * Both the producer (this app) and any consumer (any app) must agree on this structure.
 *
 * Think of it as the "contract" or "schema" of the message.
 * Producer writes it → Kafka stores it as JSON string → Consumer deserializes it back.
 *
 * Example JSON on the topic:
 * {
 *   "eventType": "STUDENT_CREATED",
 *   "studentId": 1,
 *   "studentName": "John",
 *   "email": "john@example.com",
 *   "occurredAt": "2026-04-14T10:30:00"
 * }
 */
public class StudentEvent {

    /**
     * Type of event — tells consumers what happened.
     * A single topic "student-events" can carry multiple event types.
     * Each consumer decides which types it cares about.
     */
    public enum EventType {
        STUDENT_CREATED,
        STUDENT_UPDATED,
        STUDENT_DELETED
    }

    private EventType eventType;
    private Long studentId;
    private String studentName;
    private String email;
    private String course;
    private LocalDateTime occurredAt;

    public StudentEvent() {}

    public StudentEvent(EventType eventType, Long studentId, String studentName,
                        String email, String course) {
        this.eventType   = eventType;
        this.studentId   = studentId;
        this.studentName = studentName;
        this.email       = email;
        this.course      = course;
        this.occurredAt  = LocalDateTime.now();
    }

    // ---- Getters & Setters ----

    public EventType getEventType()          { return eventType; }
    public void setEventType(EventType t)    { this.eventType = t; }

    public Long getStudentId()               { return studentId; }
    public void setStudentId(Long id)        { this.studentId = id; }

    public String getStudentName()           { return studentName; }
    public void setStudentName(String n)     { this.studentName = n; }

    public String getEmail()                 { return email; }
    public void setEmail(String e)           { this.email = e; }

    public String getCourse()                { return course; }
    public void setCourse(String c)          { this.course = c; }

    public LocalDateTime getOccurredAt()            { return occurredAt; }
    public void setOccurredAt(LocalDateTime t)      { this.occurredAt = t; }

    @Override
    public String toString() {
        return "StudentEvent{type=" + eventType + ", studentId=" + studentId +
               ", name='" + studentName + "', email='" + email + "'}";
    }
}
