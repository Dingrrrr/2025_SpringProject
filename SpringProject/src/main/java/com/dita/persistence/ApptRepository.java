package com.dita.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import com.dita.domain.Appt;

public interface ApptRepository extends JpaRepository<Appt, Integer> {
    // 필요 시 예약 검색 메서드 추가 가능
}
