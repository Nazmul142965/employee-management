package com.example.employeemanagement.config;

import com.example.employeemanagement.model.AppUser;
import com.example.employeemanagement.model.Employee;
import com.example.employeemanagement.repository.AppUserRepository;
import com.example.employeemanagement.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Seeds a default ADMIN user and initial sample employees on startup
 * if collections are empty, so the system is ready to use immediately.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final AppUserRepository appUserRepository;
    private final EmployeeRepository employeeRepository;
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

        if (employeeRepository.count() == 0) {
            Employee e1 = new Employee(null, "Nazmul Hasan", "nazmul@company.com", "01712345678", "Engineering", "Senior Software Engineer", new BigDecimal("85000.00"), LocalDate.now().minusYears(1), true);
            Employee e2 = new Employee(null, "Amina Rahman", "amina.rahman@company.com", "01812345678", "Human Resources", "HR Manager", new BigDecimal("65000.00"), LocalDate.now().minusMonths(8), true);
            Employee e3 = new Employee(null, "Tariqul Islam", "tariqul@company.com", "01912345678", "Design", "UI/UX Designer", new BigDecimal("60000.00"), LocalDate.now().minusMonths(4), true);
            employeeRepository.saveAll(List.of(e1, e2, e3));
            log.info("Seeded initial sample employees into MongoDB.");
        }
    }
}
