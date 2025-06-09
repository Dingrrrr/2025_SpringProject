package com.dita.persistence;


import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dita.domain.Appt;
import com.dita.vo.DoctorStatsDto;

@Repository
public interface AppointmentRepository extends JpaRepository<Appt, Integer> { 
	
	//기본 전체 담당자별 환자 수 현황
	@Query("SELECT new com.dita.vo.DoctorStatsDto(a.doctor.usersName, COUNT(a)) " +
		       "FROM Appt a WHERE a.status = '확정' GROUP BY a.doctor.usersName")
		List<DoctorStatsDto> countVisitsPerDoctor();
	
	//날짜 선택 담당자별 환자 수 현황
	@Query("SELECT new com.dita.vo.DoctorStatsDto(a.doctor.usersName, COUNT(a)) " +
		       "FROM Appt a " +
		       "WHERE a.scheduledAt BETWEEN :start AND :end " +
		       "AND a.status = '확정' " +
		       "GROUP BY a.doctor.usersName")
		List<DoctorStatsDto> countVisitsPerDoctorBetweenDateTime(
		    @Param("start") LocalDateTime start,
		    @Param("end") LocalDateTime end
		);
	
	//진료수 현황
	@Query("SELECT FUNCTION('DATE', a.scheduledAt) as date, COUNT(a) " +
		       "FROM Appt a " +
		       "WHERE a.status = '확정' AND a.scheduledAt BETWEEN :start AND :end " +
		       "GROUP BY FUNCTION('DATE', a.scheduledAt) " +
		       "ORDER BY date")
		List<Object[]> countVisitsPerDayBetween(
		    @Param("start") LocalDateTime start,
		    @Param("end") LocalDateTime end
		);
}
