package com.dita.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dita.domain.Appt;

public interface ApptRepository extends JpaRepository<Appt, Integer> {
	List<Appt> findByStatus(com.dita.domain.Appt status);
}
