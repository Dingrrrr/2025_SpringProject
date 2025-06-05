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
	
	}



