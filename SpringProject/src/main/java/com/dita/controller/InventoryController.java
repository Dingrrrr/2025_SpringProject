package com.dita.controller;

import com.dita.domain.Inv_log;
import com.dita.service.InvLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InvLogService invLogService;

    public InventoryController(InvLogService invLogService) {
        this.invLogService = invLogService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addStock(@RequestBody Inv_log log) {
        try {
            invLogService.addStockLog(log);
            return ResponseEntity.ok().body("약품 입고 기록이 저장되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("입고 실패: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("서버 오류 발생");
        }
    }
}
