package com.devsecops.ems.mapper;

import com.devsecops.ems.dto.EmployeeRequest;
import com.devsecops.ems.dto.EmployeeResponse;
import com.devsecops.ems.entity.Department;
import com.devsecops.ems.entity.Employee;

import java.util.List;

/**
 * Utility class for mapping between {@link Employee} entities and DTOs.
 *
 * <p>Provides static methods for converting {@link EmployeeRequest} to
 * {@link Employee} entities and {@link Employee} entities to
 * {@link EmployeeResponse} DTOs.</p>
 */
public final class EmployeeMapper {

    private EmployeeMapper() {
        throw new UnsupportedOperationException("Utility class — cannot be instantiated");
    }

    /**
     * Converts an {@link EmployeeRequest} DTO to a new {@link Employee} entity.
     *
     * @param request    the employee request DTO
     * @param department the department to associate with the employee
     * @return a new Employee entity (unsaved)
     */
    public static Employee toEntity(EmployeeRequest request, Department department) {
        return Employee.builder()
                .employeeCode(request.getEmployeeCode())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .gender(request.getGender())
                .dateOfBirth(request.getDateOfBirth())
                .joiningDate(request.getJoiningDate())
                .designation(request.getDesignation())
                .salary(request.getSalary())
                .status(request.getStatus())
                .department(department)
                .build();
    }

    /**
     * Converts an {@link Employee} entity to an {@link EmployeeResponse} DTO.
     *
     * @param entity the employee entity
     * @return the employee response DTO
     */
    public static EmployeeResponse toResponse(Employee entity) {
        EmployeeResponse.EmployeeResponseBuilder builder = EmployeeResponse.builder()
                .id(entity.getId())
                .employeeCode(entity.getEmployeeCode())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .gender(entity.getGender())
                .dateOfBirth(entity.getDateOfBirth())
                .joiningDate(entity.getJoiningDate())
                .designation(entity.getDesignation())
                .salary(entity.getSalary())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt());

        if (entity.getDepartment() != null) {
            builder.departmentId(entity.getDepartment().getId())
                    .departmentName(entity.getDepartment().getDepartmentName())
                    .departmentCode(entity.getDepartment().getDepartmentCode());
        }

        return builder.build();
    }

    /**
     * Converts a list of {@link Employee} entities to a list of {@link EmployeeResponse} DTOs.
     *
     * @param employees the list of employee entities
     * @return the list of employee response DTOs
     */
    public static List<EmployeeResponse> toResponseList(List<Employee> employees) {
        return employees.stream()
                .map(EmployeeMapper::toResponse)
                .toList();
    }

    /**
     * Updates an existing {@link Employee} entity with values from an {@link EmployeeRequest} DTO.
     *
     * @param employee   the existing employee entity to update
     * @param request    the employee request DTO with new values
     * @param department the department to associate
     */
    public static void updateEntity(Employee employee, EmployeeRequest request, Department department) {
        employee.setEmployeeCode(request.getEmployeeCode());
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setPhone(request.getPhone());
        employee.setGender(request.getGender());
        employee.setDateOfBirth(request.getDateOfBirth());
        employee.setJoiningDate(request.getJoiningDate());
        employee.setDesignation(request.getDesignation());
        employee.setSalary(request.getSalary());
        employee.setStatus(request.getStatus());
        employee.setDepartment(department);
    }
}
