package com.ems.consumer.listener;

import com.ems.consumer.event.EmployeeEvent;
import com.ems.consumer.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class
EmployeeKafkaConsumer {

    private final EmployeeService employeeService;

    @KafkaListener(
            topics = "${spring.kafka.topic.employee}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(EmployeeEvent event) {

        log.info("Received {} event for employee {}",
                event.getOperation(),
                event.getEmployeeDTO().getId());

        employeeService.processEmployeeEvent(event);
    }
}