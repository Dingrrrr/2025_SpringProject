package com.dita.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dita.domain.Patient;
import com.dita.domain.PatientType;


public interface PatientRepository extends JpaRepository<Patient, Integer>{
	List<Patient> findByPatientType(PatientType patientType); //입원대기 조회다음
}