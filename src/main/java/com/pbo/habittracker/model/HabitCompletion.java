package com.pbo.habittracker.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class HabitCompletion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "habit_id")
    private Habit habit;

    private LocalDate tanggalSelesai;

    // getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Habit getHabit() { return habit; }
    public void setHabit(Habit habit) { this.habit = habit; }

    public LocalDate getTanggalSelesai() { return tanggalSelesai; }
    public void setTanggalSelesai(LocalDate tanggalSelesai) { this.tanggalSelesai = tanggalSelesai; }

    @Transient
private String tanggalSelesaiFormatted;

public String getTanggalSelesaiFormatted() {
    return tanggalSelesaiFormatted;
}

public void setTanggalSelesaiFormatted(String tanggalSelesaiFormatted) {
    this.tanggalSelesaiFormatted = tanggalSelesaiFormatted;
}

}
