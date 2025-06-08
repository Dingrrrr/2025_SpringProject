package com.dita.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.dita.domain.Bed;
import com.dita.domain.StatusBed;

public interface BedRepository extends JpaRepository<Bed, Integer> {
	List<Bed> findByWard_WardId(int wardId);
	List<Bed>findByBedstatus(StatusBed bedstatus);; // "empty" 병상만 조회
	
	// ✅ 상태별 병상 수 (도넛 차트용)
	@Query("SELECT b.bedstatus, COUNT(b) FROM Bed b GROUP BY b.bedstatus")
	List<Object[]> countByBedStatus();
}
