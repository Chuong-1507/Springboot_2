package com.example.springboot_2.service.Student;

import com.example.springboot_2.model.Student.Department;
import com.example.springboot_2.repository.Student.DepartmentRepository;

import java.util.List;

public class DepartmentService {
    private final DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

   public List<Department> getAllDepartments(){
        return departmentRepository.findAll();
   }
}
