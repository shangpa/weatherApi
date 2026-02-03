package com.jaemin.weather;

import com.jaemin.weather.service.WeatherService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class WeatherApplication {

    public static void main(String[] args) {
        SpringApplication.run(WeatherApplication.class, args);
    }
    @Bean
    public CommandLineRunner run(WeatherService weatherService) {
        return args -> {
            weatherService.fetchAndSaveWeather();
        };
    }
}
