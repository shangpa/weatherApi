package com.jaemin.weather.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class WeatherService {

    @Value("${weather.api.key}")
    private String serviceKey;

    @Value("${weather.api.url}")
    private String apiUrl;

    public void fetchAndSaveWeather() {
        try {
            RestTemplate restTemplate = new RestTemplate();

            // [기존 로직] 날짜/시간 설정 및 URI 생성 부분은 그대로 유지
            String baseDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String baseTime = LocalTime.now().minusHours(1).format(DateTimeFormatter.ofPattern("HH00"));

            URI uri = UriComponentsBuilder.fromUriString(apiUrl)
                    .queryParam("serviceKey", serviceKey)
                    .queryParam("pageNo", "1")
                    .queryParam("numOfRows", "1000")
                    .queryParam("dataType", "JSON")
                    .queryParam("base_date", baseDate)
                    .queryParam("base_time", baseTime)
                    .queryParam("nx", "59")
                    .queryParam("ny", "126")
                    .build(true)
                    .toUri();

            // 1. API 호출
            String response = restTemplate.getForObject(uri, String.class);

            // 2. Jackson ObjectMapper로 데이터 깎기
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response);
            JsonNode items = root.path("response").path("body").path("items").path("item");

            Map<String, String> weatherData = new HashMap<>();

            // [추가] 마지막으로 새로고침된(데이터를 가져온) 시간 기록
            // 형식: 2024-05-20 14:30:05
            String lastUpdateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            weatherData.put("lastUpdate", lastUpdateTime);

            // 데이터가 비어있지 않은지 확인 후 루프
            if (items.isArray()) {
                for (JsonNode item : items) {
                    String category = item.path("category").asText();
                    String value = item.path("obsrValue").asText();

                    if (category.equals("T1H")) weatherData.put("temp", value);
                    if (category.equals("RN1")) weatherData.put("rain", value);
                    if (category.equals("PTY")) weatherData.put("skyCode", value);
                }
            }

            // 3. 간결해진 데이터를 weather.json으로 저장
            String refinedJson = objectMapper.writeValueAsString(weatherData);
            Files.writeString(Paths.get("weather.json"), refinedJson);

            System.out.println("가공 성공! 결과: " + refinedJson);

        } catch (Exception e) {
            System.err.println("가공 중 에러: " + e.getMessage());
            e.printStackTrace();
        }
    }
}