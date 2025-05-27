package com.dita.domain;

import java.time.LocalDateTime;

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
@Table(name="med_admin")//투약 기록
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Med_admin {
	

	@Id
	@Column(name="admin_id", length = 50, nullable = false)
	private String admin_id;// 관리자 아이디
	
	@ManyToOne
	@JoinColumn(name = "prescription_id", nullable = false)
	private Prescription prescription;// 처방 아이디
	
	
	@Column(name="admin_time", nullable = false)
	private LocalDateTime adminTime;// 투약일시
	
	@ManyToOne
	@JoinColumn(name="nurse_id", nullable = false)
	private User nurse;// 간호사 아이디
	
	@ManyToOne
	@JoinColumn(name="patient_id", nullable = false)
	private Patient patient;//환자 아이디
	
	@Column(name = "dosage_given", length = 50)
	private String dosageGiven;//실제 투여량
	
	@Column(length = 1000)
	private String remarks;//비고
}
