package com.dita.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dita.service.AdminStatService;

@RestController
@RequestMapping("/admin/adminTotalManage")
public class AdminChartController {

    private final AdminStatService chartService;

    public AdminChartController(AdminStatService chartService) {
        this.chartService = chartService;
    }

    @GetMapping("/monthly-stats")
    public Map<String, Integer> getStatusStats() {
        return chartService.getStatusStats();
    }

}
