package com.example.studentapi.service;

import com.example.studentapi.exception.SubjectNotFoundException;
import com.example.studentapi.model.Subject;
import com.example.studentapi.repository.SubjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * SERVICE LAYER — SubjectService
 *
 * WHY a separate service layer?
 *   The controller's job is to translate HTTP → Java and Java → HTTP.
 *   All actual BUSINESS RULES belong here so they can be tested,
 *   reused, and reasoned about independently of HTTP concepts.
 *
 * @Service
 *   Marks this class as a Spring-managed bean (component).
 *   Spring creates exactly ONE instance (singleton scope by default)
 *   and stores it in the IoC (Inversion of Control) container.
 *   Other beans (controllers, other services) receive this singleton
 *   via constructor injection — they never call "new SubjectService()".
 *
 * @Transactional  (class-level)
 *   Every public method in this class runs inside a database transaction
 *   by default.  If the method throws any RuntimeException, Spring
 *   automatically rolls back all DB changes made in that method call.
 *   This gives you atomicity — either everything succeeds or nothing does.
 *
 *   Individual methods can override this with their own @Transactional
 *   (e.g. readOnly = true for SELECT-only methods, which is a performance
 *   hint that disables the "dirty-checking" overhead Hibernate applies on
 *   write transactions).
 */
@Service
@Transactional
public class SubjectService {

    /**
     * DEPENDENCY INJECTION — Constructor Injection
     *
     * Spring sees that SubjectService needs a SubjectRepository.
     * It finds the repository bean in the IoC container and passes it here.
     * We store it as final — it can never be accidentally reassigned.
     *
     * Constructor injection is the recommended style over field injection
     * (@Autowired on a field) because:
     *   1. Dependencies are explicit (not hidden inside the class).
     *   2. The class is easy to unit-test — just pass a mock in tests.
     *   3. Works without @Autowired annotation (Spring auto-detects single
     *      constructor injection since Spring 4.3+).
     */
    private final SubjectRepository subjectRepository;

    public SubjectService(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    // ================================================================
    // READ operations — readOnly=true for performance (no dirty-check)
    // ================================================================

    /**
     * Returns every subject in the database.
     * SELECT * FROM subjects — no filtering.
     *
     * @Transactional(readOnly = true) tells Hibernate: "no writes expected".
     * Hibernate skips the "dirty-checking" pass at the end of the transaction,
     * which improves performance for large result sets.
     */
    @Transactional(readOnly = true)
    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    /**
     * Returns a single subject by its numeric ID, or throws an exception.
     *
     * findById() returns Optional<Subject>.
     * orElseThrow() unwraps it — if empty, it throws SubjectNotFoundException,
     * which the GlobalExceptionHandler maps to HTTP 404.
     *
     * @param id the subject's primary key
     * @return the found Subject entity
     * @throws SubjectNotFoundException if no subject has that ID
     */
    @Transactional(readOnly = true)
    public Subject getSubjectById(Long id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new SubjectNotFoundException(id));
    }

    // ================================================================
    // WRITE operations — full @Transactional (default, read-write)
    // ================================================================

    /**
     * Persists a new Subject to the database.
     *
     * BUSINESS RULE: subject codes are unique across the system.
     * We check upfront (existsByCode) to return a readable 409 Conflict
     * instead of letting the DB unique-constraint violation surface as a
     * raw 500 Internal Server Error.
     *
     * save() issues an INSERT because the entity has no ID yet.
     * Hibernate fills the generated ID back into the returned object.
     *
     * @param subject the Subject to persist (ID should be null/absent)
     * @return the saved Subject with its generated ID populated
     * @throws IllegalArgumentException if the subject code already exists
     */
    public Subject createSubject(Subject subject) {
        if (subjectRepository.existsByCode(subject.getCode())) {
            throw new IllegalArgumentException(
                "A subject with code '" + subject.getCode() + "' already exists"
            );
        }
        return subjectRepository.save(subject);  // INSERT
    }

    /**
     * Fully replaces a subject's data (HTTP PUT semantics).
     *
     * 1. Verify the subject exists (throws 404 if not).
     * 2. If the code is CHANGING, verify the new code is not taken by another subject.
     * 3. Apply all field updates.
     * 4. save() issues an UPDATE because the entity already has an ID.
     *
     * @param id   the ID of the subject to update
     * @param data a Subject object carrying the new field values
     * @return the updated Subject
     * @throws SubjectNotFoundException  if no subject has the given ID
     * @throws IllegalArgumentException  if the new code conflicts with another subject
     */
    public Subject updateSubject(Long id, Subject data) {
        Subject existing = getSubjectById(id);  // throws 404 if missing

        // Only block if the code actually CHANGED to a value owned by a different subject
        if (!existing.getCode().equals(data.getCode())
                && subjectRepository.existsByCode(data.getCode())) {
            throw new IllegalArgumentException(
                "Subject code '" + data.getCode() + "' is already in use by another subject"
            );
        }

        existing.setName(data.getName());
        existing.setCode(data.getCode());
        existing.setCredits(data.getCredits());

        return subjectRepository.save(existing);  // UPDATE
    }

    /**
     * Removes a subject from the database by ID.
     *
     * We verify existence first so we can throw a meaningful 404 instead of
     * silently doing nothing (deleteById is a no-op if the ID doesn't exist).
     *
     * NOTE: if any enrollments reference this subject, the DB will reject the
     * delete with a foreign-key constraint violation unless cascades are configured.
     * In production you would either cascade deletes or unenroll students first.
     *
     * @param id the subject's primary key
     * @throws SubjectNotFoundException if no subject has the given ID
     */
    public void deleteSubject(Long id) {
        if (!subjectRepository.existsById(id)) {
            throw new SubjectNotFoundException(id);
        }
        subjectRepository.deleteById(id);  // DELETE
    }
}
