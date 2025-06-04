package com.dita.domain;


import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
@Table(name = "inv_log")// 약품 재고
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Inv_log {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "log_id",nullable = false)
	private int logId;//재고 아이디
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)//N:1 관계
	@JoinColumn(name = "drug_id", nullable = false)
	@JsonIgnore
	private Drug drug;// 약품 아이디
	
	@Enumerated(EnumType.STRING)
	@Column(name = "change_type",nullable = false)
	private ChangeType changeType;// 입출고 구분
	
	@Column(nullable = false)
	private int quantity;// 수량
	
	@Column(name = "occurred_at", nullable = false)
	private LocalDateTime occurredAt;// 발생일시
	
	@ManyToOne(fetch = FetchType.LAZY)//N:1 관계
	@JoinColumn(name = "recorded_by", nullable = true)
	private User recordedBy; //처리자 아이디 
	
	@Column(name = "location")// 재고 위치
	private String location;   
}
