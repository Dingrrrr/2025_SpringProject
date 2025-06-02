package com.dita.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.dita.domain.Patient;
import com.dita.domain.PatientType;
import com.dita.persistence.LoginPageRepository;
import com.dita.persistence.PatientRepository;
import com.dita.service.EmailService;

import lombok.extern.java.Log;



@Controller
@Log
@RequestMapping("/acceptance/")
public class AcceptPageController {
	
	private final PatientRepository repo;
	
	public AcceptPageController(PatientRepository repo) {
		this.repo = repo;

	}
	
	//환자들만 검색해서 보여줌
	@GetMapping("/acceptanceHome")
	public String showAcceptanceHomePage(Model model) {
		List<Patient> allPatients = repo.findAll();
		model.addAttribute("patients", allPatients);
		return "acceptance/acceptanceHome";
	}
	
	@GetMapping("/acceptanceDoctor")
    public String AcceptanceDoctorPage(Model model) {
		// 필요 시 model에 데이터 추가 가능
        return "acceptance/acceptanceDoctor"; 
    }
	
	@GetMapping("/acceptanceCondition")
    public String AcceptanceConditionPage(Model model) {
		// 필요 시 model에 데이터 추가 가능
        return "acceptance/acceptanceCondition"; 
	}
	@GetMapping("/AcceptanceReceipt")
    public String showAcceptanceReceiptPage(Model model) {
		// 필요 시 model에 데이터 추가 가능
        return "acceptance/AcceptanceReceipt"; 

    }
	
	@GetMapping("/acceptanceReception")
    public String showAcceptanceReceptionPage(Model model) {
		// 필요 시 model에 데이터 추가 가능
        return "acceptance/acceptanceReception"; 

    }
	
	@PostMapping("/acceptanceHome")
	public String processPatient(@RequestParam String patientName,
												 @RequestParam String patientBirth, @RequestParam String patientPhone,
												 @RequestParam String patientSymptom, @RequestParam String patientGender,
												 @RequestParam PatientType patientType, @RequestParam String patientAddress
	) {
		
			Patient p = new Patient();
			p.setPatientName(patientName);
			p.setPatientBirth(patientBirth);
			p.setPatientPhone(patientPhone);
			p.setPatientSymptom(patientSymptom);
			p.setPatientGender(patientGender);
			p.setPatientType(patientType);
			p.setPatientAddress(patientAddress);
		
			repo.save(p);
			return "redirect:/acceptance/acceptanceHome";
	}
}

