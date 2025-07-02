package com.pbo.habittracker.controller;

import com.pbo.habittracker.model.Habit;
import com.pbo.habittracker.model.Task;
import com.pbo.habittracker.model.User;
import com.pbo.habittracker.repository.HabitRepository;
import com.pbo.habittracker.repository.TaskRepository;
import com.pbo.habittracker.service.HabitService;
import com.pbo.habittracker.service.UserService;
import com.pbo.habittracker.service.TaskService;
import jakarta.servlet.http.HttpSession;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

@Controller
public class AuthController {

    private final UserService userService;
    private final HabitService habitService;
    private final TaskService taskService;

    public AuthController(UserService userService, HabitService habitService, TaskService taskService) {
        this.userService = userService;
        this.habitService = habitService;
        this.taskService = taskService;
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user) {
        userService.register(user);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/home")
    public String home(Model model, Principal principal) {
        String username = principal.getName();

        List<Habit> habits = habitService.findHabitsByUser(username);
        List<Task> tasks = taskService.findTasksByUser(username);

        model.addAttribute("username", username);
        model.addAttribute("habits", habits);
        model.addAttribute("tasks", tasks);
        model.addAttribute("selectedDate", LocalDate.now().toString());

        return "home";
    }



    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
