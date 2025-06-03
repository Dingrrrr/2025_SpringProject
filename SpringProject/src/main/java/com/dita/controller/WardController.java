package com.dita.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dita.domain.Ward;
import com.dita.persistence.WardRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class WardController {

    private final WardRepository wardRepository;

    @GetMapping("/wards")
    public List<String> getAllWardNames() {
        return wardRepository.findAll().stream()
                .map(Ward::getName)
                .collect(Collectors.toList());
    }
}
