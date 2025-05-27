package com.dita.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "payment") // 수납
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder// 빌더 방식으로 사용
public class Payment {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "payment_id", nullable = false)
	private int paymentId;//수납 아이디
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "patient_id", nullable = false)
	private Patient patient;//환자 아이디
	
	
	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal amount;//결제 금액
	
	@Column(name = "insurance_no", length = 50)
	private String insuranceNo;//보험 번호
	
	@CreationTimestamp
	@Column(name = "paid_at")
	private LocalDateTime paidAt;// 결제 일시
	
	@Enumerated(EnumType.STRING)
	@Column(name = "pay_status", nullable = false)
	private StatusPay payStatus = StatusPay.대기;// 수납상태, 디폴트 = 대기

}
