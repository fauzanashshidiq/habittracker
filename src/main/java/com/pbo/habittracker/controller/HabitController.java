package com.pbo.habittracker.controller;

import com.pbo.habittracker.model.Habit;
import com.pbo.habittracker.model.User;
import com.pbo.habittracker.repository.HabitRepository;
import com.pbo.habittracker.repository.UserRepository;
import com.pbo.habittracker.service.HabitCompletionService;
import com.pbo.habittracker.service.HabitService;
import com.pbo.habittracker.service.UserService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/kebiasaan")
public class HabitController {

    private final HabitRepository habitRepository;
    private final UserRepository userRepository;
    private final HabitService habitService;
    private final HabitCompletionService habitCompletionService;
    private final UserService userService;

    public HabitController(HabitRepository habitRepository, UserRepository userRepository, HabitService habitService, HabitCompletionService habitCompletionService, UserService userService) {
        this.habitRepository = habitRepository;
        this.userRepository = userRepository;
        this.habitService = habitService;
        this.habitCompletionService = habitCompletionService;
        this.userService = userService;
    }

    @GetMapping("/tambah")
    public String formTambah(Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        model.addAttribute("habit", new Habit());
        return "form_habit";
    }

    @PostMapping("/tambah")
    public String simpanHabit(
            @RequestParam String nama,
            @RequestParam String frekuensi,
            @RequestParam String tanggalMulai,
            @RequestParam(required = false) String tanggalSelesai,
            @RequestParam String waktu,
            Principal principal
    ) {
        Habit h = new Habit();
        h.setNama(nama);
        h.setFrekuensi(frekuensi);
        h.setTanggalMulai(LocalDate.parse(tanggalMulai));

        if (tanggalSelesai != null && !tanggalSelesai.isBlank()) {
            h.setTanggalSelesai(LocalDate.parse(tanggalSelesai));
        } else {
            h.setTanggalSelesai(null);
        }
        h.setWaktu(LocalTime.parse(waktu));

        // Ambil user yang sedang login
        User currentUser = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        h.setUser(currentUser);

        habitRepository.save(h);

        return "redirect:/home";
    }


    @GetMapping("/edit/{id}")
public String editHabitForm(@PathVariable Long id, Model model, Principal principal,
                            @RequestParam(value = "redirectPage", defaultValue = "home") String redirectPage) {
    model.addAttribute("username", principal.getName());
    Habit habit = habitRepository.findById(id).orElse(null);
    if (habit == null) {
        return "redirect:/home";
    }
    model.addAttribute("habit", habit);
    model.addAttribute("redirectPage", redirectPage);
    return "form_habit";
}



@PostMapping("/update/{id}")
public String updateHabit(
        @PathVariable Long id,
        @ModelAttribute Habit habitBaru,
        @RequestParam(value = "redirectPage", required = false) String redirectPage
) {
    Habit h = habitRepository.findById(id).orElseThrow();
    h.setNama(habitBaru.getNama());
    h.setFrekuensi(habitBaru.getFrekuensi());
    h.setTanggalMulai(habitBaru.getTanggalMulai());
    h.setTanggalSelesai(habitBaru.getTanggalSelesai());
    h.setWaktu(habitBaru.getWaktu());
    habitRepository.save(h);

    // Redirect ke halaman sesuai asal (default ke /home)
    return "redirect:/" + (redirectPage != null ? redirectPage : "home");
}


@PostMapping("hapus/{id}")
public String deleteHabit(
        @PathVariable Long id,
        @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
        @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
        @RequestParam(value = "redirectPage", required = false) String redirectPage,
        Principal principal
) {
    Habit habit = habitService.findById(id);
    if (habit == null || !habit.getUser().getUsername().equals(principal.getName())) {
        throw new RuntimeException("Habit tidak ditemukan atau bukan milik Anda.");
    }

    habitService.deleteHabit(habit);

    if ("kebiasaan".equals(redirectPage)) {
        return "redirect:/kebiasaan";
    }

    return "redirect:/home?date=" + date + "&start=" + start;
}


@GetMapping("")
public String kebiasaanSelesai(
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

    User user = userService.findByUsername(username); // bukan new User() ya
List<Habit> completedHabits = habitService.getCompletedHabits(user);



    Map<Long, Long> sisaMingguan = new HashMap<>();
    Map<Long, Long> sisaBulanan = new HashMap<>();

    for (Habit h : completedHabits) {
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

    model.addAttribute("habits", completedHabits);
    model.addAttribute("sisaMingguan", sisaMingguan);
    model.addAttribute("sisaBulanan", sisaBulanan);
    model.addAttribute("username", username);
    model.addAttribute("selectedDate", selectedDate);
    model.addAttribute("startDate", startDate);
    model.addAttribute("today", today);

    return "kebiasaan";
}

}
