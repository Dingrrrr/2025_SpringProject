package com.dita.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

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
	        @RequestParam(name = "date", required = false)
	        @DateTimeFormat(iso = ISO.DATE) LocalDate targetDate,
	        Model model, HttpServletRequest request) {
		
		    HttpSession session = request.getSession(false);
		    if (session != null) {
		        User loginUser = (User) session.getAttribute("loginUser");
		        if (loginUser != null) {
		            model.addAttribute("userName", loginUser.getUsersName());
		            model.addAttribute("usersId", loginUser.getUsersId());
		            model.addAttribute("grade", loginUser.getGrade().name());
		        }
		    }
	    // 1) 날짜 처리
	    if (targetDate == null) {
	        targetDate = LocalDate.now();
	    }
	    model.addAttribute("targetDate", targetDate);

	    // 2) Appt 리스트 (접수현황)
	    List<Appt> appts = arepo.findByScheduledAtBetween(
	        targetDate.atStartOfDay(), targetDate.plusDays(1).atStartOfDay());
	    model.addAttribute("appts", appts);

	    // 3) Patient 전체 리스트 → 예약현황
	    List<Patient> reservations = repo.findAll();
	    model.addAttribute("reservations", reservations);

	    // ✅ Appt에 포함된 환자 ID 수집
	    Set<Integer> apptPatientIds = appts.stream()
	        .map(appt -> appt.getPatient().getPatientId())
	        .collect(Collectors.toSet());

	    // 4) 의사 목록
	    List<User> doctors = userRepository.findByGrade(Grade.의사);
	    model.addAttribute("doctors", doctors);

	    return "acceptance/acceptanceHome";
	}

		
	@GetMapping("/acceptanceDoctor")
	public String AcceptanceDoctorPage(Model model, HttpServletRequest request) {
		HttpSession session = request.getSession(false);
	    if (session != null) {
	        User loginUser = (User) session.getAttribute("loginUser");
	        if (loginUser != null) {
	            model.addAttribute("userName", loginUser.getUsersName());
	            model.addAttribute("usersId", loginUser.getUsersId());
	            model.addAttribute("grade", loginUser.getGrade().name());
	        }
	    }
	    LocalDate today = LocalDate.now();
	    LocalDateTime startOfDay = today.atStartOfDay();
	    LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();

	    model.addAttribute("room1Appts", apptRepository.findByRoomAndScheduledAtToday("진료실1", startOfDay, endOfDay));
	    model.addAttribute("room2Appts", apptRepository.findByRoomAndScheduledAtToday("진료실2", startOfDay, endOfDay));
	    model.addAttribute("room3Appts", apptRepository.findByRoomAndScheduledAtToday("진료실3", startOfDay, endOfDay));
	    model.addAttribute("room4Appts", apptRepository.findByRoomAndScheduledAtToday("진료실4", startOfDay, endOfDay));

	    return "acceptance/acceptanceDoctor";
	}

	
	@GetMapping("/acceptanceCondition")
	public String AcceptanceConditionPage(Model model, HttpServletRequest request) {
		HttpSession session = request.getSession(false);
	    if (session != null) {
	        User loginUser = (User) session.getAttribute("loginUser");
	        if (loginUser != null) {
	            model.addAttribute("userName", loginUser.getUsersName());
	            model.addAttribute("usersId", loginUser.getUsersId());
	            model.addAttribute("grade", loginUser.getGrade().name());
	        }
	    }
	    LocalDate today = LocalDate.now();
	    LocalDateTime start = today.atStartOfDay();
	    LocalDateTime end = today.plusDays(1).atStartOfDay();

	    // ✅ Lazy 강제 초기화 적용
	    List<Appt> room1Appts = apptRepository.findByRoomAndScheduledAtToday("진료실1", start, end);
	    room1Appts.forEach(appt -> appt.getPatient().getPatientName()); // 👈 핵심 한 줄
	    model.addAttribute("room1Appts", room1Appts);

	    List<Appt> room2Appts = apptRepository.findByRoomAndScheduledAtToday("진료실2", start, end);
	    room2Appts.forEach(appt -> appt.getPatient().getPatientName());
	    model.addAttribute("room2Appts", room2Appts);

	    List<Appt> room3Appts = apptRepository.findByRoomAndScheduledAtToday("진료실3", start, end);
	    room3Appts.forEach(appt -> appt.getPatient().getPatientName());
	    model.addAttribute("room3Appts", room3Appts);

	    List<Appt> room4Appts = apptRepository.findByRoomAndScheduledAtToday("진료실4", start, end);
	    room4Appts.forEach(appt -> appt.getPatient().getPatientName());
	    model.addAttribute("room4Appts", room4Appts);

	    return "acceptance/acceptanceCondition";
	}


		@GetMapping("/AcceptanceReceipt")
	    public String showAcceptanceReceiptPage(@RequestParam(name="date", required = false)
		@DateTimeFormat(iso = ISO.DATE) LocalDate targetDate, Model model, HttpServletRequest request) {
			HttpSession session = request.getSession(false);
		    if (session != null) {
		        User loginUser = (User) session.getAttribute("loginUser");
		        if (loginUser != null) {
		            model.addAttribute("userName", loginUser.getUsersName());
		            model.addAttribute("usersId", loginUser.getUsersId());
		            model.addAttribute("grade", loginUser.getGrade().name());
		        }
		    }
			if(targetDate == null) {
				targetDate = LocalDate.now();
			}
			model.addAttribute("targetDate", targetDate);
			
			LocalDateTime startOfDay = targetDate.atStartOfDay();
	        LocalDateTime endOfDay = targetDate.plusDays(1).atStartOfDay();
	        List<Appt> appts = arepo.findByScheduledAtBetween(startOfDay, endOfDay);
	        model.addAttribute("appts", appts);
			
	        return "acceptance/AcceptanceReceipt"; 

	    }
		
		@GetMapping("/acceptanceReception")
	    public String showAcceptanceReceptionPage(Model model, HttpServletRequest request) {
			HttpSession session = request.getSession(false);
		    if (session != null) {
		        User loginUser = (User) session.getAttribute("loginUser");
		        if (loginUser != null) {
		            model.addAttribute("userName", loginUser.getUsersName());
		            model.addAttribute("usersId", loginUser.getUsersId());
		            model.addAttribute("grade", loginUser.getGrade().name());
		        }
		    }
			// 필요 시 model에 데이터 추가 가능
	        return "acceptance/acceptanceReception"; 

	    }
		
		@GetMapping("/api/doctors")
		@ResponseBody
		public List<Map<String, String>> getDoctors() {
		    return userRepository.findByGrade(Grade.의사)
		        .stream()
		        .map(user -> {
		            Map<String, String> map = new HashMap<>();
		            map.put("usersId", user.getUsersId());
		            map.put("usersName", user.getUsersName());
		            return map;
		        })
		        .collect(Collectors.toList());
		}
		
		@PostMapping("acceptanceHome")
		public String registerPatient(@RequestParam String patientName,
		                              @RequestParam String patientBirth,
		                              @RequestParam String patientPhone,
		                              @RequestParam String patientSymptom,
		                              @RequestParam String patientGender,
		                              @RequestParam PatientType patientType,
		                              @RequestParam String patientAddress,
		                              @RequestParam(required = false) String room,
		                              @RequestParam(required = false) String visitTime,
		                              @RequestParam(required = false) String doctorId) {

		    // 1. 환자 정보 저장
		    Patient patient = new Patient();
		    patient.setPatientName(patientName);
		    patient.setPatientBirth(patientBirth);
		    patient.setPatientPhone(patientPhone);
		    patient.setPatientSymptom(patientSymptom);
		    patient.setPatientGender(patientGender);
		    patient.setPatientType(patientType);
		    patient.setPatientAddress(patientAddress);

		    repo.save(patient); // 저장

		    // 2. 진료대기일 경우 Appt에도 저장
		    if (patientType == PatientType.진료대기) {
		        Appt appt = new Appt();
		        appt.setPatient(patient);
		        appt.setRoom(room);
		        appt.setScheduledAt(LocalDateTime.now());
		        appt.setCreatedAt(LocalDateTime.now());
		        appt.setStatus(Status.대기);

		        if (doctorId != null && !doctorId.isBlank()) {
		            userRepository.findById(doctorId).ifPresent(appt::setDoctor);
		        }

		        apptRepository.save(appt); // 예약 테이블 저장
		    }

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
		    if (dto.getStatus() != null) {
		        appt.setStatus(dto.getStatus());
		    }
		    if (dto.getStatus() == Status.확정) {
		        Patient patient = appt.getPatient();
		        patient.setPatientType(PatientType.진료대기);
		        repo.save(patient);
		    }
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
		
		@GetMapping("/appointments")
	    @ResponseBody
	    public ResponseEntity<List<AppointmentDto>> getAppointments(
	            @RequestParam(name = "date", required = false)
	            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

	        List<Appt> apptList;

	        if (date != null) {
	            LocalDateTime startOfDay     = date.atStartOfDay();
	            LocalDateTime startOfNextDay = date.plusDays(1).atStartOfDay();
	            apptList = apptRepository.findByScheduledAtBetween(startOfDay, startOfNextDay);
	        } else {
	            apptList = apptRepository.findAll();
	        }

	        // Appt → AppointmentDto 변환
	        List<AppointmentDto> dtoList = apptList.stream().map(appt -> {
	            AppointmentDto dto = new AppointmentDto();
	            dto.setName(appt.getPatient().getPatientName());
	            dto.setRrn(appt.getPatient().getPatientBirth());
	            dto.setPhone(appt.getPatient().getPatientPhone());
	            dto.setDate(appt.getScheduledAt());
	            dto.setRoom(appt.getRoom());
	            dto.setDisease(appt.getPatient().getPatientSymptom());
	            dto.setDoctor(appt.getDoctor().getUsersName());
	            dto.setStatus(appt.getStatus());
	            return dto;
	        }).collect(Collectors.toList());
	        return ResponseEntity.ok(dtoList);
	    }
		

		    @PostMapping("/acceptance/updatePatient")
		    public String updatePatient(@ModelAttribute Patient patient, Model model) {
		        Optional<Patient> optional = repo.findById(patient.getPatientId());

		        if (optional.isPresent()) {
		            Patient existing = optional.get();

		            existing.setPatientName(patient.getPatientName());
		            existing.setPatientGender(patient.getPatientGender());
		            existing.setPatientBirth(patient.getPatientBirth());
		            existing.setPatientPhone(patient.getPatientPhone());
		            existing.setPatientSymptom(patient.getPatientSymptom());
		            existing.setPatientType(patient.getPatientType());
		            existing.setPatientAddress(patient.getPatientAddress());

		            repo.save(existing);
		        }

		        return "redirect:/acceptance/acceptanceHome";
		    }
		    
		    @PostMapping("/updatePatientType")
		    public String updatePatientType(@RequestParam Integer patientId,
		                                    @RequestParam PatientType patientType,
		                                    @RequestParam(required = false) String room,
		                                    @RequestParam(required = false) String visitTime,
		                                    @RequestParam(required = false) String doctorId) {
		        Optional<Patient> optional = repo.findById(patientId);
		        if (optional.isPresent()) {
		            Patient patient = optional.get();
		            patient.setPatientType(patientType);
		            repo.save(patient);

		            // 진료대기 상태일 경우 예약 자동 등록
		            if (patientType == PatientType.진료대기 && room != null && visitTime != null) {
		                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
		                LocalTime time = LocalTime.parse(visitTime, formatter);
		                LocalDateTime scheduledAt = LocalDateTime.of(LocalDate.now(), time);

		                Appt appt = new Appt();
		                appt.setPatient(patient);
		                appt.setRoom(room);
		                appt.setScheduledAt(scheduledAt);
		                appt.setCreatedAt(LocalDateTime.now());
		                appt.setStatus(Status.대기);

		                if (doctorId != null && !doctorId.isBlank()) {
		                    userRepository.findById(doctorId).ifPresent(appt::setDoctor);
		                }

		                apptRepository.save(appt);
		            }
		        }

		        return "redirect:/acceptance/acceptanceHome";
		    }

		    
		    
		}

