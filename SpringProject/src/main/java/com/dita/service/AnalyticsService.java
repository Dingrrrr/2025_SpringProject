package com.dita.service;

import com.dita.vo.PatientData;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class AnalyticsService {

    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, Object> analyzePatients(List<PatientData> patients) {
        String flaskUrl = "http://localhost:5000/analyze";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<List<PatientData>> request = new HttpEntity<>(patients, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                flaskUrl,
                HttpMethod.POST,
                request,
                Map.class
            );

            return response.getBody();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.singletonMap("error", "Flask 호출 실패: " + e.getMessage());
        }
    }
}
