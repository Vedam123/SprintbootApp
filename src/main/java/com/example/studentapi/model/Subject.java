package com.example.studentapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

/**
 * MODEL / ENTITY LAYER — Subject
 *
 * This class maps to the "subjects" database table.
 * Hibernate reads the JPA annotations and creates the table automatically.
 *
 * Validation annotations (@NotBlank, @Min) are enforced by Spring's
 * @Valid mechanism when a Subject arrives in a @RequestBody.
 * They fire BEFORE the data reaches the service layer, keeping
 * business logic clean.
 *
 * JPA annotations used here:
 *   @Entity          → Hibernate manages this class as a persistent entity
 *   @Table           → Customises the physical table name ("subjects")
 *   @Id              → Marks the primary key field
 *   @GeneratedValue  → Tells the DB to auto-increment the ID column
 *   @Column          → Customises a column (nullable, unique, length, etc.)
 */
@Entity
@Table(name = "subjects")
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Auto-increment PK
    private Long id;

    /**
     * The human-readable subject name, e.g. "Computer Science".
     * @NotBlank rejects null, empty strings, and whitespace-only strings.
     * @Column(nullable = false) enforces the constraint at the DB level too.
     */
    @NotBlank(message = "Subject name is required")
    @Column(nullable = false)
    private String name;

    /**
     * A short unique code for the subject, e.g. "CS101".
     * unique = true on @Column creates a UNIQUE index in the DB.
     * The service layer also checks for duplicates before saving to give a
     * friendlier error message than a raw SQL constraint violation.
     */
    @NotBlank(message = "Subject code is required")
    @Column(nullable = false, unique = true)
    private String code;

    /**
     * Number of credits the subject is worth (must be at least 1).
     * @Min enforces this at the validation layer before it hits the DB.
     */
    @Min(value = 1, message = "Credits must be at least 1")
    private int credits;

    // ---- Constructors ----

    /**
     * No-arg constructor required by JPA/Hibernate.
     * Hibernate uses it when it reconstructs objects from DB rows.
     */
    public Subject() {}

    public Subject(String name, String code, int credits) {
        this.name = name;
        this.code = code;
        this.credits = credits;
    }

    // ---- Getters & Setters ----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }

    @Override
    public String toString() {
        return "Subject{id=" + id + ", name='" + name + "', code='" + code +
               "', credits=" + credits + "}";
    }
}
