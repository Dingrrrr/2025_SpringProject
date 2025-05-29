package com.dita.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dita.domain.Admission;

public interface AdmissionRepository extends JpaRepository<Admission, Integer>{
	//커스텀 매소드 추가
}
