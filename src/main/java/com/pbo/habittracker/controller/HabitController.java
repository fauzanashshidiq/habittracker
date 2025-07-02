package com.pbo.habittracker.controller;

import com.pbo.habittracker.model.Habit;
import com.pbo.habittracker.model.User;
import com.pbo.habittracker.repository.HabitRepository;
import com.pbo.habittracker.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;

@Controller
@RequestMapping("/kebiasaan")
public class HabitController {

    private final HabitRepository habitRepository;
    private final UserRepository userRepository;

    // Constructor injection untuk kedua repository
    public HabitController(HabitRepository habitRepository, UserRepository userRepository) {
        this.habitRepository = habitRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/tambah")
    public String formTambah(Model model, Principal principal) {
        model.addAttribute("username", principal.getName());
        return "kebiasaan_tambah";
    }

    @PostMapping("/tambah")
    public String simpanHabit(
            @RequestParam String nama,
            @RequestParam String frekuensi,
            @RequestParam String tanggalMulai,
            @RequestParam(required = false) String tanggalSelesai,
            @RequestParam String waktu,
            Principal principal // 🟢 INI UNTUK AMBIL USER LOGIN
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
}
