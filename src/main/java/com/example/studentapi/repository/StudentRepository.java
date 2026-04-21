package com.example.studentapi.repository;

import com.example.studentapi.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * REPOSITORY LAYER — Data Access Layer (DAL)
 *
 * This interface talks to the database. You do NOT write any SQL or
 * JDBC boilerplate. Spring Data JPA generates the implementation at runtime.
 *
 * JpaRepository<Student, Long> gives you these methods for FREE:
 *   save(student)         → INSERT or UPDATE
 *   findById(id)          → SELECT WHERE id = ?
 *   findAll()             → SELECT * FROM students
 *   deleteById(id)        → DELETE WHERE id = ?
 *   existsById(id)        → SELECT COUNT(*) WHERE id = ?
 *   count()               → SELECT COUNT(*)
 *   ... and more
 *
 * @Repository marks this as a Spring bean so it can be injected elsewhere.
 * (Actually optional here since JpaRepository is already detected, but
 * good practice for clarity and better exception translation.)
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    /**
     * DERIVED QUERY METHOD
     * Spring reads the method name and generates SQL automatically.
     * "findByEmail" → "SELECT * FROM students WHERE email = ?"
     * No SQL needed — the method name IS the query.
     */
    Optional<Student> findByEmail(String email);

    /**
     * Another derived query:
     * "findByCourse" → "SELECT * FROM students WHERE course = ?"
     */
    List<Student> findByCourse(String course);

    /**
     * JPQL CUSTOM QUERY
     * When the method name approach isn't enough, write JPQL.
     * JPQL uses class names (Student) and field names (s.name),
     * not table/column names — it's database-independent.
     */
    @Query("SELECT s FROM Student s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Student> searchByName(String name);

    /**
     * Check if a student with this email already exists.
     * Spring generates: "SELECT COUNT(*) > 0 WHERE email = ?"
     */
    boolean existsByEmail(String email);
}
