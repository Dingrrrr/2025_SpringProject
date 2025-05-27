package com.dita.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.java.Log;

@Controller
@Log
@RequestMapping("/Login/")
public class LoginPageController {

	@GetMapping("/Login")
	public String showAttendancePage(Model model) {
		// 필요 시 model에 데이터 추가 가능
		return "Login/Login"; //templates/admin/adminMemberManage.html 호출
	}
	
}
