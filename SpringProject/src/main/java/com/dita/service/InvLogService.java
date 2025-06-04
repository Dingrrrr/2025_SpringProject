package com.dita.service;

import com.dita.domain.ChangeType;
import com.dita.domain.Drug;
import com.dita.domain.Inv_log;
import com.dita.persistence.DrugRepository;
import com.dita.persistence.InvLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class InvLogService {

    private final InvLogRepository invLogRepo;
    private final DrugRepository drugRepository;

    public InvLogService(InvLogRepository invLogRepo, DrugRepository drugRepository) {
        this.invLogRepo = invLogRepo;
        this.drugRepository = drugRepository;
    }

    public void addStockLog(Inv_log log) {
        // 프론트에서 전달된 drug.drugId를 기반으로 약품 조회 (Integer로 수정됨)
        Integer drugId = log.getDrug().getDrugId();  // ✅ Integer 사용
        Drug drug = drugRepository.findById(drugId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 약품이 존재하지 않습니다: " + drugId));

        // 영속성 연결된 Drug로 설정
        log.setDrug(drug);

        // 입고 정보 설정
        log.setChangeType(ChangeType.IN);
        log.setOccurredAt(LocalDateTime.now());

        // 저장
        invLogRepo.save(log);
    }
}
