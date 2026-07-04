package com.devsecops.ems.mapper;

import com.devsecops.ems.dto.DepartmentRequest;
import com.devsecops.ems.dto.DepartmentResponse;
import com.devsecops.ems.entity.Department;

import java.util.List;

/**
 * Utility class for mapping between {@link Department} entities and DTOs.
 *
 * <p>Provides static methods for converting {@link DepartmentRequest} to
 * {@link Department} entities and {@link Department} entities to
 * {@link DepartmentResponse} DTOs.</p>
 */
public final class DepartmentMapper {

    private DepartmentMapper() {
        throw new UnsupportedOperationException("Utility class — cannot be instantiated");
    }

    /**
     * Converts a {@link DepartmentRequest} DTO to a new {@link Department} entity.
     *
     * @param request the department request DTO
     * @return a new Department entity (unsaved)
     */
    public static Department toEntity(DepartmentRequest request) {
        return Department.builder()
                .departmentCode(request.getDepartmentCode())
                .departmentName(request.getDepartmentName())
                .description(request.getDescription())
                .build();
    }

    /**
     * Converts a {@link Department} entity to a {@link DepartmentResponse} DTO.
     *
     * @param entity the department entity
     * @return the department response DTO
     */
    public static DepartmentResponse toResponse(Department entity) {
        return DepartmentResponse.builder()
                .id(entity.getId())
                .departmentCode(entity.getDepartmentCode())
                .departmentName(entity.getDepartmentName())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .employeeCount(entity.getEmployees() != null ? entity.getEmployees().size() : 0)
                .build();
    }

    /**
     * Converts a list of {@link Department} entities to a list of {@link DepartmentResponse} DTOs.
     *
     * @param departments the list of department entities
     * @return the list of department response DTOs
     */
    public static List<DepartmentResponse> toResponseList(List<Department> departments) {
        return departments.stream()
                .map(DepartmentMapper::toResponse)
                .toList();
    }
}
