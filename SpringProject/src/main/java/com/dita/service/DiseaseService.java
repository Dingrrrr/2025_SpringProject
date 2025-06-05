package com.dita.service;

import com.dita.domain.Disease;
import com.dita.persistence.DiseaseRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DiseaseService {

    private final DiseaseRepository diseaseRepository;

    public void saveIfNotExists(Disease disease) {
        if (!diseaseRepository.existsById(disease.getCode())) {
            diseaseRepository.save(disease);
        }
    }
}