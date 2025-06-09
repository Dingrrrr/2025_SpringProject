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
import jakarta.persistence.Transient;
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
	
	@Transient
	public int getAge() {
	    if (patientBirth == null || patientBirth.length() < 6) return 0;
	    try {
	        // 앞 6자리는 yyMMdd
	        String yyMMdd = patientBirth.substring(0,6);
	        // RRN 두 번째 숫자(7번째 문자)로 세기(1/2→19xx, 3/4→20xx) 구분
	        char centuryCode = patientBirth.charAt(7);
	        int century = (centuryCode=='1'||centuryCode=='2') ? 1900 : 2000;
	        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyMMdd");
	        LocalDate d = LocalDate.parse(yyMMdd, f).withYear(
	            century + Integer.parseInt(yyMMdd.substring(0,2))
	        );
	        return Period.between(d, LocalDate.now()).getYears();
	    } catch (Exception e) {
	        return 0;
	    }
	}

}