package com.example.springboot_2.model.User;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="Users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true,nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;


    @ElementCollection(fetch = FetchType.EAGER)          // tạo bảng phụ riêng
    @CollectionTable(
            name = "user_roles",                          // tên bảng phụ
            joinColumns = @JoinColumn(name = "user_id")    // cột khoá ngoại trỏ về users.id
    )

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Set<Role> roles = new HashSet<>();


}
