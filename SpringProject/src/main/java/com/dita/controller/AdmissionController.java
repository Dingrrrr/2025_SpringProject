package com.dita.controller;

import java.time.LocalDate;
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
     * 병실별 입원 환자 리스트 (병실1, 병실2 등)
     */
    @GetMapping("/admitted/{wardName}")
    public List<Map<String, Object>> getPatientsByWard(@PathVariable String wardName) {
        List<Admission> list = admissionRepository.findByWardName(wardName);

        return list.stream().map(adm -> {
            Map<String, Object> map = new HashMap<>();
            map.put("patientId", adm.getPatient().getPatientId()); //  반드시 포함되어야 합니다
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
    
    // 환자 상세 정보 조회
    @GetMapping("/detail")
    public ResponseEntity<?> getPatientDetail(@RequestParam int patientId) {
        Optional<Admission> admissionOpt = admissionRepository.findByPatientId(patientId);
        if (admissionOpt.isEmpty()) return ResponseEntity.notFound().build();

        Admission admission = admissionOpt.get();
        Map<String, Object> data = new HashMap<>();
        data.put("name", admission.getPatient().getPatientName());
        data.put("gender", admission.getPatient().getPatientGender());
        data.put("age", admission.getPatient().getAge());
        data.put("diagnosis", admission.getPatient().getPatientSymptom());
        data.put("date", admission.getAdmittedAt().toLocalDate().toString());
        data.put("doctor", admission.getDoctor().getUsersName());
        data.put("status", admission.getPatient().getPatientType().name());

        return ResponseEntity.ok(data);
    }
    
    //수정 요청
    @PutMapping("/detail")
    public ResponseEntity<?> updatePatientDetail(@RequestBody Map<String, Object> dto) {
        int patientId = Integer.parseInt(dto.get("patientId").toString());
        Optional<Admission> admissionOpt = admissionRepository.findByPatientId(patientId);
        if (admissionOpt.isEmpty()) return ResponseEntity.notFound().build();

        Admission admission = admissionOpt.get();
        Patient patient = admission.getPatient();

        patient.setPatientSymptom((String) dto.get("diagnosis"));
        patient.setPatientType(PatientType.valueOf((String) dto.get("status")));
        patientRepository.save(patient);

        String dateStr = (String) dto.get("date");
        admission.setAdmittedAt(LocalDate.parse(dateStr).atStartOfDay()); // ✅ 수정된 부분
        admissionRepository.save(admission);

        return ResponseEntity.ok("수정 완료");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteAdmission(@RequestParam int patientId) {
        Optional<Admission> admissionOpt = admissionRepository.findByPatientId(patientId);
        if (admissionOpt.isEmpty()) return ResponseEntity.notFound().build();

        Admission admission = admissionOpt.get();

        // 병상 상태 복구
        if (admission.getBed() != null) {
            Bed bed = admission.getBed();
            bed.setBedstatus(StatusBed.사용가능);
            bedRepository.save(bed);
        }

        // 환자 상태 초기화
        Patient patient = admission.getPatient();
        patient.setPatientType(PatientType.입원대기);
        patientRepository.save(patient);

        // 입원 정보 삭제
        admissionRepository.delete(admission);

        return ResponseEntity.ok("삭제 완료");
    }


}

