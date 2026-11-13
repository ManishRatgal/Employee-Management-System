package com.example.employee_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.employee_service.client.DepartmentClient;
import com.example.employee_service.dto.DepartmentResponseDTO;
import com.example.employee_service.dto.EmployeeRequestDTO;
import com.example.employee_service.dto.EmployeeResponseDTO;
import com.example.employee_service.entity.Employee;
import com.example.employee_service.exception.ResourceNotFoundException;
import com.example.employee_service.repository.EmployeeRepository;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentClient departmentClient;

    public EmployeeService(EmployeeRepository employeeRepository,
                           DepartmentClient departmentClient) {
        this.employeeRepository = employeeRepository;
        this.departmentClient = departmentClient;
    }

    // Convert Employee entity to response DTO
    private EmployeeResponseDTO mapToResponse(Employee employee) {

        DepartmentResponseDTO department =
                departmentClient.getDepartmentById(employee.getDepartmentId());

        return new EmployeeResponseDTO(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getSalary(),
                employee.getDepartmentId(),
                department.getDepartmentName(),
                department.getDepartmentCode()
        );
    }

    // Create Employee
    public EmployeeResponseDTO createEmployee(EmployeeRequestDTO dto) {

        // Verify that the department exists
        departmentClient.getDepartmentById(dto.getDepartmentId());

        Employee employee = new Employee();

        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setEmail(dto.getEmail());
        employee.setDepartmentId(dto.getDepartmentId());
        employee.setSalary(dto.getSalary());

        return mapToResponse(employeeRepository.save(employee));
    }

    // Get All Employees
    public List<EmployeeResponseDTO> getAllEmployees() {
        return employeeRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Get Employee By ID
    public EmployeeResponseDTO getEmployeeById(Long id) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee not found with ID: " + id));

        return mapToResponse(employee);
    }

    // Update Employee
    public EmployeeResponseDTO updateEmployee(Long id,
                                               EmployeeRequestDTO dto) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee not found with ID: " + id));

        departmentClient.getDepartmentById(dto.getDepartmentId());

        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setEmail(dto.getEmail());
        employee.setDepartmentId(dto.getDepartmentId());
        employee.setSalary(dto.getSalary());

        return mapToResponse(employeeRepository.save(employee));
    }

    // Delete Employee
    public void deleteEmployee(Long id) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee not found with ID: " + id));

        employeeRepository.delete(employee);
    }

    // Search by Department ID
    public List<EmployeeResponseDTO> getEmployeesByDepartment(Long departmentId) {
        return employeeRepository.findByDepartmentId(departmentId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // Search by First Name
    public List<EmployeeResponseDTO> searchEmployees(String name) {
        return employeeRepository.findByFirstNameContainingIgnoreCase(name)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
}