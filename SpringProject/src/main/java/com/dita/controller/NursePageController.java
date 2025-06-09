package com.dita.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dita.domain.Grade;
import com.dita.domain.User;
import com.dita.persistence.LoginPageRepository;
import com.dita.persistence.UserRepository;
import com.dita.service.NotifService;
import com.dita.service.NurseChartService;
import com.dita.vo.ChartSaveRequestDto;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

@Controller
@Log
@RequestMapping("/nurse/")
@RequiredArgsConstructor
public class NursePageController {
	
	private final NotifService notifService;
	private final LoginPageRepository repo;
	private final UserRepository userRepository;
	private final NurseChartService nurseChartService; // Repository 대신 Service 주입

    @GetMapping("/NurseChart")
    public String showNurseChartPage(HttpServletRequest request, Model model) {
    	HttpSession session = request.getSession(false);
		if (session == null) {
			return "redirect:/Login/Login";
		}
		User loginUser = (User) session.getAttribute("loginUser");
		if (loginUser == null || !loginUser.getGrade().equals(Grade.간호사)) {
			return "redirect:/Login/Login";
		}
		
		model.addAttribute("userName", loginUser.getUsersName());
        model.addAttribute("usersId", loginUser.getUsersId());
        model.addAttribute("grade", loginUser.getGrade().name());
        return "nurse/NurseChart";
    }

    @GetMapping("/VitalRecord")
    public String showVitalRecordPage(HttpServletRequest request, Model model) {
    	HttpSession session = request.getSession(false);
		if (session == null) {
			return "redirect:/Login/Login";
		}
		User loginUser = (User) session.getAttribute("loginUser");
		if (loginUser == null || !loginUser.getGrade().equals(Grade.간호사)) {
			return "redirect:/Login/Login";
		}
		
		model.addAttribute("userName", loginUser.getUsersName());
        model.addAttribute("usersId", loginUser.getUsersId());
        model.addAttribute("grade", loginUser.getGrade().name());
        return "nurse/VitalRecord";
    }

    @GetMapping("/MedicationRecord")
    public String showMedicatonRecordPage(HttpServletRequest request, Model model) {
    	HttpSession session = request.getSession(false);
		if (session == null) {
			return "redirect:/Login/Login";
		}
		User loginUser = (User) session.getAttribute("loginUser");
		if (loginUser == null || !loginUser.getGrade().equals(Grade.간호사)) {
			return "redirect:/Login/Login";
		}
		
		model.addAttribute("userName", loginUser.getUsersName());
        model.addAttribute("usersId", loginUser.getUsersId());
        model.addAttribute("grade", loginUser.getGrade().name());
        return "nurse/MedicationRecord";
    }

    // ============= API 엔드포인트들 (Service 사용으로 변경) =============
    
    /**
     * 입원 환자 목록 API
     */
    @GetMapping("/api/patients/inpatients")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getInpatients() {
        try {
            log.info("입원 환자 목록 요청");
            List<Map<String, Object>> inpatients = nurseChartService.getInpatients();
            log.info("입원 환자 " + inpatients.size() + "명 조회 완료");
            return ResponseEntity.ok(inpatients);
        } catch (Exception e) {
            log.severe("입원 환자 목록 조회 실패: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * 특정 환자의 차트 목록 API
     */
    @GetMapping("/api/charts/patient/{patientId}")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getPatientCharts(@PathVariable int patientId) {
        try {
            log.info("환자 " + patientId + "의 차트 목록 요청");
            List<Map<String, Object>> charts = nurseChartService.getPatientCharts(patientId);
            log.info("환자 " + patientId + "의 차트 " + charts.size() + "개 조회 완료");
            return ResponseEntity.ok(charts);
        } catch (Exception e) {
            log.severe("환자 차트 목록 조회 실패: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * 차트 저장 API
     */
    @PostMapping("/api/charts/save")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> saveChart(@RequestBody ChartSaveRequestDto request) {
        try {
            log.info("차트 저장 요청 - 환자ID: " + request.getPatientId());
            
            int savedCount = nurseChartService.saveVitalSigns(request);
            
            Map<String, Object> response = Map.of(
                "success", true,
                "message", "차트가 성공적으로 저장되었습니다.",
                "savedRecords", savedCount
            );
            
            log.info("차트 저장 완료 - " + savedCount + "개 레코드 저장");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.severe("차트 저장 실패: " + e.getMessage());
            Map<String, Object> errorResponse = Map.of(
                "success", false,
                "message", "차트 저장 중 오류가 발생했습니다: " + e.getMessage()
            );
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    /**
     * 특정 날짜의 차트 상세 조회 API
     */
    @GetMapping("/api/charts/detail/{patientId}/{recordedDate}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getChartDetail(@PathVariable int patientId, 
                                                             @PathVariable String recordedDate) {
        try {
            log.info("차트 상세 조회 요청 - 환자ID: " + patientId + ", 날짜: " + recordedDate);
            Map<String, Object> chartDetail = nurseChartService.getChartDetail(patientId, recordedDate);
            return ResponseEntity.ok(chartDetail);
        } catch (Exception e) {
            log.severe("차트 상세 조회 실패: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * 환자의 최신 바이탈 사인 조회 API
     */
    @GetMapping("/api/vitals/latest/{patientId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getLatestVitals(@PathVariable int patientId) {
        try {
            log.info("환자 " + patientId + "의 최신 바이탈 사인 요청");
            Map<String, Object> latestVital = nurseChartService.getLatestVitalSigns(patientId);
            return ResponseEntity.ok(latestVital);
        } catch (Exception e) {
            log.severe("최신 바이탈 사인 조회 실패: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 환자의 바이탈 차트 데이터 조회 API
     */
    @GetMapping("/api/vitals/chart-data/{patientId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getVitalChartData(@PathVariable int patientId) {
        try {
            log.info("환자 " + patientId + "의 차트 데이터 요청");
            Map<String, Object> chartData = nurseChartService.getVitalChartData(patientId);
            return ResponseEntity.ok(chartData);
        } catch (Exception e) {
            log.severe("바이탈 차트 데이터 조회 실패: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }
    
    /**
     * 간호사 홈 페이지
     */
    @GetMapping("/NurseHome")
    public String showNurseHome(HttpServletRequest request, Authentication authentication, Model model) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return "redirect:/Login/Login";
        }
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null || !loginUser.getGrade().equals(Grade.간호사)) {
            return "redirect:/Login/Login";
        }
        
        model.addAttribute("userName", loginUser.getUsersName());
        model.addAttribute("usersId", loginUser.getUsersId());
        model.addAttribute("grade", loginUser.getGrade().name());
        
        return "nurse/NurseHome";
    }
}