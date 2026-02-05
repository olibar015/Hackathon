package com.bingotask;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // Generate password for "admin123"
        String adminPassword = encoder.encode("admin123");
        System.out.println("Admin password: " + adminPassword);

        // Generate password for "test123"
        String testPassword = encoder.encode("test123");
        System.out.println("Test password: " + testPassword);
    }
}
