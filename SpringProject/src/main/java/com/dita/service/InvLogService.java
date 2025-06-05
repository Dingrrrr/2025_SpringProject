package com.dita.service;

import com.dita.domain.ChangeType;
import com.dita.domain.Drug;
import com.dita.domain.Form_Type;
import com.dita.domain.Inv_log;
import com.dita.persistence.DrugRepository;
import com.dita.persistence.InvLogRepository;
import com.dita.vo.DrugInventoryDto;
import com.dita.vo.InventoryAddRequest;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvLogService {

    private final InvLogRepository invLogRepo;
    private final DrugRepository drugRepository;

    public InvLogService(InvLogRepository invLogRepo, DrugRepository drugRepository) {
        this.invLogRepo = invLogRepo;
        this.drugRepository = drugRepository;
    }

    /**
     * 약품 입고 기록 저장
     */
    public void addStockLog(InventoryAddRequest dto) {
        Drug drug;

        // 기존 약품이면 drugId 기준 조회
        if (dto.getDrugId() != null && drugRepository.existsById(dto.getDrugId())) {
            drug = drugRepository.findById(dto.getDrugId())
                    .orElseThrow(() -> new IllegalArgumentException("해당 약품이 존재하지 않습니다: " + dto.getDrugId()));
        }
        // 신규 약품이면 새로 생성
        else {
        	drug = Drug.builder()
        	        .drugName(dto.getName())
        	        .drugCode(dto.getCode())
        	        .formType(Form_Type.valueOf(dto.getType().toUpperCase()))
        	        .build();
            drugRepository.save(drug);
        }

        // 입고 로그 객체 생성
        Inv_log log = Inv_log.builder()
                .drug(drug)
                .changeType(ChangeType.IN)
                .quantity(dto.getQuantity())
                .occurredAt(LocalDateTime.now())
                .location(dto.getLocation())
                .build();

        invLogRepo.save(log);
    }


    /**
     * 현재 보유 재고 요약 목록 조회
     */
    public List<DrugInventoryDto> getInventorySummary() {
        List<Object[]> rawList = invLogRepo.findAllCurrentInventory();

        return rawList.stream()
            .map(obj -> {
                try {
                	Form_Type formType;
                	Object rawType = obj[2];

                	if (rawType instanceof Number) {
                	    int ordinal = ((Number) rawType).intValue();
                	    formType = Form_Type.values()[ordinal]; // 예: 0 → 내복약
                	} else {
                	    formType = Form_Type.valueOf(rawType.toString());
                	}


                    return new DrugInventoryDto(
                        ((Number) obj[0]).intValue(),   // drugId
                        String.valueOf(obj[1]),         // name
                        formType,                       // type (enum)
                        String.valueOf(obj[3]),         // code
                        String.valueOf(obj[4]),         // location
                        obj[5] != null ? ((Number) obj[5]).intValue() : 0 // stock
                    );
                } catch (Exception e) {
                    System.err.println("오류 발생 row: " + Arrays.toString(obj));
                    e.printStackTrace();
                    return null;
                }
            })
            .filter(dto -> dto != null)
            .collect(Collectors.toList());
    }
    
    // 출고
    public void withdrawStock(int drugId, int quantity) {
        // 약품 조회
        Drug drug = drugRepository.findById(drugId)
            .orElseThrow(() -> new IllegalArgumentException("해당 약품이 존재하지 않습니다: " + drugId));

        // 현재 재고 계산
        int currentStock = invLogRepo.calculateCurrentStock(drugId);
        if (currentStock < quantity) {
            throw new IllegalStateException("재고가 부족합니다. 현재 재고: " + currentStock);
        }

        // 출고 로그 생성 (location은 저장하지 않음)
        Inv_log log = Inv_log.builder()
            .drug(drug)
            .changeType(ChangeType.OUT)
            .quantity(quantity)
            .occurredAt(LocalDateTime.now())
            .build();

        // 로그 저장
        invLogRepo.save(log);
    }

    public void updateStockLog(Inv_log updated) {
        Inv_log original = invLogRepo.findById(updated.getLogId())
            .orElseThrow(() -> new IllegalArgumentException("로그 ID가 존재하지 않음"));

        original.setLocation(updated.getLocation());
        original.setQuantity(updated.getQuantity());

        invLogRepo.save(original);
    }

}
