package com.pbo.habittracker.repository;

import com.pbo.habittracker.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserUsername(String username);
    List<Task> findByUserUsernameAndTanggalDeadline(String username, LocalDate tanggalDeadline);
}

