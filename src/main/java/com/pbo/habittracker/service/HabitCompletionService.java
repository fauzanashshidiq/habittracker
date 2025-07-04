package com.pbo.habittracker.service;

import com.pbo.habittracker.model.Habit;
import com.pbo.habittracker.model.HabitCompletion;
import com.pbo.habittracker.repository.HabitCompletionRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class HabitCompletionService {
    private final HabitCompletionRepository completionRepository;

    @Autowired
    public HabitCompletionService(HabitCompletionRepository completionRepository) {
        this.completionRepository = completionRepository;
    }

    public void markHabitDoneToday(Habit habit, LocalDate tanggal) {
        HabitCompletion hc = new HabitCompletion();
        hc.setHabit(habit);
        hc.setTanggalSelesai(tanggal);
        completionRepository.save(hc);
    }
    
    public void markHabitDoneToday(Habit habit) {
        markHabitDoneToday(habit, LocalDate.now());
    }
    

    public List<HabitCompletion> getCompletionsThisPeriod(Habit habit, LocalDate start, LocalDate end) {
        return completionRepository.findByHabitAndTanggalSelesaiBetween(habit, start, end);
    }

    public boolean isDoneToday(Habit habit) {
        return completionRepository.existsByHabitAndTanggalSelesai(habit, LocalDate.now());
    }
    
    public boolean isDoneOnDate(Habit habit, LocalDate tanggal) {
        return completionRepository.existsByHabitAndTanggalSelesai(habit, tanggal);
    }

    @Transactional
    public void deleteByHabitAndDate(Habit habit, LocalDate tanggal) {
        completionRepository.deleteByHabitAndTanggalSelesai(habit, tanggal);
    }
    
    public long countByHabitAndTanggalSelesaiBetween(Habit habit, LocalDate start, LocalDate end) {
        return completionRepository.countByHabitAndTanggalSelesaiBetween(habit, start, end);
    }
    
}
