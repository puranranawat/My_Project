package com.devsecops.ems.controller;

import com.devsecops.ems.dto.EmployeeRequest;
import com.devsecops.ems.dto.EmployeeResponse;
import com.devsecops.ems.service.EmployeeService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * REST controller for managing employees.
 *
 * <p>Provides CRUD endpoints and a search endpoint under {@code /api/employees}.</p>
 */
@Slf4j
@RestController
@RequestMapping("/api/employees")
@Validated
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    public ResponseEntity<List<EmployeeResponse>> getAllEmployees() {
        log.info("Fetching all employees");
        List<EmployeeResponse> employees = employeeService.getAllEmployees();
        log.info("Retrieved {} employees", employees.size());
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> getEmployeeById(@PathVariable Long id) {
        log.info("Fetching employee with id: {}", id);
        EmployeeResponse employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(employee);
    }

    @PostMapping
    public ResponseEntity<EmployeeResponse> createEmployee(
            @Valid @RequestBody EmployeeRequest request) {
        log.info("Creating employee with email: {}", request.getEmail());
        EmployeeResponse createdEmployee = employeeService.createEmployee(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdEmployee.getId())
                .toUri();

        log.info("Employee created successfully with id: {}", createdEmployee.getId());
        return ResponseEntity.created(location).body(createdEmployee);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponse> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeRequest request) {
        log.info("Updating employee with id: {}", id);
        EmployeeResponse updatedEmployee = employeeService.updateEmployee(id, request);
        log.info("Employee updated successfully with id: {}", id);
        return ResponseEntity.ok(updatedEmployee);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        log.info("Deleting employee with id: {}", id);
        employeeService.deleteEmployee(id);
        log.info("Employee deleted successfully with id: {}", id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<EmployeeResponse>> searchEmployees(
            @RequestParam String keyword) {
        log.info("Searching employees with keyword: {}", keyword);
        List<EmployeeResponse> employees = employeeService.searchEmployees(keyword);
        log.info("Found {} employees matching keyword: {}", employees.size(), keyword);
        return ResponseEntity.ok(employees);
    }
}
