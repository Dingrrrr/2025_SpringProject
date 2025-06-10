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
public class MedRecServiceImpl implements MedRecService {

    private final PatientController patientController;
    private final PrescriptionRepository prescriptionRepo;
    private final MedRecRepository medRecRepo;
    private final ApptRepository apptRepo;
    private final PatientRepository patientRepo;
    private final UserRepository userRepo;
    private final DiseaseRepository diseaseRepo;
    private final DrugRepository drugRepo;

    @Override
    public Med_rec saveRecord(Integer apptId,
                              Integer patientId,
                              String doctorId,
                              String chiefComplaint,
                              Long diseaseId,
                              Integer drugId,
                              String notes) {

        Appt appt = apptRepo.findById(apptId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약입니다."));

        Patient patient = patientRepo.findById(patientId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 환자입니다."));

        User doctor = userRepo.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 의사입니다."));

        Disease disease = diseaseRepo.findById(diseaseId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 진단코드입니다."));

        Drug drug = drugRepo.findById(drugId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 약물코드입니다."));

        Med_rec rec = Med_rec.builder()
                .apptId(appt)
                .patient(patient)
                .doctor(doctor)
                .chiefComplaint(chiefComplaint)
                .Id(disease)
                .drugId(drug)
                .notes(notes)
                .build();

        patient.setPatientType(PatientType.수납);
        patientRepo.save(patient);

        return medRecRepo.save(rec);
    }

    @Override
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

        prescriptionRepo.save(p);
    }

    @Override
    public List<Med_rec> findRecordsByPatient(Patient patient) {
        return medRecRepo.findByPatient(patient);
    }

    @Override
    public List<Med_rec> findTop2RecordsByPatient(Patient patient) {
        return medRecRepo.findTop2ByPatientOrderByCreatedAtDesc(patient);
    }

    @Override
    public Med_rec findById(Integer recordId) {
        return medRecRepo.findById(recordId).orElse(null);
    }

    @Override
    public Med_rec save(Med_rec rec) {
        return medRecRepo.save(rec);
    }

    // ✅ 해당 예약(appt)으로 이미 작성된 차트가 있는지 확인
    @Override
    public List<Med_rec> findByAppt(Appt appt) {
        return medRecRepo.findByApptId(appt);
    }

    @Override
    public boolean existsByApptId(Integer apptId) {
        return medRecRepo.existsByApptId_ApptId(apptId);
    }

    @Override
    public Med_rec findLatestRecordByPatient(Patient patient) {
        return medRecRepo.findTopByPatientOrderByCreatedAtDesc(patient);  // ✅ 인자 전달
    }
}

