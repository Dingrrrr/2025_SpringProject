package com.dita.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dita.domain.Appt;
import com.dita.domain.Grade;
import com.dita.domain.Patient;
import com.dita.domain.PatientType;
import com.dita.domain.Status;
import com.dita.domain.User;
import com.dita.persistence.*;
import com.dita.service.EmailService;
import com.dita.vo.AppointmentDto;
import com.dita.persistence.ApptRepository;

import lombok.extern.java.Log;


@Controller
@Log
@RequestMapping("/acceptance/")
public class AcceptPageController {

    private final UserRepository userRepository;
	
	private final PatientRepository repo; 
	
	private final ApptRepository apptRepository;
	
	public AcceptPageController(PatientRepository repo, UserRepository userRepository, ApptRepository apptRepository) {
		this.repo = repo;
		this.userRepository = userRepository;
		this.apptRepository = apptRepository;

	}
	
	@GetMapping("/acceptanceHome")
    public String showAcceptanceHomePage(Model model) {
		// 필요 시 model에 데이터 추가 가능
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
	    List<User> doctorList = userRepository.findByGrade(Grade.의사); // 또는 Grade.DOCTOR
	    model.addAttribute("doctorList", doctorList);
	    return "acceptance/acceptanceReception"; // templates/acceptance/acceptanceReception.html 파일 있어야 함
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
	
	@PostMapping("/appointment")
	@ResponseBody 
	public String saveAppointment(@RequestBody AppointmentDto dto) {
	    Patient p = new Patient();
	    p.setPatientName(dto.getName());
	    p.setPatientGender(dto.getGender());
	    p.setPatientBirth(dto.getRrn());
	    p.setPatientPhone(dto.getPhone());
	    p.setPatientSymptom(dto.getDisease());

	    Patient savedPatient = repo.save(p); // ID 생성된 객체

	    Appt a = new Appt();
	    a.setScheduledAt(dto.getDate());
	    a.setRoom(dto.getRoom());
	    a.setStatus(Status.PENDING);
	    a.setPatient(savedPatient);  // 반드시 저장된 객체로 연결

	    userRepository.findById(dto.getDoctor()).ifPresent(a::setDoctor);

	    apptRepository.save(a); // patient_id 값 포함되어야 함

	    return "success";
	}
	
}

