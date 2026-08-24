package com.example.springboot_2.controller.Student;

import com.example.springboot_2.model.Student.Department;
import com.example.springboot_2.repository.Student.DepartmentRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DepartmentController {
    private final DepartmentRepository departmentRepository;

    public DepartmentController(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    @GetMapping("/departments")
    public List<Department> getAll() {
        return departmentRepository.findAll();
    }
}
