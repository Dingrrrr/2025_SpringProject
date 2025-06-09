package com.dita.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dita.domain.Med_rec;

public interface MedRecRepository extends JpaRepository<Med_rec, Integer> {

}
