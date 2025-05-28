package com.dita.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/Inpatient")
public class InpatientController {

    @GetMapping("/Inpatient")
    public String showInpatientPage() {
        return "Inpatient/Inpatient";  // 확장자 .html은 생략
    }
    
    @GetMapping("/Popup")
    public String showPopupPage() {
        return "Inpatient/PatientDetailPopup"; // templates/Inpatient/PatientDetailPopup.html
    }
    
    @GetMapping("/WaitingList")
    public String showWaitingListPage() {
        return "Inpatient/PatientWaitingPopup"; // templates/Inpatient/PatientWaitingPopup.html
    }

}

