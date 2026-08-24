package com.example.springboot_2.security;

import com.example.springboot_2.model.User.RefreshToken;
import com.example.springboot_2.model.User.User;
import com.example.springboot_2.repository.User.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.sql.Ref;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private static final long REFRESH_TOKEN_DURATION = 1000L * 60 * 60 * 24 * 7; //7 ngày

    @Transactional
    public RefreshToken createRefreshToken(User user) {
        //Xóa refresh token cũ của user này (nếu có) - mỗi user chỉ giữ 1 refresh token còn hiệu lực
        refreshTokenRepository.deleteByUser_Id(user.getId());

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString()); // chuỗi random đủ khó đoán, không cần JWT
        refreshToken.setExpiryDate(Instant.now().plusMillis(REFRESH_TOKEN_DURATION));

        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token expired");
        }
        return token;
    }


    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }
}