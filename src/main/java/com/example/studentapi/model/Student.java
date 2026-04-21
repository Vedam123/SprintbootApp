package com.example.studentapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

/**
 * MODEL / ENTITY LAYER
 *
 * This class serves two purposes:
 *
 * 1. JPA ENTITY (@Entity, @Table, @Id, @Column)
 *    Hibernate maps this Java class to a database table.
 *    You never write "CREATE TABLE" SQL — Hibernate does it for you.
 *
 * 2. VALIDATION (@NotBlank, @Email, @Min)
 *    When a request comes in, Spring validates these constraints
 *    before the data even reaches your service layer.
 */
@Entity                          // Marks this as a JPA entity (DB table)
@Table(name = "students")        // Optional: customize the table name
public class Student {

    @Id                          // This field is the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Auto-increment ID
    private Long id;

    @NotBlank(message = "Name is required")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Course is required")
    private String course;

    @Min(value = 18, message = "Age must be at least 18")
    private int age;

    // ---- Constructors ----

    public Student() {}   // JPA requires a no-arg constructor

    public Student(String name, String email, String course, int age) {
        this.name = name;
        this.email = email;
        this.course = course;
        this.age = age;
    }

    // ---- Getters & Setters ----

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    @Override
    public String toString() {
        return "Student{id=" + id + ", name='" + name + "', email='" + email +
               "', course='" + course + "', age=" + age + "}";
    }
}
