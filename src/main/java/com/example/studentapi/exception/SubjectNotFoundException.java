package com.example.studentapi.exception;

/**
 * CUSTOM EXCEPTION — SubjectNotFoundException
 *
 * A domain-specific exception thrown whenever a Subject cannot be found
 * by its ID in the database.
 *
 * WHY extend RuntimeException (unchecked)?
 *   Checked exceptions (extending Exception) must be declared in every
 *   method signature that can throw them, polluting the code.
 *   RuntimeException (unchecked) propagates up the call stack automatically
 *   until it hits our GlobalExceptionHandler, which maps it to HTTP 404.
 *
 * The GlobalExceptionHandler has a dedicated @ExceptionHandler for this
 * class, so it never reaches the default HTTP 500 handler.
 *
 * Pattern mirrors StudentNotFoundException for consistency across the API.
 */
public class SubjectNotFoundException extends RuntimeException {

    /**
     * Primary constructor — used when a subject is not found by numeric ID.
     * Produces a message like: "Subject not found with id: 42"
     *
     * @param id the subject ID that was searched for
     */
    public SubjectNotFoundException(Long id) {
        super("Subject not found with id: " + id);
    }

    /**
     * Secondary constructor — used when a custom message is more appropriate,
     * e.g. "Subject not found with code: CS999".
     *
     * @param message a descriptive error message
     */
    public SubjectNotFoundException(String message) {
        super(message);
    }
}
