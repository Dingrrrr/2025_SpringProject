package com.dita.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@Table(name = "vital_sign") // 바이탈 사인
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder// 빌더 방식으로 사용
public class Vital_sign {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "vital_id", nullable = false)
	private int vitalId;// 바이탈 아이디
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "patient_id", nullable = false)
	private Patient patient; //환자 아이디
	
	@ManyToOne
	@JoinColumn(name = "users_id", nullable = false)
	private User nurse;// 간호사 아이디
	
	@CreationTimestamp
	@Column(name = "recorded_at", nullable = false, updatable = false)
	private LocalDateTime recordedAt;// 기록 일시
	
	@Column(precision = 4, scale = 1, updatable = false)
	private BigDecimal temperature;//체온
	
	@Column(name = "bp_systolic")
	private int bpSystolic;//수축기 혈압
	
	@Column(name = "bp_diastolic")
	private int bpDiastolic;//이완기 혈압
	
	@Column(name = "pulse_rate")
	private int pulseRate;//맥박(회/분)
	
	@Column(name = "respiration_rate")
	private int respirationRate;//호흡(회/분)
}