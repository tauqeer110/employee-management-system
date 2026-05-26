package com.ems.employee.controller;

import com.ems.employee.dto.EmployeeDTO;
import com.ems.employee.exception.EmployeeNotFoundException;
import com.ems.employee.service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmployeeController.class)
public class EmployeeControllerTest{

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    private EmployeeDTO employeeDTO;

    @BeforeEach
    void setUp(){
        employeeDTO = new EmployeeDTO();
        employeeDTO.setId("1");
        employeeDTO.setName("Abbas");
        employeeDTO.setAge(22);
        employeeDTO.setAddress("Bangalore");
    }

    // ------------------------------------------------------------------ CREATE SUCCESS CASE
    @Test
    void createEmployee_ShouldReturn201() throws Exception {
        when(employeeService.createEmployee(any(EmployeeDTO.class))).thenReturn(employeeDTO);

        mockMvc.perform(post("/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employeeDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("Abbas"));

        verify(employeeService, times(1)).createEmployee(any(EmployeeDTO.class));
    }

    // ------------------------------------------------------------------ CREATE EXCEPTION CASE WHEN NAME IS BLANK OR NULL
    @Test
    void createEmployee_ShouldThrowException_WhenNameIsBlank() throws Exception {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setName("");  // blank name
        dto.setAge(28);
        dto.setAddress("Bangalore");

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    // ------------------------------------------------------------------ CREATE EXCEPTION CASE WHEN AGE IS NULL
    @Test
    void createEmployee_ShouldThrowException_WhenAgeIsNull() throws Exception {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setName("Abbas");
        dto.setAge(null); // Age is null
        dto.setAddress("Bangalore");

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    // ------------------------------------------------------------------ CREATE EXCEPTION CASE WHEN AGE IS BELOW 18
    @Test
    void createEmployee_ShouldThrowException_WhenAgeIsBelow18() throws Exception {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setName("Abbas");
        dto.setAge(10); // Age is Below 18
        dto.setAddress("Bangalore");

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    // ------------------------------------------------------------------ CREATE EXCEPTION CASE WHEN ADDRESS IS BLANK
    @Test
    void createEmployee_ShouldThrowException_WhenAddressIsBlank() throws Exception {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setName("Abbas");
        dto.setAge(22); // Age is Below 18
        dto.setAddress(" ");

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    // ------------------------------------------------------------------ GET ALL
    @Test
    void getAllEmployees_ShouldReturn200() throws Exception {
        when(employeeService.getAllEmployees()).thenReturn(List.of(employeeDTO));

        mockMvc.perform(get("/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Abbas"));

        verify(employeeService, times(1)).getAllEmployees();
    }

    // ------------------------------------------------------------------ GET BY ID
    @Test
    void getEmployeeById_ShouldReturn200_WhenFound() throws Exception {
        when(employeeService.getEmployeeById("1")).thenReturn(employeeDTO);

        mockMvc.perform(get("/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("Abbas"));

        verify(employeeService, times(1)).getEmployeeById("1");
    }

    // ------------------------------------------------------------------ GET BY ID EXCEPTION CASE
    @Test
    void getEmployeeById_ShouldThrowException_WhenNotFound() throws Exception {
        when(employeeService.getEmployeeById("99"))
                .thenThrow(new EmployeeNotFoundException("Employee not found with id: 99"));

        mockMvc.perform(get("/employees/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Employee not found with id: 99"))
                .andExpect(jsonPath("$.status").value(404));
    }

    // ------------------------------------------------------------------ UPDATE
    @Test
    void updateEmployeeById_ShouldReturn200() throws Exception{
        when(employeeService.updateEmployee(eq("1"), any(EmployeeDTO.class))).thenReturn(employeeDTO);

        mockMvc.perform(put("/employees/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employeeDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("Abbas"));

        verify(employeeService, times(1)).updateEmployee(eq("1"), any(EmployeeDTO.class));
    }

    // ------------------------------------------------------------------ UPDATE EXCEPTION CASE WHEN NOT FOUND
    @Test
    void updateEmployeeById_ShouldThrowException_WhenNotFound() throws Exception{
        when(employeeService.updateEmployee(eq("99"), any(EmployeeDTO.class)))
                .thenThrow(new EmployeeNotFoundException("Employee not found with id: 99"));

        mockMvc.perform(put("/employees/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Employee not found with id: 99"));
    }

    // ------------------------------------------------------------------ DELETE
    @Test
    void deleteEmployee_ShouldReturn204() throws Exception {
        doNothing().when(employeeService).deleteEmployee("1");

        mockMvc.perform(delete("/employees/1"))
                .andExpect(status().isNoContent());

        verify(employeeService, times(1)).deleteEmployee("1");
    }

    // ------------------------------------------------------------------ DELETE EXCEPTION CASE WHEN NOT FOUND
    @Test
    void deleteEmployee_ShouldThrowException_WhenNotFound() throws Exception {
        doThrow(new EmployeeNotFoundException("Employee not found with id: 99"))
                .when(employeeService).deleteEmployee("99");

        mockMvc.perform(delete("/employees/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Employee not found with id: 99"));
    }
}