package com.pbo.habittracker.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String judul;
    private String deskripsi;

    private LocalDate tanggalDeadline;
    private LocalTime waktuDeadline;

    @ManyToOne
    private User user; // relasi ke user login

    // Getter & Setter
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getJudul() {
        return judul;
    }
    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getDeskripsi() {
        return deskripsi;
    }
    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public LocalDate getTanggalDeadline() {
        return tanggalDeadline;
    }
    public void setTanggalDeadline(LocalDate tanggalDeadline) {
        this.tanggalDeadline = tanggalDeadline;
    }

    public LocalTime getWaktuDeadline() {
        return waktuDeadline;
    }
    public void setWaktuDeadline(LocalTime waktuDeadline) {
        this.waktuDeadline = waktuDeadline;
    }

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
}
