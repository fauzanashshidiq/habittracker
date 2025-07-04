package com.pbo.habittracker.repository;

import com.pbo.habittracker.model.HabitCompletion;

import com.pbo.habittracker.model.Habit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface HabitCompletionRepository extends JpaRepository<HabitCompletion, Long> {
    List<HabitCompletion> findByHabitAndTanggalSelesaiBetween(Habit habit, LocalDate start, LocalDate end);
    boolean existsByHabitAndTanggalSelesai(Habit habit, LocalDate tanggalSelesai);
    void deleteByHabitAndTanggalSelesai(Habit habit, LocalDate tanggalSelesai);
    long countByHabitAndTanggalSelesaiBetween(Habit habit, LocalDate start, LocalDate end);
}
