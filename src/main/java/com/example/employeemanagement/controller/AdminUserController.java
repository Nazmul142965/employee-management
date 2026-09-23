package com.example.employeemanagement.controller;

import com.example.employeemanagement.model.AppUser;
import com.example.employeemanagement.repository.AppUserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    // List all users (ADMIN only - enforced in SecurityConfig)
    @GetMapping
    public String listUsers(Model model) {
        model.addAttribute("users", appUserRepository.findAll());
        return "admin/users";
    }

    @GetMapping("/new")
    public String newUserForm(Model model) {
        model.addAttribute("appUser", new AppUser());
        return "admin/user-form";
    }

    @PostMapping
    public String createUser(@Valid @ModelAttribute("appUser") AppUser appUser,
                              BindingResult result,
                              @RequestParam String roleChoice,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/user-form";
        }
        if (appUserRepository.existsByUsername(appUser.getUsername())) {
            model.addAttribute("errorMessage", "Username already taken");
            return "admin/user-form";
        }
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        appUser.setRole("ADMIN".equalsIgnoreCase(roleChoice) ? "ROLE_ADMIN" : "ROLE_USER");
        appUser.setEnabled(true);
        appUserRepository.save(appUser);
        redirectAttributes.addFlashAttribute("successMessage", "User created successfully!");
        return "redirect:/admin/users";
    }

    @PostMapping("/toggle/{id}")
    public String toggleUser(@PathVariable String id, RedirectAttributes redirectAttributes) {
        appUserRepository.findById(id).ifPresent(u -> {
            u.setEnabled(!u.isEnabled());
            appUserRepository.save(u);
        });
        redirectAttributes.addFlashAttribute("successMessage", "User status updated!");
        return "redirect:/admin/users";
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable String id, RedirectAttributes redirectAttributes) {
        appUserRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "User deleted!");
        return "redirect:/admin/users";
    }
}
