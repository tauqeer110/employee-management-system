package com.ems.employee.serviceImpl;

import com.ems.employee.dto.EmployeeDTO;
import com.ems.employee.entity.Employee;
import com.ems.employee.exception.EmployeeNotFoundException;
import com.ems.employee.repository.EmployeeRepository;
import com.ems.employee.service.EmployeeService;
import com.ems.employee.util.EmployeeMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.stream.Collectors;

import static com.ems.employee.util.EmployeeMapper.mapToDTO;
import static com.ems.employee.util.EmployeeMapper.mapToEntity;

@Slf4j
@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }


    // ------------------------------------------------------------------ CREATE
    @Override
    @CacheEvict(value = "employees", key = "'all'")
    public EmployeeDTO createEmployee(EmployeeDTO employeeDTO) {
        log.info("Creating new employee with name: {}", employeeDTO.getName());

        Employee employee = mapToEntity(employeeDTO);
        Employee savedEmployee = employeeRepository.save(employee);

        log.debug("Employee saved with id: {}", savedEmployee.getId());
        return mapToDTO(savedEmployee);
    }

    // ------------------------------------------------------------------ GET ALL
    @Override
    @Cacheable(value = "employees", key = "'all'")
    public List<EmployeeDTO> getAllEmployees() {
        log.info("Fetching all employees from database");

        return employeeRepository.findAll()
                .stream()
                .map(EmployeeMapper::mapToDTO)
                .collect(Collectors.toList());
    }

    // ------------------------------------------------------------------ GET BY ID
    @Override
    @Cacheable(value = "employees", key = "#id")
    public EmployeeDTO getEmployeeById(String id) {
        log.info("Fetching employee with id: {}", id);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + id));

        log.debug("Employee found: {}", employee.getName());
        return mapToDTO(employee);
    }

    // ------------------------------------------------------------------ UPDATE
    @Override
    @CachePut(value = "employees", key = "#id")
    public EmployeeDTO updateEmployee(String id, EmployeeDTO employeeDTO) {
        log.info("Updating employee with id: {}", id);

        Employee existing = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + id));

        // Update fields
        existing.setName(employeeDTO.getName());
        existing.setAge(employeeDTO.getAge());
        existing.setAddress(employeeDTO.getAddress());

        Employee updated = employeeRepository.save(existing);
        log.debug("Employee updated: {}", updated.getId());

        return mapToDTO(updated);
    }

    // ------------------------------------------------------------------ DELETE
    @Override
    @CacheEvict(value = "employees", allEntries = true)
    public void deleteEmployee(String id) {
        log.info("Deleting employee with id: {}", id);

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + id));

        employeeRepository.delete(employee);
        log.debug("Employee deleted with id: {}", id);
    }

}
