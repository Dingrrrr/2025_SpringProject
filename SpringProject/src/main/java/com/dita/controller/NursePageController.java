package com.dita.controller;


import java.security.Principal;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dita.persistence.NurseChartRepository;
import com.dita.vo.ChartSaveRequestDto;

import com.dita.domain.Grade;
import com.dita.domain.User;
import com.dita.persistence.LoginPageRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.java.Log;

@Controller
@Log
@RequestMapping("/nurse/")
public class NursePageController {
	
	private final LoginPageRepository repo;

    public NursePageController(LoginPageRepository repo) {
        this.repo = repo;
    }
	
	@GetMapping("/NurseChart")
    @Autowired
    private NurseChartRepository nurseChartRepository;

    @GetMapping("/NurseChart")
    public String showNurseChartPage(Model model) {
        return "nurse/NurseChart";
    }

    @GetMapping("/VitalRecord")
    public String showVitalRecordPage(Model model) {
        return "nurse/VitalRecord";
    }

    @GetMapping("/MedicationRecord")
    public String showMedicatonRecordPage(Model model) {
        return "nurse/MedicationRecord";
    }

    // 입원 환자 목록 API
    @GetMapping("/api/patients/inpatients")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getInpatients() {
        try {
            log.info("입원 환자 목록 요청");
            List<Map<String, Object>> inpatients = nurseChartRepository.getInpatients();
            log.info("입원 환자 " + inpatients.size() + "명 조회 완료");
            return ResponseEntity.ok(inpatients);
        } catch (Exception e) {
            log.severe("입원 환자 목록 조회 실패: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }
    // 특정 환자의 차트 목록 API 추가
    @GetMapping("/api/charts/patient/{patientId}")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getPatientCharts(@PathVariable int patientId) {
        try {
            log.info("환자 " + patientId + "의 차트 목록 요청");
            List<Map<String, Object>> charts = nurseChartRepository.getPatientCharts(patientId);
            log.info("환자 " + patientId + "의 차트 " + charts.size() + "개 조회 완료");
            return ResponseEntity.ok(charts);
        } catch (Exception e) {
            log.severe("환자 차트 목록 조회 실패: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }
 // 차트 저장 API 추가
    @PostMapping("/api/charts/save")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> saveChart(@RequestBody ChartSaveRequestDto request) {
        try {
            log.info("차트 저장 요청 - 환자ID: " + request.getPatientId());
            
            int savedCount = nurseChartRepository.saveVitalSigns(request);
            
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
    
 // 특정 날짜의 차트 상세 조회 API
    @GetMapping("/api/charts/detail/{patientId}/{recordedDate}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getChartDetail(@PathVariable int patientId, @PathVariable String recordedDate) {
        try {
            Map<String, Object> chartDetail = nurseChartRepository.getChartDetail(patientId, recordedDate);
            return ResponseEntity.ok(chartDetail);
        } catch (Exception e) {
            log.severe("차트 상세 조회 실패: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }
	
	@GetMapping("/NurseHome")
	public String showNurseHome(HttpServletRequest request, Model model) {
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
