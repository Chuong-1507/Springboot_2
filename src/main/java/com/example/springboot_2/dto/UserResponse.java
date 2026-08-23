package com.example.springboot_2.service.User;

import com.example.springboot_2.model.User.Role;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private Set<Role> roles = new HashSet<>();


}

