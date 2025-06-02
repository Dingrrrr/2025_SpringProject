package com.dita.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dita.domain.PatientType;
import com.dita.service.AdmissionService;
import com.dita.vo.PatientDto;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/Inpatient")
@RequiredArgsConstructor
public class InpatientController {
	
	private final AdmissionService admissionSe;
	
///////페이지 연결////////
    @GetMapping("/Inpatient")//입원 메인
    public String showInpatientPage() {
        return "Inpatient/Inpatient";  // 확장자 .html은 생략
    }
    
    @GetMapping("/Popup")// 환자 디테일
    public String showPopupPage() {
        return "Inpatient/PatientDetailPopup"; // templates/Inpatient/PatientDetailPopup.html
    }
    
    @GetMapping("/WaitingList")// 대기입원환자 추가
    public String showWaitingListPage() {
        return "Inpatient/PatientWaitingPopup"; // templates/Inpatient/PatientWaitingPopup.html
    }
    
    @GetMapping("/AssignBedPopup")
    public String showAssignBedPopup() {
        return "Inpatient/AssignBedPopup"; // templates/Inpatient/AssignBedPopup.html
    }
    
    @GetMapping("/InpatientStatistics")
    public String showStatisticsPage() {
        return "/inpatient/InpatientStatistics"; // templates/InpatientStatistics.html
    }
   ////////////////////// 
    
   //입원 대기 환자 리스트
    @GetMapping("/waiting-patients")
    @ResponseBody
    public List<PatientDto> getWaitingPatients() {
        return admissionSe.getPatientsByType(PatientType.입원대기); // ✅ Patient 기준
    }

}

