package com.jaemin.weather;

import com.jaemin.weather.service.WeatherService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class WeatherApplicationTests {

    @Autowired
    private WeatherService weatherService;

    @Test
    void contextLoads() {
        weatherService.fetchAndSaveWeather(); // 실행 후 자동 종료됨
    }

}
