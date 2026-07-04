package com.devsecops.ems.service.impl;

import com.devsecops.ems.dto.EmployeeRequest;
import com.devsecops.ems.dto.EmployeeResponse;
import com.devsecops.ems.entity.Department;
import com.devsecops.ems.entity.Employee;
import com.devsecops.ems.exception.BadRequestException;
import com.devsecops.ems.exception.ResourceNotFoundException;
import com.devsecops.ems.mapper.EmployeeMapper;
import com.devsecops.ems.repository.DepartmentRepository;
import com.devsecops.ems.repository.EmployeeRepository;
import com.devsecops.ems.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of {@link EmployeeService}.
 *
 * <p>Handles all employee business logic including CRUD operations,
 * uniqueness validation, and search functionality.</p>
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    @Transactional
    public EmployeeResponse createEmployee(EmployeeRequest request) {
        log.info("Creating employee with code: {}", request.getEmployeeCode());

        if (employeeRepository.existsByEmployeeCode(request.getEmployeeCode())) {
            throw new BadRequestException(
                    "Employee with code '" + request.getEmployeeCode() + "' already exists");
        }

        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException(
                    "Employee with email '" + request.getEmail() + "' already exists");
        }

        Department department = findDepartmentById(request.getDepartmentId());

        Employee employee = EmployeeMapper.toEntity(request, department);
        Employee savedEmployee = employeeRepository.save(employee);

        log.info("Employee created successfully with id: {}", savedEmployee.getId());
        return EmployeeMapper.toResponse(savedEmployee);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeById(Long id) {
        log.debug("Fetching employee with id: {}", id);

        Employee employee = findEmployeeById(id);
        return EmployeeMapper.toResponse(employee);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponse> getAllEmployees() {
        log.debug("Fetching all employees");

        return EmployeeMapper.toResponseList(employeeRepository.findAll());
    }

    @Override
    @Transactional
    public EmployeeResponse updateEmployee(Long id, EmployeeRequest request) {
        log.info("Updating employee with id: {}", id);

        Employee existingEmployee = findEmployeeById(id);

        employeeRepository.findByEmployeeCode(request.getEmployeeCode())
                .filter(emp -> !emp.getId().equals(id))
                .ifPresent(emp -> {
                    throw new BadRequestException(
                            "Employee with code '" + request.getEmployeeCode() + "' already exists");
                });

        employeeRepository.findByEmail(request.getEmail())
                .filter(emp -> !emp.getId().equals(id))
                .ifPresent(emp -> {
                    throw new BadRequestException(
                            "Employee with email '" + request.getEmail() + "' already exists");
                });

        Department department = findDepartmentById(request.getDepartmentId());

        EmployeeMapper.updateEntity(existingEmployee, request, department);
        Employee updatedEmployee = employeeRepository.save(existingEmployee);

        log.info("Employee updated successfully with id: {}", updatedEmployee.getId());
        return EmployeeMapper.toResponse(updatedEmployee);
    }

    @Override
    @Transactional
    public void deleteEmployee(Long id) {
        log.info("Deleting employee with id: {}", id);

        Employee employee = findEmployeeById(id);
        employeeRepository.delete(employee);

        log.info("Employee deleted successfully with id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeResponse> searchEmployees(String keyword) {
        log.debug("Searching employees with keyword: {}", keyword);

        return EmployeeMapper.toResponseList(employeeRepository.searchEmployees(keyword));
    }

    // ---------------------------------------------------------------
    // Private helper methods
    // ---------------------------------------------------------------

    private Employee findEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
    }

    private Department findDepartmentById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", id));
    }
}
