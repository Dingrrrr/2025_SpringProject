package com.dita.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.dita.domain.Med_rec;
import com.dita.domain.Patient;

public interface MedRecRepository extends JpaRepository<Med_rec, Integer> {

    // 환자 기준으로 진료기록 전체 조회
    List<Med_rec> findByPatient(Patient patient);
}
