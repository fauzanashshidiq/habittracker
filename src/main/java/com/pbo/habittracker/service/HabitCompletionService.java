package com.pbo.habittracker.service;

import com.pbo.habittracker.model.Habit;
import com.pbo.habittracker.model.HabitCompletion;
import com.pbo.habittracker.repository.HabitCompletionRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class HabitCompletionService {
    private final HabitCompletionRepository completionRepository;
    private final HabitService habitService;

    @Autowired
    public HabitCompletionService(HabitCompletionRepository completionRepository, HabitService habitService) {
        this.completionRepository = completionRepository;
        this.habitService = habitService;
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
    public int countByUserThisWeek(String username) {
    LocalDate today = LocalDate.now();
    LocalDate startOfWeek = today.with(DayOfWeek.MONDAY);
    LocalDate endOfWeek = today.with(DayOfWeek.SUNDAY);

    return completionRepository.countByUsernameAndTanggalSelesaiBetween(username, startOfWeek, endOfWeek);
}
// Menghitung jumlah penyelesaian habit per hari untuk 7 hari terakhir
public Map<LocalDate, Long> countHabitCompletionsPerDayThisWeek(String username) {
    LocalDate today = LocalDate.now();
    LocalDate startOfWeek = today.minusDays(6);
    
    List<HabitCompletion> completions = completionRepository
        .findByHabitUserUsernameAndTanggalSelesaiBetween(username, startOfWeek, today);

    Map<LocalDate, Long> countPerDay = new HashMap<>();
    for (int i = 0; i < 7; i++) {
        countPerDay.put(startOfWeek.plusDays(i), 0L);
    }

    for (HabitCompletion hc : completions) {
        LocalDate date = hc.getTanggalSelesai();
        countPerDay.put(date, countPerDay.get(date) + 1);
    }

    return countPerDay;
}

// Menghitung jumlah penyelesaian untuk setiap habit di minggu ini
public Map<Habit, Long> countHabitProgressThisWeek(String username) {
    LocalDate today = LocalDate.now();
    LocalDate startOfWeek = today.minusDays(6);

    List<Habit> habits = habitService.getHabitsByUser(username);
    Map<Habit, Long> progressMap = new HashMap<>();

    for (Habit habit : habits) {
        long done = completionRepository.countByHabitAndTanggalSelesaiBetween(habit, startOfWeek, today);
        progressMap.put(habit, done);
    }

    return progressMap;
}

}
