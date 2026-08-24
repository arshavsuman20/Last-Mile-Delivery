package com.lastmile.delivery.dto;

import com.lastmile.delivery.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email address format")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    private String phone;

    private User.Role role; // CUSTOMER, DELIVERY_AGENT, ADMIN

    private Long zoneId; // Optional: Zone ID for Delivery Agent registration
}
