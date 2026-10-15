package com.ems.consumer.serviceImpl;

import com.ems.consumer.dto.EmployeeDTO;
import com.ems.consumer.entity.Employee;
import com.ems.consumer.event.EmployeeEvent;
import com.ems.consumer.repository.EmployeeRepository;
import com.ems.consumer.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.ems.consumer.util.EmployeeMapper.mapToEntity;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository repository;

    @Override
    public void processEmployeeEvent(EmployeeEvent event) {

        EmployeeDTO dto = event.getEmployeeDTO();

        switch (event.getOperation()) {

            case CREATE -> {
                Employee employee = mapToEntity(dto);
                repository.save(employee);
            }

            case UPDATE -> {
                Employee employee = repository.findById(dto.getId())
                        .orElseThrow(() ->
                                new RuntimeException("Employee not found"));

                employee.setId(dto.getId());
                employee.setName(dto.getName());
                employee.setAge(dto.getAge());
                employee.setAddress(dto.getAddress());

                repository.save(employee);
            }

            case DELETE -> {
                Employee employee = repository.findById(dto.getId())
                        .orElseThrow(() ->
                                new RuntimeException("Employee not found"));

                repository.deleteById(employee.getId());
            }
        }

    }
}