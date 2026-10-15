package com.ems.employee.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ems.employee.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, String> {
    
}
