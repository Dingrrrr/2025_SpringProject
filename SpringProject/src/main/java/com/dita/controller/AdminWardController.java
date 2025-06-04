package com.dita.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.dita.domain.Ward;
import com.dita.persistence.WardRepository;
import com.dita.vo.WardDto;

import java.util.Optional;

@RestController
@RequestMapping("/admin/adminRoomManage")
public class AdminWardController {

    @Autowired
    private WardRepository wardRepository;

    // 병동 추가
    @PostMapping("/add")
    public ResponseEntity<String> addWard(@RequestBody WardDto dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("병동 이름은 필수입니다.");
        }

        Ward ward = Ward.builder()
                .name(dto.getName())
                .build();
        wardRepository.save(ward);
        return ResponseEntity.ok("병동이 추가되었습니다.");
    }

    // 병동 수정
    @PutMapping("/{wardId}")
    public ResponseEntity<String> updateWard(@PathVariable int wardId, @RequestBody WardDto dto) {
        Optional<Ward> optionalWard = wardRepository.findById(wardId);
        if (optionalWard.isEmpty()) {
            return ResponseEntity.badRequest().body("해당 병동이 존재하지 않습니다.");
        }

        Ward ward = optionalWard.get();
        ward.setName(dto.getName());
        wardRepository.save(ward);
        return ResponseEntity.ok("병동이 수정되었습니다.");
    }

    // 병동 삭제
    @DeleteMapping("/{wardId}")
    public ResponseEntity<String> deleteWard(@PathVariable int wardId) {
        Optional<Ward> optionalWard = wardRepository.findById(wardId);
        if (optionalWard.isEmpty()) {
            return ResponseEntity.badRequest().body("해당 병동이 존재하지 않습니다.");
        }

        wardRepository.deleteById(wardId);
        return ResponseEntity.ok("병동이 삭제되었습니다.");
    }
}
