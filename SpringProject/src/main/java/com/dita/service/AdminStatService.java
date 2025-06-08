package com.dita.service;

import java.time.LocalDateTime;
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

    public Map<String, Integer> getTodayStatusStats() {
        List<Object[]> result = apptRepository.countTodayByStatus();
        return toMap(result);
    }

    public Map<String, Integer> getMonthlyStats() {
        List<Object[]> result = apptRepository.countMonthlyOutpatients();
        return toMap(result);
    }

    public Map<String, Integer> getAgeGroupStats() {
    	List<Object[]> result = apptRepository.countByAgeGroup();
    	for (Object[] row : result) {
    	    String ageGroup = (String) row[0];
    	    Long count = ((Number) row[1]).longValue();
    	    System.out.println(ageGroup + ": " + count + "명");
    	}
    	return toMap(result);
    }

    public Map<String, Integer> getWeeklyOutpatientStats() {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        List<Object[]> result = apptRepository.countWeeklyOutpatients(sevenDaysAgo);
        return toMap(result);
    }

    private Map<String, Integer> toMap(List<Object[]> rows) {
        Map<String, Integer> map = new LinkedHashMap<>();
        for (Object[] row : rows) {
            map.put(row[0].toString(), ((Number) row[1]).intValue());
        }
        return map;
    }
}


