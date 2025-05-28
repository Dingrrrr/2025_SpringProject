package com.dita.domain;

import java.time.LocalDate;

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
@Table(name = "drug") // 약품
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder// 빌더 방식으로 사용
public class Drug {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "drug_id",nullable = false)
	private int drugId;//약품 아이디
	
	@Column(name = "drug_name", length= 100, nullable = false)
	private String drugName;//약품 명
	
	private String description;// 설명
	
	@Column(length=20, nullable = false)
	private int stock;// 재고 수량
	
	private String unit;// 단위

	@Column(name = "expiry_date")
	private LocalDate expiryDate;// 유통기한
	
	@Column(name = "drug_img", updatable = false)
	private String drugImg;// 약품 이미지

}
