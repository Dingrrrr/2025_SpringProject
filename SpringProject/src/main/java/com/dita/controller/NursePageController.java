package com.dita.controller;

import java.security.Principal;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.dita.domain.Grade;
import com.dita.domain.User;
import com.dita.persistence.LoginPageRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.java.Log;

@Controller
@Log
@RequestMapping("/nurse/")
public class NursePageController {
	
	private final LoginPageRepository repo;

    public NursePageController(LoginPageRepository repo) {
        this.repo = repo;
    }
	
	@GetMapping("/NurseChart")
    public String showNurseChartPage(Model model) {
		// 필요 시 model에 데이터 추가 가능
        return "nurse/NurseChart"; // templates/nurse/NurseChart.html 호출
    }
	
	@GetMapping("/VitalRecord")
    public String showVitalRecordPage(Model model) {
		// 필요 시 model에 데이터 추가 가능
        return "nurse/VitalRecord"; // templates/nurse/VitalRecord.html 호출
    }
	
	@GetMapping("/MedicationRecord")
    public String showMedicatonRecordPage(Model model) {
		// 필요 시 model에 데이터 추가 가능
        return "nurse/MedicationRecord"; // templates/nurse/MedicationRecord.html 호출
    }
	
	@GetMapping("/NurseHome")
	public String showNurseHome(HttpServletRequest request, Model model) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return "redirect:/Login/Login";
		}
		User loginUser = (User) session.getAttribute("loginUser");
		if (loginUser == null || !loginUser.getGrade().equals(Grade.간호사)) {
			return "redirect:/Login/Login";
		}
		
		model.addAttribute("userName", loginUser.getUsersName());
        model.addAttribute("usersId", loginUser.getUsersId());
        model.addAttribute("grade", loginUser.getGrade().name());
		
		return "nurse/NurseHome";
	}
	
}
