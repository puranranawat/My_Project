package com.devsecops.ems.mapper;

import com.devsecops.ems.dto.DepartmentRequest;
import com.devsecops.ems.dto.DepartmentResponse;
import com.devsecops.ems.entity.Department;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class DepartmentMapperTest {

    // ========== Helper Methods ==========

    private Department buildDepartment(Long id, String name, String code, String description) {
        Department department = new Department();
        department.setId(id);
        department.setDepartmentName(name);
        department.setDepartmentCode(code);
        department.setDescription(description);
        department.setEmployees(new ArrayList<>());
        department.setCreatedAt(LocalDateTime.now());
        department.setUpdatedAt(LocalDateTime.now());
        return department;
    }

    // ========== toEntity Tests ==========

    @Test
    @DisplayName("toEntity - Should map DepartmentRequest to Department entity")
    void toEntity_MapsCorrectly() {
        DepartmentRequest request = DepartmentRequest.builder()
                .departmentCode("ENG")
                .departmentName("Engineering")
                .description("Engineering Department")
                .build();

        Department department = DepartmentMapper.toEntity(request);

        assertNotNull(department);
        assertEquals("ENG", department.getDepartmentCode());
        assertEquals("Engineering", department.getDepartmentName());
        assertEquals("Engineering Department", department.getDescription());
        assertNull(department.getId());
    }

    // ========== toResponse Tests ==========

    @Test
    @DisplayName("toResponse - Should map Department entity to DepartmentResponse DTO")
    void toResponse_MapsCorrectly() {
        Department department = buildDepartment(1L, "Engineering", "ENG", "Engineering Department");

        DepartmentResponse response = DepartmentMapper.toResponse(department);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("ENG", response.getDepartmentCode());
        assertEquals("Engineering", response.getDepartmentName());
        assertEquals("Engineering Department", response.getDescription());
        assertEquals(0, response.getEmployeeCount());
    }

    @Test
    @DisplayName("toResponse - Should handle null employees list")
    void toResponse_NullEmployees_ReturnsZeroCount() {
        Department department = buildDepartment(1L, "Engineering", "ENG", "Engineering Department");
        department.setEmployees(null);

        DepartmentResponse response = DepartmentMapper.toResponse(department);

        assertNotNull(response);
        assertEquals(0, response.getEmployeeCount());
    }

    // ========== toResponseList Tests ==========

    @Test
    @DisplayName("toResponseList - Should map list of entities to list of DTOs")
    void toResponseList_MapsListCorrectly() {
        Department dept1 = buildDepartment(1L, "Engineering", "ENG", "Engineering Department");
        Department dept2 = buildDepartment(2L, "Marketing", "MKT", "Marketing Department");

        List<DepartmentResponse> responses = DepartmentMapper.toResponseList(List.of(dept1, dept2));

        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals("Engineering", responses.get(0).getDepartmentName());
        assertEquals("Marketing", responses.get(1).getDepartmentName());
    }

    @Test
    @DisplayName("toResponseList - Should return empty list for empty input")
    void toResponseList_EmptyList_ReturnsEmptyList() {
        List<DepartmentResponse> responses = DepartmentMapper.toResponseList(List.of());

        assertNotNull(responses);
        assertEquals(0, responses.size());
    }
}
