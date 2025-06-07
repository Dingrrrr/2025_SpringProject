package com.dita.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dita.service.BedStatService;

@RestController
@RequestMapping("/admin/adminTotalManage")
public class BedChartController {

    private final BedStatService bedStatService;

    public BedChartController(BedStatService bedStatService) {
        this.bedStatService = bedStatService;
    }

    @GetMapping("/bed-status")
    public Map<String, Integer> getBedStatusStats() {
        return bedStatService.getBedStatusStats();
    }
}

