package com.dita.persistence;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dita.domain.Appt;

import com.dita.domain.Patient;
import com.dita.domain.PatientType;
import com.dita.domain.Status;


public interface ApptRepository extends JpaRepository<Appt, Integer> {

	List<Appt> findByStatus(Status status);
	List<Appt> findAllByStatusAndPatient_PatientTypeIn(Status status, List<PatientType> types);
	List<Appt> findByScheduledAtBetween(LocalDateTime start, LocalDateTime end);
	Optional<Appt> findByScheduledAtAndPatient_PatientBirth(LocalDateTime date, String patientBirth);
	
	
	@Query("SELECT a.status, COUNT(a) FROM Appt a GROUP BY a.status")
	List<Object[]> countByStatus();

    // ✅ 진료 상태별
    @Query("SELECT a.status, COUNT(a) FROM Appt a WHERE DATE(a.scheduledAt) = CURRENT_DATE GROUP BY a.status")
    List<Object[]> countTodayByStatus();

    // ✅ 월별 외래환자 수
    @Query("SELECT FUNCTION('DATE_FORMAT', a.scheduledAt, '%Y-%m'), COUNT(a) FROM Appt a GROUP BY FUNCTION('DATE_FORMAT', a.scheduledAt, '%Y-%m') ORDER BY 1")
    List<Object[]> countMonthlyOutpatients();

    // ✅ 최근 7일 외래환자 수
    @Query("SELECT FUNCTION('DATE_FORMAT', a.scheduledAt, '%m/%d'), COUNT(a) " +
    	       "FROM Appt a WHERE a.scheduledAt >= :startDate " +
    	       "GROUP BY FUNCTION('DATE_FORMAT', a.scheduledAt, '%m/%d') " +
    	       "ORDER BY FUNCTION('DATE_FORMAT', a.scheduledAt, '%m/%d')")
    List<Object[]> countWeeklyOutpatients(LocalDateTime startDate);
    
    @Query("SELECT a FROM Appt a JOIN FETCH a.patient WHERE a.room = :room AND a.scheduledAt BETWEEN :start AND :end ORDER BY a.scheduledAt ASC")
    List<Appt> findByRoomAndScheduledAtToday(@Param("room") String room,
                                             @Param("start") LocalDateTime start,
                                             @Param("end") LocalDateTime end);
    @Query("SELECT a FROM Appt a JOIN FETCH a.patient")
    List<Appt> findAllWithPatient();
    //환자리스트
    @Query("SELECT a FROM Appt a WHERE a.room = :room AND a.patient.patientType IN :types ORDER BY a.scheduledAt ASC")
    List<Appt> findByRoomAndPatientTypes(@Param("room") int room, @Param("types") List<PatientType> types);




    // ✅ 연령대 통계
    @Query(value = """
    	    SELECT 
    	        CASE 
    	            WHEN TIMESTAMPDIFF(YEAR, STR_TO_DATE(p.patient_birth, '%Y.%m.%d'), CURDATE()) < 10 THEN '10세 미만'
    	            WHEN TIMESTAMPDIFF(YEAR, STR_TO_DATE(p.patient_birth, '%Y.%m.%d'), CURDATE()) BETWEEN 10 AND 19 THEN '10대'
    	            WHEN TIMESTAMPDIFF(YEAR, STR_TO_DATE(p.patient_birth, '%Y.%m.%d'), CURDATE()) BETWEEN 20 AND 29 THEN '20대'
    	            WHEN TIMESTAMPDIFF(YEAR, STR_TO_DATE(p.patient_birth, '%Y.%m.%d'), CURDATE()) BETWEEN 30 AND 39 THEN '30대'
    	            WHEN TIMESTAMPDIFF(YEAR, STR_TO_DATE(p.patient_birth, '%Y.%m.%d'), CURDATE()) BETWEEN 40 AND 49 THEN '40대'
    	            WHEN TIMESTAMPDIFF(YEAR, STR_TO_DATE(p.patient_birth, '%Y.%m.%d'), CURDATE()) BETWEEN 50 AND 59 THEN '50대'
    	            ELSE '60세 이상'
    	        END AS ageGroup,
    	        COUNT(*) AS cnt
    	    FROM patient p
    	    WHERE STR_TO_DATE(p.patient_birth, '%Y.%m.%d') IS NOT NULL
    	    GROUP BY ageGroup
    	    ORDER BY ageGroup
    	""", nativeQuery = true)
    	List<Object[]> countByAgeGroup();
    	
    	// ✅ 환자의 모든 예약 삭제
        void deleteAllByPatient(Patient patient);
}