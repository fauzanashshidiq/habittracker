package com.pbo.habittracker.service;

import com.pbo.habittracker.model.Task;
import com.pbo.habittracker.model.User;
import com.pbo.habittracker.repository.TaskRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> getTasksByUserAndDate(String username, LocalDate tanggal) {
        return taskRepository.findByUserUsernameAndTanggalDeadline(username, tanggal);
    }    

    public void save(Task task) {
        taskRepository.save(task);
    }

    public List<Task> findAllByUser(String username) {
        return taskRepository.findByUserUsernameOrderByTanggalDeadlineAsc(username);
    }

    public Task findById(Long id) {
        return taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task tidak ditemukan"));
    }

    public List<Task> getTasksByDate(LocalDate date) {
        return taskRepository.findByTanggalDeadline(date);
    }
    
    public void delete(Task task) {
        taskRepository.delete(task);
    }
    
    public List<Task> getTasksByStatus(User user, boolean selesai) {
        return taskRepository.findByUserAndSelesai(user, selesai);
    }
    public int countCompletedThisWeek(String username) {
    LocalDate startOfWeek = LocalDate.now().with(DayOfWeek.MONDAY);
    LocalDate endOfWeek = LocalDate.now().with(DayOfWeek.SUNDAY);

    return taskRepository.countByUserUsernameAndSelesaiIsTrueAndTanggalDeadlineBetween(
        username, startOfWeek, endOfWeek
    );
}

public int countOverdue(String username) {
    return taskRepository.countOverdue(username, LocalDate.now());
}
public Map<LocalDate, Long> countCompletedTasksPerDayThisWeek(String username) {
    LocalDate today = LocalDate.now();
    LocalDate startOfWeek = today.minusDays(6); // 7 hari terakhir termasuk hari ini

    // Ambil semua tugas yang selesai dalam rentang tanggal tersebut
    List<Task> completedTasks = taskRepository.findCompletedTasksBetweenDates(username, startOfWeek, today);

    // Kelompokkan berdasarkan tanggal_deadline atau tanggal_selesai (tergantung field kamu)
    Map<LocalDate, Long> result = completedTasks.stream()
        .collect(Collectors.groupingBy(Task::getTanggalDeadline, Collectors.counting()));

    // Pastikan semua 7 hari tetap ada meskipun tidak ada data
    Map<LocalDate, Long> completeMap = new TreeMap<>();
    for (int i = 0; i < 7; i++) {
        LocalDate date = startOfWeek.plusDays(i);
        completeMap.put(date, result.getOrDefault(date, 0L));
    }

    return completeMap;
}



}
