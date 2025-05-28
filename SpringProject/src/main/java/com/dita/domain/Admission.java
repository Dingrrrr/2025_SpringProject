package com.dita.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "admission")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Admission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="admission_id", nullable = false)
    private int admissionId;//입원 고유번호

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;//환자 아이디

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private User doctor;// 담당 의사 아이디

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bed_id", nullable = false)
    private Bed bed;// 병상 아이디

    @Column(name = "admitted_at", nullable = false)
    private LocalDateTime admittedAt;// 입원 일시

    @Column(name = "discharge_at")
    private LocalDateTime dischargeAt;// 퇴원 일시

    @Column(name = "admission_reason")
    private String admissionReason;// 입원 사유

    @Column(name = "discharge_notes")
    private String dischargeNotes;// 퇴원 기록
}

