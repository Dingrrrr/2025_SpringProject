package com.dita.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.java.Log;

@Controller
@Log
@RequestMapping("/nurse/")
public class NursePageController {
	
	@GetMapping("/NurseChart")
    public String showNurseChartPage(Model model) {
		// 필요 시 model에 데이터 추가 가능
        return "nurse/NurseChart"; // templates/nurse/NurseChart.html 호출
    }
	
	@GetMapping("/VitalRecord")
    public String showVitalRecordPage(Model model) {
		// 필요 시 model에 데이터 추가 가능
        return "nurse/VitalRecord"; // templates/nurse/VitalRecord.html 호출
    }
	
	@GetMapping("/MedicationRecord")
    public String showMedicatonRecordPage(Model model) {
		// 필요 시 model에 데이터 추가 가능
        return "nurse/MedicationRecord"; // templates/nurse/MedicationRecord.html 호출
    }
	
}
