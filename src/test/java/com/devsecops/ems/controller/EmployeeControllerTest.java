package com.devsecops.ems.controller;

import com.devsecops.ems.dto.EmployeeRequest;
import com.devsecops.ems.dto.EmployeeResponse;
import com.devsecops.ems.entity.EmployeeStatus;
import com.devsecops.ems.entity.Gender;
import com.devsecops.ems.exception.GlobalExceptionHandler;
import com.devsecops.ems.exception.ResourceNotFoundException;
import com.devsecops.ems.service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class EmployeeControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(employeeController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    // ========== Helper Methods ==========

    private EmployeeResponse buildEmployeeResponse(Long id, String firstName, String lastName, String email) {
        return EmployeeResponse.builder()
                .id(id)
                .employeeCode("EMP00" + id)
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .phone("+1234567890")
                .gender(Gender.MALE)
                .dateOfBirth(LocalDate.of(1990, 1, 15))
                .joiningDate(LocalDate.of(2023, 1, 1))
                .designation("Software Engineer")
                .salary(new BigDecimal("75000.00"))
                .status(EmployeeStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .departmentId(1L)
                .departmentName("Engineering")
                .departmentCode("ENG")
                .build();
    }

    private EmployeeRequest buildEmployeeRequest() {
        return EmployeeRequest.builder()
                .employeeCode("EMP001")
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("+1234567890")
                .gender(Gender.MALE)
                .dateOfBirth(LocalDate.of(1990, 1, 15))
                .joiningDate(LocalDate.of(2023, 1, 1))
                .designation("Software Engineer")
                .salary(new BigDecimal("75000.00"))
                .status(EmployeeStatus.ACTIVE)
                .departmentId(1L)
                .build();
    }

    // ========== GET /api/employees ==========

    @Test
    @DisplayName("GET /api/employees - Should return all employees")
    void getAllEmployees_ReturnsEmployeeList() throws Exception {
        EmployeeResponse emp1 = buildEmployeeResponse(1L, "John", "Doe", "john@example.com");
        EmployeeResponse emp2 = buildEmployeeResponse(2L, "Jane", "Smith", "jane@example.com");

        when(employeeService.getAllEmployees()).thenReturn(List.of(emp1, emp2));

        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].firstName", is("John")))
                .andExpect(jsonPath("$[1].firstName", is("Jane")));

        verify(employeeService).getAllEmployees();
    }

    // ========== GET /api/employees/{id} ==========

    @Test
    @DisplayName("GET /api/employees/{id} - Should return employee when found")
    void getEmployeeById_ReturnsEmployee() throws Exception {
        EmployeeResponse response = buildEmployeeResponse(1L, "John", "Doe", "john@example.com");

        when(employeeService.getEmployeeById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.email", is("john@example.com")));

        verify(employeeService).getEmployeeById(1L);
    }

    @Test
    @DisplayName("GET /api/employees/{id} - Should return 404 when not found")
    void getEmployeeById_NotFound_Returns404() throws Exception {
        when(employeeService.getEmployeeById(99L))
                .thenThrow(new ResourceNotFoundException("Employee", "id", 99L));

        mockMvc.perform(get("/api/employees/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));

        verify(employeeService).getEmployeeById(99L);
    }

    // ========== POST /api/employees ==========

    @Test
    @DisplayName("POST /api/employees - Should create employee and return 201")
    void createEmployee_ReturnsCreated() throws Exception {
        EmployeeRequest request = buildEmployeeRequest();
        EmployeeResponse response = buildEmployeeResponse(1L, "John", "Doe", "john.doe@example.com");

        when(employeeService.createEmployee(any(EmployeeRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("John")));

        verify(employeeService).createEmployee(any(EmployeeRequest.class));
    }

    @Test
    @DisplayName("POST /api/employees - Should return 400 for invalid request")
    void createEmployee_InvalidRequest_Returns400() throws Exception {
        EmployeeRequest invalidRequest = EmployeeRequest.builder().build(); // missing required fields

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    // ========== PUT /api/employees/{id} ==========

    @Test
    @DisplayName("PUT /api/employees/{id} - Should update employee successfully")
    void updateEmployee_ReturnsUpdatedEmployee() throws Exception {
        EmployeeRequest request = buildEmployeeRequest();
        EmployeeResponse response = buildEmployeeResponse(1L, "John", "Updated", "john.doe@example.com");

        when(employeeService.updateEmployee(eq(1L), any(EmployeeRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/employees/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName", is("Updated")));

        verify(employeeService).updateEmployee(eq(1L), any(EmployeeRequest.class));
    }

    // ========== DELETE /api/employees/{id} ==========

    @Test
    @DisplayName("DELETE /api/employees/{id} - Should delete employee and return 204")
    void deleteEmployee_ReturnsNoContent() throws Exception {
        doNothing().when(employeeService).deleteEmployee(1L);

        mockMvc.perform(delete("/api/employees/1"))
                .andExpect(status().isNoContent());

        verify(employeeService).deleteEmployee(1L);
    }

    @Test
    @DisplayName("DELETE /api/employees/{id} - Should return 404 when not found")
    void deleteEmployee_NotFound_Returns404() throws Exception {
        doThrow(new ResourceNotFoundException("Employee", "id", 99L))
                .when(employeeService).deleteEmployee(99L);

        mockMvc.perform(delete("/api/employees/99"))
                .andExpect(status().isNotFound());

        verify(employeeService).deleteEmployee(99L);
    }

    // ========== GET /api/employees/search ==========

    @Test
    @DisplayName("GET /api/employees/search - Should return matching employees")
    void searchEmployees_ReturnsMatchingEmployees() throws Exception {
        EmployeeResponse response = buildEmployeeResponse(1L, "John", "Doe", "john@example.com");

        when(employeeService.searchEmployees("John")).thenReturn(List.of(response));

        mockMvc.perform(get("/api/employees/search")
                        .param("keyword", "John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].firstName", is("John")));

        verify(employeeService).searchEmployees("John");
    }
}
