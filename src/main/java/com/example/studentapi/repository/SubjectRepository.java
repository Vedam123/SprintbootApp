package com.example.studentapi.repository;

import com.example.studentapi.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * REPOSITORY LAYER — SubjectRepository
 *
 * Extends JpaRepository<Subject, Long>, which provides full CRUD for free:
 *
 *   save(subject)       → INSERT (new) or UPDATE (existing, matched by ID)
 *   findById(id)        → SELECT * FROM subjects WHERE id = ?  (returns Optional)
 *   findAll()           → SELECT * FROM subjects
 *   deleteById(id)      → DELETE FROM subjects WHERE id = ?
 *   existsById(id)      → SELECT COUNT(*) > 0 WHERE id = ?
 *   count()             → SELECT COUNT(*) FROM subjects
 *
 * Spring Data JPA generates the concrete implementation of this interface at
 * application startup — you never write a single line of JDBC or SQL for
 * the methods below.
 *
 * @Repository
 *   Marks this as a Spring-managed bean so it can be injected elsewhere.
 *   Also enables Spring's PersistenceExceptionTranslation — raw JDBC
 *   exceptions (e.g. SQLIntegrityConstraintViolationException) are
 *   automatically converted to Spring's DataAccessException hierarchy,
 *   making them easier to catch and handle uniformly.
 */
@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {

    /**
     * DERIVED QUERY METHOD — findByCode
     *
     * Spring reads the method name and auto-generates the SQL:
     *   SELECT * FROM subjects WHERE code = ?
     *
     * Naming convention breakdown:
     *   findBy  → SELECT + WHERE clause
     *   Code    → the field name in the Subject class (maps to "code" column)
     *
     * Returns Optional<Subject> because the code might not exist.
     * The caller uses .orElseThrow() or .isPresent() to handle absence.
     *
     * Use-case: look up a subject by its unique short code (e.g. "CS101")
     * without needing to know the numeric ID.
     */
    Optional<Subject> findByCode(String code);

    /**
     * DERIVED QUERY METHOD — existsByCode
     *
     * Auto-generated SQL:
     *   SELECT COUNT(*) > 0 FROM subjects WHERE code = ?
     *
     * Returns a boolean directly — no need to wrap in Optional.
     * Much more efficient than loading the full entity just to check existence.
     *
     * Use-case: in SubjectService.createSubject() we call this before saving
     * to give a friendly "code already exists" error instead of letting
     * a DB unique-constraint violation bubble up as a raw 500.
     */
    boolean existsByCode(String code);
}
