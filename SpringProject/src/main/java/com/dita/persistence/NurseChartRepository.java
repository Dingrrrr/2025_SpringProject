package com.dita.persistence;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dita.domain.Patient;
import com.dita.domain.Vital_sign;
import com.dita.domain.TimePeriod;

@Repository
public interface NurseChartRepository extends JpaRepository<Vital_sign, Integer> {
    
    /**
     * 입원중인 환자 목록을 조회합니다.
     */
    @Query(value = "SELECT patient_id, patient_name, patient_gender, patient_birth, patient_phone " +
                  "FROM patient WHERE patient_type = '입원중' " +
                  "ORDER BY patient_name", nativeQuery = true)
    List<Map<String, Object>> findInpatients();
    
    /**
     * 특정 환자의 차트 목록을 조회합니다.
     */
    @Query(value = "SELECT recorded_date, " +
                  "DATE_FORMAT(MAX(recorded_at), '%H:%i') as latest_time, " +
                  "COUNT(*) as record_count " +
                  "FROM vital_sign WHERE patient_id = :patientId " +
                  "GROUP BY recorded_date " +
                  "ORDER BY recorded_date DESC", nativeQuery = true)
    List<Map<String, Object>> findPatientCharts(@Param("patientId") int patientId);
    
    /**
     * 특정 환자의 특정 날짜 바이탈 사인을 조회합니다.
     */
    @Query(value = "SELECT time_period, temperature, bp_systolic, bp_diastolic, pulse_rate, respiration_rate " +
                  "FROM vital_sign WHERE patient_id = :patientId AND recorded_date = :recordedDate", nativeQuery = true)
    List<Map<String, Object>> findVitalSignsByPatientAndDate(@Param("patientId") int patientId, 
                                                             @Param("recordedDate") String recordedDate);
    
    /**
     * 환자의 최신 바이탈 사인을 조회합니다 (오늘 날짜).
     */
    @Query(value = "SELECT patient_id, bp_systolic, bp_diastolic, pulse_rate, " +
                  "temperature, respiration_rate, recorded_at, time_period " +
                  "FROM vital_sign WHERE patient_id = :patientId " +
                  "AND recorded_date = CURDATE() " +
                  "ORDER BY recorded_date DESC, recorded_at DESC LIMIT 1", nativeQuery = true)
    Map<String, Object> findLatestVitalSigns(@Param("patientId") int patientId);
    
    /**
     * 환자의 오늘 시간대별 바이탈 데이터를 조회합니다.
     */
    @Query(value = "SELECT time_period, " +
                  "bp_systolic as avg_systolic, " +
                  "bp_diastolic as avg_diastolic, " +
                  "pulse_rate as avg_pulse, " +
                  "temperature as avg_temp, " +
                  "respiration_rate as avg_resp " +
                  "FROM vital_sign " +
                  "WHERE patient_id = :patientId " +
                  "AND recorded_date = CURDATE() " +
                  "ORDER BY recorded_date DESC, recorded_at DESC", nativeQuery = true)
    List<Map<String, Object>> findTodayVitalsByTimePeriod(@Param("patientId") int patientId);
    
    /**
     * 환자의 최근 7일 일별 평균 바이탈 데이터를 조회합니다.
     */
    @Query(value = "SELECT recorded_date as date, " +
                  "AVG(bp_systolic) as avg_systolic, " +
                  "AVG(bp_diastolic) as avg_diastolic, " +
                  "AVG(pulse_rate) as avg_pulse, " +
                  "AVG(temperature) as avg_temp, " +
                  "AVG(respiration_rate) as avg_resp " +
                  "FROM vital_sign " +
                  "WHERE patient_id = :patientId " +
                  "AND recorded_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) " +
                  "GROUP BY recorded_date " +
                  "ORDER BY recorded_date", nativeQuery = true)
    List<Map<String, Object>> findWeeklyVitalAverages(@Param("patientId") int patientId);
    
    /**
     * 특정 환자, 날짜, 시간대의 바이탈 사인을 조회합니다.
     */
    @Query("SELECT v FROM Vital_sign v WHERE v.patient.patientId = :patientId " +
           "AND v.recordedDate = :recordedDate AND v.timePeriod = :timePeriod")
    Vital_sign findByPatientAndDateAndTimePeriod(@Param("patientId") int patientId,
                                               @Param("recordedDate") LocalDate recordedDate,
                                               @Param("timePeriod") TimePeriod timePeriod);
    
    /**
     * 환자별 특정 날짜의 모든 바이탈 사인을 조회합니다.
     */
    @Query("SELECT v FROM Vital_sign v WHERE v.patient.patientId = :patientId AND v.recordedDate = :recordedDate")
    List<Vital_sign> findByPatientAndDate(@Param("patientId") int patientId, 
                                        @Param("recordedDate") LocalDate recordedDate);
}