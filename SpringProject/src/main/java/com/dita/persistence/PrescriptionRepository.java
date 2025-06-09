package com.dita.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import com.dita.domain.Prescription;

public interface PrescriptionRepository extends JpaRepository<Prescription, Integer> {}
