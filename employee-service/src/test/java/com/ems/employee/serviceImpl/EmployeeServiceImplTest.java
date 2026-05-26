package com.ems.employee.serviceImpl;

import com.ems.employee.dto.EmployeeDTO;
import com.ems.employee.entity.Employee;
import com.ems.employee.exception.EmployeeNotFoundException;
import com.ems.employee.repository.EmployeeRepository;
import com.ems.employee.serviceImpl.EmployeeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee employee;
    private EmployeeDTO employeeDTO;

    @BeforeEach
    void setUp(){
        employee = new Employee();
        employee.setId("1");
        employee.setName("Abbas");
        employee.setAge(40);
        employee.setAddress("Bangalore");

        employeeDTO = new EmployeeDTO();
        employeeDTO.setName("Abbas");
        employeeDTO.setAge(40);
        employeeDTO.setAddress("Bangalore");
    }

    // ------------------------------------------------------------------ CREATE
    @Test
    void createEmployee_ShouldReturnSavedEmployee() {
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        EmployeeDTO result = employeeService.createEmployee(employeeDTO);

        assertEquals(employee.getId(), result.getId());
        assertEquals(employee.getName(), result.getName());
        assertEquals(employee.getAge(), result.getAge());
        assertEquals(employee.getAddress(), result.getAddress());

        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    // ------------------------------------------------------------------ GET BY ID VALID CASE
    @Test
    void getEmployeeById_ShouldReturnEmployee_WhenFound(){
        when(employeeRepository.findById("1")).thenReturn(Optional.of(employee));

        EmployeeDTO result = employeeService.getEmployeeById("1");

        assertEquals("1", result.getId());
        verify(employeeRepository, times(1)).findById("1");
    }

    // ------------------------------------------------------------------ GET BY ID EXCEPTION CASE
    @Test
    void getEmployeeById_ShouldReturnThrowException_WhenNotFound(){
        when(employeeRepository.findById("99")).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class,
                () -> employeeService.getEmployeeById("99"));
    }

    // ------------------------------------------------------------------ GET ALL
    @Test
    void getAllEmployees_ShouldReturnList(){

        when(employeeRepository.findAll()).thenReturn(List.of(employee));

        List<EmployeeDTO> result = employeeService.getAllEmployees();

        assertEquals(1, result.size());
        assertEquals("1", result.getFirst().getId());

        verify(employeeRepository, times(1)).findAll();
    }

    // ------------------------------------------------------------------ UPDATE VALID CASE
    @Test
    void updateEmployee_ShouldReturnUpdatedEmployee() {
        when(employeeRepository.findById("1")).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        EmployeeDTO result = employeeService.updateEmployee("1", employeeDTO);

        assertEquals("Abbas", result.getName());
        verify(employeeRepository, times(1)).findById("1");
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    // ------------------------------------------------------------------ UPDATE EXCEPTION CASE
    @Test
    void updateEmployee_ShouldThrowException_WhenNotFound() {
        when(employeeRepository.findById("99")).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class,
                () -> employeeService.updateEmployee("99", employeeDTO));
    }

    // ------------------------------------------------------------------ DELETE VALID CASE
    @Test
    void deleteEmployee_ShouldDeleteSuccessfully() {
        when(employeeRepository.findById("1")).thenReturn(Optional.of(employee));
        doNothing().when(employeeRepository).delete(employee);

        assertDoesNotThrow(() -> employeeService.deleteEmployee("1"));
        verify(employeeRepository, times(1)).delete(employee);
    }

    // ------------------------------------------------------------------ DELETE EXCEPTION CASE
    @Test
    void deleteEmployee_ShouldThrowException_WhenNotFound() {
        when(employeeRepository.findById("99")).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class,
                () -> employeeService.deleteEmployee("99"));
    }
}