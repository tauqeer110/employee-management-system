package com.ems.consumer.service;

import com.ems.consumer.event.EmployeeEvent;

public interface EmployeeService {

    void processEmployeeEvent(EmployeeEvent event);
}
