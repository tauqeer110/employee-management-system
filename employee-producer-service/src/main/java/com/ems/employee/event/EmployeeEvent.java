package com.ems.employee.event;

import com.ems.employee.dto.EmployeeDTO;
import com.ems.employee.enums.OperationsType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeEvent {
    private OperationsType operation;
    private EmployeeDTO employeeDTO;
}
