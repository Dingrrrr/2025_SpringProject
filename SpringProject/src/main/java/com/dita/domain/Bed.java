package com.dita.domain;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
@Table(name = "bed")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Bed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="bed_id", nullable = false)
    private int bedId;// 병상 고유번호

 // Bed.java
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ward_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Ward ward;
// 소속병동

    @Column(name = "bed_number", length = 10, nullable = false)
    private String bedNumber;// 병상 번호

    @Enumerated(EnumType.STRING)
    @Column(name = "bed_status", nullable = false)
    private StatusBed bedstatus = StatusBed.사용가능;// 병상상태
}
