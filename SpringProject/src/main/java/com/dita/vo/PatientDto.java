package com.dita.vo;

import com.dita.domain.Patient;
import com.dita.domain.PatientType;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PatientDto {

	private int patientId;

    @JsonProperty("patientName")
    private String name;

    @JsonProperty("patientGender")
    private String gender;

    private int age;

    @JsonProperty("patientBirth")
    private String birth;

    @JsonProperty("patientAddress")
    private String address;

    @JsonProperty("patientPhone")
    private String phone;

    @JsonProperty("patientSymptom")
    private String symptom;

    @JsonProperty("patientType")
    private PatientType type;

    // Patient 엔티티 → DTO 변환 생성자
    public PatientDto(Patient p) {
        this.patientId = p.getPatientId();
        this.name = p.getPatientName();
        this.gender = (p.getPatientGender() != null) ? p.getPatientGender().toString() : "미정";
        this.birth = p.getPatientBirth();
        this.age = calculateAge(p.getPatientBirth());
        this.address = p.getPatientAddress();
        this.phone = p.getPatientPhone();
        this.symptom = p.getPatientSymptom();
        this.type = p.getPatientType();
    }

    // 생년월일로 만 나이 계산
    private int calculateAge(String birthStr) {
        try {
            // 6자리 입력 처리 (예: 990521 → 1999-05-21 또는 2002-04-15 등)
            if (birthStr != null && birthStr.length() == 6) {
                int year = Integer.parseInt(birthStr.substring(0, 2));
                int month = Integer.parseInt(birthStr.substring(2, 4));
                int day = Integer.parseInt(birthStr.substring(4, 6));

                // 00~24는 2000년대, 나머지는 1900년대
                year += (year <= 24) ? 2000 : 1900;

                LocalDate birthDate = LocalDate.of(year, month, day);
                return Period.between(birthDate, LocalDate.now()).getYears();
            }

            // 이미 yyyy-MM-dd 형식이면 그대로 처리
            LocalDate birthDate = LocalDate.parse(birthStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return Period.between(birthDate, LocalDate.now()).getYears();

        } catch (Exception e) {
            return 0;
        }
    }

}
