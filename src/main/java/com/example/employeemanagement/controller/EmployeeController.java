package com.example.employeemanagement.controller;

import com.example.employeemanagement.model.Employee;
import com.example.employeemanagement.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    // LIST (Admin + User can view)
    @GetMapping
    public String listEmployees(Model model, @RequestParam(required = false) String keyword) {
        if (keyword != null && !keyword.isBlank()) {
            model.addAttribute("employees", employeeService.searchByName(keyword));
            model.addAttribute("keyword", keyword);
        } else {
            model.addAttribute("employees", employeeService.getAllEmployees());
        }
        return "employees/list";
    }

    // NEW FORM (Admin only - enforced in SecurityConfig)
    @GetMapping("/new")
    public String newEmployeeForm(Model model) {
        Employee employee = new Employee();
        employee.setJoiningDate(LocalDate.now());
        model.addAttribute("employee", employee);
        model.addAttribute("isEdit", false);
        return "employees/form";
    }

    // CREATE
    @PostMapping
    public String createEmployee(@Valid @ModelAttribute("employee") Employee employee,
                                  BindingResult result,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("isEdit", false);
            return "employees/form";
        }
        try {
            employeeService.createEmployee(employee);
            redirectAttributes.addFlashAttribute("successMessage", "Employee added successfully!");
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("isEdit", false);
            return "employees/form";
        }
        return "redirect:/employees";
    }

    // EDIT FORM (Admin only)
    @GetMapping("/edit/{id}")
    public String editEmployeeForm(@PathVariable String id, Model model) {
        model.addAttribute("employee", employeeService.getEmployeeById(id));
        model.addAttribute("isEdit", true);
        return "employees/form";
    }

    // UPDATE
    @PostMapping("/edit/{id}")
    public String updateEmployee(@PathVariable String id,
                                  @Valid @ModelAttribute("employee") Employee employee,
                                  BindingResult result,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("isEdit", true);
            return "employees/form";
        }
        try {
            employeeService.updateEmployee(id, employee);
            redirectAttributes.addFlashAttribute("successMessage", "Employee updated successfully!");
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("isEdit", true);
            return "employees/form";
        }
        return "redirect:/employees";
    }

    // DELETE (Admin only)
    @PostMapping("/delete/{id}")
    public String deleteEmployee(@PathVariable String id, RedirectAttributes redirectAttributes) {
        employeeService.deleteEmployee(id);
        redirectAttributes.addFlashAttribute("successMessage", "Employee deleted successfully!");
        return "redirect:/employees";
    }

    // VIEW single employee detail
    @GetMapping("/{id}")
    public String viewEmployee(@PathVariable String id, Model model) {
        model.addAttribute("employee", employeeService.getEmployeeById(id));
        return "employees/view";
    }
}
