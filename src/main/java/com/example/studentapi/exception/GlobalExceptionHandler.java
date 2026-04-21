package com.example.studentapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

// StudentNotFoundException and SubjectNotFoundException are in the same package
// (com.example.studentapi.exception) — no import statements needed for them.

/**
 * GLOBAL EXCEPTION HANDLER
 *
 * @RestControllerAdvice is an AOP (Aspect-Oriented Programming) concept.
 * Instead of putting try-catch in every controller, you define error
 * handling ONCE here and Spring routes all exceptions through it.
 *
 * This is the "cross-cutting concern" idea from AOP:
 *   - Error handling applies across ALL controllers
 *   - You write it in one place
 *   - Controllers stay clean and focused on business logic
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles StudentNotFoundException → HTTP 404
     *
     * Whenever any controller throws StudentNotFoundException,
     * Spring intercepts it and calls this method instead of
     * returning a raw 500 error.
     */
    @ExceptionHandler(StudentNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleStudentNotFound(StudentNotFoundException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now().toString());
        error.put("status", 404);
        error.put("error", "Not Found");
        error.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Handles SubjectNotFoundException → HTTP 404
     *
     * Mirrors handleStudentNotFound — whenever a controller or service throws
     * SubjectNotFoundException (e.g. GET /api/subjects/999 for a missing ID),
     * Spring routes the exception here via AOP and we return a structured 404.
     *
     * The @ExceptionHandler annotation on each method tells Spring exactly
     * which exception class this method handles.  Having separate handlers for
     * Student vs Subject lets us add class-specific fields to the response body
     * in the future without changing the other handler.
     */
    @ExceptionHandler(SubjectNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleSubjectNotFound(SubjectNotFoundException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now().toString());
        error.put("status", 404);
        error.put("error", "Not Found");
        error.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Handles validation errors → HTTP 400
     *
     * When @Valid fails on a @RequestBody (e.g. missing name, invalid email),
     * Spring throws MethodArgumentNotValidException.
     * We catch it here and return a friendly list of field errors.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now().toString());
        error.put("status", 400);
        error.put("error", "Validation Failed");
        error.put("fields", fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handles duplicate email → HTTP 409
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now().toString());
        error.put("status", 409);
        error.put("error", "Conflict");
        error.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /**
     * Catch-all for any unexpected exception → HTTP 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now().toString());
        error.put("status", 500);
        error.put("error", "Internal Server Error");
        error.put("message", "Something went wrong");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
