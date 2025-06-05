package com.dita.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dita.domain.Patient;
import com.dita.service.PatientService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {
    private final PatientService patientService;

    @PostMapping("/admit")
    public Patient createPatient(@RequestBody Patient patient) {
        return patientService.admitPatient(patient);
    }

    @PutMapping("/{id}")
    public Patient updatePatient(
            @PathVariable("id") Integer patientId,
            @RequestBody Patient patient
    ) {
        patient.setPatientId(patientId);
        return patientService.admitPatient(patient);
    }
}

