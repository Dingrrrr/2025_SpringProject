package com.dita.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.dita.domain.Grade;
import com.dita.domain.Sched;
import com.dita.domain.Ward;
import com.dita.persistence.AdminMemberRepository;
import com.dita.persistence.BedRepository;
import com.dita.persistence.WardRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

@Controller
@Log
@RequiredArgsConstructor
@RequestMapping("/admin/")
public class AdminPageController {

    private final BedRepository bedRepository;
    private final WardRepository wardRepository;

    private final AdminMemberRepository adminMemberRepository;

    @GetMapping("/adminRoomManage")
    public String showBedPage(Model model) {
        List<Ward> wards = wardRepository.findAll();

        model.addAttribute("wards", wards);
        model.addAttribute("bedMap", wards.stream()
            .collect(Collectors.toMap(
                Ward::getWardId,
                ward -> bedRepository.findByWard_WardId(ward.getWardId())
            ))
        );

        return "admin/adminRoomManage"; 
    }

    @GetMapping("/adminTotalManage")
    public String showTotalPage(Model model) {
        return "admin/adminTotalManage";
    }
}
