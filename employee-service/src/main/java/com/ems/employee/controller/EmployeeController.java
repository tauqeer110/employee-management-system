package com.ems.employee.controller;

import com.ems.employee.dto.EmployeeDTO;
import com.ems.employee.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/*
  REST Controller for Employee CRUD operations.
  @Valid triggers DTO validation before the request reaches the service layer.
  ResponseEntity gives us full control over HTTP status codes.
 */
@RestController
@RequestMapping("/employees")
@Slf4j
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /*
      POST / employees
      Creates a new employee.
      Returns HTTP 201 Created on success.
     */
    @PostMapping
    public ResponseEntity<EmployeeDTO> createEmployee(@Valid @RequestBody EmployeeDTO employeeDTO) {
        log.info("POST /employees - Creating employee: {}", employeeDTO.getName());
        EmployeeDTO created = employeeService.createEmployee(employeeDTO);
        log.info("Employee created with id: {}", created.getId());
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /*
      GET /employees
      Returns all employees.
      Returns HTTP 200 OK.
    */
    @GetMapping
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees() {
        log.info("GET /employees - Fetching all employees");
        List<EmployeeDTO> employees = employeeService.getAllEmployees();
        log.info("Returning {} employees", employees.size());
        return ResponseEntity.ok(employees);
    }

    /**
     * GET /employees/{id}
     * Returns a single employee by ID.
     * Returns HTTP 200 OK, or 404 if not found (handled by GlobalExceptionHandler).
     */
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDTO> getEmployeeById(@PathVariable String id) {
        log.info("GET /employees/{} - Fetching employee", id);
        EmployeeDTO employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(employee);
    }

    /**
     * PUT /employees/{id}
     * Updates an existing employee.
     * Returns HTTP 200 OK on success.
     */
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDTO> updateEmployee(
            @PathVariable String id,
            @Valid @RequestBody EmployeeDTO employeeDTO) {
        log.info("PUT /employees/{} - Updating employee", id);
        EmployeeDTO updated = employeeService.updateEmployee(id, employeeDTO);
        log.info("Employee updated: {}", updated.getId());
        return ResponseEntity.ok(updated);
    }

    /**
     * DELETE /employees/{id}
     * Deletes an employee by ID.
     * Returns HTTP 204 No Content on success.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable String id) {
        log.info("DELETE /employees/{} - Deleting employee", id);
        employeeService.deleteEmployee(id);
        log.info("Employee deleted: {}", id);
        return ResponseEntity.noContent().build();
    }
}
