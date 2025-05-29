package com.dita.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import com.dita.domain.Patient;

@Getter
@Setter
@AllArgsConstructor
public class PatientDto {
    private int patientId;
    private String name;
    private String birth;
    private String phone;
    private String symptom;

    // Entity → DTO 변환 생성자
    public PatientDto(Patient p) {
        this.patientId = p.getPatientId();
        this.name = p.getPatientName();
        this.birth = p.getPatientBirth();
        this.phone = p.getPatientPhone();
        this.symptom = p.getPatientSymptom();
    }
}
