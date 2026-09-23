package com.example.employeemanagement.service;

import com.example.employeemanagement.exception.EmployeeNotFoundException;
import com.example.employeemanagement.exception.DuplicateEmailException;
import com.example.employeemanagement.model.Employee;
import com.example.employeemanagement.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    // Cache the full employee list under key "all"
    @Cacheable(value = "employees", key = "'all'")
    public List<Employee> getAllEmployees() {
        log.info("Fetching all employees from MongoDB (cache miss)");
        return employeeRepository.findAll();
    }

    // Cache individual employee lookups by id
    @Cacheable(value = "employeeById", key = "#id")
    public Employee getEmployeeById(String id) {
        log.info("Fetching employee {} from MongoDB (cache miss)", id);
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + id));
    }

    // Adding a new employee evicts the "all" list cache so it's refreshed next read
    @CacheEvict(value = "employees", key = "'all'")
    public Employee createEmployee(Employee employee) {
        if (employeeRepository.existsByEmail(employee.getEmail())) {
            throw new DuplicateEmailException("An employee with email " + employee.getEmail() + " already exists");
        }
        log.info("Creating new employee: {}", employee.getEmail());
        return employeeRepository.save(employee);
    }

    // Updating: refresh the byId cache entry AND evict the "all" list cache
    @CachePut(value = "employeeById", key = "#id")
    @CacheEvict(value = "employees", key = "'all'")
    public Employee updateEmployee(String id, Employee updated) {
        Employee existing = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + id));

        // if email changed, make sure new email isn't already used by someone else
        if (!existing.getEmail().equalsIgnoreCase(updated.getEmail())
                && employeeRepository.existsByEmail(updated.getEmail())) {
            throw new DuplicateEmailException("An employee with email " + updated.getEmail() + " already exists");
        }

        existing.setName(updated.getName());
        existing.setEmail(updated.getEmail());
        existing.setPhone(updated.getPhone());
        existing.setDepartment(updated.getDepartment());
        existing.setDesignation(updated.getDesignation());
        existing.setSalary(updated.getSalary());
        existing.setJoiningDate(updated.getJoiningDate());
        existing.setActive(updated.isActive());

        log.info("Updating employee {}", id);
        return employeeRepository.save(existing);
    }

    @CacheEvict(value = {"employees", "employeeById"}, allEntries = true)
    public void deleteEmployee(String id) {
        if (!employeeRepository.existsById(id)) {
            throw new EmployeeNotFoundException("Employee not found with id: " + id);
        }
        log.info("Deleting employee {}", id);
        employeeRepository.deleteById(id);
    }

    public List<Employee> searchByName(String name) {
        return employeeRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Employee> getByDepartment(String department) {
        return employeeRepository.findByDepartmentIgnoreCase(department);
    }
}
