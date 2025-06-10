package com.dita.service;

import com.dita.domain.Appt;
import com.dita.domain.Med_rec;
import com.dita.domain.Patient;

import java.util.List;

public interface MedRecService {

    Med_rec saveRecord(Integer apptId,
                       Integer patientId,
                       String doctorId,
                       String chiefComplaint,
                       Long diseaseId,
                       Integer drugId,
                       String notes);

    void savePrescription(Med_rec rec, Integer drugId, String dosage, String frequency, String duration);

    List<Med_rec> findRecordsByPatient(Patient patient);

    Med_rec findLatestRecordByPatient(Patient patient);  // ✅ 최근 기록 1건

    List<Med_rec> findTop2RecordsByPatient(Patient patient);  // ✅ 최근 2건

    Med_rec findById(Integer recordId);

    Med_rec save(Med_rec rec);
    
    List<Med_rec> findByAppt(Appt appt);

    boolean existsByApptId(Integer apptId); // ✅ 예약에 해당하는 진료기록이 존재하는지 확인
}
