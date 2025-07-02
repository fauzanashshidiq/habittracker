package com.pbo.habittracker.repository;

import com.pbo.habittracker.model.Habit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface HabitRepository extends JpaRepository<Habit, Long> {
    List<Habit> findByUserUsername(String username);
    List<Habit> findByUserUsernameAndTanggalMulai(String username, LocalDate tanggalMulai);
}
