package com.sms.dao;

import com.sms.model.Student;
import java.sql.SQLException;
import java.util.List;

public interface StudentDAO {
    void add(Student student) throws SQLException;
    void update(Student student) throws SQLException;
    void delete(int id) throws SQLException;
    Student getById(int id) throws SQLException;
    List<Student> getAll() throws SQLException;
    boolean exists(int id) throws SQLException;
} 