package com.pbo.habittracker.controller;

import com.pbo.habittracker.model.Task;
import com.pbo.habittracker.model.User;
import com.pbo.habittracker.repository.TaskRepository;
import com.pbo.habittracker.repository.UserRepository;
import com.pbo.habittracker.service.TaskService;

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
    private final TaskService taskService;

    public TaskController(TaskRepository taskRepository, UserRepository userRepository, TaskService taskService) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.taskService = taskService;
    }

    @GetMapping("/tambah")
    public String formTambah(Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        model.addAttribute("task", new Task()); // kosong
        model.addAttribute("formMode", "tambah");
        return "form_task";
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

    @PostMapping("/hapus/{id}")
public String hapusTask(
        @PathVariable Long id,
        Principal principal
) {
    Task task = taskService.findById(id);

    if (!task.getUser().getUsername().equals(principal.getName())) {
        throw new RuntimeException("Task bukan milik Anda");
    }

    taskService.delete(task);
    return "redirect:/home";
}

@PostMapping("/selesai/{id}")
public String toggleSelesai(
        @PathVariable Long id,
        @RequestParam String date,
        @RequestParam(required = false) String start,
        Principal principal
) {
    Task task = taskService.findById(id);

    if (!task.getUser().getUsername().equals(principal.getName())) {
        throw new RuntimeException("Task bukan milik Anda");
    }

    task.setSelesai(!task.isSelesai());
    taskService.save(task);

    // Tetap di hari yang dipilih
    String redirectUrl = "redirect:/home?date=" + date;
    if (start != null && !start.isEmpty()) {
        redirectUrl += "&start=" + start;
    }
    return redirectUrl;
}


@GetMapping("/edit/{id}")
public String formEdit(
        @PathVariable Long id,
        Model model,
        Principal principal
) {
    Task task = taskService.findById(id);

    if (!task.getUser().getUsername().equals(principal.getName())) {
        throw new RuntimeException("Task bukan milik Anda");
    }

    model.addAttribute("task", task);
    model.addAttribute("username", principal.getName());
    model.addAttribute("formMode", "edit");
    return "form_task";
}

@PostMapping("/edit/{id}")
public String simpanEdit(
        @PathVariable Long id,
        @RequestParam String judul,
        @RequestParam String deskripsi,
        @RequestParam String tanggalDeadline,
        @RequestParam String waktuDeadline,
        Principal principal
) {
    Task task = taskService.findById(id);

    if (!task.getUser().getUsername().equals(principal.getName())) {
        throw new RuntimeException("Task bukan milik Anda");
    }

    task.setJudul(judul);
    task.setDeskripsi(deskripsi);
    task.setTanggalDeadline(LocalDate.parse(tanggalDeadline));
    task.setWaktuDeadline(LocalTime.parse(waktuDeadline));

    taskService.save(task);

    return "redirect:/home";
}

}
