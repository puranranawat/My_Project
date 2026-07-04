package com.devsecops.ems.service.impl;

import com.devsecops.ems.dto.EmployeeRequest;
import com.devsecops.ems.dto.EmployeeResponse;
import com.devsecops.ems.entity.Department;
import com.devsecops.ems.entity.Employee;
import com.devsecops.ems.entity.EmployeeStatus;
import com.devsecops.ems.entity.Gender;
import com.devsecops.ems.exception.BadRequestException;
import com.devsecops.ems.exception.ResourceNotFoundException;
import com.devsecops.ems.repository.DepartmentRepository;
import com.devsecops.ems.repository.EmployeeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    // ========== Helper Methods ==========

    private Department buildDepartment(Long id, String name, String code) {
        Department department = new Department();
        department.setId(id);
        department.setDepartmentName(name);
        department.setDepartmentCode(code);
        department.setDescription(name + " Department");
        department.setCreatedAt(LocalDateTime.now());
        department.setUpdatedAt(LocalDateTime.now());
        return department;
    }

    private Employee buildEmployee(Long id, String firstName, String lastName, String email,
                                   String employeeCode, Department department) {
        Employee employee = new Employee();
        employee.setId(id);
        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        employee.setEmail(email);
        employee.setEmployeeCode(employeeCode);
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

    private EmployeeRequest buildEmployeeRequest(String firstName, String lastName, String email,
                                                  String employeeCode, Long departmentId) {
        EmployeeRequest request = new EmployeeRequest();
        request.setFirstName(firstName);
        request.setLastName(lastName);
        request.setEmail(email);
        request.setEmployeeCode(employeeCode);
        request.setPhone("+1234567890");
        request.setGender(Gender.MALE);
        request.setDateOfBirth(LocalDate.of(1990, 1, 15));
        request.setJoiningDate(LocalDate.of(2023, 1, 1));
        request.setDesignation("Software Engineer");
        request.setSalary(new BigDecimal("75000.00"));
        request.setStatus(EmployeeStatus.ACTIVE);
        request.setDepartmentId(departmentId);
        return request;
    }

    // ========== Create Employee Tests ==========

    @Test
    @DisplayName("Should create employee successfully")
    void testCreateEmployee_Success() {
        // Given
        Department department = buildDepartment(1L, "Engineering", "ENG");
        EmployeeRequest request = buildEmployeeRequest("John", "Doe", "john.doe@example.com", "EMP001", 1L);
        Employee savedEmployee = buildEmployee(1L, "John", "Doe", "john.doe@example.com", "EMP001", department);

        when(employeeRepository.existsByEmployeeCode("EMP001")).thenReturn(false);
        when(employeeRepository.existsByEmail("john.doe@example.com")).thenReturn(false);
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(employeeRepository.save(any(Employee.class))).thenReturn(savedEmployee);

        // When
        EmployeeResponse response = employeeService.createEmployee(request);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("John", response.getFirstName());
        assertEquals("Doe", response.getLastName());
        assertEquals("john.doe@example.com", response.getEmail());
        assertEquals("EMP001", response.getEmployeeCode());

        verify(employeeRepository, times(1)).existsByEmployeeCode("EMP001");
        verify(employeeRepository, times(1)).existsByEmail("john.doe@example.com");
        verify(departmentRepository, times(1)).findById(1L);
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    @DisplayName("Should throw BadRequestException when employee code already exists")
    void testCreateEmployee_DuplicateCode_ThrowsBadRequest() {
        // Given
        EmployeeRequest request = buildEmployeeRequest("John", "Doe", "john.doe@example.com", "EMP001", 1L);

        when(employeeRepository.existsByEmployeeCode("EMP001")).thenReturn(true);

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> employeeService.createEmployee(request));

        assertTrue(exception.getMessage().contains("EMP001"));
        verify(employeeRepository, times(1)).existsByEmployeeCode("EMP001");
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    @DisplayName("Should throw BadRequestException when email already exists")
    void testCreateEmployee_DuplicateEmail_ThrowsBadRequest() {
        // Given
        EmployeeRequest request = buildEmployeeRequest("John", "Doe", "john.doe@example.com", "EMP001", 1L);

        when(employeeRepository.existsByEmployeeCode("EMP001")).thenReturn(false);
        when(employeeRepository.existsByEmail("john.doe@example.com")).thenReturn(true);

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> employeeService.createEmployee(request));

        assertTrue(exception.getMessage().contains("john.doe@example.com"));
        verify(employeeRepository, times(1)).existsByEmail("john.doe@example.com");
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when department not found during employee creation")
    void testCreateEmployee_DepartmentNotFound_ThrowsException() {
        // Given
        EmployeeRequest request = buildEmployeeRequest("John", "Doe", "john.doe@example.com", "EMP001", 99L);

        when(employeeRepository.existsByEmployeeCode("EMP001")).thenReturn(false);
        when(employeeRepository.existsByEmail("john.doe@example.com")).thenReturn(false);
        when(departmentRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> employeeService.createEmployee(request));

        assertTrue(exception.getMessage().contains("99"));
        verify(departmentRepository, times(1)).findById(99L);
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    // ========== Get Employee By ID Tests ==========

    @Test
    @DisplayName("Should return employee when found by ID")
    void testGetEmployeeById_Success() {
        // Given
        Department department = buildDepartment(1L, "Engineering", "ENG");
        Employee employee = buildEmployee(1L, "John", "Doe", "john.doe@example.com", "EMP001", department);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        // When
        EmployeeResponse response = employeeService.getEmployeeById(1L);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("John", response.getFirstName());
        assertEquals("Doe", response.getLastName());
        assertEquals("john.doe@example.com", response.getEmail());

        verify(employeeRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when employee not found by ID")
    void testGetEmployeeById_NotFound_ThrowsException() {
        // Given
        when(employeeRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> employeeService.getEmployeeById(99L));

        assertTrue(exception.getMessage().contains("99"));
        verify(employeeRepository, times(1)).findById(99L);
    }

    // ========== Get All Employees Tests ==========

    @Test
    @DisplayName("Should return all employees successfully")
    void testGetAllEmployees_Success() {
        // Given
        Department department = buildDepartment(1L, "Engineering", "ENG");
        Employee emp1 = buildEmployee(1L, "John", "Doe", "john.doe@example.com", "EMP001", department);
        Employee emp2 = buildEmployee(2L, "Jane", "Smith", "jane.smith@example.com", "EMP002", department);

        when(employeeRepository.findAll()).thenReturn(List.of(emp1, emp2));

        // When
        List<EmployeeResponse> responses = employeeService.getAllEmployees();

        // Then
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals("John", responses.get(0).getFirstName());
        assertEquals("Jane", responses.get(1).getFirstName());

        verify(employeeRepository, times(1)).findAll();
    }

    // ========== Update Employee Tests ==========

    @Test
    @DisplayName("Should update employee successfully")
    void testUpdateEmployee_Success() {
        // Given
        Long employeeId = 1L;
        Department department = buildDepartment(1L, "Engineering", "ENG");
        Employee existingEmployee = buildEmployee(employeeId, "John", "Doe", "john.doe@example.com", "EMP001", department);
        EmployeeRequest updateRequest = buildEmployeeRequest("John", "Updated", "john.updated@example.com", "EMP001", 1L);

        Department updatedDepartment = buildDepartment(1L, "Engineering", "ENG");
        Employee updatedEmployee = buildEmployee(employeeId, "John", "Updated", "john.updated@example.com", "EMP001", updatedDepartment);

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(existingEmployee));
        when(employeeRepository.findByEmployeeCode("EMP001")).thenReturn(Optional.of(existingEmployee));
        when(employeeRepository.findByEmail("john.updated@example.com")).thenReturn(Optional.empty());
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(updatedDepartment));
        when(employeeRepository.save(any(Employee.class))).thenReturn(updatedEmployee);

        // When
        EmployeeResponse response = employeeService.updateEmployee(employeeId, updateRequest);

        // Then
        assertNotNull(response);
        assertEquals(employeeId, response.getId());
        assertEquals("Updated", response.getLastName());
        assertEquals("john.updated@example.com", response.getEmail());

        verify(employeeRepository, times(1)).findById(employeeId);
        verify(departmentRepository, times(1)).findById(1L);
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    // ========== Delete Employee Tests ==========

    @Test
    @DisplayName("Should delete employee successfully")
    void testDeleteEmployee_Success() {
        // Given
        Long employeeId = 1L;
        Department department = buildDepartment(1L, "Engineering", "ENG");
        Employee employee = buildEmployee(employeeId, "John", "Doe", "john.doe@example.com", "EMP001", department);

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        doNothing().when(employeeRepository).delete(employee);

        // When
        employeeService.deleteEmployee(employeeId);

        // Then
        verify(employeeRepository, times(1)).findById(employeeId);
        verify(employeeRepository, times(1)).delete(employee);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent employee")
    void testDeleteEmployee_NotFound_ThrowsException() {
        // Given
        when(employeeRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> employeeService.deleteEmployee(99L));

        assertTrue(exception.getMessage().contains("99"));
        verify(employeeRepository, times(1)).findById(99L);
        verify(employeeRepository, never()).delete(any(Employee.class));
    }

    // ========== Search Employees Tests ==========

    @Test
    @DisplayName("Should search employees by keyword successfully")
    void testSearchEmployees_Success() {
        // Given
        String keyword = "John";
        Department department = buildDepartment(1L, "Engineering", "ENG");
        Employee emp1 = buildEmployee(1L, "John", "Doe", "john.doe@example.com", "EMP001", department);

        when(employeeRepository.searchEmployees(keyword)).thenReturn(List.of(emp1));

        // When
        List<EmployeeResponse> responses = employeeService.searchEmployees(keyword);

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("John", responses.get(0).getFirstName());

        verify(employeeRepository, times(1)).searchEmployees(keyword);
    }
}
