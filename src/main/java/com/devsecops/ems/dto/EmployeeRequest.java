package com.devsecops.ems.dto;

import com.devsecops.ems.entity.EmployeeStatus;
import com.devsecops.ems.entity.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeRequest {

    @NotBlank(message = "Employee code is required")
    @Size(min = 2, max = 20, message = "Employee code must be between 2 and 20 characters")
    private String employeeCode;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    private String email;

    @Size(max = 20, message = "Phone must not exceed 20 characters")
    private String phone;

    @NotNull(message = "Gender is required")
    private Gender gender;

    private LocalDate dateOfBirth;

    @NotNull(message = "Joining date is required")
    private LocalDate joiningDate;

    @Size(max = 100, message = "Designation must not exceed 100 characters")
    private String designation;

    @Positive(message = "Salary must be a positive value")
    private BigDecimal salary;

    @NotNull(message = "Status is required")
    private EmployeeStatus status;

    @NotNull(message = "Department ID is required")
    @Positive(message = "Department ID must be a positive value")
    private Long departmentId;
}
