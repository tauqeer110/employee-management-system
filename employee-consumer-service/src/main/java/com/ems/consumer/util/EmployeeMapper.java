package com.ems.consumer.util;

import com.ems.consumer.dto.EmployeeDTO;
import com.ems.consumer.entity.Employee;

public class EmployeeMapper {

    //  Converts EmployeeDTO → Employee entity.

    public static Employee mapToEntity(EmployeeDTO dto) {
        Employee employee = new Employee();
        employee.setId(dto.getId());
        employee.setName(dto.getName());
        employee.setAge(dto.getAge());
        employee.setAddress(dto.getAddress());
        return employee;
    }
}
