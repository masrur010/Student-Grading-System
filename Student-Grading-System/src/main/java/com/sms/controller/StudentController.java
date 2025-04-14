package com.sms.controller;

import com.sms.dao.StudentDAO;
import com.sms.model.Student;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.SQLException;
import java.util.List;

public class StudentController {
    private final StudentDAO studentDAO;
    private final ObservableList<Student> studentList;

    public StudentController(StudentDAO studentDAO) {
        this.studentDAO = studentDAO;
        this.studentList = FXCollections.observableArrayList();
        refreshStudentList();
    }

    public ObservableList<Student> getStudentList() {
        return studentList;
    }

    public void addStudent(Student student) throws SQLException {
        // Validate student data
        validateStudent(student);
        
        // Check if ID already exists
        if (studentDAO.exists(student.getId())) {
            throw new IllegalArgumentException("Student ID already exists");
        }

        studentDAO.add(student);
        refreshStudentList();
    }

    public void updateStudent(Student student) throws SQLException {
        validateStudent(student);
        studentDAO.update(student);
        refreshStudentList();
    }

    public void deleteStudent(int id) throws SQLException {
        studentDAO.delete(id);
        refreshStudentList();
    }

    public Student getStudent(int id) throws SQLException {
        return studentDAO.getById(id);
    }

    private void validateStudent(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("Student cannot be null");
        }
        
        // Name validation
        if (student.getName() == null || student.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Student name cannot be empty");
        }

        // Age validation
        if (student.getAge() < 5 || student.getAge() > 100) {
            throw new IllegalArgumentException("Student age must be between 5 and 100");
        }

        // Grade validation
        if (student.getGrade() < 0 || student.getGrade() > 100) {
            throw new IllegalArgumentException("Student grade must be between 0 and 100");
        }
    }

    private void refreshStudentList() {
        try {
            List<Student> students = studentDAO.getAll();
            studentList.setAll(students);
        } catch (SQLException e) {
            throw new RuntimeException("Error refreshing student list", e);
        }
    }
} 