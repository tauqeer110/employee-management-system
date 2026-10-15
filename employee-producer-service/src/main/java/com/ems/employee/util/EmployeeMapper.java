package com.ems.employee.util;

import com.ems.employee.dto.EmployeeDTO;
import com.ems.employee.entity.Employee;

public class EmployeeMapper {

    //  Converts EmployeeDTO → Employee entity.

    public static Employee mapToEntity(EmployeeDTO dto) {
        Employee employee = new Employee();
        employee.setName(dto.getName());
        employee.setAge(dto.getAge());
        employee.setAddress(dto.getAddress());
        return employee;
    }

    // Converts Employee entity → EmployeeDTO.

    public static EmployeeDTO mapToDTO(Employee employee) {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(employee.getId());
        dto.setName(employee.getName());
        dto.setAge(employee.getAge());
        dto.setAddress(employee.getAddress());
        return dto;
    }
}
