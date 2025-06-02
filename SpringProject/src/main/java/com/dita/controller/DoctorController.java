package com.dita.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dita.domain.Grade;
import com.dita.persistence.UserRepository;

@RestController
@RequestMapping("/api")
public class DoctorController {


    @Autowired
    private UserRepository userRepository;


    // 모든 의사 목록 조회
    @GetMapping("/doctors")
    public List<Map<String, String>> getAllDoctors() {
        return userRepository.findAll().stream()
            .filter(user -> Grade.의사.equals(user.getGrade()))
            .map(user -> {
                Map<String, String> dto = new HashMap<>();
                dto.put("usersId", user.getUsersId());
                dto.put("usersName", user.getUsersName());
                return dto;
            })
            .collect(Collectors.toList());
    }
}

