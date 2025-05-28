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
	public String showLoginPage(Model model) {
		// 필요 시 model에 데이터 추가 가능
		return "Login/Login";
	}
	
	@GetMapping("/Join")
	public String showJoinPage(Model model) {
		return "Login/Join";
	}
	
	@GetMapping("/Findid")
	public String showFindidpage(Model model) {
		return "Login/Findid";
	}
	
	@GetMapping("/Resultid")
	public String showResultid(Model model) {
		return "Login/Resultid";
	}
	
	@GetMapping("/Findpwd")
	public String showFindpwdpage(Model model) {
		return "Login/Findpwd";
	}
	
	@GetMapping("/Resultpwd")
	public String showResultpwd(Model model) {
		return "Login/Resultpwd";
	}
	
}
