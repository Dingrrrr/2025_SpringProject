package com.dita.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;



import com.dita.domain.*;
import com.dita.persistence.*;
import com.dita.service.AdmissionService;
import com.dita.service.PatientService;
import com.dita.vo.AdmissionDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/Inpatient")
@RequiredArgsConstructor
@Slf4j
public class AdmissionController {

    private final PatientService patientService;
    private final AdmissionRepository admissionRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final BedRepository bedRepository;
    private final AdmissionService admissionService;
    
    //  입원 버튼 처리 (타임리프 form에서 POST 요청)
    @PostMapping("/admit")
    public String admitPatient(
            @RequestParam("patientId") int patientId,
            @RequestParam("bedId") int bedId,
            @RequestParam("doctorId") String doctorId,
            Model model) {

        Optional<Patient> patientOpt = patientRepository.findById(patientId);
        Optional<Bed> bedOpt = bedRepository.findById(bedId);
        Optional<User> doctorOpt = userRepository.findById(doctorId);

        if (patientOpt.isEmpty() || bedOpt.isEmpty() || doctorOpt.isEmpty()) {
            model.addAttribute("error", "입원 실패: 정보 없음");
            return "error"; // 오류 페이지 또는 메시지 표시
        }

        Patient patient = patientOpt.get();
        Bed bed = bedOpt.get();
        User doctor = doctorOpt.get();

        Admission admission = Admission.builder()
                .patient(patient)
                .doctor(doctor)
                .bed(bed)
                .admittedAt(LocalDateTime.now())
                .admissionReason("입원 대기 → 입원 처리")
                .build();
        admissionRepository.save(admission);

        bed.setBedstatus(StatusBed.사용중);
        bedRepository.save(bed);

        patient.setPatientType(PatientType.입원중);
        patientRepository.save(patient);

        return "redirect:/Inpatient/PatientWaitingPopup";  // 다시 대기 목록으로
    }
    
    
    @GetMapping("/new")
    public String showAdmissionForm(Model model) {
        model.addAttribute("patient", new Patient());
        return "admission/NewPatientForm";  // 예: templates/admission/NewPatientForm.html
    }

    @PostMapping("/new")
    public String processAdmission(@ModelAttribute Patient patient) {
        log.info("새로운 환자 등록 요청: " + patient.getPatientName() 
                 + ", 상태 = " + patient.getPatientType());

        // ─── 반드시 patientRepository.save()가 아니라 service를 통해 저장 ───
        patientService.admitPatient(patient);

        // 저장 후에 “입원 환자 목록” 페이지로 리다이렉트
        return "redirect:/nurse/NurseHome";
    }
    
    @GetMapping("/stats/admission")
    @ResponseBody
    public Map<String, Map<String, Long>> getAdmissionStats(
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end) {

        return admissionService.getAdmissionStatistics(start, end);
    }
}

