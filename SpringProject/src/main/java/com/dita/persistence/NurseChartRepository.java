package com.dita.persistence;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.dita.vo.ChartSaveRequestDto;

import lombok.extern.java.Log;

@Repository
@Log
public class NurseChartRepository {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    /**
     * 입원중인 환자 목록을 조회합니다.
     * @return 입원 환자 정보 리스트 (patient_id, patient_name, patient_gender, patient_birth, patient_phone)
     */
    public List<Map<String, Object>> getInpatients() {
        try {
            log.info("DB에서 입원 환자 목록 조회 시작");
            
            String sql = "SELECT patient_id, patient_name, patient_gender, patient_birth, patient_phone " +
                        "FROM patient WHERE patient_type = '입원중' " +
                        "ORDER BY patient_name";
            
            List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
            
            log.info("DB에서 입원 환자 " + result.size() + "명 조회 완료");
            return result;
            
        } catch (Exception e) {
            log.severe("DB 입원 환자 목록 조회 중 오류: " + e.getMessage());
            throw new RuntimeException("입원 환자 목록 조회에 실패했습니다.", e);
        }
    }
    
    public List<Map<String, Object>> getPatientCharts(int patientId) {
        try {
            log.info("DB에서 환자 " + patientId + "의 차트 목록 조회 시작");
            
            String sql = "SELECT recorded_date, " +
                        "DATE_FORMAT(MAX(recorded_at), '%H:%i') as latest_time, " +  // 가장 최근 작성 시간
                        "COUNT(*) as record_count " +
                        "FROM vital_sign WHERE patient_id = ? " +
                        "GROUP BY recorded_date " +
                        "ORDER BY recorded_date DESC";
            
            List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, patientId);
            
            log.info("DB에서 환자 " + patientId + "의 차트 " + result.size() + "개 조회 완료");
            return result;
        } catch (Exception e) {
            log.severe("DB 환자 차트 목록 조회 중 오류: " + e.getMessage());
            throw new RuntimeException("환자 차트 목록 조회에 실패했습니다.", e);
        }
    }

    // 바이탈 사인 저장 메서드 추가
    public int saveVitalSigns(ChartSaveRequestDto request) {
        try {
            log.info("바이탈 사인 저장 시작 - 환자ID: " + request.getPatientId());
            
            String nurseId;
            try {
            	String sqlNurse = "SELECT users_id FROM users WHERE grade = ? LIMIT 1";
                nurseId = jdbcTemplate.queryForObject(sqlNurse, new Object[]{"간호사"}, String.class);
                log.info("조회된 간호사 ID: " + nurseId);
			} catch (Exception e) {
				log.severe("간호사 조회 실패: " + e.getMessage());
                throw new RuntimeException("간호사 계정이 존재하지 않습니다.", e);
			}
            
            String sql = "INSERT INTO vital_sign " +
                    "(patient_id, users_id, recorded_date, time_period, temperature, bp_systolic, bp_diastolic, pulse_rate, respiration_rate, recorded_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW()) " +
                    "ON DUPLICATE KEY UPDATE " +
                    "temperature = VALUES(temperature), " +
                    "bp_systolic = VALUES(bp_systolic), " +
                    "bp_diastolic = VALUES(bp_diastolic), " +
                    "pulse_rate = VALUES(pulse_rate), " +
                    "respiration_rate = VALUES(respiration_rate), " +
                    "recorded_at = NOW()";

            String[] timePeriods = {"아침", "점심", "저녁", "야간"};
            int savedCount = 0;

            // 날짜 파싱
            LocalDate recordedDate;
            try {
                recordedDate = LocalDate.parse(request.getRecordedDate());
            } catch (Exception e) {
                log.warning("recordedDate 파싱 실패(" + request.getRecordedDate() + "), 오늘 날짜 사용");
                recordedDate = LocalDate.now();
            }

            // ─────────────────────────────────────────────────────────
            // 3) 각 시간대별로 데이터 유무 체크 후 INSERT/UPDATE
            // ─────────────────────────────────────────────────────────
            for (String timePeriod : timePeriods) {
                if (!hasDataForTimePeriod(request.getData(), timePeriod)) {
                    continue; // 해당 시간대 데이터가 없으면 건너뜀
                }

                // 혈압 파싱 (예: "120/80" → 120, 80)
                String bpValue = request.getData().get("혈압").get(timePeriod);
                Integer bpSystolic = null;
                Integer bpDiastolic = null;
                if (bpValue != null && bpValue.contains("/")) {
                    String[] bp = bpValue.split("/");
                    try {
                        bpSystolic = Integer.parseInt(bp[0].trim());
                        bpDiastolic = Integer.parseInt(bp[1].trim());
                    } catch (NumberFormatException e) {
                        log.warning("혈압 파싱 실패(" + bpValue + ")");
                    }
                }

                BigDecimal temperature = parseDecimal(request.getData().get("체온").get(timePeriod));
                Integer pulseRate = parseInt(request.getData().get("심박수").get(timePeriod));
                Integer respirationRate = parseInt(request.getData().get("호흡수").get(timePeriod));

                // ─────────────────────────────────────────────────────────
                // 4) 쿼리에 파라미터 바인딩
                //    - patient_id
                //    - users_id (DB에서 조회해온 nurseId)
                //    - recorded_date, time_period, temperature, bp_systolic, bp_diastolic, pulse_rate, respiration_rate
                // ─────────────────────────────────────────────────────────
                int result = jdbcTemplate.update(sql,
                        request.getPatientId(),
                        nurseId,
                        recordedDate,
                        timePeriod,
                        temperature,
                        bpSystolic,
                        bpDiastolic,
                        pulseRate,
                        respirationRate
                );

                if (result > 0) {
                    savedCount++;
                    log.info(timePeriod + " 시간대 바이탈 사인 저장 완료");
                }
            }

            log.info("바이탈 사인 저장 완료 - 총 " + savedCount + "개 레코드 저장");
            return savedCount;

        } catch (Exception e) {
            log.severe("바이탈 사인 저장 중 오류: " + e.getMessage());
            throw new RuntimeException("바이탈 사인 저장에 실패했습니다.", e);
        }
    }
    
    // 헬퍼 메서드들
    private boolean hasDataForTimePeriod(Map<String, Map<String, String>> data, String timePeriod) {
        return data.values().stream()
                .anyMatch(timeData -> {
                    String value = timeData.get(timePeriod);
                    return value != null && !value.trim().isEmpty();
                });
    }
    
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
    
    public Map<String, Object> getChartDetail(int patientId, String recordedDate) {
        try {
            String sql = "SELECT time_period, temperature, bp_systolic, bp_diastolic, pulse_rate, respiration_rate " +
                        "FROM vital_sign WHERE patient_id = ? AND recorded_date = ?";
            
            List<Map<String, Object>> vitalSigns = jdbcTemplate.queryForList(sql, patientId, recordedDate);
            
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
            throw new RuntimeException("차트 상세 조회 실패", e);
        }
    }
    
    
    
 // NurseChartRepository에 추가할 메서드들
    public Map<String, Object> getLatestVitalSigns(int patientId) {
        try {
            String sql = "SELECT patient_id, bp_systolic, bp_diastolic, pulse_rate, " +
                        "temperature, respiration_rate, recorded_at, time_period " +
                        "FROM vital_sign WHERE patient_id = ? " +
                        "AND recorded_date = CURDATE() " +  // 오늘 날짜로 제한 추가
                        "ORDER BY recorded_date DESC, recorded_at DESC LIMIT 1";
            
            List<Map<String, Object>> result = jdbcTemplate.queryForList(sql, patientId);
            
            if (result.isEmpty()) {
                log.info("환자 " + patientId + "의 오늘 바이탈 데이터 없음");
                return new HashMap<>();
            }
            
            log.info("환자 " + patientId + "의 오늘 최신 바이탈 데이터 조회 완료");
            return result.get(0);
            
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
            
            // 시간대별 데이터 - 오늘 날짜만 조회
            String timeSql = "SELECT time_period, " +
                            "bp_systolic as avg_systolic, " +
                            "bp_diastolic as avg_diastolic, " +
                            "pulse_rate as avg_pulse, " +
                            "temperature as avg_temp, " +
                            "respiration_rate as avg_resp " +
                            "FROM vital_sign " +
                            "WHERE patient_id = ? " +
                            "AND recorded_date = CURDATE() " +  // 오늘 날짜로 제한
                            "ORDER BY recorded_date DESC, recorded_at DESC";
            
            // 일별 평균 데이터 (최근 7일)
            String dailySql = "SELECT recorded_date as date, " +
                             "AVG(bp_systolic) as avg_systolic, " +
                             "AVG(bp_diastolic) as avg_diastolic, " +
                             "AVG(pulse_rate) as avg_pulse, " +
                             "AVG(temperature) as avg_temp, " +
                             "AVG(respiration_rate) as avg_resp " +
                             "FROM vital_sign " +
                             "WHERE patient_id = ? " +
                             "AND recorded_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) " +
                             "GROUP BY recorded_date " +
                             "ORDER BY recorded_date";
            
            List<Map<String, Object>> timeData = jdbcTemplate.queryForList(timeSql, patientId);
            List<Map<String, Object>> dailyData = jdbcTemplate.queryForList(dailySql, patientId);
            
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