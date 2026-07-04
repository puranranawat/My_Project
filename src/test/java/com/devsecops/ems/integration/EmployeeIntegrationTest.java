package com.devsecops.ems.integration;

import com.devsecops.ems.dto.DepartmentRequest;
import com.devsecops.ems.dto.EmployeeRequest;
import com.devsecops.ems.entity.EmployeeStatus;
import com.devsecops.ems.entity.Gender;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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

import java.math.BigDecimal;
import java.time.LocalDate;
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
 * Integration tests for Employee CRUD operations.
 *
 * <p>Uses H2 in-memory database via the "test" profile.
 * Tests the full request lifecycle through all layers.</p>
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EmployeeIntegrationTest {

    private static final AtomicLong COUNTER = new AtomicLong(1);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Long departmentId;

    /**
     * Generates a unique code that fits within the 20-character validation limit.
     */
    private String uniqueCode(String prefix) {
        return prefix + COUNTER.getAndIncrement();
    }

    @BeforeEach
    void setUp() throws Exception {
        String deptCode = uniqueCode("DEPT");
        String deptName = uniqueCode("Department ");

        DepartmentRequest deptRequest = DepartmentRequest.builder()
                .departmentCode(deptCode)
                .departmentName(deptName)
                .description("IT Department for testing")
                .build();

        String deptResponse = mockMvc.perform(post("/api/departments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deptRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        departmentId = objectMapper.readTree(deptResponse).get("id").asLong();
    }

    private EmployeeRequest buildEmployeeRequest(String code, String email) {
        return EmployeeRequest.builder()
                .employeeCode(code)
                .firstName("John")
                .lastName("Doe")
                .email(email)
                .phone("+1234567890")
                .gender(Gender.MALE)
                .dateOfBirth(LocalDate.of(1990, 1, 15))
                .joiningDate(LocalDate.of(2023, 1, 1))
                .designation("Software Engineer")
                .salary(new BigDecimal("75000.00"))
                .status(EmployeeStatus.ACTIVE)
                .departmentId(departmentId)
                .build();
    }

    // ========== CRUD Lifecycle ==========

    @Test
    @Order(1)
    @DisplayName("Full CRUD lifecycle: Create → Read → Update → Delete")
    void fullCrudLifecycle() throws Exception {
        String code = uniqueCode("EMP");
        String email = uniqueCode("john") + "@example.com";

        // CREATE
        EmployeeRequest createRequest = buildEmployeeRequest(code, email);

        String createResponse = mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName", is("John")))
                .andExpect(jsonPath("$.employeeCode", is(code)))
                .andReturn().getResponse().getContentAsString();

        Long employeeId = objectMapper.readTree(createResponse).get("id").asLong();

        // READ
        mockMvc.perform(get("/api/employees/" + employeeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(employeeId.intValue())))
                .andExpect(jsonPath("$.firstName", is("John")));

        // UPDATE
        String updatedEmail = uniqueCode("updated") + "@example.com";
        EmployeeRequest updateRequest = buildEmployeeRequest(code, updatedEmail);
        updateRequest.setLastName("Updated");

        mockMvc.perform(put("/api/employees/" + employeeId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName", is("Updated")))
                .andExpect(jsonPath("$.email", is(updatedEmail)));

        // DELETE
        mockMvc.perform(delete("/api/employees/" + employeeId))
                .andExpect(status().isNoContent());

        // VERIFY DELETED
        mockMvc.perform(get("/api/employees/" + employeeId))
                .andExpect(status().isNotFound());
    }

    // ========== Validation Tests ==========

    @Test
    @Order(2)
    @DisplayName("Should return 400 when creating employee with missing required fields")
    void createEmployee_MissingFields_Returns400() throws Exception {
        EmployeeRequest invalidRequest = EmployeeRequest.builder().build();

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    // ========== Not Found Tests ==========

    @Test
    @Order(3)
    @DisplayName("Should return 404 when getting non-existent employee")
    void getEmployee_NotFound_Returns404() throws Exception {
        mockMvc.perform(get("/api/employees/999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
    }

    // ========== Search Tests ==========

    @Test
    @Order(4)
    @DisplayName("Should search employees by keyword")
    void searchEmployees_ReturnsResults() throws Exception {
        String code = uniqueCode("SRCH");
        String email = uniqueCode("srch") + "@example.com";
        EmployeeRequest request = buildEmployeeRequest(code, email);
        request.setFirstName("Searchable");

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/employees/search")
                        .param("keyword", "Searchable"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }
}
