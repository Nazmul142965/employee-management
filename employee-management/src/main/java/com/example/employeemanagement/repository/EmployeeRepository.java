package com.example.employeemanagement.repository;

import com.example.employeemanagement.model.Employee;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends MongoRepository<Employee, String> {

    List<Employee> findByDepartmentIgnoreCase(String department);

    List<Employee> findByNameContainingIgnoreCase(String name);

    boolean existsByEmail(String email);

    List<Employee> findByActiveTrue();
}
