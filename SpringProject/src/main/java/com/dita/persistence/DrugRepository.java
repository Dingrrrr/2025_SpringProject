package com.dita.persistence;

import com.dita.domain.Drug;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DrugRepository extends JpaRepository<Drug, Integer> {
    
    // 중복 확인용 메서드
    boolean existsByDrugCode(String drugCode);
    
    List<Drug> findByDrugNameContaining(String keword);
    
    Optional<Drug> findByDrugCode(String drugCode);
}
