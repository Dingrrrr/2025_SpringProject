package com.dita.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
	private int patientId;//환자 아이디
	
	@Column(name = "patient_name", length = 50, nullable = false)
	private String patientName;// 환자 이름
	
	@Column(name = "patient_birth", length = 50, nullable = false)
	private String patientBirth;// 환자 생년월일
	
	@Column(name = "patient_phone", length = 50, nullable = false)
	private String patientPhone;// 환자 전화번호
	
	@Column(name = "patient_symptom")
	private String patientSymptom;// 환자 증상
}
