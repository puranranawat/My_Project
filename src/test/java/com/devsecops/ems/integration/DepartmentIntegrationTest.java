package com.devsecops.ems.integration;

import com.devsecops.ems.dto.DepartmentRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.concurrent.atomic.AtomicLong;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for Department CRUD operations.
 *
 * <p>Uses H2 in-memory database via the "test" profile.
 * Tests the full request lifecycle through all layers.</p>
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DepartmentIntegrationTest {

    private static final AtomicLong COUNTER = new AtomicLong(100);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Generates a unique code that fits within the 20-character validation limit.
     */
    private String uniqueCode(String prefix) {
        return prefix + COUNTER.getAndIncrement();
    }

    private DepartmentRequest buildDepartmentRequest(String code, String name) {
        return DepartmentRequest.builder()
                .departmentCode(code)
                .departmentName(name)
                .description(name + " Description")
                .build();
    }

    // ========== CRUD Lifecycle ==========

    @Test
    @Order(1)
    @DisplayName("Full CRUD lifecycle: Create → Read → Update → Delete")
    void fullCrudLifecycle() throws Exception {
        String code = uniqueCode("DPT");
        String name = uniqueCode("Dept ");

        // CREATE
        DepartmentRequest createRequest = buildDepartmentRequest(code, name);

        String createResponse = mockMvc.perform(post("/api/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.departmentCode", is(code)))
                .andExpect(jsonPath("$.departmentName", is(name)))
                .andReturn().getResponse().getContentAsString();

        Long departmentId = objectMapper.readTree(createResponse).get("id").asLong();

        // READ
        mockMvc.perform(get("/api/departments/" + departmentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(departmentId.intValue())))
                .andExpect(jsonPath("$.departmentCode", is(code)));

        // UPDATE
        String updatedName = uniqueCode("Updated ");
        DepartmentRequest updateRequest = buildDepartmentRequest(code, updatedName);
        updateRequest.setDescription("Updated description");

        mockMvc.perform(put("/api/departments/" + departmentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.departmentName", is(updatedName)))
                .andExpect(jsonPath("$.description", is("Updated description")));

        // DELETE
        mockMvc.perform(delete("/api/departments/" + departmentId))
                .andExpect(status().isNoContent());

        // VERIFY DELETED
        mockMvc.perform(get("/api/departments/" + departmentId))
                .andExpect(status().isNotFound());
    }

    // ========== Validation Tests ==========

    @Test
    @Order(2)
    @DisplayName("Should return 400 when creating department with missing required fields")
    void createDepartment_MissingFields_Returns400() throws Exception {
        DepartmentRequest invalidRequest = DepartmentRequest.builder().build();

        mockMvc.perform(post("/api/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    // ========== Not Found Tests ==========

    @Test
    @Order(3)
    @DisplayName("Should return 404 when getting non-existent department")
    void getDepartment_NotFound_Returns404() throws Exception {
        mockMvc.perform(get("/api/departments/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
    }

    // ========== List Tests ==========

    @Test
    @Order(4)
    @DisplayName("Should return list of departments")
    void getAllDepartments_ReturnsList() throws Exception {
        String code = uniqueCode("LST");
        String name = uniqueCode("List ");

        // Create a department first
        DepartmentRequest request = buildDepartmentRequest(code, name);
        mockMvc.perform(post("/api/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Get all departments
        mockMvc.perform(get("/api/departments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }
}
