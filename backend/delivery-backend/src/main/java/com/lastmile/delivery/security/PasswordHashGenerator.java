package com.lastmile.delivery.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator {

    public static void main(String[] args) {

        BCryptPasswordEncoder encoder =
                new BCryptPasswordEncoder();

        System.out.println("customer: " +
                encoder.encode("password123"));

        System.out.println("agent: " +
                encoder.encode("password123"));
    }
}