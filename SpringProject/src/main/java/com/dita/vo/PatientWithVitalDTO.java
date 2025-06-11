package com.dita.vo;

import com.dita.domain.Patient;
import com.dita.domain.Vital_sign;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** 환자 + 최신 바이탈 요약 DTO */
@Getter
@AllArgsConstructor
public class PatientWithVitalDTO {
    private Patient     patient;  // 환자 기본 정보
    private Vital_sign  vital;    // 가장 최근 바이탈 (null 가능)
}
