
// NurseChartRepository.java - 메인 Repository
package com.dita.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.dita.domain.Nurse_chart;
import com.dita.domain.Patient;
import java.util.List;
import java.time.LocalDateTime;

@Repository
public interface NurseChartRepository extends JpaRepository<Nurse_chart, Integer> {
    
    // 환자 검색 (간호사 차트를 통해 환자 정보 조회)
    @Query("SELECT DISTINCT nc.vitalId.patient FROM Nurse_chart nc WHERE " +
           "nc.vitalId.patient.patientName LIKE %:searchTerm% OR " +
           "nc.vitalId.patient.patientPhone LIKE %:searchTerm% " +
           "ORDER BY nc.vitalId.patient.patientName")
    List<Patient> findPatientsBySearchTerm(@Param("searchTerm") String searchTerm);
    
    // 모든 환자 조회 (간호사 차트가 있는 환자만)
    @Query("SELECT DISTINCT nc.vitalId.patient FROM Nurse_chart nc ORDER BY nc.vitalId.patient.patientName")
    List<Patient> findAllPatientsWithCharts();
    
    // 특정 환자의 간호사 차트 조회 (최신순)
    @Query("SELECT nc FROM Nurse_chart nc WHERE nc.vitalId.patient.patientId = :patientId " +
           "ORDER BY nc.entryTime DESC")
    List<Nurse_chart> findByPatientIdOrderByEntryTimeDesc(@Param("patientId") int patientId);
    
    // 특정 환자의 날짜별 차트 그룹화
    @Query("SELECT DATE(nc.entryTime) as chartDate, COUNT(nc) as chartCount " +
           "FROM Nurse_chart nc WHERE nc.vitalId.patient.patientId = :patientId " +
           "GROUP BY DATE(nc.entryTime) " +
           "ORDER BY chartDate DESC")
    List<Object[]> findChartGroupsByPatient(@Param("patientId") int patientId);
    
    // 특정 날짜의 환자 차트 조회
    @Query("SELECT nc FROM Nurse_chart nc WHERE nc.vitalId.patient.patientId = :patientId " +
           "AND DATE(nc.entryTime) = DATE(:date) " +
           "ORDER BY nc.entryTime DESC")
    List<Nurse_chart> findByPatientAndDate(@Param("patientId") int patientId, 
                                          @Param("date") LocalDateTime date);
    
    // 환자 ID로 환자 정보 조회 (차트를 통해)
    @Query("SELECT nc.vitalId.patient FROM Nurse_chart nc WHERE nc.vitalId.patient.patientId = :patientId")
    Patient findPatientById(@Param("patientId") int patientId);
}