package com.dita.controller;

import java.time.LocalDateTime;
import java.util.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dita.domain.*;
import com.dita.persistence.*;
import com.dita.vo.AdmissionDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/Inpatient")
@RequiredArgsConstructor
public class AdmissionController {

    private final AdmissionRepository admissionRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final BedRepository bedRepository;

    /**
     * 환자 입원 처리
     */
    @PostMapping("/admit")
    public ResponseEntity<?> admit(@RequestBody AdmissionDto dto) {
        Optional<Patient> patientOpt = patientRepository.findById(dto.getPatientId());
        Optional<Bed> bedOpt = bedRepository.findById(dto.getBedId());
        Optional<User> doctorOpt = userRepository.findById(dto.getDoctorId());

        if (patientOpt.isEmpty() || bedOpt.isEmpty() || doctorOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("환자, 병상 또는 의사 정보를 찾을 수 없습니다.");
        }

        Patient patient = patientOpt.get();
        Bed bed = bedOpt.get();
        User doctor = doctorOpt.get();

        Admission admission = Admission.builder()
                .patient(patient)
                .doctor(doctor)
                .bed(bed)
                .admittedAt(LocalDateTime.now())
                .admissionReason(Optional.ofNullable(dto.getReason()).orElse("병상 자동 배정"))
                .build();

        admissionRepository.save(admission);

        // 병상 상태 및 환자 상태 갱신
        bed.setBedstatus(StatusBed.사용중);
        bedRepository.save(bed);

        patient.setPatientType(PatientType.입원중);
        patientRepository.save(patient);

        return ResponseEntity.ok("입원 처리 완료");
    }

    /**
     * 입원 대기 환자 리스트 (대기실)
     */
    @GetMapping("/admitted/waiting-room")
    public List<Map<String, Object>> getPatientsInWaitingRoom() {
        List<Admission> list = admissionRepository.findWaitingPatients();

        return list.stream().map(adm -> {
            Map<String, Object> map = new HashMap<>();
            map.put("ward", "대기실");
            map.put("room", "미정");
            map.put("name", adm.getPatient().getPatientName());
            map.put("gender", adm.getPatient().getPatientGender());
            map.put("age", adm.getPatient().getAge());
            map.put("diagnosis", adm.getPatient().getPatientSymptom());
            map.put("date", adm.getAdmittedAt().toLocalDate().toString());
            map.put("doctor", adm.getDoctor().getUsersName());
            map.put("status", adm.getPatient().getPatientType().name());
            return map;
        }).toList();
    }

    /**
     * 병실별 입원 환자 리스트 (병실1, 병실2 등)
     */
    @GetMapping("/admitted/{wardName}")
    public List<Map<String, Object>> getPatientsByWard(@PathVariable String wardName) {
        List<Admission> list = admissionRepository.findByWardName(wardName);

        return list.stream().map(adm -> {
            Map<String, Object> map = new HashMap<>();
            map.put("room", adm.getBed().getWard().getName());
            map.put("name", adm.getPatient().getPatientName());
            map.put("gender", adm.getPatient().getPatientGender());
            map.put("age", adm.getPatient().getAge());
            map.put("diagnosis", adm.getPatient().getPatientSymptom());
            map.put("date", adm.getAdmittedAt().toLocalDate().toString());
            map.put("doctor", adm.getDoctor().getUsersName());
            map.put("patientType", adm.getPatient().getPatientType().name());
            return map;
        }).toList();
    }
}

