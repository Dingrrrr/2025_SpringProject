package com.dita.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import com.dita.domain.Appt;
import com.dita.domain.Med_rec;
import com.dita.domain.Patient;

public interface MedRecRepository extends JpaRepository<Med_rec, Integer> {

    List<Med_rec> findByApptId(Appt appt);

    List<Med_rec> findByPatient(Patient patient);

    List<Med_rec> findTop2ByPatientOrderByCreatedAtDesc(Patient patient);  // ✅ 최근 2건

    Med_rec findTop1ByPatientOrderByCreatedAtDesc(Patient patient);       // ✅ 최근 1건

    boolean existsByApptId_ApptId(Integer apptId);                         // ✅ 이거 추가!
    
    Med_rec findTopByPatientOrderByCreatedAtDesc(Patient patient);
}
