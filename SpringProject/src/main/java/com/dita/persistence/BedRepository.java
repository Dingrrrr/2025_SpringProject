package com.dita.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.dita.domain.Bed;
import com.dita.domain.StatusBed;

public interface BedRepository extends JpaRepository<Bed, Integer> {
	List<Bed> findByWard_WardId(int wardId);
	List<Bed>findByBedstatus(StatusBed bedstatus);; // "empty" 병상만 조회
}
