-- This file is automatically executed by Spring Boot on startup
-- (when spring.jpa.hibernate.ddl-auto=create-drop, Hibernate creates the
-- table first, then Spring runs this SQL to seed initial data)

-- ── STUDENTS (original 5 rows) ─────────────────────────────────────────────
INSERT INTO students (name, email, course, age) VALUES ('Alice Johnson', 'alice@example.com', 'Computer Science', 20);
INSERT INTO students (name, email, course, age) VALUES ('Bob Smith', 'bob@example.com', 'Mathematics', 22);
INSERT INTO students (name, email, course, age) VALUES ('Carol White', 'carol@example.com', 'Computer Science', 21);
INSERT INTO students (name, email, course, age) VALUES ('David Brown', 'david@example.com', 'Physics', 23);
INSERT INTO students (name, email, course, age) VALUES ('Eve Davis', 'eve@example.com', 'Mathematics', 19);

-- ── SUBJECTS ────────────────────────────────────────────────────────────────
-- Five subjects across different disciplines.
-- Columns: name, code (unique), credits
-- IDs will be auto-generated as 1..5 by H2's identity column.
INSERT INTO subjects (name, code, credits) VALUES ('Mathematics',        'MATH101', 4);
INSERT INTO subjects (name, code, credits) VALUES ('Physics',            'PHYS101', 4);
INSERT INTO subjects (name, code, credits) VALUES ('Computer Science',   'CS101',   3);
INSERT INTO subjects (name, code, credits) VALUES ('Chemistry',          'CHEM101', 3);
INSERT INTO subjects (name, code, credits) VALUES ('English',            'ENG101',  2);

-- ── ENROLLMENTS ─────────────────────────────────────────────────────────────
-- Links students (by their auto-generated IDs 1..5) to subjects (IDs 1..5).
-- enrolled_at is set to today by the Enrollment constructor in Java, but for
-- seed data we supply an explicit date so tests have deterministic values.
-- grade is NULL until the course is assessed (left NULL here intentionally).
--
-- Alice (1) → CS101 (3), MATH101 (1)
INSERT INTO enrollments (student_id, subject_id, enrolled_at, grade) VALUES (1, 3, '2026-01-15', 'A');
INSERT INTO enrollments (student_id, subject_id, enrolled_at, grade) VALUES (1, 1, '2026-01-15', NULL);

-- Bob (2) → MATH101 (1), PHYS101 (2)
INSERT INTO enrollments (student_id, subject_id, enrolled_at, grade) VALUES (2, 1, '2026-01-16', 'B+');
INSERT INTO enrollments (student_id, subject_id, enrolled_at, grade) VALUES (2, 2, '2026-01-16', NULL);

-- Carol (3) → CS101 (3), ENG101 (5)
INSERT INTO enrollments (student_id, subject_id, enrolled_at, grade) VALUES (3, 3, '2026-01-17', 'A-');
INSERT INTO enrollments (student_id, subject_id, enrolled_at, grade) VALUES (3, 5, '2026-01-17', NULL);

-- David (4) → PHYS101 (2), CHEM101 (4)
INSERT INTO enrollments (student_id, subject_id, enrolled_at, grade) VALUES (4, 2, '2026-01-18', 'B');
INSERT INTO enrollments (student_id, subject_id, enrolled_at, grade) VALUES (4, 4, '2026-01-18', NULL);

-- Eve (5) → MATH101 (1)
INSERT INTO enrollments (student_id, subject_id, enrolled_at, grade) VALUES (5, 1, '2026-01-19', NULL);
