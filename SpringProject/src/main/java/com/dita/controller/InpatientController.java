package com.dita.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dita.domain.Admission;
import com.dita.domain.Bed;
import com.dita.domain.Grade;
import com.dita.domain.Patient;
import com.dita.domain.PatientType;
import com.dita.domain.User;
import com.dita.domain.Ward;
import com.dita.persistence.AdmissionRepository;
import com.dita.persistence.BedRepository;
import com.dita.persistence.PatientRepository;
import com.dita.persistence.UserRepository;
import com.dita.persistence.WardRepository;
import com.dita.service.AdmissionService;
import com.dita.vo.PatientDto;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/Inpatient")
@RequiredArgsConstructor
public class InpatientController {

    private final AdmissionService admissionSe;
    private final UserRepository userRepository;
    private final BedRepository bedRepository;
    private final PatientRepository patientRepository;
    private final AdmissionRepository admissionRepository;
    private final WardRepository wardRepository;
    
    @GetMapping("/Inpatient")
    public String showInpatientPage(Model model) {
        // 📌 병동 테이블에서 모든 병실 이름 가져오기
        List<String> wards = wardRepository.findAll().stream()
                .map(Ward::getName)
                .collect(Collectors.toList());

        // 📌 입원 중인 환자 전체 정보 (JOIN FETCH)
        List<Admission> admissions = admissionRepository.findAllCurrentAdmissionsWithDetails();

        model.addAttribute("wards", wards);       // 병실 탭용
        model.addAttribute("beds", admissions);   // 환자 카드용

        return "Inpatient/Inpatient";
    }

    @GetMapping("/PatientWaitingPopup")
    public String showWaitingPatients(Model model) {
        List<Patient> patients = patientRepository.findByPatientType(PatientType.입원대기);
        List<User> doctors = userRepository.findByGrade(Grade.의사);

        model.addAttribute("patients", patients);
        model.addAttribute("doctors", doctors);
        return "Inpatient/PatientWaitingPopup"; 
    }
    
    //환자 수정
    @PostMapping("/updateStatus")
    public String updatePatientStatus(@RequestParam("patientId") int patientId,
                                      @RequestParam("status") String status) {
    	admissionSe.updatePatientStatus(patientId, status);
    	return "redirect:/Inpatient/Popup?patientId=" + patientId;
    }
    
    // 환자 디테일
    @GetMapping("/Popup")
    public String showPopup(@RequestParam("patientId") int patientId, Model model) {
        Admission admission = admissionRepository.findByPatientId(patientId)
                                    .orElseThrow(() -> new IllegalArgumentException("환자 없음"));
        model.addAttribute("admission", admission);
        return "Inpatient/PatientDetailPopup";
    }

    // 병상 배정 팝업
    @GetMapping("/AssignBedPopup")
    public String showAssignBedPopup(Model model) {
        List<Bed> beds = bedRepository.findAll(); // 직접 Repository 사용

        Map<String, List<Bed>> grouped = beds.stream()
            .collect(Collectors.groupingBy(b -> b.getWard().getName()));

        model.addAttribute("bedsGroupedByWard", grouped);
        return "Inpatient/AssignBedPopup";
    }

    // 입원 통계
    @GetMapping("/InpatientStatistics")
    public String showStatisticsPage() {
        return "inpatient/InpatientStatistics";
    }

    @GetMapping("/WaitingList") // 입원 대기 환자 목록 페이지
    public String showWaitingListPage(Model model) {
        // 1. 입원 대기 환자 조회
        List<PatientDto> waitingPatients = admissionSe.getWaitingPatients();

        // 2. 담당 의사 목록 조회 (선택용 드롭다운)
        List<User> doctorList = userRepository.findByGrade(Grade.의사);

        // 3. 모델에 데이터 추가
        model.addAttribute("patients", waitingPatients);
        model.addAttribute("doctors", doctorList);

        // 4. Thymeleaf 템플릿 반환
        return "Inpatient/PatientWaitingPopup";
    }


}
