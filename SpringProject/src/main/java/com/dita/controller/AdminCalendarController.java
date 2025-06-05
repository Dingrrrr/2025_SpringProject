package com.dita.controller;

import com.dita.domain.Grade;
import com.dita.domain.Sched;
import com.dita.domain.User;
import com.dita.persistence.AdminMemberRepository;
import com.dita.persistence.UserRepository;
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
    private final UserRepository userRepository;
    
    @GetMapping("")
    public String showSchedulePage(Model model) {
        // 유저 전체 조회 (스케줄과 무관하게)
        List<User> doctorUsers = userRepository.findByGrade(Grade.의사);
        List<User> nurseUsers = userRepository.findByGrade(Grade.간호사);
        List<User> billingUsers = userRepository.findByGrade(Grade.수납);

        // 스케줄 조회
        List<Sched> doctorScheds = adminMemberRepository.findByUserGrade(Grade.의사);
        List<Sched> nurseScheds = adminMemberRepository.findByUserGrade(Grade.간호사);
        List<Sched> billingScheds = adminMemberRepository.findByUserGrade(Grade.수납);

        // 뷰에 전달
        model.addAttribute("doctorUsers", doctorUsers);
        model.addAttribute("nurseUsers", nurseUsers);
        model.addAttribute("billingUsers", billingUsers);

        model.addAttribute("doctorScheds", doctorScheds);
        model.addAttribute("nurseScheds", nurseScheds);
        model.addAttribute("billingScheds", billingScheds);

        return "admin/adminCalendarManage";
    }
    
    @GetMapping("/users")
    @ResponseBody
    public List<User> getUsersByGrade(@RequestParam Grade grade) {
        return userRepository.findByGrade(grade);
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
    
    @PostMapping("/create")
    @ResponseBody
    public String createSchedule(@RequestBody SchedDto dto) {
        if (dto.getUsersId() == null || dto.getUsersId().isBlank()) {
            throw new IllegalArgumentException("usersId는 반드시 전달되어야 합니다.");
        }

        User user = userRepository.findById(dto.getUsersId())
            .orElseThrow(() -> new RuntimeException("해당 유저 없음: " + dto.getUsersId()));

        Sched sched = Sched.builder()
            .user(user)
            .startTime(dto.getStartTime())
            .endTime(dto.getEndTime())
            .type(dto.getType())
            .workDays(dto.getWorkDays())
            .build();

        adminMemberRepository.save(sched);
        return "생성 완료";
    }
}
