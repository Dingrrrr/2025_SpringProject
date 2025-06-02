package com.dita.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import com.dita.domain.Ward;

public interface WardRepository extends JpaRepository<Ward, Integer> {}
