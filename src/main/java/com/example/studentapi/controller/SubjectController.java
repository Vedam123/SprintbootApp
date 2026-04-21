package com.example.studentapi.controller;

import com.example.studentapi.model.Subject;
import com.example.studentapi.service.SubjectService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CONTROLLER LAYER — SubjectController
 *
 * Handles all HTTP requests under /api/subjects.
 *
 * @RestController
 *   A shortcut for @Controller + @ResponseBody.
 *   @Controller  → this class participates in Spring MVC request mapping.
 *   @ResponseBody → return values are automatically serialised to JSON
 *                   by Jackson (the default JSON library bundled with
 *                   spring-boot-starter-web).
 *
 * @RequestMapping("/api/subjects")
 *   All handler methods in this class share this base URL prefix.
 *   Individual methods refine the path with @GetMapping, @PostMapping, etc.
 *
 * SINGLE RESPONSIBILITY:
 *   This class ONLY handles HTTP mechanics (parsing, routing, status codes).
 *   Business logic (duplicate checks, 404 handling) lives in SubjectService.
 *   Database access lives in SubjectRepository.
 *   Each layer is independently testable.
 *
 * DEPENDENCY INJECTION:
 *   Spring injects SubjectService via the constructor.  We never call
 *   "new SubjectService()" — the IoC container manages the lifecycle.
 */
@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    private final SubjectService subjectService;

    /**
     * Constructor injection — the recommended DI style.
     * Spring detects this single constructor and injects SubjectService
     * automatically (no @Autowired annotation required since Spring 4.3+).
     */
    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    // ================================================================
    // GET /api/subjects
    // Returns all subjects as a JSON array.
    // HTTP 200 OK on success.
    // ================================================================
    @GetMapping
    public ResponseEntity<List<Subject>> getAllSubjects() {
        List<Subject> subjects = subjectService.getAllSubjects();
        return ResponseEntity.ok(subjects);  // 200 OK
    }

    // ================================================================
    // GET /api/subjects/{id}
    // Returns a single subject by ID.
    // HTTP 200 OK if found; 404 Not Found if the ID doesn't exist
    // (thrown by SubjectService, caught by GlobalExceptionHandler).
    // ================================================================
    @GetMapping("/{id}")
    public ResponseEntity<Subject> getSubjectById(@PathVariable Long id) {
        Subject subject = subjectService.getSubjectById(id);
        return ResponseEntity.ok(subject);
    }

    // ================================================================
    // POST /api/subjects
    // Creates a new subject.
    //
    // @RequestBody  — deserialises the JSON request body into a Subject object.
    // @Valid        — triggers Bean Validation (@NotBlank, @Min, …) on the
    //                 Subject fields.  If validation fails, Spring throws
    //                 MethodArgumentNotValidException BEFORE this method
    //                 is even called; GlobalExceptionHandler catches it → 400.
    //
    // HTTP 201 Created on success, with the saved subject (including new ID)
    // in the response body.
    // ================================================================
    @PostMapping
    public ResponseEntity<Subject> createSubject(@Valid @RequestBody Subject subject) {
        Subject created = subjectService.createSubject(subject);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);  // 201 Created
    }

    // ================================================================
    // PUT /api/subjects/{id}
    // Full replacement update (HTTP PUT semantics — all fields replaced).
    //
    // @PathVariable  — binds the {id} URL segment to the Long id parameter.
    // @Valid          — re-validates the incoming JSON against Subject constraints.
    //
    // HTTP 200 OK with the updated subject on success.
    // 404 if the ID doesn't exist; 409 if the new code clashes with another subject.
    // ================================================================
    @PutMapping("/{id}")
    public ResponseEntity<Subject> updateSubject(
            @PathVariable Long id,
            @Valid @RequestBody Subject subject) {
        Subject updated = subjectService.updateSubject(id, subject);
        return ResponseEntity.ok(updated);  // 200 OK
    }

    // ================================================================
    // DELETE /api/subjects/{id}
    // Removes a subject by ID.
    //
    // HTTP 204 No Content on success (no body — nothing to return).
    // HTTP 404 if the subject doesn't exist.
    // ================================================================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubject(@PathVariable Long id) {
        subjectService.deleteSubject(id);
        return ResponseEntity.noContent().build();  // 204 No Content
    }
}
