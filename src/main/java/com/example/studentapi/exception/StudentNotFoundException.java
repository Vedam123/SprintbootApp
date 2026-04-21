package com.example.studentapi.exception;

/**
 * CUSTOM EXCEPTION
 *
 * A domain-specific exception for when a student isn't found.
 * Extending RuntimeException means you don't need to declare
 * it in method signatures (unchecked exception).
 *
 * The @ResponseStatus annotation on the handler (not here) will
 * map this to HTTP 404 automatically.
 */
public class StudentNotFoundException extends RuntimeException {

    public StudentNotFoundException(Long id) {
        super("Student not found with id: " + id);
    }

    public StudentNotFoundException(String message) {
        super(message);
    }
}
