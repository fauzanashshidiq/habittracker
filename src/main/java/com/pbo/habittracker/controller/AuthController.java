package com.pbo.habittracker.controller;

import com.pbo.habittracker.model.Habit;
import com.pbo.habittracker.model.Task;
import com.pbo.habittracker.model.User;
import com.pbo.habittracker.service.HabitCompletionService;
import com.pbo.habittracker.service.HabitService;
import com.pbo.habittracker.service.UserService;
import com.pbo.habittracker.service.TaskService;
import jakarta.servlet.http.HttpSession;

import java.security.Principal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

@Controller
public class AuthController {

    private final UserService userService;
    private final HabitService habitService;
    private final TaskService taskService;

    @Autowired
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
public String home(
        @RequestParam(value = "date", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate date,
        @RequestParam(value = "start", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate start,
        Model model,
        Principal principal
) {
    LocalDate today = LocalDate.now();
    LocalDate selectedDate = (date != null) ? date : today;
    LocalDate startDate = (start != null) ? start : today.minusDays(3);

    String username = principal.getName();

    List<Habit> habits = habitService.getHabitsByUserAndDate(username, selectedDate);
    List<Task> semuaTugas = taskService.findAllByUser(username);

    LocalDate hariIni = LocalDate.now();

    List<Task> overdueTasks = new ArrayList<>();
    List<Task> hariIniTasks = new ArrayList<>();
    List<Task> selectedDateTasks = new ArrayList<>();
    List<Task> futureTasks = new ArrayList<>();

    for (Task t : semuaTugas) {
        if (t.getTanggalDeadline().isBefore(hariIni) && !t.isSelesai()) {
            overdueTasks.add(t);
        } 
        if (t.getTanggalDeadline().isEqual(hariIni)) {
            hariIniTasks.add(t);
        }
        if (t.getTanggalDeadline().isEqual(selectedDate)) {
            selectedDateTasks.add(t);
        }
        if (t.getTanggalDeadline().isAfter(hariIni) && !t.isSelesai()) {
            futureTasks.add(t);
        }
    }

    model.addAttribute("today", today);
    model.addAttribute("selectedDate", selectedDate);
    model.addAttribute("startDate", startDate);
    model.addAttribute("prevWeek", startDate.minusDays(7));
    model.addAttribute("nextWeek", startDate.plusDays(7));
    model.addAttribute("days", generate7Days(startDate));
    model.addAttribute("habits", habits);
    model.addAttribute("todayTasks", hariIniTasks);
    model.addAttribute("selectedDateTasks", selectedDateTasks);
    model.addAttribute("overdueTasks", overdueTasks);
    model.addAttribute("futureTasks", futureTasks);
    model.addAttribute("selectedDate", selectedDate);
    model.addAttribute("today", hariIni);
    model.addAttribute("username", username);

    Map<Long, Long> sisaMingguan = new HashMap<>();
Map<Long, Long> sisaBulanan = new HashMap<>();

for (Habit h : habits) {
    long total;
    long done;

    if ("Mingguan".equals(h.getFrekuensi())) {
        total = 7;
        done = habitCompletionService.countByHabitAndTanggalSelesaiBetween(
            h,
            selectedDate.minusDays(6),
            selectedDate
        );
        sisaMingguan.put(h.getId(), total - done);
    }
    if ("Bulanan".equals(h.getFrekuensi())) {
        YearMonth ym = YearMonth.from(selectedDate);
        total = ym.lengthOfMonth();
        done = habitCompletionService.countByHabitAndTanggalSelesaiBetween(
            h,
            ym.atDay(1),
            ym.atEndOfMonth()
        );
        sisaBulanan.put(h.getId(), total - done);
    }
}
model.addAttribute("sisaMingguan", sisaMingguan);
model.addAttribute("sisaBulanan", sisaBulanan);

    return "home";
}

@Autowired
private HabitCompletionService habitCompletionService;

@PostMapping("/kebiasaan/selesai/{id}")
public String toggleChecklist(
        @PathVariable Long id,
        @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
        @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
        Principal principal
) {
    Habit habit = habitService.findById(id);

    if (habit == null || !habit.getUser().getUsername().equals(principal.getName())) {
        throw new RuntimeException("Habit tidak ditemukan atau bukan milik Anda.");
    }

    boolean sudahAda = habitCompletionService.isDoneOnDate(habit, date);

    if (sudahAda) {
        // Kalau sudah ada, hapus (cancel checklist)
        habitCompletionService.deleteByHabitAndDate(habit, date);
    } else {
        // Kalau belum, insert baru
        habitCompletionService.markHabitDoneToday(habit, date);
    }

    return "redirect:/home?date=" + date + "&start=" + start;
}

private List<LocalDate> generate7Days(LocalDate startDate) {
    List<LocalDate> days = new ArrayList<>();
    for (int i = 0; i < 7; i++) {
        days.add(startDate.plusDays(i));
    }
    return days;
}

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
