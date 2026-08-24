package com.example.springboot_2.model.Student;

import jakarta.persistence.*;

@Entity
public class Student {
    @Id
    private Long id;
    private String name;

    public Student() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;
}
