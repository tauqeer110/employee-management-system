package com.ems.employee.serviceImpl;

import com.ems.employee.dto.EmployeeDTO;
import com.ems.employee.entity.Employee;
import com.ems.employee.enums.OperationsType;
import com.ems.employee.event.EmployeeEvent;
import com.ems.employee.exception.EmployeeNotFoundException;
import com.ems.employee.repository.EmployeeRepository;
import com.ems.employee.service.EmployeeKafkaProducerService;
import com.ems.employee.service.EmployeeService;
import com.ems.employee.util.EmployeeMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import static com.ems.employee.util.EmployeeMapper.mapToDTO;

@Slf4j
@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    private final EmployeeKafkaProducerService kafkaProducerService;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, EmployeeKafkaProducerService kafkaProducerService) {
        this.employeeRepository = employeeRepository;
        this.kafkaProducerService = kafkaProducerService;
    }


    // ------------------------------------------------------------------ CREATE
    @Override
    @CacheEvict(value = "employees", key = "'allEmployees'")
    public EmployeeDTO createEmployee(EmployeeDTO employeeDTO) {
        log.info("Creating new employee with name: {}", employeeDTO.getName());

        employeeDTO.setId(UUID.randomUUID().toString());

        EmployeeEvent event = new EmployeeEvent();
        event.setOperation(OperationsType.CREATE);
        event.setEmployeeDTO(employeeDTO);

        kafkaProducerService.publishEmployee(event);

        return employeeDTO;
    }

    // ------------------------------------------------------------------ GET ALL
    @Override
    @Cacheable(value = "employees", key = "'allEmployees'")
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
    @CacheEvict(value = "employees", key = "'allEmployees'")
    public EmployeeDTO updateEmployee(String id, EmployeeDTO employeeDTO) {
        log.info("Updating employee with id: {}", id);

        employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee Does Not Exists"));
        
        employeeDTO.setId(id);

        EmployeeEvent event = new EmployeeEvent();
        event.setOperation(OperationsType.UPDATE);
        event.setEmployeeDTO(employeeDTO);

        kafkaProducerService.publishEmployee(event);

        return employeeDTO;
    }

    // ------------------------------------------------------------------ DELETE
    @Override
    @CacheEvict(value = "employees", allEntries = true)
    public void deleteEmployee(String id) {
        log.info("Deleting employee with id: {}", id);

        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setId(id);

        EmployeeEvent event = new EmployeeEvent();
        event.setOperation(OperationsType.DELETE);
        event.setEmployeeDTO(employeeDTO);

        kafkaProducerService.publishEmployee(event);

        log.debug("Delete event published for employee id: {}", id);
    }

}
