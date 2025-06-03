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

    @PostMapping("/add")
    public ResponseEntity<String> addBed(@RequestBody BedDto request) {
        Ward ward = wardRepository.findById(request.getWardId())
                .orElseThrow(() -> new IllegalArgumentException("병동 ID가 유효하지 않습니다."));

        Bed bed = new Bed();
        bed.setBedNumber(request.getBedNumber());
        bed.setBedstatus(StatusBed.fromDisplayName(request.getBedStatus())); // enum 변환
        bed.setWard(ward);

        bedRepository.save(bed);
        return ResponseEntity.ok("ok");
    }

    @DeleteMapping("/{bedId}")
    public ResponseEntity<String> deleteBed(@PathVariable int bedId) {
        if (!bedRepository.existsById(bedId)) {
            return ResponseEntity.badRequest().body("해당 침대가 존재하지 않습니다.");
        }
        bedRepository.deleteById(bedId);
        return ResponseEntity.ok("deleted");
    }
}
