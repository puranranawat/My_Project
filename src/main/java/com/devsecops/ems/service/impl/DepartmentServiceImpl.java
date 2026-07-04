package com.devsecops.ems.service.impl;

import com.devsecops.ems.dto.DepartmentRequest;
import com.devsecops.ems.dto.DepartmentResponse;
import com.devsecops.ems.entity.Department;
import com.devsecops.ems.exception.BadRequestException;
import com.devsecops.ems.exception.ResourceNotFoundException;
import com.devsecops.ems.mapper.DepartmentMapper;
import com.devsecops.ems.repository.DepartmentRepository;
import com.devsecops.ems.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of {@link DepartmentService}.
 *
 * <p>Handles all department business logic including CRUD operations
 * and uniqueness validation for department codes and names.</p>
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    @Override
    @Transactional
    public DepartmentResponse createDepartment(DepartmentRequest request) {
        log.info("Creating department with code: {}", request.getDepartmentCode());

        if (departmentRepository.existsByDepartmentCode(request.getDepartmentCode())) {
            throw new BadRequestException(
                    "Department with code '" + request.getDepartmentCode() + "' already exists");
        }

        if (departmentRepository.existsByDepartmentName(request.getDepartmentName())) {
            throw new BadRequestException(
                    "Department with name '" + request.getDepartmentName() + "' already exists");
        }

        Department department = DepartmentMapper.toEntity(request);
        Department savedDepartment = departmentRepository.save(department);

        log.info("Department created successfully with id: {}", savedDepartment.getId());
        return DepartmentMapper.toResponse(savedDepartment);
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentResponse getDepartmentById(Long id) {
        log.debug("Fetching department with id: {}", id);

        Department department = findDepartmentById(id);
        return DepartmentMapper.toResponse(department);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentResponse> getAllDepartments() {
        log.debug("Fetching all departments");

        return DepartmentMapper.toResponseList(departmentRepository.findAll());
    }

    @Override
    @Transactional
    public DepartmentResponse updateDepartment(Long id, DepartmentRequest request) {
        log.info("Updating department with id: {}", id);

        Department existingDepartment = findDepartmentById(id);

        departmentRepository.findByDepartmentCode(request.getDepartmentCode())
                .filter(dept -> !dept.getId().equals(id))
                .ifPresent(dept -> {
                    throw new BadRequestException(
                            "Department with code '" + request.getDepartmentCode() + "' already exists");
                });

        departmentRepository.findByDepartmentName(request.getDepartmentName())
                .filter(dept -> !dept.getId().equals(id))
                .ifPresent(dept -> {
                    throw new BadRequestException(
                            "Department with name '" + request.getDepartmentName() + "' already exists");
                });

        existingDepartment.setDepartmentCode(request.getDepartmentCode());
        existingDepartment.setDepartmentName(request.getDepartmentName());
        existingDepartment.setDescription(request.getDescription());

        Department updatedDepartment = departmentRepository.save(existingDepartment);

        log.info("Department updated successfully with id: {}", updatedDepartment.getId());
        return DepartmentMapper.toResponse(updatedDepartment);
    }

    @Override
    @Transactional
    public void deleteDepartment(Long id) {
        log.info("Deleting department with id: {}", id);

        Department department = findDepartmentById(id);
        departmentRepository.delete(department);

        log.info("Department deleted successfully with id: {}", id);
    }

    // ---------------------------------------------------------------
    // Private helper methods
    // ---------------------------------------------------------------

    private Department findDepartmentById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", id));
    }
}
