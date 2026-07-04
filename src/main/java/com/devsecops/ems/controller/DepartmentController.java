package com.devsecops.ems.controller;

import com.devsecops.ems.dto.DepartmentRequest;
import com.devsecops.ems.dto.DepartmentResponse;
import com.devsecops.ems.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * REST controller for managing departments.
 *
 * <p>Provides CRUD endpoints under {@code /api/departments}.</p>
 */
@Slf4j
@RestController
@RequestMapping("/api/departments")
@Validated
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping
    public ResponseEntity<List<DepartmentResponse>> getAllDepartments() {
        log.info("Fetching all departments");
        List<DepartmentResponse> departments = departmentService.getAllDepartments();
        log.info("Retrieved {} departments", departments.size());
        return ResponseEntity.ok(departments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartmentResponse> getDepartmentById(@PathVariable Long id) {
        log.info("Fetching department with id: {}", id);
        DepartmentResponse department = departmentService.getDepartmentById(id);
        return ResponseEntity.ok(department);
    }

    @PostMapping
    public ResponseEntity<DepartmentResponse> createDepartment(
            @Valid @RequestBody DepartmentRequest request) {
        log.info("Creating department with name: {}", request.getDepartmentName());
        DepartmentResponse createdDepartment = departmentService.createDepartment(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdDepartment.getId())
                .toUri();

        log.info("Department created successfully with id: {}", createdDepartment.getId());
        return ResponseEntity.created(location).body(createdDepartment);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DepartmentResponse> updateDepartment(
            @PathVariable Long id,
            @Valid @RequestBody DepartmentRequest request) {
        log.info("Updating department with id: {}", id);
        DepartmentResponse updatedDepartment = departmentService.updateDepartment(id, request);
        log.info("Department updated successfully with id: {}", id);
        return ResponseEntity.ok(updatedDepartment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        log.info("Deleting department with id: {}", id);
        departmentService.deleteDepartment(id);
        log.info("Department deleted successfully with id: {}", id);
        return ResponseEntity.noContent().build();
    }
}
