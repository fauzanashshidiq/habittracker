package com.pbo.habittracker.service;

import com.pbo.habittracker.model.Habit;
import com.pbo.habittracker.repository.HabitRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HabitService {

    private final HabitRepository habitRepository;

    public HabitService(HabitRepository habitRepository) {
        this.habitRepository = habitRepository;
    }

    public List<Habit> findAll() {
        return habitRepository.findAll();
    }

    public Habit save(Habit habit) {
        return habitRepository.save(habit);
    }

    public Habit findById(Long id) {
        return habitRepository.findById(id).orElse(null);
    }
    public List<Habit> findHabitsByUser(String username) {
        return habitRepository.findByUserUsername(username);
    }
    
}
