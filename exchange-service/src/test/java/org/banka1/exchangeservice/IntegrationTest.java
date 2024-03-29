package org.banka1.exchangeservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redis.testcontainers.RedisContainer;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import javax.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.util.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
//@AutoConfigureTestDatabase
@Transactional
@TestPropertySource(value = "/application-test_it.properties")
@AutoConfigureMockMvc
@ActiveProfiles("test_it")
public abstract class IntegrationTest {

    @LocalServerPort
    int port;

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Value("${jwt.secret}")
    protected String SECRET_KEY;

    @Container
    private static final RedisContainer REDIS_CONTAINER =
            new RedisContainer(DockerImageName.parse("redis:7.0.11-alpine")).withExposedPorts(6379);

    @DynamicPropertySource
    private static void registerRedisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.redis.host", REDIS_CONTAINER::getHost);
        registry.add("spring.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379).toString());
    }

    protected String getToken() {
        List<String> role = new ArrayList<>();
        role.add("ROLE_ADMIN");

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", 1L);
        claims.put("roles", role);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject("admin@admin.com")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY.getBytes(StandardCharsets.UTF_8)).compact();
    }
}
