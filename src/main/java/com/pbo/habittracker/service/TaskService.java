package com.pbo.habittracker.service;

import com.pbo.habittracker.model.Task;
import com.pbo.habittracker.repository.TaskRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

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

    public List<Task> findTasksByUser(String username) {
        return taskRepository.findByUserUsername(username);
    }

    public List<Task> getTasksByDate(LocalDate date) {
        return taskRepository.findByTanggalDeadline(date);
    }
}
