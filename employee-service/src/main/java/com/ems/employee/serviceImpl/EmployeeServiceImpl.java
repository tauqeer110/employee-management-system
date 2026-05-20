package com.ems.employee.serviceImpl;

import com.ems.employee.dto.EmployeeDTO;
import com.ems.employee.entity.Employee;
import com.ems.employee.exception.EmployeeNotFoundException;
import com.ems.employee.repository.EmployeeRepository;
import com.ems.employee.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/*
 * Spring Cache annotations:
 *  - @Cacheable    : returns cached value if present (used on getById and getAll)
 *  - @CachePut     : always runs the method and updates the cache (used on update)
 *  - @CacheEvict   : removes the entry from cache (used on delete)
 */
@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);
    private static final String CACHE_NAME = "employees";

    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    // ------------------------------------------------------------------ CREATE
    @Override
    public EmployeeDTO createEmployee(EmployeeDTO employeeDTO) {
        logger.info("Creating new employee with name: {}", employeeDTO.getName());

        Employee employee = mapToEntity(employeeDTO);
        Employee savedEmployee = employeeRepository.save(employee);

        logger.debug("Employee saved with id: {}", savedEmployee.getId());
        return mapToDTO(savedEmployee);
    }

    // ------------------------------------------------------------------ GET ALL
    @Override
    @Cacheable(value = CACHE_NAME, key = "'all'")
    public List<EmployeeDTO> getAllEmployees() {
        logger.info("Fetching all employees from database");

        return employeeRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // ------------------------------------------------------------------ GET BY ID
    @Override
    @Cacheable(value = CACHE_NAME, key = "#id")
    public EmployeeDTO getEmployeeById(String id) {
        logger.info("Fetching employee with id: {}", id);

        // Java 8: Optional — avoids NullPointerException on missing records
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + id));

        logger.debug("Employee found: {}", employee.getName());
        return mapToDTO(employee);
    }

    // ------------------------------------------------------------------ UPDATE
    @Override
    @CachePut(value = CACHE_NAME, key = "#id")
    public EmployeeDTO updateEmployee(String id, EmployeeDTO employeeDTO) {
        logger.info("Updating employee with id: {}", id);

        Employee existing = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + id));

        // Update fields
        existing.setName(employeeDTO.getName());
        existing.setAge(employeeDTO.getAge());
        existing.setAddress(employeeDTO.getAddress());

        Employee updated = employeeRepository.save(existing);
        logger.debug("Employee updated: {}", updated.getId());

        return mapToDTO(updated);
    }

    // ------------------------------------------------------------------ DELETE
    @Override
    @CacheEvict(value = CACHE_NAME, allEntries = true)
    public void deleteEmployee(String id) {
        logger.info("Deleting employee with id: {}", id);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + id));

        employeeRepository.delete(employee);
        logger.debug("Employee deleted with id: {}", id);
    }

    // ------------------------------------------------------------------ HELPERS

    /*
      Converts EmployeeDTO → Employee entity.
      Reusable mapping method — keeps the code DRY.
     */
    private Employee mapToEntity(EmployeeDTO dto) {
        Employee employee = new Employee();
        employee.setName(dto.getName());
        employee.setAge(dto.getAge());
        employee.setAddress(dto.getAddress());
        return employee;
    }


    // Converts Employee entity → EmployeeDTO.

    private EmployeeDTO mapToDTO(Employee employee) {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(employee.getId());
        dto.setName(employee.getName());
        dto.setAge(employee.getAge());
        dto.setAddress(employee.getAddress());
        return dto;
    }
}
