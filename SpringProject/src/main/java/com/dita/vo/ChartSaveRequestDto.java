package com.dita.vo;

import java.time.LocalDate;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ChartSaveRequestDto {
    private int patientId;
    private String nurseId;
    private String recordedDate; 
    private Map<String, Map<String, String>> data;
}