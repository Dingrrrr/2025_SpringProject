package com.dita.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

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
@Table(name="nurse_chart")//간호사 차트
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Nurse_chart {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "chart_id", nullable = false)
	private int chartId;//차트 고유번호
	
	@ManyToOne
	@JoinColumn(name = "admission_id", nullable = false)
	private Admission admission;// 입원 기록
	
	@ManyToOne
	@JoinColumn(name = "nurse_id", nullable = false)
	private User nurse;// 작성 간호사 아이디
	
	@CreationTimestamp
	@Column(name = "entry_time")
	private LocalDateTime entryTime;//작성 일시
	
	@Column(length = 1000)
	private String notes;//간호 내용
}
