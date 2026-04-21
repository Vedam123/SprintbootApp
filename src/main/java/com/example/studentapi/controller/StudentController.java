package com.example.studentapi.controller;

import com.example.studentapi.model.Student;
import com.example.studentapi.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CONTROLLER LAYER — HTTP / REST Layer
 *
 * @RestController = @Controller + @ResponseBody
 *   - @Controller    → Spring MVC: this class handles HTTP requests
 *   - @ResponseBody  → return values are serialized to JSON automatically
 *                      (Jackson library, included with spring-boot-starter-web)
 *
 * @RequestMapping("/api/students") sets the base URL for all endpoints here.
 *
 * The controller's ONLY job:
 *   1. Receive HTTP request
 *   2. Validate input (@Valid)
 *   3. Call the service
 *   4. Return HTTP response
 *
 * No business logic here. No SQL here. Just HTTP in → HTTP out.
 */
@RestController
@RequestMapping("/api/students")
public class StudentController {

    /**
     * DEPENDENCY INJECTION — same pattern as the service.
     * Spring injects StudentService here automatically.
     */
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // ================================================================
    // GET /api/students
    // Returns all students
    // ================================================================
    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {
        List<Student> students = studentService.getAllStudents();
        return ResponseEntity.ok(students);  // HTTP 200 + JSON body
    }

    // ================================================================
    // GET /api/students/{id}
    // Returns one student by ID, or 404 if not found
    // ================================================================
    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
        Student student = studentService.getStudentById(id);
        return ResponseEntity.ok(student);
    }

    // ================================================================
    // GET /api/students/course/{course}
    // Returns students enrolled in a specific course
    // ================================================================
    @GetMapping("/course/{course}")
    public ResponseEntity<List<Student>> getStudentsByCourse(@PathVariable String course) {
        return ResponseEntity.ok(studentService.getStudentsByCourse(course));
    }

    // ================================================================
    // GET /api/students/search?name=john
    // Search students by name (partial, case-insensitive)
    // ================================================================
    @GetMapping("/search")
    public ResponseEntity<List<Student>> searchByName(@RequestParam String name) {
        return ResponseEntity.ok(studentService.searchStudentsByName(name));
    }

    // ================================================================
    // POST /api/students
    // Create a new student
    //
    // @RequestBody    → deserialize JSON request body into Student object
    // @Valid          → run the validation annotations on Student fields
    //                   (triggers GlobalExceptionHandler on failure)
    // ================================================================
    @PostMapping
    public ResponseEntity<Student> createStudent(@Valid @RequestBody Student student) {
        Student created = studentService.createStudent(student);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);  // HTTP 201
    }

    // ================================================================
    // PUT /api/students/{id}
    // Update an existing student (full update)
    // ================================================================
    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody Student student) {
        Student updated = studentService.updateStudent(id, student);
        return ResponseEntity.ok(updated);
    }

    // ================================================================
    // DELETE /api/students/{id}
    // Delete a student by ID
    // ================================================================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();  // HTTP 204 No Content
    }
}
