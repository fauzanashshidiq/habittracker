package com.pbo.habittracker.repository;

import com.pbo.habittracker.model.Task;
import com.pbo.habittracker.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserUsername(String username);
    List<Task> findByUserUsernameAndTanggalDeadline(String username, LocalDate tanggalDeadline);
    List<Task> findByTanggalDeadline(LocalDate tanggal);
    List<Task> findByUserUsernameOrderByTanggalDeadlineAsc(String username);
    List<Task> findByUserAndSelesai(User user, boolean selesai);
    @Query("SELECT COUNT(t) FROM Task t WHERE t.user.username = :username AND t.selesai = false AND t.tanggalDeadline < :tanggal")
int countOverdue(@Param("username") String username, @Param("tanggal") LocalDate tanggal);
int countByUserUsernameAndSelesaiIsTrueAndTanggalDeadlineBetween(
    String username,
    LocalDate start,
    LocalDate end
);
@Query("SELECT t FROM Task t JOIN t.user u WHERE u.username = :username AND t.selesai = true AND t.tanggalDeadline BETWEEN :start AND :end")
List<Task> findCompletedTasksBetweenDates(@Param("username") String username,
                                          @Param("start") LocalDate start,
                                          @Param("end") LocalDate end);

}

