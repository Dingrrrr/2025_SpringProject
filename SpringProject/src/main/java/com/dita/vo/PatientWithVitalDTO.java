package com.dita.vo;

import com.dita.domain.Patient;
import com.dita.domain.Vital_sign;

import lombok.AllArgsConstructor;
import lombok.Getter;

//환자 정보 + 최근 Vital Sign 정보를 함께 담는 DTO 객체
@Getter
@AllArgsConstructor
public class PatientWithVitalDTO {
 private Patient patient;       // 환자 기본 정보
 private Vital_sign vital;      // 가장 최근의 바이탈 사인
}