package com.pbo.habittracker.config;

import com.pbo.habittracker.service.HabitCompletionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ThymeleafConfig {
    @Bean
    public HabitCompletionService habitCompletionServiceBean(HabitCompletionService s) {
        return s;
    }
}
