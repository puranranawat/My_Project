package com.devsecops.ems.mapper;

import com.devsecops.ems.dto.EmployeeRequest;
import com.devsecops.ems.dto.EmployeeResponse;
import com.devsecops.ems.entity.Department;
import com.devsecops.ems.entity.Employee;
import com.devsecops.ems.entity.EmployeeStatus;
import com.devsecops.ems.entity.Gender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class EmployeeMapperTest {

    // ========== Helper Methods ==========

    private Department buildDepartment() {
        Department department = new Department();
        department.setId(1L);
        department.setDepartmentCode("ENG");
        department.setDepartmentName("Engineering");
        return department;
    }

    private EmployeeRequest buildRequest() {
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

    private Employee buildEmployee(Department department) {
        Employee employee = new Employee();
        employee.setId(1L);
        employee.setEmployeeCode("EMP001");
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setEmail("john.doe@example.com");
        employee.setPhone("+1234567890");
        employee.setGender(Gender.MALE);
        employee.setDateOfBirth(LocalDate.of(1990, 1, 15));
        employee.setJoiningDate(LocalDate.of(2023, 1, 1));
        employee.setDesignation("Software Engineer");
        employee.setSalary(new BigDecimal("75000.00"));
        employee.setStatus(EmployeeStatus.ACTIVE);
        employee.setDepartment(department);
        employee.setCreatedAt(LocalDateTime.now());
        employee.setUpdatedAt(LocalDateTime.now());
        return employee;
    }

    // ========== toEntity Tests ==========

    @Test
    @DisplayName("toEntity - Should map EmployeeRequest to Employee entity")
    void toEntity_MapsCorrectly() {
        EmployeeRequest request = buildRequest();
        Department department = buildDepartment();

        Employee employee = EmployeeMapper.toEntity(request, department);

        assertNotNull(employee);
        assertEquals("EMP001", employee.getEmployeeCode());
        assertEquals("John", employee.getFirstName());
        assertEquals("Doe", employee.getLastName());
        assertEquals("john.doe@example.com", employee.getEmail());
        assertEquals(Gender.MALE, employee.getGender());
        assertEquals(EmployeeStatus.ACTIVE, employee.getStatus());
        assertEquals(department, employee.getDepartment());
        assertNull(employee.getId()); // ID should not be set
    }

    // ========== toResponse Tests ==========

    @Test
    @DisplayName("toResponse - Should map Employee entity to EmployeeResponse DTO")
    void toResponse_MapsCorrectly() {
        Department department = buildDepartment();
        Employee employee = buildEmployee(department);

        EmployeeResponse response = EmployeeMapper.toResponse(employee);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("EMP001", response.getEmployeeCode());
        assertEquals("John", response.getFirstName());
        assertEquals("Doe", response.getLastName());
        assertEquals("john.doe@example.com", response.getEmail());
        assertEquals(1L, response.getDepartmentId());
        assertEquals("Engineering", response.getDepartmentName());
        assertEquals("ENG", response.getDepartmentCode());
    }

    @Test
    @DisplayName("toResponse - Should handle null department gracefully")
    void toResponse_NullDepartment_MapsWithoutDepartment() {
        Employee employee = buildEmployee(null);
        employee.setDepartment(null);

        EmployeeResponse response = EmployeeMapper.toResponse(employee);

        assertNotNull(response);
        assertNull(response.getDepartmentId());
        assertNull(response.getDepartmentName());
        assertNull(response.getDepartmentCode());
    }

    // ========== toResponseList Tests ==========

    @Test
    @DisplayName("toResponseList - Should map list of entities to list of DTOs")
    void toResponseList_MapsListCorrectly() {
        Department department = buildDepartment();
        Employee emp1 = buildEmployee(department);
        Employee emp2 = buildEmployee(department);
        emp2.setId(2L);
        emp2.setFirstName("Jane");
        emp2.setEmail("jane@example.com");

        List<EmployeeResponse> responses = EmployeeMapper.toResponseList(List.of(emp1, emp2));

        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals("John", responses.get(0).getFirstName());
        assertEquals("Jane", responses.get(1).getFirstName());
    }

    // ========== updateEntity Tests ==========

    @Test
    @DisplayName("updateEntity - Should update existing entity with request values")
    void updateEntity_UpdatesAllFields() {
        Department department = buildDepartment();
        Employee employee = buildEmployee(department);

        Department newDepartment = new Department();
        newDepartment.setId(2L);
        newDepartment.setDepartmentCode("MKT");
        newDepartment.setDepartmentName("Marketing");

        EmployeeRequest updateRequest = EmployeeRequest.builder()
                .employeeCode("EMP002")
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .phone("+9876543210")
                .gender(Gender.FEMALE)
                .dateOfBirth(LocalDate.of(1992, 5, 20))
                .joiningDate(LocalDate.of(2024, 3, 1))
                .designation("Marketing Manager")
                .salary(new BigDecimal("85000.00"))
                .status(EmployeeStatus.ACTIVE)
                .departmentId(2L)
                .build();

        EmployeeMapper.updateEntity(employee, updateRequest, newDepartment);

        assertEquals("EMP002", employee.getEmployeeCode());
        assertEquals("Jane", employee.getFirstName());
        assertEquals("Smith", employee.getLastName());
        assertEquals("jane.smith@example.com", employee.getEmail());
        assertEquals(Gender.FEMALE, employee.getGender());
        assertEquals(newDepartment, employee.getDepartment());
    }
}
