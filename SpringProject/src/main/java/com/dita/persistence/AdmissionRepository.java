package com.dita.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.dita.domain.Admission;

public interface AdmissionRepository extends JpaRepository<Admission, Integer>{
	  @Query("SELECT a FROM Admission a WHERE a.dischargeAt IS NULL AND a.bed IS NULL")
	    List<Admission> findWaitingPatients();// 대기환자 조회
}
