package com.dita.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.dita.domain.Patient;
import com.dita.domain.Vital_sign;
import com.dita.domain.Nurse_chart;
import com.dita.domain.User;
import com.dita.repository.NurseChartRepository;
import com.dita.repository.VitalSignRepository;
import com.dita.persistence.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.math.BigDecimal;

import lombok.extern.java.Log;

@Controller
@Log
@RequestMapping("/nurse/")
public class NursePageController {
	
	@Autowired
	private NurseChartRepository nurseChartRepository;
	
	@Autowired
	private VitalSignRepository vitalSignRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	// ============= 페이지 이동 메서드들 =============
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
	
	// ============= NurseChart API 메서드들 =============
	
	// 환자 검색 API (간호사 차트가 있는 환자만)
	@GetMapping("/api/patients")
	@ResponseBody
	public ResponseEntity<List<Map<String, Object>>> getPatients(
			@RequestParam(required = false, defaultValue = "") String search) {
		
		List<Patient> patients;
		
		if (search.trim().isEmpty()) {
			patients = nurseChartRepository.findAllPatientsWithCharts();
		} else {
			patients = nurseChartRepository.findPatientsBySearchTerm(search.trim());
		}
		
		List<Map<String, Object>> result = patients.stream()
			.map(this::convertPatientToMap)
			.collect(Collectors.toList());
			
		return ResponseEntity.ok(result);
	}
	
	// 특정 환자의 간호사 차트 그룹 조회 (날짜별)
	@GetMapping("/api/chart-groups/{patientId}")
	@ResponseBody
	public ResponseEntity<List<Map<String, Object>>> getChartGroups(@PathVariable int patientId) {
		
		List<Object[]> chartGroups = nurseChartRepository.findChartGroupsByPatient(patientId);
		
		List<Map<String, Object>> result = chartGroups.stream()
			.map(group -> {
				Map<String, Object> map = new HashMap<>();
				map.put("date", group[0].toString());
				map.put("chartCount", group[1]);
				map.put("status", "complete");
				return map;
			})
			.collect(Collectors.toList());
			
		return ResponseEntity.ok(result);
	}
	
	// 특정 날짜의 환자 간호사 차트 상세 조회
	@GetMapping("/api/chart-details/{patientId}")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getChartDetails(
			@PathVariable int patientId,
			@RequestParam String date) {
		
		try {
			LocalDateTime targetDate = LocalDate.parse(date).atStartOfDay();
			List<Nurse_chart> charts = nurseChartRepository.findByPatientAndDate(patientId, targetDate);
			
			Map<String, Object> result = new HashMap<>();
			
			if (!charts.isEmpty()) {
				// 시간대별로 분류하여 바이탈 정보 정리
				Map<String, Map<String, Object>> timeGroups = new HashMap<>();
				
				for (Nurse_chart chart : charts) {
					String timeSlot = getTimeSlot(chart.getEntryTime());
					Vital_sign vital = chart.getVitalId();
					
					Map<String, Object> chartData = new HashMap<>();
					chartData.put("chartId", chart.getChartId());
					chartData.put("entryTime", chart.getEntryTime().toString());
					
					// 연결된 바이탈 사인 정보
					if (vital != null) {
						chartData.put("vitalId", vital.getVitalId());
						chartData.put("temperature", vital.getTemperature());
						chartData.put("bpSystolic", vital.getBpSystolic());
						chartData.put("bpDiastolic", vital.getBpDiastolic());
						chartData.put("pulseRate", vital.getPulseRate());
						chartData.put("respirationRate", vital.getRespirationRate());
						chartData.put("recordedAt", vital.getRecordedAt().toString());
					}
					
					timeGroups.put(timeSlot, chartData);
				}
				
				result.put("chartRecords", timeGroups);
				result.put("patientId", patientId);
				result.put("date", date);
			}
			
			return ResponseEntity.ok(result);
			
		} catch (Exception e) {
			log.severe("차트 상세 조회 오류: " + e.getMessage());
			return ResponseEntity.badRequest().build();
		}
	}
	
