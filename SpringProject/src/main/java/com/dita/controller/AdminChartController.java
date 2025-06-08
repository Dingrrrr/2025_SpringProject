package com.dita.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dita.service.AdminStatService;

@RestController
@RequestMapping("/admin/adminTotalManage")
public class AdminChartController {

    private final AdminStatService statService;

    public AdminChartController(AdminStatService statService) {
        this.statService = statService;
    }

    @GetMapping("/monthly-stats")
    public Map<String, Integer> getMonthlyStats() {
        return statService.getMonthlyStats(); // ✅ service에 메서드 필요
    }

    @GetMapping("/age-groups")
    public Map<String, Integer> getAgeGroupStats() {
        return statService.getAgeGroupStats(); // ✅ service에 메서드 필요
    }

    @GetMapping("/weekly-outpatients")
    public Map<String, Integer> getWeeklyOutpatientStats() {
        return statService.getWeeklyOutpatientStats(); // ✅ service에 메서드 필요
    }

    @GetMapping("/status-today")
    public Map<String, Integer> getTodayStatusStats() {
        return statService.getTodayStatusStats();
    }
    
    

}
