package com.example.studentapi.service;

import com.example.studentapi.exception.StudentNotFoundException;
import com.example.studentapi.kafka.StudentEvent;
import com.example.studentapi.kafka.StudentEventProducer;
import com.example.studentapi.model.Student;
import com.example.studentapi.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * SERVICE LAYER — Business Logic Layer
 *
 * @Service marks this as a Spring-managed bean (component).
 * Spring creates ONE instance of this class (singleton) and manages it.
 * The controller doesn't create this with "new StudentService()" —
 * Spring INJECTS it via Dependency Injection (DI).
 *
 * Why a separate service layer?
 *   - Controllers handle HTTP (request/response)
 *   - Services handle BUSINESS RULES (validation, logic, orchestration)
 *   - Repositories handle DATA ACCESS (SQL)
 *   - Separation of concerns — each layer has one job
 *
 * @Transactional ensures that if multiple DB operations happen in one
 * method, they either ALL succeed or ALL roll back (atomicity).
 */
@Service
@Transactional   // All public methods are transactional by default
public class StudentService {

    /**
     * DEPENDENCY INJECTION
     *
     * Spring sees that StudentService needs a StudentRepository.
     * It creates (or finds) the repository bean and passes it in.
     * You never write: StudentRepository repo = new StudentRepository();
     *
     * Constructor injection is the recommended style:
     *   - Makes dependencies explicit
     *   - Easy to test (just pass a mock in the constructor)
     *   - Works without @Autowired annotation (Spring auto-detects it)
     */
    private final StudentRepository studentRepository;
    private final StudentEventProducer studentEventProducer;

    public StudentService(StudentRepository studentRepository,
                          StudentEventProducer studentEventProducer) {
        this.studentRepository = studentRepository;
        this.studentEventProducer = studentEventProducer;
    }

    // ---- READ operations (no DB changes, so readOnly=true for performance) ----

    @Transactional(readOnly = true)
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Student getStudentById(Long id) {
        // findById returns Optional<Student>
        // orElseThrow triggers our GlobalExceptionHandler → HTTP 404
        return studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<Student> getStudentsByCourse(String course) {
        return studentRepository.findByCourse(course);
    }

    @Transactional(readOnly = true)
    public List<Student> searchStudentsByName(String name) {
        return studentRepository.searchByName(name);
    }

    // ---- WRITE operations ----

    public Student createStudent(Student student) {
        // Business rule: no duplicate emails
        if (studentRepository.existsByEmail(student.getEmail())) {
            throw new IllegalArgumentException(
                "A student with email '" + student.getEmail() + "' already exists"
            );
        }

        // Save to DB first — get the generated ID back
        Student saved = studentRepository.save(student);  // INSERT

        // Publish event to Kafka AFTER successful DB save.
        // If Kafka is down this is logged but does NOT roll back the DB save.
        StudentEvent event = new StudentEvent(
                StudentEvent.EventType.STUDENT_CREATED,
                saved.getId(),
                saved.getName(),
                saved.getEmail(),
                saved.getCourse()
        );
        studentEventProducer.publishStudentEvent(event);

        return saved;
    }

    public Student updateStudent(Long id, Student updatedData) {
        // First verify the student exists (throws 404 if not)
        Student existing = getStudentById(id);

        // Business rule: if email is changing, ensure the new one isn't taken
        if (!existing.getEmail().equals(updatedData.getEmail())
                && studentRepository.existsByEmail(updatedData.getEmail())) {
            throw new IllegalArgumentException(
                "Email '" + updatedData.getEmail() + "' is already in use"
            );
        }

        // Update fields
        existing.setName(updatedData.getName());
        existing.setEmail(updatedData.getEmail());
        existing.setCourse(updatedData.getCourse());
        existing.setAge(updatedData.getAge());

        return studentRepository.save(existing);  // UPDATE
    }

    public void deleteStudent(Long id) {
        // Verify student exists before deleting
        if (!studentRepository.existsById(id)) {
            throw new StudentNotFoundException(id);
        }
        studentRepository.deleteById(id);  // DELETE
    }
}
