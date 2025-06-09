package com.dita.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dita.domain.Grade;
import com.dita.domain.Patient;
import com.dita.domain.TimePeriod;
import com.dita.domain.User;
import com.dita.domain.Vital_sign;
import com.dita.persistence.NurseChartRepository;
import com.dita.persistence.PatientRepository;
import com.dita.persistence.UserRepository;
import com.dita.vo.ChartSaveRequestDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

@Service
@Log
@RequiredArgsConstructor
public class NurseChartService {
    
    private final NurseChartRepository nurseChartRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    
    /**
     * 입원중인 환자 목록을 조회합니다.
     */
    public List<Map<String, Object>> getInpatients() {
        try {
            log.info("입원 환자 목록 조회 시작");
            List<Map<String, Object>> result = nurseChartRepository.findInpatients();
            log.info("입원 환자 " + result.size() + "명 조회 완료");
            return result;
        } catch (Exception e) {
            log.severe("입원 환자 목록 조회 중 오류: " + e.getMessage());
            throw new RuntimeException("입원 환자 목록 조회에 실패했습니다.", e);
        }
    }
    
    /**
     * 특정 환자의 차트 목록을 조회합니다.
     */
    public List<Map<String, Object>> getPatientCharts(int patientId) {
        try {
            log.info("환자 " + patientId + "의 차트 목록 조회 시작");
            List<Map<String, Object>> result = nurseChartRepository.findPatientCharts(patientId);
            log.info("환자 " + patientId + "의 차트 " + result.size() + "개 조회 완료");
            return result;
        } catch (Exception e) {
            log.severe("환자 차트 목록 조회 중 오류: " + e.getMessage());
            throw new RuntimeException("환자 차트 목록 조회에 실패했습니다.", e);
        }
    }
    
