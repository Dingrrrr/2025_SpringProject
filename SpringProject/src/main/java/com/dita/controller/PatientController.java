package com.dita.controller;

import com.dita.domain.Patient;
import com.dita.service.PatientService;
import com.dita.vo.PatientWithVitalDTO;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    // 기존 admit / update 유지
    @PostMapping("/admit")
    public Patient createPatient(@RequestBody Patient patient) {
        return patientService.admitPatient(patient);
    }

    @PutMapping("/{id}")
    public Patient updatePatient(@PathVariable("id") Integer patientId,
                                 @RequestBody Patient patient) {
        patient.setPatientId(patientId);
        return patientService.admitPatient(patient);
    }

 // ✅ [3] [GET] /api/patients/search?name=홍길동
    // → 동명이인이 있을 경우 여러 환자 정보를 JSON 배열로 반환
    @GetMapping("/search")
    public ResponseEntity<?> searchPatientsByName(@RequestParam("name") String name) {
        List<PatientWithVitalDTO> patients = patientService.findAllPatientsWithVitalByName(name);
        if (patients.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(patients);
    }
    
    
  }