	// 새로운 간호사 차트 저장 (바이탈 사인과 함께)
	@PostMapping("/api/nurse-charts")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> saveNurseChart(
			@RequestBody Map<String, Object> chartData) {
		
		try {
			int patientId = (Integer) chartData.get("patientId");
			int nurseId = (Integer) chartData.get("nurseId");
			
			// 환자 정보 조회
			Patient patient = nurseChartRepository.findPatientById(patientId);
			User nurse = userRepository.findById(nurseId).orElse(null);
			
			if (patient == null || nurse == null) {
				Map<String, Object> error = new HashMap<>();
				error.put("success", false);
				error.put("message", "환자 또는 간호사 정보를 찾을 수 없습니다.");
				return ResponseEntity.badRequest().body(error);
			}
			
			// 시간대별 바이탈 사인 및 간호사 차트 저장
			Map<String, Object> timeSlots = (Map<String, Object>) chartData.get("timeSlots");
			List<Nurse_chart> savedCharts = new ArrayList<>();
			
			for (Map.Entry<String, Object> entry : timeSlots.entrySet()) {
				String timeSlot = entry.getKey();
				Map<String, Object> vitalInfo = (Map<String, Object>) entry.getValue();
				
				if (hasVitalData(vitalInfo)) {
					// 1. 먼저 바이탈 사인 생성
					Vital_sign vital = Vital_sign.builder()
						.patient(patient)
						.nurse(nurse)
						.recordedAt(getTimeSlotDateTime(timeSlot))
						.temperature(getBigDecimalValue(vitalInfo, "temperature"))
						.bpSystolic(getIntValue(vitalInfo, "bpSystolic"))
						.bpDiastolic(getIntValue(vitalInfo, "bpDiastolic"))
						.pulseRate(getIntValue(vitalInfo, "pulseRate"))
						.respirationRate(getIntValue(vitalInfo, "respirationRate"))
						.build();
					
					Vital_sign savedVital = vitalSignRepository.save(vital);
					
					// 2. 바이탈 사인을 참조하는 간호사 차트 생성
					Nurse_chart chart = Nurse_chart.builder()
						.nurse(nurse)
						.vitalId(savedVital)
						.build();
					
					savedCharts.add(nurseChartRepository.save(chart));
				}
			}
			
			Map<String, Object> result = new HashMap<>();
			result.put("success", true);
			result.put("savedCount", savedCharts.size());
			result.put("message", savedCharts.size() + "개의 간호사 차트가 저장되었습니다.");
			
			log.info("간호사 차트 저장 성공: " + savedCharts.size() + "개");
			return ResponseEntity.ok(result);
			
		} catch (Exception e) {
			log.severe("차트 저장 오류: " + e.getMessage());
			Map<String, Object> error = new HashMap<>();
			error.put("success", false);
			error.put("message", "저장 중 오류가 발생했습니다: " + e.getMessage());
			return ResponseEntity.badRequest().body(error);
		}
	}
	
	// ============= Helper Methods =============
	
	private Map<String, Object> convertPatientToMap(Patient patient) {
		Map<String, Object> map = new HashMap<>();
		map.put("patientId", patient.getPatientId());
		map.put("patientName", patient.getPatientName());
		map.put("patientGender", patient.getPatientGender().toString());
		map.put("patientBirth", patient.getPatientBirth());
		map.put("patientPhone", patient.getPatientPhone());
		return map;
	}
	
	private String getTimeSlot(LocalDateTime dateTime) {
		int hour = dateTime.getHour();
		if (hour >= 6 && hour < 12) return "morning";
		else if (hour >= 12 && hour < 18) return "lunch";
		else if (hour >= 18 && hour < 24) return "evening";
		else return "night";
	}
	
	private LocalDateTime getTimeSlotDateTime(String timeSlot) {
		LocalDateTime now = LocalDateTime.now();
		switch (timeSlot) {
			case "morning": return now.withHour(9).withMinute(0).withSecond(0);
			case "lunch": return now.withHour(13).withMinute(0).withSecond(0);
			case "evening": return now.withHour(19).withMinute(0).withSecond(0);
			case "night": return now.withHour(22).withMinute(0).withSecond(0);
			default: return now;
		}
	}
	
	private boolean hasVitalData(Map<String, Object> vitalInfo) {
		return vitalInfo.get("temperature") != null || 
			   vitalInfo.get("bpSystolic") != null || 
			   vitalInfo.get("pulseRate") != null || 
			   vitalInfo.get("respirationRate") != null;
	}
	
	private BigDecimal getBigDecimalValue(Map<String, Object> map, String key) {
		Object value = map.get(key);
		if (value == null || value.toString().trim().isEmpty()) return null;
		try {
			return new BigDecimal(value.toString());
		} catch (NumberFormatException e) {
			return null;
		}
	}
	
	private Integer getIntValue(Map<String, Object> map, String key) {
		Object value = map.get(key);
		if (value == null || value.toString().trim().isEmpty()) return null;
		try {
			return Integer.parseInt(value.toString());
		} catch (NumberFormatException e) {
			return null;
		}
	}
}