package com.example.studentapi.service;

import com.example.studentapi.exception.StudentNotFoundException;
import com.example.studentapi.exception.SubjectNotFoundException;
import com.example.studentapi.model.Enrollment;
import com.example.studentapi.model.Student;
import com.example.studentapi.model.Subject;
import com.example.studentapi.repository.EnrollmentRepository;
import com.example.studentapi.repository.StudentRepository;
import com.example.studentapi.repository.SubjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * SERVICE LAYER — EnrollmentService
 *
 * Orchestrates the relationship between Students and Subjects.
 * This service intentionally depends on THREE repositories because it
 * must validate that both the student and subject exist before linking them.
 *
 * ORCHESTRATION PATTERN:
 *   A service that coordinates multiple repositories is called an
 *   "orchestration service" or "use-case service".
 *   Keeping this logic here (not in the controller) means:
 *     - The controller stays thin and HTTP-focused.
 *     - The rules can be tested without spinning up an HTTP server.
 *     - The logic can be called from other services if needed.
 *
 * @Service — registers this class as a singleton Spring bean.
 * @Transactional — all write methods run in a DB transaction.
 *   If enroll() saves an Enrollment and then something later throws,
 *   the entire transaction rolls back — no partial data.
 */
@Service
@Transactional
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository    studentRepository;
    private final SubjectRepository    subjectRepository;

    /**
     * CONSTRUCTOR INJECTION — three dependencies.
     *
     * Spring detects the single constructor and automatically injects
     * the matching beans from the IoC container.  No @Autowired needed.
     *
     * Using final fields guarantees that the collaborators can never be
     * null after construction, making the service safe to use immediately.
     */
    public EnrollmentService(EnrollmentRepository enrollmentRepository,
                             StudentRepository studentRepository,
                             SubjectRepository subjectRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.studentRepository    = studentRepository;
        this.subjectRepository    = subjectRepository;
    }

    // ================================================================
    // READ operations
    // ================================================================

    /**
     * Returns every enrollment row in the table.
     * Each Enrollment carries the full Student and Subject objects
     * because we used FetchType.EAGER on the @ManyToOne relations.
     */
    @Transactional(readOnly = true)
    public List<Enrollment> getAllEnrollments() {
        return enrollmentRepository.findAll();
    }

    /**
     * Returns all subjects a given student is enrolled in.
     *
     * Uses the derived query findByStudentId() in EnrollmentRepository.
     * Does NOT verify the student exists — if the ID is wrong we simply
     * return an empty list (same behaviour as a valid student with no enrollments).
     *
     * @param studentId the student's primary key
     * @return list of Enrollment records (may be empty)
     */
    @Transactional(readOnly = true)
    public List<Enrollment> getEnrollmentsByStudent(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId);
    }

    /**
     * Returns all students enrolled in a given subject.
     *
     * @param subjectId the subject's primary key
     * @return list of Enrollment records (may be empty)
     */
    @Transactional(readOnly = true)
    public List<Enrollment> getEnrollmentsBySubject(Long subjectId) {
        return enrollmentRepository.findBySubjectId(subjectId);
    }

    // ================================================================
    // WRITE operations
    // ================================================================

    /**
     * Enrolls a student in a subject.
     *
     * VALIDATION STEPS (in order):
     *   1. Verify the student exists — throws StudentNotFoundException (HTTP 404)
     *   2. Verify the subject exists — throws SubjectNotFoundException (HTTP 404)
     *   3. Check for duplicate enrollment — throws IllegalArgumentException (HTTP 409)
     *   4. Create and persist the Enrollment entity.
     *
     * Step 3 gives a user-friendly 409 Conflict instead of letting the DB
     * unique constraint on (student_id, subject_id) bubble up as a 500 error.
     *
     * The entire method is wrapped in one @Transactional (inherited from class),
     * so all DB reads and the final save are in the same transaction.
     *
     * @param studentId the ID of the student to enroll
     * @param subjectId the ID of the subject to enroll the student in
     * @return the saved Enrollment with its generated ID and today's date
     * @throws StudentNotFoundException  if studentId doesn't exist
     * @throws SubjectNotFoundException  if subjectId doesn't exist
     * @throws IllegalArgumentException  if the student is already enrolled
     */
    public Enrollment enroll(Long studentId, Long subjectId) {
        // Step 1 — verify student
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));

        // Step 2 — verify subject
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new SubjectNotFoundException(subjectId));

        // Step 3 — guard against duplicate enrollment
        if (enrollmentRepository.existsByStudentIdAndSubjectId(studentId, subjectId)) {
            throw new IllegalArgumentException(
                "Student " + studentId + " is already enrolled in subject " + subjectId
            );
        }

        // Step 4 — create and save
        Enrollment enrollment = new Enrollment(student, subject);
        return enrollmentRepository.save(enrollment);  // INSERT
    }

    /**
     * Updates (or assigns for the first time) the grade on an existing enrollment.
     *
     * PATCH semantics: only the grade field changes; enrolledAt and the
     * student/subject associations remain untouched.
     *
     * Common values: "A", "A-", "B+", "B", "C", "D", "F", "Pass", "Fail".
     * No validation is applied here — the caller decides the grading scale.
     *
     * @param enrollmentId the enrollment record to update
     * @param grade        the new grade string
     * @return the updated Enrollment
     * @throws StudentNotFoundException if the enrollment ID is not found
     *         (reusing StudentNotFoundException with a custom message to avoid
     *         creating an extra EnrollmentNotFoundException class — the
     *         GlobalExceptionHandler maps ANY StudentNotFoundException to 404)
     */
    public Enrollment updateGrade(Long enrollmentId, String grade) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new StudentNotFoundException("Enrollment not found with id: " + enrollmentId));

        enrollment.setGrade(grade);
        return enrollmentRepository.save(enrollment);  // UPDATE
    }

    /**
     * Removes an enrollment (unenrolls a student from a subject).
     *
     * Verifies existence first so we return 404 rather than silently
     * succeeding when the ID doesn't exist.
     *
     * @param enrollmentId the enrollment record to delete
     * @throws StudentNotFoundException if no enrollment has the given ID
     *         (message: "Enrollment not found with id: X" → HTTP 404)
     */
    public void unenroll(Long enrollmentId) {
        if (!enrollmentRepository.existsById(enrollmentId)) {
            throw new StudentNotFoundException("Enrollment not found with id: " + enrollmentId);
        }
        enrollmentRepository.deleteById(enrollmentId);  // DELETE
    }
}
