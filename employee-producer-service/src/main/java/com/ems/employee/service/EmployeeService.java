package com.ems.employee.service;

import com.ems.employee.dto.EmployeeDTO;

import java.util.List;

public interface EmployeeService {

    EmployeeDTO createEmployee(EmployeeDTO employeeDTO);

    List<EmployeeDTO> getAllEmployees();

    EmployeeDTO getEmployeeById(String id);

    EmployeeDTO updateEmployee(String id, EmployeeDTO employeeDTO);

    void deleteEmployee(String id);
}
