package com.dita.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "drug")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Drug {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "drug_id")
    private int drugId; // 약품 고유 ID (PK)

    @Column(name = "drug_code", length = 50)
    private String drugCode; // 공공데이터 기준 약품코드

    @Column(name = "drug_name", length = 100)
    private String drugName; // 성분명 + 용량 문자열

    @Column(name = "main_ingredient", length = 100)
    private String mainIngredient; // 한글 주성분명

    @Column(name = "category", length = 20)
    private Drug_category category; // '일반', '향정', '대마' 등

    @Column(name = "form_type", length = 20)
    private Form_Type formType; // '내복약', '외용약', '주사', '수액', '기타'
}

