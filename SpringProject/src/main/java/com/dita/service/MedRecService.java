package com.dita.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.dita.controller.PatientController;
import com.dita.domain.Appt;
import com.dita.domain.Disease;
import com.dita.domain.Drug;
import com.dita.domain.Med_rec;
import com.dita.domain.Patient;
import com.dita.domain.PatientType;
import com.dita.domain.Prescription;
import com.dita.domain.User;
import com.dita.persistence.ApptRepository;
import com.dita.persistence.DiseaseRepository;
import com.dita.persistence.DrugRepository;
import com.dita.persistence.MedRecRepository;
import com.dita.persistence.PatientRepository;
import com.dita.persistence.PrescriptionRepository;
import com.dita.persistence.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MedRecService {

    private final PatientController patientController;
	
	private final PrescriptionRepository prescriptionRepo;

    private final MedRecRepository medRecRepo;
    private final ApptRepository apptRepo;
    private final PatientRepository patientRepo;
    private final UserRepository userRepo;
    private final DiseaseRepository diseaseRepo;
    private final DrugRepository drugRepo;

   

    /**
     * 진료기록 저장 및 환자 상태를 '수납'으로 변경하는 서비스 메서드
     */
    public Med_rec saveRecord(Integer apptId,
                              Integer patientId,
                              String doctorId,
                              String chiefComplaint,
                              Long diseaseId,
                              Integer drugId,
                              String notes) {

        // 예약 조회
        Appt appt = apptRepo.findById(apptId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약입니다."));

        // 환자 조회
        Patient patient = patientRepo.findById(patientId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 환자입니다."));

        // 의사 조회
        User doctor = userRepo.findById(doctorId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 의사입니다."));

        // 진단명 조회
        Disease disease = diseaseRepo.findById(diseaseId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 진단코드입니다."));

        // 약물 조회
        Drug drug = drugRepo.findById(drugId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 약물코드입니다."));

        // 진료기록 객체 생성
        Med_rec rec = Med_rec.builder()
            .apptId(appt)
            .patient(patient)
            .doctor(doctor)
            .chiefComplaint(chiefComplaint)
            .Id(disease)
            .drugId(drug)
            .notes(notes)
            .build();

        // ✅ 환자 상태를 '수납'으로 변경
        patient.setPatientType(PatientType.수납);
        patientRepo.save(patient);  // 변경된 환자 정보 저장

        // 진료기록 저장
        return medRecRepo.save(rec);
    }
        
    public void savePrescription(Med_rec rec, Integer drugId, String dosage, String frequency, String duration) {
        Drug drug = drugRepo.findById(drugId)
            .orElseThrow(() -> new IllegalArgumentException("약물이 존재하지 않습니다."));

        Prescription p = Prescription.builder()
            .recodeId(rec)
            .drugId(drug)
            .dosage(dosage)
            .frequency(frequency)
            .duration(duration)
            .build();

        prescriptionRepo.save(p);  // 저장
    }
    
    public List<Med_rec> findRecordsByPatient(Patient patient) {
        return medRecRepo.findByPatient(patient);
    }

}
