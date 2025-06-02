package com.dita.controller;

import com.dita.domain.Sched;
import com.dita.domain.Type;
import com.dita.persistence.AdminMemberRepository;
import com.dita.vo.SchedDto;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/member")
public class AdminMemberApiController {

    private final AdminMemberRepository adminMemberRepository;

    @PostMapping("/update")
    @ResponseBody
    public String updateSchedule(@RequestBody SchedDto dto) {
        Sched sched = adminMemberRepository.findById(dto.getScheduleId())
                             .orElseThrow(() -> new RuntimeException("스케줄 없음"));

        // 값 갱신
        sched.setStartTime(dto.getStartTime());
        sched.setEndTime(dto.getEndTime());
        sched.setType(dto.getType());
        sched.setWorkDays(dto.getWorkDays());

        adminMemberRepository.save(sched);
        return "수정 완료";
    }

    @DeleteMapping("/delete/{id}")
    public String deleteSchedule(@PathVariable int id) {
        if (adminMemberRepository.existsById(id)) {
            adminMemberRepository.deleteById(id);
            return "삭제 완료";
        }
        return "삭제 실패: 해당 ID 없음";
    }
}
