package com.dita.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
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
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "vital_sign", 
       uniqueConstraints = @UniqueConstraint(
           columnNames = {"patient_id", "recorded_date", "time_period"}
       )) // 같은 환자의 같은 날짜, 같은 시간대 중복 방지
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Vital_sign {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vital_id", nullable = false)
    private int vitalId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;
    
    @ManyToOne
    @JoinColumn(name = "users_id", nullable = false)
    private User nurse;
    
    @CreationTimestamp
    @Column(name = "recorded_at", nullable = false, updatable = false, 
            columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime recordedAt; // 실제 기록된 시간
    
    
    @Column(name = "recorded_date", nullable = false)
    private LocalDate recordedDate; // 기록 날짜 (2025-06-04)
    
    @Enumerated(EnumType.STRING)
    @Column(name = "time_period", nullable = false)
    private TimePeriod timePeriod; // 시간대 (아침, 점심, 저녁, 야간)
    
    @Column(precision = 4, scale = 1)
    private BigDecimal temperature;
    
    @Column(name = "bp_systolic")
    private Integer bpSystolic;
    
    @Column(name = "bp_diastolic")
    private Integer bpDiastolic;
    
    @Column(name = "pulse_rate")
    private Integer pulseRate;
    
    @Column(name = "respiration_rate")
    private Integer respirationRate;
}