package com.ems.employee.service;

import com.ems.employee.event.EmployeeEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmployeeKafkaProducerService {

    private final KafkaTemplate<String, EmployeeEvent> kafkaTemplate;

    @Value("${spring.kafka.topic.employee}")
    private String topic;

    public EmployeeKafkaProducerService(KafkaTemplate<String, EmployeeEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishEmployee(EmployeeEvent employeeEvent) {
        log.info("Publishing {} event for employee: {}", employeeEvent.getOperation(), employeeEvent.getEmployeeDTO().getName());

        kafkaTemplate.send(topic, employeeEvent.getEmployeeDTO().getId(), employeeEvent);
    }
}