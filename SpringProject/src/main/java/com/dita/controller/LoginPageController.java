package com.dita.controller;

import com.dita.domain.Grade;
import com.dita.domain.User;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dita.persistence.LoginPageRepository;

import lombok.extern.java.Log;

@Controller
@Log
@RequestMapping("/Login/")
public class LoginPageController {

	 private final LoginPageRepository repo;

	 public LoginPageController(LoginPageRepository repo) {
	     this.repo = repo;
	 }
	
	@GetMapping("/Login")
	public String showLoginPage(Model model) {
		// 필요 시 model에 데이터 추가 가능
		return "Login/Login";
	}
	
	@GetMapping("/Join")
	public String showJoinPage(Model model) {
		return "Login/Join";
	}
	
	@PostMapping("/Join")
    public String processJoin(
            @RequestParam String usersId,
            @RequestParam String usersPwd,
            @RequestParam String usersName,
            @RequestParam String usersBirth,
            @RequestParam String usersPhone,
            @RequestParam String usersEmail,
            @RequestParam String usersAddress,
            @RequestParam String usersGender,
            @RequestParam String usersIdcard,
            @RequestParam Grade grade
            // deptId가 필요하면 추가: @RequestParam Integer deptId
    ) {
        User u = new User();
        u.setUsersId(usersId);
        u.setUsersPwd(usersPwd);
        u.setUsersName(usersName);
        u.setUsersBirth(usersBirth);
        u.setUsersPhone(usersPhone);
        u.setUsersEmail(usersEmail);
        u.setUsersAddress(usersAddress);
        u.setUsersGender(usersGender);
        u.setUsersIdcard(usersIdcard);
        u.setGrade(grade);
        // u.setDeptId(deptId);

        repo.save(u);
        log.info("New user registered: " + usersId);
        return "redirect:/Login/Login";
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
	
	@GetMapping("/checkId")
    @ResponseBody
    public ResponseEntity<Boolean> checkId(@RequestParam String usersId) {
        boolean exists = repo.existsById(usersId);
        return ResponseEntity.ok(!exists);
    }
	
}
