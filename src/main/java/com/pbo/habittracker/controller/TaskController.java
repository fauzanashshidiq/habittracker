package com.pbo.habittracker.controller;

import com.pbo.habittracker.model.Task;
import com.pbo.habittracker.model.User;
import com.pbo.habittracker.repository.TaskRepository;
import com.pbo.habittracker.repository.UserRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;

@Controller
@RequestMapping("/tugas")
public class TaskController {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskController(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/tambah")
    public String formTambah(Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        return "tugas_tambah";
    }

    @PostMapping("/tambah")
    public String simpanTugas(
            @RequestParam String judul,
            @RequestParam String deskripsi,
            @RequestParam String tanggalDeadline,
            @RequestParam String waktuDeadline,
            Principal principal
    ) {
        Task t = new Task();
        t.setJudul(judul);
        t.setDeskripsi(deskripsi);
        t.setTanggalDeadline(LocalDate.parse(tanggalDeadline));
        t.setWaktuDeadline(LocalTime.parse(waktuDeadline));

        User currentUser = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        t.setUser(currentUser);

        taskRepository.save(t);

        return "redirect:/home";
    }
}
