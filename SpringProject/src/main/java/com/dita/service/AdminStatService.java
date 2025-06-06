package com.dita.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.dita.persistence.ApptRepository;

@Service
public class AdminStatService {

    private final ApptRepository apptRepository;

    public AdminStatService(ApptRepository apptRepository) {
        this.apptRepository = apptRepository;
    }

    public Map<String, Integer> getStatusStats() {
        List<Object[]> result = apptRepository.countByStatus();
        Map<String, Integer> stats = new LinkedHashMap<>();
        for (Object[] row : result) {
            stats.put(row[0].toString(), ((Number) row[1]).intValue());
        }
        return stats;
    }

}

