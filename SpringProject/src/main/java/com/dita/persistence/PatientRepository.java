package com.dita.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dita.domain.Patient;
import com.dita.domain.PatientType;


public interface PatientRepository extends JpaRepository<Patient, Integer>{

	List<Patient> findByPatientType(PatientType patientType);
	Patient findByPatientNameAndPatientBirthAndPatientPhone(String name, String birth, String phone);
	List<Patient> findByPatientNameAndPatientType(String name, PatientType type);
	Patient findByPatientBirth(String patientBirth);
	
	Optional<Patient> findByPatientName(String patientName);
	//이름으로 환자를 다건 조회하기 위한 Spring Data JPA 메서드
	List<Patient> findAllByPatientName(String patientName);
	}