    /**
     * 바이탈 사인을 저장합니다.
     */
    @Transactional
    public int saveVitalSigns(ChartSaveRequestDto request) {
        try {
            log.info("바이탈 사인 저장 시작 - 환자ID: " + request.getPatientId());
            
            // 간호사 계정 조회
            List<User> nurses = userRepository.findByGrade(Grade.간호사);
            if (nurses.isEmpty()) {
                throw new RuntimeException("간호사 계정이 존재하지 않습니다.");
            }
            User nurse = nurses.get(0); // 첫 번째 간호사 선택
            
            // 환자 정보 조회
            Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new RuntimeException("환자 정보를 찾을 수 없습니다."));
            
            TimePeriod[] timePeriods = {TimePeriod.아침, TimePeriod.점심, TimePeriod.저녁, TimePeriod.야간};
            int savedCount = 0;
            
            // 날짜 파싱
            LocalDate recordedDate;
            try {
                recordedDate = LocalDate.parse(request.getRecordedDate());
            } catch (Exception e) {
                log.warning("recordedDate 파싱 실패(" + request.getRecordedDate() + "), 오늘 날짜 사용");
                recordedDate = LocalDate.now();
            }
            
            // 각 시간대별로 데이터 저장
            for (TimePeriod timePeriod : timePeriods) {
                if (!hasDataForTimePeriod(request.getData(), timePeriod.name())) {
                    continue; // 해당 시간대 데이터가 없으면 건너뜀
                }
                
                // 기존 데이터 확인
                Vital_sign existingVital = nurseChartRepository.findByPatientAndDateAndTimePeriod(
                    request.getPatientId(), recordedDate, timePeriod);
                
                Vital_sign vitalSign;
                if (existingVital != null) {
                    vitalSign = existingVital; // 업데이트
                } else {
                    vitalSign = new Vital_sign(); // 새로 생성
                    vitalSign.setPatient(patient);
                    vitalSign.setNurse(nurse);
                    vitalSign.setRecordedDate(recordedDate);
                    vitalSign.setTimePeriod(timePeriod);
                }
                
                // 데이터 설정
                setVitalSignData(vitalSign, request.getData(), timePeriod.name());
                vitalSign.setRecordedAt(LocalDateTime.now());
                
                // 저장
                nurseChartRepository.save(vitalSign);
                savedCount++;
                
                log.info(timePeriod.name() + " 시간대 바이탈 사인 저장 완료");
            }
            
            log.info("바이탈 사인 저장 완료 - 총 " + savedCount + "개 레코드 저장");
            return savedCount;
            
        } catch (Exception e) {
            log.severe("바이탈 사인 저장 중 오류: " + e.getMessage());
            throw new RuntimeException("바이탈 사인 저장에 실패했습니다.", e);
        }
    }
    
    /**
     * 바이탈 사인 엔티티에 데이터를 설정합니다.
     */
    private void setVitalSignData(Vital_sign vitalSign, Map<String, Map<String, String>> data, String timePeriod) {
        // 혈압 설정
        String bpValue = data.get("혈압").get(timePeriod);
        if (bpValue != null && bpValue.contains("/")) {
            String[] bp = bpValue.split("/");
            try {
                vitalSign.setBpSystolic(Integer.parseInt(bp[0].trim()));
                vitalSign.setBpDiastolic(Integer.parseInt(bp[1].trim()));
            } catch (NumberFormatException e) {
                log.warning("혈압 파싱 실패(" + bpValue + ")");
            }
        }
        
        // 체온 설정
        BigDecimal temperature = parseDecimal(data.get("체온").get(timePeriod));
        if (temperature != null) {
            vitalSign.setTemperature(temperature);
        }
        
        // 맥박수 설정
        Integer pulseRate = parseInt(data.get("심박수").get(timePeriod));
        if (pulseRate != null) {
            vitalSign.setPulseRate(pulseRate);
        }
        
        // 호흡수 설정
        Integer respirationRate = parseInt(data.get("호흡수").get(timePeriod));
        if (respirationRate != null) {
            vitalSign.setRespirationRate(respirationRate);
        }
    }
    
    /**
     * 특정 시간대에 데이터가 있는지 확인합니다.
     */
    private boolean hasDataForTimePeriod(Map<String, Map<String, String>> data, String timePeriod) {
        return data.values().stream()
                .anyMatch(timeData -> {
                    String value = timeData.get(timePeriod);
                    return value != null && !value.trim().isEmpty();
                });
    }
    
    /**
     * 문자열을 BigDecimal로 파싱합니다.
     */
    private BigDecimal parseDecimal(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    /**
     * 문자열을 Integer로 파싱합니다.
     */
    private Integer parseInt(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    /**
     * 특정 날짜의 차트 상세 정보를 조회합니다.
     */
    public Map<String, Object> getChartDetail(int patientId, String recordedDate) {
        try {
            List<Map<String, Object>> vitalSigns = nurseChartRepository.findVitalSignsByPatientAndDate(patientId, recordedDate);
            
            // 데이터를 프론트엔드 형식으로 변환
            Map<String, Map<String, String>> data = new HashMap<>();
            data.put("혈압", new HashMap<>());
            data.put("심박수", new HashMap<>());
            data.put("체온", new HashMap<>());
            data.put("호흡수", new HashMap<>());
            
            for (Map<String, Object> vital : vitalSigns) {
                String timePeriod = (String) vital.get("time_period");
                
                // 혈압 조합
                Integer systolic = (Integer) vital.get("bp_systolic");
                Integer diastolic = (Integer) vital.get("bp_diastolic");
                if (systolic != null && diastolic != null) {
                    data.get("혈압").put(timePeriod, systolic + "/" + diastolic);
                }
                
                // 다른 값들
                if (vital.get("pulse_rate") != null) {
                    data.get("심박수").put(timePeriod, vital.get("pulse_rate").toString());
                }
                if (vital.get("temperature") != null) {
                    data.get("체온").put(timePeriod, vital.get("temperature").toString());
                }
                if (vital.get("respiration_rate") != null) {
                    data.get("호흡수").put(timePeriod, vital.get("respiration_rate").toString());
                }
            }
            
            return Map.of("data", data);
            
        } catch (Exception e) {
            log.severe("차트 상세 조회 실패: " + e.getMessage());
            throw new RuntimeException("차트 상세 조회 실패", e);
        }
    }
    
    /**
     * 환자의 최신 바이탈 사인을 조회합니다.
     */
    public Map<String, Object> getLatestVitalSigns(int patientId) {
        try {
            Map<String, Object> latestVital = nurseChartRepository.findLatestVitalSigns(patientId);
            
            if (latestVital == null || latestVital.isEmpty()) {
                log.info("환자 " + patientId + "의 오늘 바이탈 데이터 없음");
                return new HashMap<>();
            }
            
            log.info("환자 " + patientId + "의 오늘 최신 바이탈 데이터 조회 완료");
            return latestVital;
            
        } catch (Exception e) {
            log.severe("최신 바이탈 사인 조회 중 오류: " + e.getMessage());
            throw new RuntimeException("최신 바이탈 사인 조회에 실패했습니다.", e);
        }
    }
    
    /**
     * 환자의 차트용 바이탈 데이터를 조회합니다 (시간대별/주간별).
     */
    public Map<String, Object> getVitalChartData(int patientId) {
        try {
            log.info("환자 " + patientId + "의 차트 데이터 조회 시작");
            
            // 오늘 시간대별 데이터
            List<Map<String, Object>> timeData = nurseChartRepository.findTodayVitalsByTimePeriod(patientId);
            
            // 최근 7일 일별 평균 데이터
            List<Map<String, Object>> dailyData = nurseChartRepository.findWeeklyVitalAverages(patientId);
            
            log.info("조회된 시간대별 데이터: " + timeData.size() + "개 (오늘 날짜만)");
            log.info("조회된 일별 데이터: " + dailyData.size() + "개");
            
            // 디버깅용 로그
            timeData.forEach(data -> log.info("오늘 시간대별 데이터: " + data.toString()));
            dailyData.forEach(data -> log.info("일별 데이터: " + data.toString()));
            
            return Map.of(
                "timeData", timeData,
                "dailyData", dailyData
            );
            
        } catch (Exception e) {
            log.severe("바이탈 차트 데이터 조회 중 오류: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("바이탈 차트 데이터 조회에 실패했습니다.", e);
        }
    }
}