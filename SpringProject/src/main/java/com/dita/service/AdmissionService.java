package com.dita.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dita.domain.Admission;
import com.dita.domain.Patient;
import com.dita.domain.PatientType;
import com.dita.persistence.AdmissionRepository;
import com.dita.persistence.BedRepository;
import com.dita.persistence.PatientRepository;
import com.dita.persistence.UserRepository;
import com.dita.vo.PatientDto;
import com.dita.vo.PatientData;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdmissionService {

    private final AdmissionRepository admissionRepo;
    private final PatientRepository patientRepo;
    private final BedRepository bedRepository;
    private final UserRepository userRepo;

      //환자 상태 기준 필터 (ex: 입원대기 상태 환자)
      //- 단순 필드 조회용

    @Transactional(readOnly = true)
    public List<PatientDto> getPatientsByType(PatientType type) {
        return patientRepo.findByPatientType(type).stream()
                .map(PatientDto::new)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<PatientData> getAllPatientData() {
        return admissionRepo.findAll().stream()
            .map(adm -> {
                Patient patient = adm.getPatient();
                String name = patient.getPatientName();
                int age = patient.getAge();
                String status = patient.getPatientType().name();

                String doctorName = adm.getDoctor().getUsersName();
                String usersId = adm.getDoctor().getUsersId();


                return new PatientData(name, age, status, doctorName, usersId);
            })
            .collect(Collectors.toList());
    }


}
