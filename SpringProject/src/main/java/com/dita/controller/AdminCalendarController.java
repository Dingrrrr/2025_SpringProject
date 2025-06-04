package com.dita.controller;

import com.dita.domain.Grade;
import com.dita.domain.Sched;
import com.dita.persistence.AdminMemberRepository;
import com.dita.vo.SchedDto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/adminCalendarManage")
public class AdminCalendarController {

    private final AdminMemberRepository adminMemberRepository;

    @GetMapping("")
    public String showSchedulePage(Model model) {
        List<Sched> doctorScheds = adminMemberRepository.findByUserGrade(Grade.의사);
        List<Sched> nurseScheds = adminMemberRepository.findByUserGrade(Grade.간호사);
        List<Sched> billingScheds = adminMemberRepository.findByUserGrade(Grade.수납);

        model.addAttribute("doctorScheds", doctorScheds);
        model.addAttribute("nurseScheds", nurseScheds);
        model.addAttribute("billingScheds", billingScheds);

        return "admin/adminCalendarManage"; // templates/admin/adminCalendarManage.html
    }

    @PostMapping("/update")
    @ResponseBody
    public String updateSchedule(@RequestBody SchedDto dto) {
        Sched sched = adminMemberRepository.findById(dto.getScheduleId())
                .orElseThrow(() -> new RuntimeException("스케줄 없음"));
        sched.setStartTime(dto.getStartTime());
        sched.setEndTime(dto.getEndTime());
        sched.setType(dto.getType());
        sched.setWorkDays(dto.getWorkDays());
        adminMemberRepository.save(sched);
        return "수정 완료";
    }

    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public String deleteSchedule(@PathVariable int id) {
        if (adminMemberRepository.existsById(id)) {
            adminMemberRepository.deleteById(id);
            return "삭제 완료";
        }
        return "삭제 실패: 해당 ID 없음";
    }
}
