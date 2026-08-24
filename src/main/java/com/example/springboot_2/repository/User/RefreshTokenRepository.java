package com.example.springboot_2.repository.User;

import com.example.springboot_2.model.User.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUser_Id(Long userId); //Dùng khi logout hoặc cấp lại token mới
}
