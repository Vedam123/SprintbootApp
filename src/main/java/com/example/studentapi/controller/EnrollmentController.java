package com.example.studentapi.controller;

import com.example.studentapi.model.Enrollment;
import com.example.studentapi.service.EnrollmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CONTROLLER LAYER — EnrollmentController
 *
 * Handles all HTTP requests under /api/enrollments.
 *
 * @RestController — combines @Controller and @ResponseBody.
 *   Every return value is serialised to JSON automatically by Jackson.
 *
 * @RequestMapping("/api/enrollments") — base path for all endpoints here.
 *
 * DESIGN NOTE — @RequestParam vs @PathVariable:
 *   @PathVariable   → value is embedded in the URL path  (e.g. /enrollments/42)
 *   @RequestParam   → value is a query-string parameter  (e.g. ?studentId=1&subjectId=2)
 *
 *   We use @RequestParam for the enroll and grade-update endpoints because
 *   the IDs/grade are "inputs to an action", not resource identifiers.
 *   This keeps the URL structure clean: POST /enroll?studentId=1&subjectId=2
 *   clearly reads as "perform the enroll action with these parameters".
 *
 * DEPENDENCY INJECTION:
 *   Spring injects EnrollmentService through the constructor — no @Autowired needed.
 */
@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    /**
     * Constructor injection — Spring resolves and injects EnrollmentService
     * from the IoC container at application startup.
     */
    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    // ================================================================
    // GET /api/enrollments
    // Returns every enrollment record (student + subject + grade + date).
    // HTTP 200 OK.
    // ================================================================
    @GetMapping
    public ResponseEntity<List<Enrollment>> getAllEnrollments() {
        List<Enrollment> enrollments = enrollmentService.getAllEnrollments();
        return ResponseEntity.ok(enrollments);  // 200 OK
    }

    // ================================================================
    // GET /api/enrollments/student/{studentId}
    // Returns all subjects a specific student is enrolled in.
    //
    // @PathVariable binds the {studentId} path segment to Long studentId.
    // Returns an empty array (not 404) if the student has no enrollments.
    // HTTP 200 OK.
    // ================================================================
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Enrollment>> getEnrollmentsByStudent(@PathVariable Long studentId) {
        List<Enrollment> enrollments = enrollmentService.getEnrollmentsByStudent(studentId);
        return ResponseEntity.ok(enrollments);
    }

    // ================================================================
    // GET /api/enrollments/subject/{subjectId}
    // Returns all students enrolled in a specific subject (the "class list").
    //
    // Useful for instructors to see who is in their class.
    // HTTP 200 OK; empty array if no one is enrolled yet.
    // ================================================================
    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<List<Enrollment>> getEnrollmentsBySubject(@PathVariable Long subjectId) {
        List<Enrollment> enrollments = enrollmentService.getEnrollmentsBySubject(subjectId);
        return ResponseEntity.ok(enrollments);
    }

    // ================================================================
    // POST /api/enrollments/enroll?studentId=1&subjectId=2
    // Enrolls a student in a subject.
    //
    // @RequestParam — reads the query-string parameters studentId and subjectId.
    //   required = true by default, so Spring returns 400 if either is missing.
    //
    // HTTP 201 Created on success, with the new Enrollment in the body.
    // HTTP 404 if the student or subject ID doesn't exist.
    // HTTP 409 if the student is already enrolled in that subject.
    // ================================================================
    @PostMapping("/enroll")
    public ResponseEntity<Enrollment> enroll(
            @RequestParam Long studentId,
            @RequestParam Long subjectId) {
        Enrollment enrollment = enrollmentService.enroll(studentId, subjectId);
        return ResponseEntity.status(HttpStatus.CREATED).body(enrollment);  // 201 Created
    }

    // ================================================================
    // PATCH /api/enrollments/{id}/grade?grade=A
    // Updates (or assigns) a grade on an existing enrollment.
    //
    // PATCH is semantically correct here because we are partially updating
    // the resource — only the "grade" field changes; everything else stays.
    //
    // @PathVariable Long id         — the enrollment record to update.
    // @RequestParam String grade    — the new grade value (e.g. "A", "B+").
    //
    // HTTP 200 OK with the updated Enrollment.
    // HTTP 404 if the enrollment ID doesn't exist.
    // ================================================================
    @PatchMapping("/{id}/grade")
    public ResponseEntity<Enrollment> updateGrade(
            @PathVariable Long id,
            @RequestParam String grade) {
        Enrollment updated = enrollmentService.updateGrade(id, grade);
        return ResponseEntity.ok(updated);  // 200 OK
    }

    // ================================================================
    // DELETE /api/enrollments/{id}
    // Unenrolls a student (removes the enrollment record).
    //
    // HTTP 204 No Content on success (nothing to return after deletion).
    // HTTP 404 if the enrollment ID doesn't exist.
    // ================================================================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> unenroll(@PathVariable Long id) {
        enrollmentService.unenroll(id);
        return ResponseEntity.noContent().build();  // 204 No Content
    }
}
