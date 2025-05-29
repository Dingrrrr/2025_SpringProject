package com.dita.service;

import com.dita.domain.Drug;
import com.dita.persistence.DrugRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DrugCsvService {

    private final DrugRepository drugRepository;

    @Transactional
    public void saveIfNotExists(Drug drug) {
        if (!drugRepository.existsByDrugCode(drug.getDrugCode())) {
            drugRepository.save(drug);
            System.out.println("Saved: " + drug.getDrugCode());
        } else {
            System.out.println("Skipped duplicate: " + drug.getDrugCode());
        }
    }
}
