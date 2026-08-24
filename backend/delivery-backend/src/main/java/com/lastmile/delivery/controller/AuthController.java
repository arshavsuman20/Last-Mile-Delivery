package com.lastmile.delivery.controller;

import com.lastmile.delivery.dto.LoginRequest;
import com.lastmile.delivery.dto.LoginResponse;
import com.lastmile.delivery.dto.RegisterRequest;
import com.lastmile.delivery.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody @Valid LoginRequest request) {

        return ResponseEntity.ok(
                authService.login(request)
        );
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(
            @RequestBody @Valid RegisterRequest request) {

        return ResponseEntity.ok(
                authService.register(request)
        );
    }
}