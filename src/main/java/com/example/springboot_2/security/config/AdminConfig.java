package com.example.springboot_2.security.config;

import com.example.springboot_2.model.User.Role;
import com.example.springboot_2.model.User.User;
import com.example.springboot_2.repository.User.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;

@Profile("!test")
@Component
@RequiredArgsConstructor
public class AdminConfig implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByUsername("admin").isEmpty()){
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("123456"));
            admin.setRoles(Set.of(Role.ADMIN,Role.USER));//admin vừa là admin, vừa là user
            userRepository.save(admin);
            System.out.println("ADMIN has been created with password: 123456, please change it !");
        }
    }
}
