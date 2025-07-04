package com.pbo.habittracker.service;

import com.pbo.habittracker.model.Habit;
import com.pbo.habittracker.repository.HabitRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class HabitService {

    private final HabitRepository habitRepository;

    @Autowired
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

    public List<Habit> getHabitsByUserAndDate(String username, LocalDate date) {
        return habitRepository.findHabitsActiveOnDate(username, date);
    }

    @Transactional
public void deleteHabit(Habit habit) {
    habitRepository.delete(habit);
}

}
