package com.ems.consumer.event;

import com.ems.consumer.dto.EmployeeDTO;
import com.ems.consumer.enums.OperationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeEvent {

    private OperationType operation;
    private EmployeeDTO employeeDTO;
}
