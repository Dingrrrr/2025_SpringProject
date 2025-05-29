package com.dita.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/Inpatient")
public class InpatientController {

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
}

