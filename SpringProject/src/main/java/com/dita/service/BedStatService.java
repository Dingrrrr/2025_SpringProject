package com.dita.service;

import com.dita.persistence.BedRepository;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class BedStatService {

    private final BedRepository bedRepository;

    public BedStatService(BedRepository bedRepository) {
        this.bedRepository = bedRepository;
    }

    public Map<String, Integer> getBedStatusStats() {
        List<Object[]> result = bedRepository.countByBedStatus();
        Map<String, Integer> map = new LinkedHashMap<>();
        for (Object[] row : result) {
            map.put(row[0].toString(), ((Number) row[1]).intValue());
        }
        return map;
    }
}
