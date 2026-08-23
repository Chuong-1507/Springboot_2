package com.example.springboot_2.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
public class UserRepository {

    @Autowired
    private UserRepository userRepository;

    @Test
    
}
