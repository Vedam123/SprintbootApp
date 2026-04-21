package com.example.studentapi.model;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * MODEL / ENTITY LAYER — Enrollment (Join Table with Extra Data)
 *
 * An Enrollment represents the many-to-many relationship between Student and
 * Subject, but with EXTRA data attached (enrolledAt, grade).  This is why we
 * use a dedicated entity class + table instead of a plain @ManyToMany.
 *
 * DATABASE TABLE: enrollments
 *   Columns: id, student_id (FK), subject_id (FK), enrolled_at, grade
 *
 * KEY JPA CONCEPTS demonstrated here:
 *
 *   @ManyToOne
 *     Many enrollments can reference the same student/subject.
 *     Hibernate adds a foreign-key column (student_id / subject_id) to
 *     the enrollments table automatically.
 *
 *   FetchType.EAGER
 *     When Hibernate loads an Enrollment row it IMMEDIATELY also loads
 *     the associated Student and Subject in the same SQL round-trip
 *     (via a JOIN).  This avoids Jackson's "lazy-loading" serialisation
 *     problem where the proxy object can't be serialised to JSON after
 *     the transaction closes.
 *
 *   @UniqueConstraint
 *     Defined at the @Table level to span TWO columns (student_id, subject_id).
 *     This prevents a student from being enrolled in the same subject twice.
 *     The service layer also checks this first for a user-friendly error.
 */
@Entity
@Table(
    name = "enrollments",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_enrollment_student_subject",
            columnNames = {"student_id", "subject_id"}
        )
    }
)
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The student who enrolled.
     *
     * @ManyToOne — many Enrollment rows can point to one Student.
     * @JoinColumn — specifies the FK column name in the enrollments table.
     * nullable = false — every enrollment must have a student.
     * FetchType.EAGER — load the Student immediately with the Enrollment
     *                   so Jackson can serialise the full object graph.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    /**
     * The subject the student enrolled in.
     * Same reasoning as the student field above.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    /**
     * The date the enrollment was created.
     * Set automatically to today in the constructor — the caller never
     * needs to supply this value.
     */
    private LocalDate enrolledAt;

    /**
     * The student's grade in this subject (e.g. "A", "B+", "C").
     * Nullable — a student has no grade until the course is assessed.
     * Updated later via the PATCH /api/enrollments/{id}/grade endpoint.
     */
    private String grade;

    // ---- Constructors ----

    /**
     * No-arg constructor required by JPA.
     * Also sets enrolledAt to today so it is never null after construction.
     */
    public Enrollment() {
        this.enrolledAt = LocalDate.now();
    }

    public Enrollment(Student student, Subject subject) {
        this.student = student;
        this.subject = subject;
        this.enrolledAt = LocalDate.now();   // auto-stamped
    }

    // ---- Getters & Setters ----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }

    public LocalDate getEnrolledAt() { return enrolledAt; }
    public void setEnrolledAt(LocalDate enrolledAt) { this.enrolledAt = enrolledAt; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    @Override
    public String toString() {
        return "Enrollment{id=" + id +
               ", studentId=" + (student != null ? student.getId() : null) +
               ", subjectId=" + (subject != null ? subject.getId() : null) +
               ", enrolledAt=" + enrolledAt +
               ", grade='" + grade + "'}";
    }
}
