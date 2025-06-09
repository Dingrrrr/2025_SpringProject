package com.dita.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dita.service.StatisticsService;
import com.dita.vo.DoctorStatsDto;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    // ✅ 1. 기간 없이 전체 통계
    @GetMapping("/doctor-visits")
    public List<DoctorStatsDto> getDoctorVisitCounts() {
        return statisticsService.getDoctorVisitStats();
    }

    // ✅ 2. 기간이 있는 통계 조회
    @GetMapping("/doctor-visits-by-date")
    public List<DoctorStatsDto> getDoctorStatsByDate(
        @RequestParam String startDate,
        @RequestParam String endDate
    ) {
        return statisticsService.getDoctorStats(LocalDate.parse(startDate), LocalDate.parse(endDate));
    }
    
    // ✅ 3. 진료 현황 통계 조회
    @GetMapping("/visit-daily")
    public Map<String, Long> getVisitDailyStats(
        @RequestParam String startDate,
        @RequestParam String endDate
    ) {
        return statisticsService.getVisitStatsPerDay(
            LocalDate.parse(startDate), LocalDate.parse(endDate)
        );
    }
}
