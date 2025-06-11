package com.dita.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.dita.service.StatisticsService;
import com.dita.vo.DiseaseStatDto;


import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/statistics")
public class StatisticsApiController {


    private final StatisticsService statisticsService;

    @GetMapping("/disease-stats")
    public List<DiseaseStatDto> getDiseaseStats(@RequestParam String startDate,
                                                @RequestParam String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        return statisticsService.getDiseaseStats(start, end);
    }
    @GetMapping("/age-group-stats")
    public Map<String, Long> getAgeGroupStats(@RequestParam String startDate,
                                              @RequestParam String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        return statisticsService.getAgeGroupStats(start, end);
    }

    @GetMapping("/gender-stats")
    public Map<String, Long> getGenderStats(@RequestParam String startDate,
                                            @RequestParam String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        return statisticsService.getGenderStats(start, end);
    }
}
