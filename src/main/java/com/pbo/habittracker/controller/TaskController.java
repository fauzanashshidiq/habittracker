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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        @RequestParam(required = false) String redirectPage,
        Principal principal
) {
    Task task = taskService.findById(id);

    if (!task.getUser().getUsername().equals(principal.getName())) {
        throw new RuntimeException("Task bukan milik Anda");
    }

    taskService.delete(task);

    if ("tugas".equals(redirectPage)) {
        return "redirect:/tugas?selesai=" + task.isSelesai();
    }

    return "redirect:/home";
}


@PostMapping("/selesai/{id}")
public String toggleSelesai(
        @PathVariable Long id,
        @RequestParam(required = false) String date,
        @RequestParam(required = false) String start,
        @RequestParam(required = false) String redirectPage,
        Principal principal
) {
    Task task = taskService.findById(id);

    if (!task.getUser().getUsername().equals(principal.getName())) {
        throw new RuntimeException("Task bukan milik Anda");
    }

    task.setSelesai(!task.isSelesai());
    taskService.save(task);

    if ("tugas".equals(redirectPage)) {
        return "redirect:/tugas?selesai=" + task.isSelesai();
    }

    // default redirect ke home
    String redirectUrl = "redirect:/home?date=" + date;
    if (start != null && !start.isEmpty()) {
        redirectUrl += "&start=" + start;
    }
    return redirectUrl;
}



@GetMapping("/edit/{id}")
public String formEdit(
        @PathVariable Long id,
        @RequestParam(required = false) String redirectPage,
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
    model.addAttribute("redirectPage", redirectPage);

    return "form_task";
}

@PostMapping("/edit/{id}")
public String simpanEdit(
        @PathVariable Long id,
        @RequestParam String judul,
        @RequestParam String deskripsi,
        @RequestParam String tanggalDeadline,
        @RequestParam String waktuDeadline,
        @RequestParam(required = false) String redirectPage,
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

    if ("tugas".equals(redirectPage)) {
        return "redirect:/tugas?selesai=" + task.isSelesai();
    }

    return "redirect:/home";
}

@GetMapping("")
    public String daftarTugas(
        @RequestParam(value = "selesai", defaultValue = "false") boolean selesai,
        Model model,
        Principal principal
) {
    String username = principal.getName();
    LocalDate hariIni = LocalDate.now();

    List<Task> semuaTugas = taskService.findAllByUser(username);

    if (!selesai) {
        List<Task> overdueTasks = new ArrayList<>();
        List<Task> pendingTasks = new ArrayList<>();

        for (Task t : semuaTugas) {
            if (!t.isSelesai()) {
                if (t.getTanggalDeadline().isBefore(hariIni)) {
                    overdueTasks.add(t);
                } else {
                    pendingTasks.add(t);
                }
            }
        }

        model.addAttribute("overdueTasks", overdueTasks);
        model.addAttribute("pendingTasks", pendingTasks);
    } else {
        List<Task> completedTasks = semuaTugas.stream()
                .filter(Task::isSelesai)
                .collect(Collectors.toList());

        model.addAttribute("completedTasks", completedTasks);
    }

    model.addAttribute("selesai", selesai);
    model.addAttribute("username", username);

    return "task";
}
}
