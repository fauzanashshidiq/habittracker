package com.pbo.habittracker.service;

import com.pbo.habittracker.model.Habit;
import com.pbo.habittracker.model.User;
import com.pbo.habittracker.repository.HabitRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

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

    public List<Habit> getHabitsByUserAndDate(String username, LocalDate date) {
        return habitRepository.findHabitsActiveOnDate(username, date);
    }

    @Transactional
public void deleteHabit(Habit habit) {
    habitRepository.delete(habit);
}

public List<Habit> getCompletedHabits(User user) {
    List<Habit> habits = habitRepository.findByUser(user);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", new Locale("id", "ID"));

    for (Habit habit : habits) {
        habit.getCompletions().forEach(completion -> {
            completion.setTanggalSelesaiFormatted(
                completion.getTanggalSelesai().format(formatter)
            );
        });
    }

    return habits;
}


public List<Habit> getHabitsByUser(String username) {
    return habitRepository.findByUserUsername(username);
}


}
