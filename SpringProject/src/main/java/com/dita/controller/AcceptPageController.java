package com.dita.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

import com.dita.persistence.ApptRepository;
import com.dita.persistence.LoginPageRepository;
import com.dita.persistence.PatientRepository;
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
	private final ApptRepository arepo;
	private final ApptRepository apptRepository;
	
	public AcceptPageController(PatientRepository repo, UserRepository userRepository, ApptRepository apptRepository,ApptRepository arepo) {

		this.repo = repo;

		this.userRepository = userRepository;
		this.apptRepository = apptRepository;
		this.arepo = arepo;

	}
	
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
		public ResponseEntity<String> saveAppointment(@RequestBody AppointmentDto dto) {
		    log.info("예약 등록 요청: " + dto);

		    // 1. 환자 확인
		    Patient existing = repo.findByPatientBirth(dto.getRrn());
		    if (existing == null) {
		        log.warning("❌ 존재하지 않는 환자: " + dto.getRrn());
		        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("존재하지 않는 환자입니다.");
		    }

		    // 1-2. 환자 타입이 '예약'인지 확인
		    if (existing.getPatientType() != PatientType.예약) {
		        log.warning("❌ 예약 불가 환자 유형: " + existing.getPatientType());
		        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("예약 가능한 상태가 아닙니다.");
		    }

		    // 2. 의사 확인
		    if (dto.getDoctor() == null || dto.getDoctor().isBlank()) {
		        log.warning("❌ 의사 ID가 전달되지 않았습니다.");
		        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("의사를 선택해주세요.");
		    }

		    Optional<User> optionalDoctor = userRepository.findById(dto.getDoctor());
		    if (optionalDoctor.isEmpty()) {
		        log.warning("❌ 해당 의사 ID가 존재하지 않음: " + dto.getDoctor());
		        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("존재하지 않는 의사입니다.");
		    }

		    // 3. 예약 저장
		    Appt appt = Appt.builder()
		        .patient(existing)
		        .room(dto.getRoom())
		        .scheduledAt(dto.getDate())
		        .status(Status.대기) // 저장 시 예약 상태는 "대기"
		        .doctor(optionalDoctor.get())
		        .build();

		    apptRepository.save(appt);
		    log.info("✅ 예약 저장 완료");

		    return ResponseEntity.ok("예약이 성공적으로 등록되었습니다.");
		}



		
		@GetMapping("/searchPatientsByName")
		@ResponseBody
		public List<Map<String, String>> searchPatientsByName(@RequestParam String name) {
		    List<Map<String, String>> matches = repo.findAll().stream()
		        .filter(p -> p.getPatientName().equals(name))
		        .filter(p -> p.getPatientType() == PatientType.예약)
		        .map(p -> {
		            Map<String, String> map = new HashMap<>();
		            map.put("name", p.getPatientName());
		            map.put("rrn", p.getPatientBirth());
		            map.put("gender", p.getPatientGender());
		            map.put("phone", p.getPatientPhone());
		            return map;
		        })
		        .collect(Collectors.toList());
		    return matches;
		}
		
		@GetMapping("/appointments")
		@ResponseBody
		public List<AppointmentDto> getAppointments() {
		    List<Appt> appts = apptRepository.findAll(); // 또는 날짜 기준 필터링
		    return appts.stream().map(appt -> {
		        AppointmentDto dto = new AppointmentDto();
		        dto.setName(appt.getPatient().getPatientName());
		        dto.setRrn(appt.getPatient().getPatientBirth());
		        dto.setPhone(appt.getPatient().getPatientPhone());
		        dto.setDate(appt.getScheduledAt());
		        dto.setRoom(appt.getRoom());
		        dto.setDisease(appt.getPatient().getPatientSymptom());
		        dto.setDoctor(appt.getDoctor().getUsersName());
		        return dto;
		    }).collect(Collectors.toList());
		}
	
		
		@PutMapping("/appointment")
		@ResponseBody
		public String updateAppointment(@RequestBody AppointmentDto dto) {
		    log.info("🔁 예약 수정 요청: " + dto);

		    Optional<Appt> optionalAppt =
		            apptRepository.findByScheduledAtAndPatient_PatientBirth(dto.getOriginalDate(), dto.getRrn());
		    if (optionalAppt.isEmpty()) {
		        return "해당 예약을 찾을 수 없습니다.";
		    }

		    Appt appt = optionalAppt.get();

		    // 의사 정보 수정
		    Optional<User> doctor = userRepository.findById(dto.getDoctor());
		    if (doctor.isPresent()) {
		        appt.setDoctor(doctor.get());
		    }

		    appt.setRoom(dto.getRoom());
		    appt.setScheduledAt(dto.getDate());
		    appt.setStatus(Status.대기); // 필요시 변경
		    appt.getPatient().setPatientName(dto.getName());
		    appt.getPatient().setPatientPhone(dto.getPhone());
		    appt.getPatient().setPatientSymptom(dto.getDisease());

		    apptRepository.save(appt);

		    return "수정 완료";
		}
		
		@DeleteMapping("appointment")  // 슬래시 앞에 다시 붙이지 마세요!
		@ResponseBody
		public ResponseEntity<String> deleteAppointment(@RequestBody AppointmentDto dto) {
		    Optional<Appt> optionalAppt =
		        apptRepository.findByScheduledAtAndPatient_PatientBirth(dto.getDate(), dto.getRrn());

		    if (optionalAppt.isEmpty()) {
		        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 예약을 찾을 수 없습니다.");
		    }

		    apptRepository.delete(optionalAppt.get());
		    return ResponseEntity.ok("예약이 삭제되었습니다.");
		}

}
		

