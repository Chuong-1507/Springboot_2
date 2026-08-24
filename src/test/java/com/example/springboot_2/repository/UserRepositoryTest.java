package com.example.springboot_2.repository;

import com.example.springboot_2.model.User.Role;
import com.example.springboot_2.model.User.User;
import com.example.springboot_2.repository.User.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@ActiveProfiles("test") // dunghf để báo Spring dùng file application-test.properties vừa tạo
public class
UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByUsername_whenUserExists_shouldReturnUser(){
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("encoded-password-123");
        user.setRoles(Set.of(Role.USER));

        userRepository.save(user);

        Optional<User> found = userRepository.findByUsername("testuser");

        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("testuser");
    }

    @Test
    void findByUsername_whenUserDoesNotExist_shouldReturnEmpty(){
        Optional<User> found = userRepository.findByUsername("nontestuser");
        assertThat(found).isEmpty();
    }

    @Test
    void findByUsername_shouldBeCaseSensitive(){
        User user = new User();
        user.setUsername("TestUser");
        user.setPassword("encoded-password-123");
        user.setRoles(Set.of(Role.USER));

//        user.setUsername(user.getUsername().toLowerCase());
        userRepository.save(user);


        //Kiểm tra tìm bằng chữ thường có khớp với chữ hoa đã lưu khng
        Optional<User> found = userRepository.findByUsername("testuser");

        //H2 mặc định phân biệt hoa/thường -> nếu code lưu "TestUser" thì "testuser" không thấy
        assertThat(found).isEmpty();
    }
}
