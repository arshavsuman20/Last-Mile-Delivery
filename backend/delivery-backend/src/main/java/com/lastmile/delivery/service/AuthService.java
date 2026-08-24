package com.lastmile.delivery.service;

import com.lastmile.delivery.dto.LoginRequest;
import com.lastmile.delivery.dto.LoginResponse;
import com.lastmile.delivery.dto.RegisterRequest;
import com.lastmile.delivery.entity.DeliveryAgent;
import com.lastmile.delivery.entity.User;
import com.lastmile.delivery.entity.Zone;
import com.lastmile.delivery.repository.DeliveryAgentRepository;
import com.lastmile.delivery.repository.UserRepository;
import com.lastmile.delivery.repository.ZoneRepository;
import com.lastmile.delivery.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final DeliveryAgentRepository deliveryAgentRepository;
    private final ZoneRepository zoneRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            DeliveryAgentRepository deliveryAgentRepository,
            ZoneRepository zoneRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.deliveryAgentRepository = deliveryAgentRepository;
        this.zoneRepository = zoneRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid email or password"));

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            throw new IllegalArgumentException("Invalid email or password");
        }

        String token = jwtService.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

        return new LoginResponse(
                token,
                user.getRole().name(),
                user.getId()
        );
    }

    @Transactional
    public LoginResponse register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User with email already exists");
        }

        User.Role role = request.getRole() != null ? request.getRole() : User.Role.CUSTOMER;

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .build();

        User savedUser = userRepository.save(user);

        if (role == User.Role.DELIVERY_AGENT) {
            Zone zone = null;
            if (request.getZoneId() != null) {
                zone = zoneRepository.findById(request.getZoneId()).orElse(null);
            }
            if (zone == null) {
                zone = zoneRepository.findAll().stream().findFirst().orElse(null);
            }

            DeliveryAgent agent = DeliveryAgent.builder()
                    .user(savedUser)
                    .zone(zone)
                    .available(true)
                    .latitude(12.9716)
                    .longitude(77.5946)
                    .build();

            deliveryAgentRepository.save(agent);
        }

        String token = jwtService.generateToken(
                savedUser.getEmail(),
                savedUser.getRole().name()
        );

        return new LoginResponse(
                token,
                savedUser.getRole().name(),
                savedUser.getId()
        );
    }
}