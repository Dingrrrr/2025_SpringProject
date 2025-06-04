package com.dita.controller;

import com.dita.domain.Bed;
import com.dita.domain.StatusBed;
import com.dita.domain.Ward;
import com.dita.persistence.BedRepository;
import com.dita.persistence.WardRepository;
import com.dita.vo.BedDto;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/adminRoomManage")
public class AdminBedController {

    private final BedRepository bedRepository;
    private final WardRepository wardRepository;

    // 침대 추가
    @PostMapping("/roomadd")
    public ResponseEntity<String> addBed(@RequestBody BedDto request) {
        Ward ward = wardRepository.findById(request.getWardId())
            .orElseThrow(() -> new IllegalArgumentException("병동 ID가 유효하지 않습니다."));

        Bed bed = Bed.builder()
            .bedNumber(request.getBedNumber())
            .bedstatus(StatusBed.valueOf(request.getBedStatus())) // enum 이름 그대로 사용
            .ward(ward)
            .build();

        bedRepository.save(bed);
        return ResponseEntity.ok("ok");
    }

    @PutMapping("/room{bedId}")
    public ResponseEntity<String> updateBed(@PathVariable int bedId, @RequestBody BedDto request) {
        Bed bed = bedRepository.findById(bedId)
                .orElseThrow(() -> new IllegalArgumentException("해당 침대가 존재하지 않습니다."));

        bed.setBedNumber(request.getBedNumber());

        try {
            StatusBed status = StatusBed.valueOf(request.getBedStatus()); // ← 여기 수정됨
            bed.setBedstatus(status);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("유효하지 않은 상태 값입니다. (예: 사용가능, 사용중)");
        }

        bedRepository.save(bed);
        return ResponseEntity.ok("updated");
    }

    // 침대 삭제
    @DeleteMapping("/room{bedId}")
    public ResponseEntity<String> deleteBed(@PathVariable int bedId) {
        if (!bedRepository.existsById(bedId)) {
            return ResponseEntity.badRequest().body("해당 침대가 존재하지 않습니다.");
        }
        bedRepository.deleteById(bedId);
        return ResponseEntity.ok("deleted");
    }
}
