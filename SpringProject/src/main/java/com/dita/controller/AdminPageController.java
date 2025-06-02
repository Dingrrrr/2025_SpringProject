package com.dita.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.dita.domain.Grade;
import com.dita.domain.Sched;
import com.dita.persistence.AdminMemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

@Controller
@Log
@RequiredArgsConstructor
@RequestMapping("/admin/")
public class AdminPageController {

    private final AdminMemberRepository adminMemberRepository;

    @GetMapping("/adminMemberManage")
    public String showMemberManagePage(Model model) {
        List<Sched> doctorScheds = adminMemberRepository.findByUserGrade(Grade.의사);
        List<Sched> nurseScheds = adminMemberRepository.findByUserGrade(Grade.간호사);
        List<Sched> billingScheds = adminMemberRepository.findByUserGrade(Grade.수납);

        model.addAttribute("doctorScheds", doctorScheds);
        model.addAttribute("nurseScheds", nurseScheds);
        model.addAttribute("billingScheds", billingScheds);

        return "admin/adminMemberManage";
    }

    @GetMapping("/adminCalendarManage")
    public String showCalendarPage(Model model) {
        return "admin/adminCalendarManage";
    }

    @GetMapping("/adminRoomManage")
    public String showRoomPage(Model model) {
        return "admin/adminRoomManage";
    }

    @GetMapping("/adminTotalManage")
    public String showTotalPage(Model model) {
        return "admin/adminTotalManage";
    }
}
