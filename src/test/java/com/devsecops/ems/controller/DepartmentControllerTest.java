package com.devsecops.ems.controller;

import com.devsecops.ems.dto.DepartmentRequest;
import com.devsecops.ems.dto.DepartmentResponse;
import com.devsecops.ems.exception.GlobalExceptionHandler;
import com.devsecops.ems.exception.ResourceNotFoundException;
import com.devsecops.ems.service.DepartmentService;
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
class DepartmentControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private DepartmentService departmentService;

    @InjectMocks
    private DepartmentController departmentController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(departmentController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    // ========== Helper Methods ==========

    private DepartmentResponse buildDepartmentResponse(Long id, String name, String code) {
        return DepartmentResponse.builder()
                .id(id)
                .departmentCode(code)
                .departmentName(name)
                .description(name + " Department")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .employeeCount(5)
                .build();
    }

    private DepartmentRequest buildDepartmentRequest() {
        return DepartmentRequest.builder()
                .departmentCode("ENG")
                .departmentName("Engineering")
                .description("Engineering Department")
                .build();
    }

    // ========== GET /api/departments ==========

    @Test
    @DisplayName("GET /api/departments - Should return all departments")
    void getAllDepartments_ReturnsDepartmentList() throws Exception {
        DepartmentResponse dept1 = buildDepartmentResponse(1L, "Engineering", "ENG");
        DepartmentResponse dept2 = buildDepartmentResponse(2L, "Marketing", "MKT");

        when(departmentService.getAllDepartments()).thenReturn(List.of(dept1, dept2));

        mockMvc.perform(get("/api/departments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].departmentName", is("Engineering")))
                .andExpect(jsonPath("$[1].departmentName", is("Marketing")));

        verify(departmentService).getAllDepartments();
    }

    // ========== GET /api/departments/{id} ==========

    @Test
    @DisplayName("GET /api/departments/{id} - Should return department when found")
    void getDepartmentById_ReturnsDepartment() throws Exception {
        DepartmentResponse response = buildDepartmentResponse(1L, "Engineering", "ENG");

        when(departmentService.getDepartmentById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/departments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.departmentName", is("Engineering")));

        verify(departmentService).getDepartmentById(1L);
    }

    @Test
    @DisplayName("GET /api/departments/{id} - Should return 404 when not found")
    void getDepartmentById_NotFound_Returns404() throws Exception {
        when(departmentService.getDepartmentById(99L))
                .thenThrow(new ResourceNotFoundException("Department", "id", 99L));

        mockMvc.perform(get("/api/departments/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));

        verify(departmentService).getDepartmentById(99L);
    }

    // ========== POST /api/departments ==========

    @Test
    @DisplayName("POST /api/departments - Should create department and return 201")
    void createDepartment_ReturnsCreated() throws Exception {
        DepartmentRequest request = buildDepartmentRequest();
        DepartmentResponse response = buildDepartmentResponse(1L, "Engineering", "ENG");

        when(departmentService.createDepartment(any(DepartmentRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.departmentName", is("Engineering")));

        verify(departmentService).createDepartment(any(DepartmentRequest.class));
    }

    @Test
    @DisplayName("POST /api/departments - Should return 400 for invalid request")
    void createDepartment_InvalidRequest_Returns400() throws Exception {
        DepartmentRequest invalidRequest = DepartmentRequest.builder().build();

        mockMvc.perform(post("/api/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    // ========== PUT /api/departments/{id} ==========

    @Test
    @DisplayName("PUT /api/departments/{id} - Should update department successfully")
    void updateDepartment_ReturnsUpdatedDepartment() throws Exception {
        DepartmentRequest request = buildDepartmentRequest();
        DepartmentResponse response = buildDepartmentResponse(1L, "Engineering Updated", "ENG");

        when(departmentService.updateDepartment(eq(1L), any(DepartmentRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/departments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.departmentName", is("Engineering Updated")));

        verify(departmentService).updateDepartment(eq(1L), any(DepartmentRequest.class));
    }

    // ========== DELETE /api/departments/{id} ==========

    @Test
    @DisplayName("DELETE /api/departments/{id} - Should delete department and return 204")
    void deleteDepartment_ReturnsNoContent() throws Exception {
        doNothing().when(departmentService).deleteDepartment(1L);

        mockMvc.perform(delete("/api/departments/1"))
                .andExpect(status().isNoContent());

        verify(departmentService).deleteDepartment(1L);
    }

    @Test
    @DisplayName("DELETE /api/departments/{id} - Should return 404 when not found")
    void deleteDepartment_NotFound_Returns404() throws Exception {
        doThrow(new ResourceNotFoundException("Department", "id", 99L))
                .when(departmentService).deleteDepartment(99L);

        mockMvc.perform(delete("/api/departments/99"))
                .andExpect(status().isNotFound());

        verify(departmentService).deleteDepartment(99L);
    }
}
