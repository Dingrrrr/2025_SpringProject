package com.dita.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dita.domain.Patient;
import com.dita.domain.PatientType;
import com.dita.persistence.AdmissionRepository;
import com.dita.persistence.BedRepository;
import com.dita.persistence.PatientRepository;
import com.dita.vo.PatientDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdmissionService {
	
	private final AdmissionRepository admissionRepo;
	private final PatientRepository patientRepo;
	private final BedRepository bedRepository;

	@Transactional(readOnly = true)
	public List<PatientDto> getWaitingPatients() {
	    return patientRepo.findByPatientType(PatientType.입원대기).stream()
	        .map(PatientDto::new)
	        .collect(Collectors.toList());
	}
}
