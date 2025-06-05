package com.dita.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientData {
    private String patientName;  // 환자 이름
    private int age;             // 환자 나이
    private String status;       // 입원 상태 (입원중, 입원대기 등)
    private String doctorName;   // 담당 의사 이름
    private String doctorId;        // 담당 의사 ID
}
