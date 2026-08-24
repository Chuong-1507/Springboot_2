package com.example.springboot_2.security;

import com.example.springboot_2.model.User.Role;
import com.example.springboot_2.model.User.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.WeakKeyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JwtServiceTest {
    private JwtService jwtService;
    private final String testSecret = "test-secret-key-must-be-at-least-32-bytes-long-xxxx";

    @BeforeEach// Annotation này đánh dấu method sẽ chạy trước mỗi test case
    void setUp(){
        jwtService = new JwtService();
        //set field @Value bằng tay vì không có Spring Context ở đây
        ReflectionTestUtils.setField(jwtService,"secretString",testSecret);
        //gọi thủ công @PostConstruct vì Spring không tự gọi trong unit test thuần
        jwtService.init();
    }

    @Test
    void generateToken_thenExtractUsername_shouldMatchOriginalUsername(){
        User user = new User();
        user.setUsername("testuser");
        String token = jwtService.generateToken(user);

        String username = jwtService.extractUsername(token);

        assertThat(username).isEqualTo("testuser");
        // Kiểm tra kết quả có giống username ban đầu
        // Nếu sai assertion ném AssertionError -> Fail
    }

    @Test
    // Test that a Tampered JWT TOKEN is rejected
    void isTokenValid_withTamperedToken_shouldReturnFalse(){
        User user = new User();
        user.setUsername("testuser");
        String token = jwtService.generateToken(user);
        String tamperedToken =  token.substring(0,token.length()-2) +"xx"; //sửa Signature

        boolean valid = jwtService.isTokenValid(tamperedToken);

        assertThat(valid).isFalse();
    }

    @Test
    //Test JWT signed by another Key is invalid
    void isTokenValid_withTokenSignedByDifferentSecret_ShouldReturnFalse(){
        //generate otherKey
        SecretKey otherKey = Keys.hmacShaKeyFor(
                "completely-different-secret-key-32-bytes-min-yyy".getBytes(StandardCharsets.UTF_8));

        //generate token by otherKey
        String tokenFromOtherKey = Jwts.builder()
                .subject("testuser")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 60000))
                .signWith(otherKey)
                .compact();

        //Check token Valid
        boolean valid = jwtService.isTokenValid(tokenFromOtherKey);

        assertThat(valid).isFalse();
    }

    // Test 2 token with different username
    @Test
    void isToken_withDifferentUsername(){
        User user1 = new User();
        user1.setUsername("Alice");
        User user2 = new User();
        user2.setUsername("Bob");

        String token1 = jwtService.generateToken(user1);
        String token2 = jwtService.generateToken(user2);

        assertThat(token1).isNotEqualTo(token2);
    }

    //Test token is empty or null
    @Test
    void isTokenValid_withEmptyToken_shouldReturnFalse(){
        boolean valid = jwtService.isTokenValid("");
        assertThat(valid).isFalse();
    }
    @Test
    void isTokenValid_withNullToken_shouldReturnFalse(){
        boolean valid = jwtService.isTokenValid(null);
        assertThat(valid).isFalse();
    }

    //Token with Weak SecretKey (differ in length)
    @Test
    void init_withWeakSecretKey_shouldThrowException(){
        JwtService weakJwtService = new JwtService();
        ReflectionTestUtils.setField(weakJwtService,"secretString","abvcsjdskggg");

        assertThatThrownBy(weakJwtService::init)
                .isInstanceOf(WeakKeyException.class);
    }

    //ParameterizedTest cho nhiều username khác nhau
    @ParameterizedTest
    @ValueSource(strings = {
            "testuser",
            "user@gmail.com",
            "user with space",
            "Nguyen Van A",
            "user_123-ABC"
    })
    void generateToken_thenExtractUsername_shouldMatchForVariousUsername(String username){
        User user = new User();
        user.setUsername(username);
        String token = jwtService.generateToken(user);
        String extractedUsername = jwtService.extractUsername(token);

        assertThat(extractedUsername).isEqualTo(username);
    }

    //Verify whether the roles are correctly embedded in the token
    @Test
    void generateToken_withRoles_shouldEmbeddedScopeClaimCorrectly(){
        User user = new User();
        user.setUsername("admin1");
        user.setRoles(Set.of(Role.ADMIN,Role.USER));

        String token = jwtService.generateToken(user);

        //parse token ra để đọc claim "scope" trực tiếp
            //Lấy phần Payload
        Claims claims = Jwts.parser()
                .verifyWith((SecretKey) ReflectionTestUtils.getField(jwtService,"secretKey"))
                .build()
                .parseSignedClaims(token)
                .getPayload();
            //Lấy "scope"
        String scope = claims.get("scope", String.class);

        //Vì Set không đảm bảo thứ tự, không assert nguyên chuỗi
        //mà kiểm tra scope có chứa đủ 2 role, dùng split rồi so sánh tâp hợp
        assertThat(scope.split(" ")).containsExactlyInAnyOrder("ADMIN","USER");

    }

    // Test: xác nhận lỗ hổng — token vẫn valid dù user đã đổi username / bị coi như "xóa"
    @Test
    void tokenRemainsValid_evenAfterUsernameChangedLocally_revealsDesignGap(){
        User user = new User();
        user.setUsername("user-1");
        String token = jwtService.generateToken(user);

        user.setUsername("fake-user-1");

        boolean valid = jwtService.isTokenValid(token);
        // isTokenValid chỉ check chữ ký + hạn dùng, KHÔNG check user còn tồn tại
        // => token vẫn được coi là valid dù username đã đổi -> đây là lỗ hổng thiết kế
        assertThat(valid).isTrue();
    }


}
