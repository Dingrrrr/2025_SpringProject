package com.dita.controller;

import com.dita.domain.Disease;
import com.dita.persistence.DiseaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class DiseaseApiController {

    private final DiseaseRepository diseaseRepository;

    @GetMapping("/api/disease/list")
    public List<Map<String, Object>> getDiseaseList() {
        return diseaseRepository.findAll().stream().map(disease -> {
            Map<String, Object> map = new HashMap<>();
            map.put("type", "disease");
            map.put("name", disease.getName());
            map.put("category", "일반"); // category 필드가 있다면 수정
            return map;
        }).toList();
    }
}
