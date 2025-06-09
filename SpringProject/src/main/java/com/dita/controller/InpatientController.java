package com.dita.controller;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
        List<String> wards = wardRepository.findAll().stream()
                .map(Ward::getName)
                .collect(Collectors.toList());

        List<Admission> allAdmissions = admissionRepository.findAllCurrentAdmissionsWithDetails();

        // ✅ 여기서 입원중 환자만 필터링
        List<Admission> filtered = allAdmissions.stream()
            .filter(a -> a.getPatient().getPatientType() == PatientType.입원중)
            .collect(Collectors.toList());

        model.addAttribute("wards", wards);
        model.addAttribute("beds", filtered);
        return "Inpatient/Inpatient";
    }


    @GetMapping("/PatientWaitingPopup")
    public String showWaitingPatients(Model model) {
        List<Patient> patients = patientRepository.findByPatientType(PatientType.입원대기);
        List<User> doctors = userRepository.findByGrade(Grade.의사);

        List<PatientDto> dtoList = patients.stream()
            .map(PatientDto::new)
            .toList();

        model.addAttribute("patients", dtoList);  // ✅ DTO 기준
        model.addAttribute("doctors", doctors);
        return "Inpatient/PatientWaitingPopup"; 
    }

    //수정
    @PostMapping("/updateStatus")
    public String updatePatientStatus(@RequestParam int patientId,
                                      @RequestParam String status,
                                      @RequestParam String symptom,
                                      @RequestParam String admittedAt,
                                      @RequestParam String doctorId) {
        admissionSe.updatePatientStatus(patientId, status, symptom, admittedAt, doctorId);
        return "redirect:/Inpatient/PatientWaitingPopup";  // 또는 /Inpatient/list 등
    }
    
    //삭제
    @PostMapping("/delete")
    public String deleteAdmission(@RequestParam("patientId") int patientId) {
        admissionSe.deleteAdmission(patientId);
        return "/Inpatient/popupClose";
    }


    
    // 환자 디테일
    @GetMapping("/Popup")
    public String showPopup(@RequestParam("patientId") int patientId, Model model) {
    	List<Admission> admissions = admissionRepository.findByPatientId(patientId);
    	if (admissions.isEmpty()) {
    	    throw new IllegalArgumentException("환자 없음");
    	}
    	Admission admission = admissions.get(0); // 또는 최신 건 기준으로 정렬해서 선택

        List<User> doctors = userRepository.findByGrade(Grade.의사);
        model.addAttribute("admission", admission);
        model.addAttribute("doctors", doctors);
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
