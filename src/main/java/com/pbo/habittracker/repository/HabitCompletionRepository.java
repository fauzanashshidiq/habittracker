package com.pbo.habittracker.repository;

import com.pbo.habittracker.model.HabitCompletion;
import com.pbo.habittracker.model.User;
import com.pbo.habittracker.model.Habit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface HabitCompletionRepository extends JpaRepository<HabitCompletion, Long> {
    List<HabitCompletion> findByHabitAndTanggalSelesaiBetween(Habit habit, LocalDate start, LocalDate end);
    boolean existsByHabitAndTanggalSelesai(Habit habit, LocalDate tanggalSelesai);
    void deleteByHabitAndTanggalSelesai(Habit habit, LocalDate tanggalSelesai);
    long countByHabitAndTanggalSelesaiBetween(Habit habit, LocalDate start, LocalDate end);
    @Query("SELECT DISTINCT h FROM Habit h LEFT JOIN FETCH h.completions c WHERE h.user = :user")
List<Habit> findCompletedHabitsWithCompletions(@Param("user") User user);
@Query("SELECT COUNT(hc) FROM HabitCompletion hc WHERE hc.habit.user.username = :username AND hc.tanggalSelesai BETWEEN :start AND :end")
int countByUsernameAndTanggalSelesaiBetween(@Param("username") String username, @Param("start") LocalDate start, @Param("end") LocalDate end);
List<HabitCompletion> findByHabitUserUsernameAndTanggalSelesaiBetween(String username, LocalDate start, LocalDate end);

}
