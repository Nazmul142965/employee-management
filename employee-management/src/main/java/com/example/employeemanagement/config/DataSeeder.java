package com.example.employeemanagement.config;

import com.example.employeemanagement.model.AppUser;
import com.example.employeemanagement.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Seeds a default ADMIN user on startup if the users collection is empty,
 * so you always have a way to log in on a fresh MongoDB database.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Override
    public void run(String... args) {
        if (!appUserRepository.existsByUsername(adminUsername)) {
            AppUser admin = new AppUser();
            admin.setUsername(adminUsername);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setEmail(adminEmail);
            admin.setRole("ROLE_ADMIN");
            admin.setEnabled(true);
            appUserRepository.save(admin);
            log.info("Seeded default admin user -> username: {}, password: {}", adminUsername, adminPassword);
        } else {
            log.info("Admin user already exists, skipping seed.");
        }
    }
}
