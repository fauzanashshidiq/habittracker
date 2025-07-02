package com.pbo.habittracker.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
public class Habit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nama;
    private String frekuensi; // Harian, Mingguan, Bulanan

    private LocalDate tanggalMulai;
    private LocalDate tanggalSelesai;
    private LocalTime waktu;

    // Relasi ke User kalau mau (optional)
    @ManyToOne
    private User user;

    // getter & setter
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getNama() {
        return nama;
    }
    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getFrekuensi() {
        return frekuensi;
    }
    public void setFrekuensi(String frekuensi) {
        this.frekuensi = frekuensi;
    }

    public LocalDate getTanggalMulai() {
        return tanggalMulai;
    }
    public void setTanggalMulai(LocalDate tanggalMulai) {
        this.tanggalMulai = tanggalMulai;
    }

    public LocalDate getTanggalSelesai() {
        return tanggalSelesai;
    }
    public void setTanggalSelesai(LocalDate tanggalSelesai) {
        this.tanggalSelesai = tanggalSelesai;
    }

    public LocalTime getWaktu() {
        return waktu;
    }
    public void setWaktu(LocalTime waktu) {
        this.waktu = waktu;
    }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
