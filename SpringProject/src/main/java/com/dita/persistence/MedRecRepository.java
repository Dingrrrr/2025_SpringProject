package com.dita.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

import com.dita.domain.Appt;
import com.dita.domain.Med_rec;
import com.dita.domain.Patient;
import com.dita.vo.DiseaseStatDto;

public interface MedRecRepository extends JpaRepository<Med_rec, Integer> {

	List<Med_rec> findByApptId(Appt appt);

	List<Med_rec> findByPatient(Patient patient);

	List<Med_rec> findTop2ByPatientOrderByCreatedAtDesc(Patient patient); // ✅ 최근 2건

	Med_rec findTop1ByPatientOrderByCreatedAtDesc(Patient patient); // ✅ 최근 1건

	boolean existsByApptId_ApptId(Integer apptId); // ✅ 이거 추가!

	Med_rec findTopByPatientOrderByCreatedAtDesc(Patient patient);

	// 진료 기록 테이블에서 상병코드별 진료 수를 집계
	 @Query("SELECT new com.dita.vo.DiseaseStatDto(d.name, COUNT(m.id)) " +
			          "FROM Med_rec m JOIN m.disease d " +
			           "WHERE m.createdAt BETWEEN :start AND :end " +
			          "GROUP BY d.name " +
			          "ORDER BY COUNT(m.id) DESC")
			    List<DiseaseStatDto> getDiseaseStats(LocalDateTime start, LocalDateTime end);
		
		// 연령대별 진료 수 통계
	 @Query(value = """
		        SELECT
		          CASE
		            WHEN age < 10  THEN '10세 미만'
		            WHEN age BETWEEN 10 AND 19 THEN '10대'
		            WHEN age BETWEEN 20 AND 29 THEN '20대'
		            WHEN age BETWEEN 30 AND 39 THEN '30대'
		            WHEN age BETWEEN 40 AND 49 THEN '40대'
		            WHEN age BETWEEN 50 AND 59 THEN '50대'
		            WHEN age BETWEEN 60 AND 69 THEN '60대'
		            ELSE '70세 이상'
		          END  AS ageGroup,
		          COUNT(*)                AS cnt
		        FROM (
		          SELECT
		            /* 만 나이 계산 */
		            (YEAR(CURDATE()) -
		              CASE
		                WHEN SUBSTRING(p.patient_birth, 8, 1) IN ('1','2','5','6')
		                     THEN 1900 + CAST(SUBSTRING(p.patient_birth,1,2) AS UNSIGNED)
		                ELSE 2000 + CAST(SUBSTRING(p.patient_birth,1,2) AS UNSIGNED)
		              END
		            ) AS age
		          FROM med_rec m
		          JOIN patient p ON m.patient_id = p.patient_id
		          WHERE m.created_at BETWEEN :start AND :end
		        ) AS sub
		        GROUP BY ageGroup
		        """,
		        nativeQuery = true)
		    List<Object[]> countByAgeGroupBetween(@Param("start") LocalDateTime start,
		                                          @Param("end")   LocalDateTime end);



		// 성별별 진료 수 통계
		@Query("SELECT p.patientGender, COUNT(m) FROM Med_rec m JOIN m.patient p WHERE m.createdAt BETWEEN :start AND :end GROUP BY p.patientGender")
		List<Object[]> countByGenderBetween(LocalDateTime start, LocalDateTime end);
}
