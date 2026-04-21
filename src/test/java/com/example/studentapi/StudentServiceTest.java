package com.example.studentapi;

import com.example.studentapi.exception.StudentNotFoundException;
import com.example.studentapi.model.Student;
import com.example.studentapi.repository.StudentRepository;
import com.example.studentapi.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * UNIT TEST — Testing the Service Layer in isolation
 *
 * Key testing concepts shown here:
 *
 * @ExtendWith(MockitoExtension.class)
 *   Enables Mockito — a library for creating "mock" objects.
 *   A mock pretends to be a real object but does nothing by default.
 *   This means the test runs WITHOUT a real database.
 *
 * @Mock
 *   Creates a fake StudentRepository. We control what it returns.
 *
 * @InjectMocks
 *   Creates a real StudentService and injects the mock repository into it.
 *   This tests the service logic WITHOUT any Spring context loading.
 *   (Much faster than @SpringBootTest)
 */
@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    private Student sampleStudent;

    @BeforeEach
    void setUp() {
        sampleStudent = new Student("Alice", "alice@example.com", "CS", 20);
        sampleStudent.setId(1L);
    }

    @Test
    void getStudentById_WhenExists_ReturnsStudent() {
        // ARRANGE: tell the mock what to return
        when(studentRepository.findById(1L)).thenReturn(Optional.of(sampleStudent));

        // ACT
        Student result = studentService.getStudentById(1L);

        // ASSERT
        assertEquals("Alice", result.getName());
        assertEquals("alice@example.com", result.getEmail());
        verify(studentRepository, times(1)).findById(1L);
    }

    @Test
    void getStudentById_WhenNotExists_ThrowsNotFoundException() {
        // ARRANGE: simulate no student found
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        // ACT & ASSERT: expect the exception to be thrown
        assertThrows(StudentNotFoundException.class,
            () -> studentService.getStudentById(99L));
    }

    @Test
    void createStudent_WithDuplicateEmail_ThrowsIllegalArgumentException() {
        // ARRANGE: simulate email already exists
        when(studentRepository.existsByEmail("alice@example.com")).thenReturn(true);

        // ACT & ASSERT
        assertThrows(IllegalArgumentException.class,
            () -> studentService.createStudent(sampleStudent));

        // Verify save was never called (we short-circuited due to duplicate email)
        verify(studentRepository, never()).save(any());
    }

    @Test
    void createStudent_WithUniqueEmail_SavesAndReturnsStudent() {
        when(studentRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(studentRepository.save(sampleStudent)).thenReturn(sampleStudent);

        Student result = studentService.createStudent(sampleStudent);

        assertEquals("Alice", result.getName());
        verify(studentRepository, times(1)).save(sampleStudent);
    }
}
