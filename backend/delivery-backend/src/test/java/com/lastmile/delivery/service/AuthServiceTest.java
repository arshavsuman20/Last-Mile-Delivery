package com.lastmile.delivery.service;

import com.lastmile.delivery.dto.LoginRequest;
import com.lastmile.delivery.dto.LoginResponse;
import com.lastmile.delivery.dto.RegisterRequest;
import com.lastmile.delivery.entity.User;
import com.lastmile.delivery.repository.DeliveryAgentRepository;
import com.lastmile.delivery.repository.UserRepository;
import com.lastmile.delivery.repository.ZoneRepository;
import com.lastmile.delivery.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private DeliveryAgentRepository deliveryAgentRepository;

    @Mock
    private ZoneRepository zoneRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .name("Test User")
                .email("test@example.com")
                .password("encoded_pass")
                .role(User.Role.CUSTOMER)
                .build();
    }

    @Test
    void testSuccessfulLogin() {
        LoginRequest request = new LoginRequest("test@example.com", "password123");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "encoded_pass")).thenReturn(true);
        when(jwtService.generateToken("test@example.com", "CUSTOMER")).thenReturn("mocked-jwt-token");

        LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("mocked-jwt-token", response.getToken());
        assertEquals("CUSTOMER", response.getRole());
        assertEquals(1L, response.getUserId());
    }

    @Test
    void testLoginInvalidPassword() {
        LoginRequest request = new LoginRequest("test@example.com", "wrongpass");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongpass", "encoded_pass")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> authService.login(request));
    }

    @Test
    void testRegisterNewCustomer() {
        RegisterRequest request = RegisterRequest.builder()
                .name("New User")
                .email("new@example.com")
                .password("password123")
                .role(User.Role.CUSTOMER)
                .build();

        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encoded_pass");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(2L);
            return u;
        });
        when(jwtService.generateToken("new@example.com", "CUSTOMER")).thenReturn("new-jwt-token");

        LoginResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("new-jwt-token", response.getToken());
        assertEquals("CUSTOMER", response.getRole());
        assertEquals(2L, response.getUserId());
    }
}
