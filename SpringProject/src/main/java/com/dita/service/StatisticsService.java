package com.dita.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dita.persistence.AppointmentRepository;
import com.dita.vo.DoctorStatsDto;

@Service
public class StatisticsService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    // 전체 통계
    public List<DoctorStatsDto> getDoctorVisitStats() {
        return appointmentRepository.countVisitsPerDoctor();
    }

    // ✅ 날짜 범위 통계 (추천 방식)
    public List<DoctorStatsDto> getDoctorStats(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();                // 00:00:00
        LocalDateTime end = endDate.plusDays(1).atStartOfDay();       // 다음날 00:00:00 → exclusive 처리
        return appointmentRepository.countVisitsPerDoctorBetweenDateTime(start, end);
    }
    
    //진료 현황 통계
    public Map<String, Long> getVisitStatsPerDay(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.plusDays(1).atStartOfDay(); // exclusive
        List<Object[]> results = appointmentRepository.countVisitsPerDayBetween(start, end);

        return results.stream()
            .collect(Collectors.toMap(
                r -> r[0].toString(),       // 날짜 (String)
                r -> (Long) r[1]            // 건수
            ));
    }
}