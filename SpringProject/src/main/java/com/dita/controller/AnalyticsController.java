package com.dita.controller;

import com.dita.service.AdmissionService;
import com.dita.service.AnalyticsService;
import com.dita.vo.PatientData;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/stats")
@CrossOrigin(origins = "*")
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final AdmissionService admissionService;

    public AnalyticsController(AnalyticsService analyticsService, AdmissionService admissionService) {
        this.analyticsService = analyticsService;
        this.admissionService = admissionService;
    }

    @GetMapping("/admission")
    public ResponseEntity<Map<String, Object>> analyzeAdmissions() {
        List<PatientData> patients = admissionService.getAllPatientData();
        Map<String, Object> result = analyticsService.analyzePatients(patients);
        return ResponseEntity.ok(result);
    }
}
