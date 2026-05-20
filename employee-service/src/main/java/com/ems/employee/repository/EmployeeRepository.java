package com.ems.employee.repository;

import com.ems.employee.entity.Employee;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/*
  Repository interface for Employee.
  Extends MongoRepository to get built-in CRUD operations
  save(), findById(), findAll(), deleteById() etc.
  No implementation needed — Spring Data MongoDB generates it at runtime.
 */
@Repository
public interface EmployeeRepository extends MongoRepository<Employee, String> {
    // All basic CRUD operations are inherited from MongoRepository.
    // We can add custom query method if needed
}
