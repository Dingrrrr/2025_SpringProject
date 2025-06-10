package com.dita.vo;

import lombok.Getter;

@Getter
public class DiseaseStatDto {
    private final String diseaseName;
    private final Long count;

    public DiseaseStatDto(String diseaseName, Long count) {
        this.diseaseName = diseaseName;
        this.count = count;
    }
}
