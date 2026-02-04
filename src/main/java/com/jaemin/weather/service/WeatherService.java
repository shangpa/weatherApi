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
import java.time.ZoneId;
import java.time.ZonedDateTime;
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

            // [수정] 한국 시간(Asia/Seoul) 기준으로 현재 시간 설정
            ZonedDateTime nowSeoul = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));

            // 기상청 실황은 매시 40분 이후에 생성되므로, 40분 전이면 한 시간 전 데이터를 요청
            if (nowSeoul.getMinute() < 40) {
                nowSeoul = nowSeoul.minusHours(1);
            }
            String baseDate = nowSeoul.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String baseTime = nowSeoul.format(DateTimeFormatter.ofPattern("HH00"));

            URI uri = UriComponentsBuilder.fromUriString(apiUrl)
                    .queryParam("serviceKey", serviceKey) // 디코딩된 키 권장
                    .queryParam("pageNo", "1")
                    .queryParam("numOfRows", "1000")
                    .queryParam("dataType", "JSON")
                    .queryParam("base_date", baseDate)
                    .queryParam("base_time", baseTime)
                    .queryParam("nx", "59")
                    .queryParam("ny", "126")
                    .build(true)
                    .toUri();

            // 1. API 호출 및 로그 출력
            String response = restTemplate.getForObject(uri, String.class);
            System.out.println("기상청 응답 원본: " + response);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response);
            JsonNode items = root.path("response").path("body").path("items").path("item");

            Map<String, String> weatherData = new HashMap<>();
            String lastUpdateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            weatherData.put("lastUpdate", lastUpdateTime);

            // 2. 데이터 가공 (데이터가 없을 때 기본값 설정)
            if (items.isArray() && items.size() > 0) {
                for (JsonNode item : items) {
                    String category = item.path("category").asText();
                    String value = item.path("obsrValue").asText();

                    if (category.equals("T1H")) weatherData.put("temp", value);
                    if (category.equals("RN1")) weatherData.put("rain", value);
                    if (category.equals("PTY")) weatherData.put("skyCode", value);
                }
            } else {
                // 데이터가 없을 경우 로그를 남김
                System.err.println("경고: 기상청에서 날씨 데이터를 주지 않았습니다. (BaseDate: " + baseDate + ", BaseTime: " + baseTime + ")");
                // 화면에 NaN이 뜨지 않게 기본값이라도 넣어줌
                weatherData.put("temp", "0");
                weatherData.put("rain", "0");
                weatherData.put("skyCode", "0");
            }

            // 3. 파일 저장
            String refinedJson = objectMapper.writeValueAsString(weatherData);
            Files.writeString(Paths.get("weather.json"), refinedJson);
            System.out.println("최종 저장 데이터: " + refinedJson);

        } catch (Exception e) {
            System.err.println("가공 중 에러: " + e.getMessage());
            e.printStackTrace();
        }
    }
}