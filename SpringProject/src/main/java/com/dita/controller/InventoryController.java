package com.dita.controller;

import com.dita.domain.Inv_log;
import com.dita.persistence.InvLogRepository;
import com.dita.service.InvLogService;
import com.dita.vo.DrugInventoryDto;
import com.dita.vo.InventoryAddRequest;
import com.dita.persistence.WithdrawRequest;
import java.lang.IllegalStateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InvLogService invLogService;
    @Autowired
    private InvLogRepository invLogRepo;

    public InventoryController(InvLogService invLogService) {
        this.invLogService = invLogService;
    }
    
    //약품 입고
    @PostMapping("/add")
    public ResponseEntity<?> addStock(@RequestBody InventoryAddRequest request) {
        try {
            invLogService.addStockLog(request);
            return ResponseEntity.ok("약품 입고 기록이 저장되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("입고 실패: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("서버 오류 발생");
        }
    }


 
    //현재 보유 중인 약품 재고 요약 리스트 반환 
    @GetMapping("/summary")
    public ResponseEntity<List<DrugInventoryDto>> getInventorySummary() {
        try {
            List<DrugInventoryDto> summary = invLogService.getInventorySummary();
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    //출고 
    @PostMapping("/withdraw")
    public ResponseEntity<String> withdrawDrug(@RequestBody WithdrawRequest request) {
        try {
            invLogService.withdrawStock(request.getDrugId(), request.getQuantity());
            return ResponseEntity.ok("출고 성공");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("출고 실패");
        }
    }
    
    //입출고 내역
    @GetMapping("/logs")
    public ResponseEntity<List<Inv_log>> getLogsByDrug(@RequestParam int drugId) {
        try {
            List<Inv_log> logs = invLogRepo.findByDrug_DrugIdOrderByOccurredAtDesc(drugId);
            return ResponseEntity.ok(logs);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    //제품 관리
    @PostMapping("/update")
    public ResponseEntity<String> updateStockLog(@RequestBody Inv_log log) {
        try {
            invLogService.updateStockLog(log);
            return ResponseEntity.ok("수정 완료");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("수정 실패");
        }
    }


}
