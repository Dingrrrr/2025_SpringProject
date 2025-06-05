package com.dita.persistence;

import com.dita.domain.Disease;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiseaseRepository extends JpaRepository<Disease, Long> {

    // ✅ 이름으로 존재 여부 확인
    boolean existsByName(String name);
}
