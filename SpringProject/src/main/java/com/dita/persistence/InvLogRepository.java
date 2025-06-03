package com.dita.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dita.domain.Inv_log;

public interface InvLogRepository extends JpaRepository<Inv_log, Integer>{

}
