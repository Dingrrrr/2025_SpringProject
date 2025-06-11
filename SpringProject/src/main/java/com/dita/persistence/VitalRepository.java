package com.dita.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dita.domain.Vital_sign;

//환자의 가장 최신 Vital Sign 1건을 조회하는 JPA Repository
public interface VitalRepository extends JpaRepository<Vital_sign, Integer> {

 // 특정 환자(patient_id)의 Vital 기록 중 가장 최근(recordedAt 기준) 1건 반환
 Optional<Vital_sign> findFirstByPatient_PatientIdOrderByRecordedAtDesc(Integer patientId);
 
 
}
