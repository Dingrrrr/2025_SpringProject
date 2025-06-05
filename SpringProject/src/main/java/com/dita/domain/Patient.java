package com.dita.domain;


import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "patient")// 환자
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Patient {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "patient_id", nullable = false)
	private Integer patientId;//환자 아이디
	
	@Column(name = "patient_name", length = 50, nullable = false)
	private String patientName;// 환자 이름
	
	@Column(name = "patient_gender", nullable = false)
	private String patientGender;// 성별
	
	@Column(name = "patient_birth", length = 50, nullable = false)
	private String patientBirth;// 환자 생년월일
	
	@Column(name = "patient_phone", length = 50, nullable = false)
	private String patientPhone;// 환자 전화번호
	
	@Column(name = "patient_symptom")
	private String patientSymptom;// 환자 증상
	
	@Column(name = "patient_address")
	private String patientAddress;// 환자 증상
	
	@Enumerated(EnumType.STRING) 
	@Column(name = "patient_type")
	private PatientType patientType;// 입원여부 판단, 환자 상태: 외래, 입원대기, 입원중, 퇴원
	
	public int getAge() {
	    if (this.patientBirth == null || this.patientBirth.isEmpty()) return 0;

	    try {
	        LocalDate birthDate = LocalDate.parse(this.patientBirth, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	        return Period.between(birthDate, LocalDate.now()).getYears(); // <- 이게 만 나이
	    } catch (Exception e) {
	        return 0;
	    }
	}

}