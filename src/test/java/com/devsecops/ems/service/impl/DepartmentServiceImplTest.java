package com.devsecops.ems.service.impl;

import com.devsecops.ems.dto.DepartmentRequest;
import com.devsecops.ems.dto.DepartmentResponse;
import com.devsecops.ems.entity.Department;
import com.devsecops.ems.exception.BadRequestException;
import com.devsecops.ems.exception.ResourceNotFoundException;
import com.devsecops.ems.repository.DepartmentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceImplTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private DepartmentServiceImpl departmentService;

    // ========== Helper Methods ==========

    private Department buildDepartment(Long id, String name, String code, String description) {
        Department department = new Department();
        department.setId(id);
        department.setDepartmentName(name);
        department.setDepartmentCode(code);
        department.setDescription(description);
        department.setCreatedAt(LocalDateTime.now());
        department.setUpdatedAt(LocalDateTime.now());
        return department;
    }

    private DepartmentRequest buildDepartmentRequest(String name, String code, String description) {
        DepartmentRequest request = new DepartmentRequest();
        request.setDepartmentName(name);
        request.setDepartmentCode(code);
        request.setDescription(description);
        return request;
    }

    // ========== Create Department Tests ==========

    @Test
    @DisplayName("Should create department successfully")
    void testCreateDepartment_Success() {
        // Given
        DepartmentRequest request = buildDepartmentRequest("Engineering", "ENG", "Engineering Department");
        Department savedDepartment = buildDepartment(1L, "Engineering", "ENG", "Engineering Department");

        when(departmentRepository.existsByDepartmentCode(anyString())).thenReturn(false);
        when(departmentRepository.existsByDepartmentName(anyString())).thenReturn(false);
        when(departmentRepository.save(any(Department.class))).thenReturn(savedDepartment);

        // When
        DepartmentResponse response = departmentService.createDepartment(request);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Engineering", response.getDepartmentName());
        assertEquals("ENG", response.getDepartmentCode());
        assertEquals("Engineering Department", response.getDescription());

        verify(departmentRepository, times(1)).existsByDepartmentCode("ENG");
        verify(departmentRepository, times(1)).save(any(Department.class));
    }

    @Test
    @DisplayName("Should throw BadRequestException when department code already exists")
    void testCreateDepartment_DuplicateCode_ThrowsBadRequest() {
        // Given
        DepartmentRequest request = buildDepartmentRequest("Engineering", "ENG", "Engineering Department");

        when(departmentRepository.existsByDepartmentCode("ENG")).thenReturn(true);

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> departmentService.createDepartment(request));

        assertTrue(exception.getMessage().contains("ENG"));
        verify(departmentRepository, times(1)).existsByDepartmentCode("ENG");
        verify(departmentRepository, never()).save(any(Department.class));
    }

    // ========== Get Department By ID Tests ==========

    @Test
    @DisplayName("Should return department when found by ID")
    void testGetDepartmentById_Success() {
        // Given
        Department department = buildDepartment(1L, "Engineering", "ENG", "Engineering Department");

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));

        // When
        DepartmentResponse response = departmentService.getDepartmentById(1L);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Engineering", response.getDepartmentName());
        assertEquals("ENG", response.getDepartmentCode());

        verify(departmentRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when department not found by ID")
    void testGetDepartmentById_NotFound_ThrowsException() {
        // Given
        when(departmentRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> departmentService.getDepartmentById(99L));

        assertTrue(exception.getMessage().contains("99"));
        verify(departmentRepository, times(1)).findById(99L);
    }

    // ========== Get All Departments Tests ==========

    @Test
    @DisplayName("Should return all departments successfully")
    void testGetAllDepartments_Success() {
        // Given
        Department dept1 = buildDepartment(1L, "Engineering", "ENG", "Engineering Department");
        Department dept2 = buildDepartment(2L, "Marketing", "MKT", "Marketing Department");

        when(departmentRepository.findAll()).thenReturn(List.of(dept1, dept2));

        // When
        List<DepartmentResponse> responses = departmentService.getAllDepartments();

        // Then
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals("Engineering", responses.get(0).getDepartmentName());
        assertEquals("Marketing", responses.get(1).getDepartmentName());

        verify(departmentRepository, times(1)).findAll();
    }

    // ========== Update Department Tests ==========

    @Test
    @DisplayName("Should update department successfully")
    void testUpdateDepartment_Success() {
        // Given
        Long departmentId = 1L;
        Department existingDepartment = buildDepartment(departmentId, "Engineering", "ENG", "Old Description");
        DepartmentRequest updateRequest = buildDepartmentRequest("Engineering Updated", "ENG", "New Description");
        Department updatedDepartment = buildDepartment(departmentId, "Engineering Updated", "ENG", "New Description");

        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(existingDepartment));
        when(departmentRepository.findByDepartmentCode("ENG")).thenReturn(Optional.of(existingDepartment));
        when(departmentRepository.findByDepartmentName("Engineering Updated")).thenReturn(Optional.empty());
        when(departmentRepository.save(any(Department.class))).thenReturn(updatedDepartment);

        // When
        DepartmentResponse response = departmentService.updateDepartment(departmentId, updateRequest);

        // Then
        assertNotNull(response);
        assertEquals(departmentId, response.getId());
        assertEquals("Engineering Updated", response.getDepartmentName());
        assertEquals("New Description", response.getDescription());

        verify(departmentRepository, times(1)).findById(departmentId);
        verify(departmentRepository, times(1)).save(any(Department.class));
    }

    // ========== Delete Department Tests ==========

    @Test
    @DisplayName("Should delete department successfully")
    void testDeleteDepartment_Success() {
        // Given
        Long departmentId = 1L;
        Department department = buildDepartment(departmentId, "Engineering", "ENG", "Engineering Department");

        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(department));
        doNothing().when(departmentRepository).delete(department);

        // When
        departmentService.deleteDepartment(departmentId);

        // Then
        verify(departmentRepository, times(1)).findById(departmentId);
        verify(departmentRepository, times(1)).delete(department);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent department")
    void testDeleteDepartment_NotFound_ThrowsException() {
        // Given
        when(departmentRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> departmentService.deleteDepartment(99L));

        assertTrue(exception.getMessage().contains("99"));
        verify(departmentRepository, times(1)).findById(99L);
        verify(departmentRepository, never()).delete(any(Department.class));
    }
}
