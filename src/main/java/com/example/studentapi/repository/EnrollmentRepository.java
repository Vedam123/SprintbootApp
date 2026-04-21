package com.example.studentapi.repository;

import com.example.studentapi.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * REPOSITORY LAYER — EnrollmentRepository
 *
 * Extends JpaRepository<Enrollment, Long>.
 * Inherited free methods:
 *   save(enrollment)    → INSERT or UPDATE
 *   findById(id)        → SELECT * FROM enrollments WHERE id = ?
 *   findAll()           → SELECT * FROM enrollments
 *   deleteById(id)      → DELETE FROM enrollments WHERE id = ?
 *   existsById(id)      → SELECT COUNT(*) > 0 WHERE id = ?
 *
 * DERIVED QUERY METHODS work by Spring parsing the method name at startup
 * and generating a corresponding JPQL (and then SQL) statement.
 * The pattern is:
 *   find[By][Property][Condition]  →  SELECT ... WHERE property [op] ?
 *
 * Nested property traversal uses the dot (.) notation in JPQL, which maps
 * to an underscore (_) in the method name convention.
 * e.g. "student_id" in the DB == "student.id" in JPQL == "StudentId" in
 * the method name.
 *
 * @Repository provides exception translation (raw JDBC exceptions →
 * Spring's DataAccessException) and registers the bean in the IoC container.
 */
@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    /**
     * DERIVED QUERY — findByStudentId
     *
     * Generated SQL (approximately):
     *   SELECT e.* FROM enrollments e
     *   JOIN students s ON e.student_id = s.id
     *   WHERE s.id = ?
     *
     * "StudentId" → Spring traverses Enrollment.student → Student.id
     *
     * Returns a List (possibly empty) of all enrollments for the given student.
     * Use-case: GET /api/enrollments/student/{studentId}
     */
    List<Enrollment> findByStudentId(Long studentId);

    /**
     * DERIVED QUERY — findBySubjectId
     *
     * Generated SQL (approximately):
     *   SELECT e.* FROM enrollments e
     *   JOIN subjects sub ON e.subject_id = sub.id
     *   WHERE sub.id = ?
     *
     * Returns all enrollments for a given subject — useful to see which
     * students are in a class.
     * Use-case: GET /api/enrollments/subject/{subjectId}
     */
    List<Enrollment> findBySubjectId(Long subjectId);

    /**
     * DERIVED QUERY — existsByStudentIdAndSubjectId
     *
     * Generated SQL (approximately):
     *   SELECT COUNT(*) > 0 FROM enrollments
     *   WHERE student_id = ? AND subject_id = ?
     *
     * "And" in the method name adds a second WHERE condition.
     * Returns boolean — much faster than fetching the full row.
     *
     * Use-case: EnrollmentService.enroll() calls this to prevent a student
     * from being enrolled in the same subject twice (duplicate-enrollment guard).
     */
    boolean existsByStudentIdAndSubjectId(Long studentId, Long subjectId);

    /**
     * DERIVED QUERY — findByStudentIdAndSubjectId
     *
     * Generated SQL (approximately):
     *   SELECT * FROM enrollments
     *   WHERE student_id = ? AND subject_id = ?
     *
     * Returns an Optional<Enrollment> because the combination might not exist.
     *
     * Use-case: look up a specific student–subject pairing, e.g. to check
     * an existing grade or to unenroll by student + subject rather than by ID.
     */
    Optional<Enrollment> findByStudentIdAndSubjectId(Long studentId, Long subjectId);
}
