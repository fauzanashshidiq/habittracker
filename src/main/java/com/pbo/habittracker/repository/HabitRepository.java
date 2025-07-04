package com.pbo.habittracker.repository;

import com.pbo.habittracker.model.Habit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface HabitRepository extends JpaRepository<Habit, Long> {
    List<Habit> findByUserUsername(String username);
    List<Habit> findByUserUsernameAndTanggalMulai(String username, LocalDate tanggalMulai);
    @Query("""
    SELECT h FROM Habit h
    WHERE h.user.username = :username
      AND h.tanggalMulai <= :date
      AND (h.tanggalSelesai >= :date OR h.tanggalSelesai IS NULL)
    """)
    List<Habit> findHabitsActiveOnDate(
        @org.springframework.data.repository.query.Param("username") String username,
        @org.springframework.data.repository.query.Param("date") LocalDate date
    );
    
}
