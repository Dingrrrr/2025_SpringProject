package com.dita.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.dita.domain.Appt;
import com.dita.domain.Patient;
import com.dita.domain.PatientType;
import com.dita.persistence.ApptRepository;
import com.dita.persistence.LoginPageRepository;
import com.dita.persistence.PatientRepository;
import com.dita.service.EmailService;
import com.dita.service.PatientService;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;



@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/acceptance/")
public class AcceptPageController {
	
	private final PatientService patientService;
	
	private final PatientRepository repo;
	private final ApptRepository arepo;
	
	
	//예약 환자들만 검색해서 보여줌
	@GetMapping("/acceptanceHome")
	public String showAcceptanceHomePage(
			@RequestParam(name="date", required = false)
			@DateTimeFormat(iso = ISO.DATE)
			LocalDate targetDate,
			Model model) {
		
		// 3) “예약” 상태인 Patient(환자) 목록 조회 → 예약현황
		List<Patient> reservations = repo.findByPatientType(PatientType.예약);
	    model.addAttribute("reservations", reservations);
	    
	    //날짜
	    //1) date 파라미터가 없으면 오늘 날짜를 기본값으로 설정
	    if (targetDate ==null) {
	    	targetDate = LocalDate.now();
	    }
	    
	    // 2) targetDate 의 00:00:00 과 23:59:59 범위를 LocalDateTime 으로 생성
	    model.addAttribute("targetDate", targetDate);
	    
	    // 4) scheduledAt 이 targetDate 범위(00:00~23:59) 에 속하는 Appt(예약) 목록만 조회 → 접수현황
	    List<Appt> appts = arepo.findByScheduledAtBetween(targetDate.atStartOfDay(), targetDate.plusDays(1).atStartOfDay());
	    model.addAttribute("appts", appts);
	    
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
								@RequestParam String patientBirth,
								@RequestParam String patientPhone,
								@RequestParam String patientSymptom,
								@RequestParam String patientGender,
								@RequestParam PatientType patientType,
								@RequestParam String patientAddress
	) {
		
			Patient p = new Patient();
			p.setPatientName(patientName);
			p.setPatientBirth(patientBirth);
			p.setPatientPhone(patientPhone);
			p.setPatientSymptom(patientSymptom);
			p.setPatientGender(patientGender);
			p.setPatientType(patientType);
			p.setPatientAddress(patientAddress);
		
			log.info("[AcceptPageController] 새로운 환자 등록 요청 → 이름={}, 상태={}", patientName, patientType);
			
			patientService.admitPatient(p);
			
			return "redirect:/acceptance/acceptanceHome";
	}
	
	
	
}

