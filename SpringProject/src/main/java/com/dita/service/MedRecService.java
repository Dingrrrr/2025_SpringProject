package com.dita.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dita.domain.Appt;
import com.dita.domain.Disease;
import com.dita.domain.Drug;
import com.dita.domain.Med_rec;
import com.dita.domain.Patient;
import com.dita.domain.User;
import com.dita.persistence.ApptRepository;
import com.dita.persistence.DiseaseRepository;
import com.dita.persistence.DrugRepository;
import com.dita.persistence.MedRecRepository;
import com.dita.persistence.PatientRepository;
import com.dita.persistence.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MedRecService {
    private final MedRecRepository medRecRepo;
    private final ApptRepository apptRepo;
    private final PatientRepository patientRepo;
    private final UserRepository userRepo;
    private final DiseaseRepository diseaseRepo;
    private final DrugRepository drugRepo;

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

        return medRecRepo.save(rec);
    }
}
