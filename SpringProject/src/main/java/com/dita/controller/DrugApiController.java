package com.dita.controller;


import com.dita.persistence.DrugRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class DrugApiController {

    private final DrugRepository drugRepository;

    public DrugApiController(DrugRepository drugRepository) {
        this.drugRepository = drugRepository;
    }

    @GetMapping("/api/drug/list")
    public List<Map<String, Object>> getDrugList() {
        return drugRepository.findAll().stream().map(d -> {
            Map<String, Object> map = new HashMap<>();
            map.put("type", "drug");
            map.put("name", d.getDrugName());
            map.put("dosage", "하루 3회");
            map.put("count", 10);
            return map;
        }).toList();
    }
}