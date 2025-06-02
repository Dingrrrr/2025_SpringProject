package com.dita.domain;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "prescription") // 처방
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder// 빌더 방식으로 사용
public class Prescription {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "prescription_id", nullable = false)
	private int prescriptionId;//처방 아이디
	
	@ManyToOne
	@JoinColumn(name = "recode_id", nullable = false)
	private Med_rec recodeId;//진료기록 아이디
	
	@ManyToOne
	@JoinColumn(name = "drug_id", nullable = false)
	private Drug drugId;//약품 아이디
	
	@Column(length = 50)
	private String dosage;//용량
	
	@Column(length = 50)
	private String frequency;//투여 주기
	
	@Column(length = 50)
	private String duration;//투약 기간
	
}
