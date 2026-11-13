package com.example.employee_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.employee_service.dto.DepartmentResponseDTO;

@FeignClient(name = "department-service", url = "${department.service.url}")
public interface DepartmentClient {

    @GetMapping("/api/departments/{id}")
    DepartmentResponseDTO getDepartmentById(@PathVariable("id") Long id);
}