package com.dita.controller;

import com.dita.domain.Grade;
import com.dita.domain.User;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dita.persistence.LoginPageRepository;
import com.dita.service.EmailService;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import lombok.extern.java.Log;

@Controller
@Log
@RequestMapping("/Login")
public class LoginPageController {

	private final LoginPageRepository repo;
	private final EmailService emailService;

	public LoginPageController(LoginPageRepository repo, EmailService emailService) {
		this.repo = repo;
		this.emailService = emailService;
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
	public String processJoin(@RequestParam String usersId, @RequestParam String usersPwd,
			@RequestParam String usersName, @RequestParam String usersBirth, @RequestParam String usersPhone,
			@RequestParam String usersEmail, @RequestParam String usersAddress, @RequestParam String usersGender,
			@RequestParam String usersIdcard, @RequestParam Grade grade
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
	
	@PostMapping("/Findid")
    public String processFindId(
            @RequestParam String usersName,
            @RequestParam String usersEmail,
            @RequestParam String code,
            HttpSession session,
            Model model
    ) {
        // 1) 세션에 저장된 인증번호 검증
        String savedCode = (String) session.getAttribute("emailCode");
        if (savedCode == null || !savedCode.equals(code)) {
            model.addAttribute("error", "인증번호가 일치하지 않습니다.");
            return "Login/Findid";
        }

        // 2) 이름+이메일로 사용자 조회
        Optional<User> opt = repo.findByUsersNameAndUsersEmail(usersName, usersEmail);
        if (opt.isEmpty()) {
            model.addAttribute("error", "일치하는 회원 정보가 없습니다.");
            return "Login/Findid";
        }

        // 3) 조회된 아이디를 모델에 담고 Resultid 뷰로 이동
        model.addAttribute("usersId", opt.get().getUsersId());
        return "Login/Resultid";
    }

	@GetMapping("/Findpwd")
	public String showFindpwdpage(Model model) {
		return "Login/Findpwd";
	}
	
	@PostMapping("/Findpwd")
    public String processFindPwd(
            @RequestParam String usersId,
            @RequestParam String usersEmail,
            @RequestParam String code,
            HttpSession session,
            Model model
    ) {
        // 1) 세션에 저장된 인증번호 검증
        String savedCode = (String) session.getAttribute("emailCode");
        if (savedCode == null || !savedCode.equals(code)) {
            model.addAttribute("error", "인증번호가 일치하지 않습니다.");
            return "Login/Findpwd";
        }

        // 2) 아이디+이메일로 사용자 조회
        Optional<User> opt = repo.findByUsersIdAndUsersEmail(usersId, usersEmail);
        if (opt.isEmpty()) {
            model.addAttribute("error", "일치하는 회원 정보가 없습니다.");
            return "Login/Findpwd";
        }

        // 3) 조회된 아이디를 모델에 담고 Resultpwd 뷰로 이동
        model.addAttribute("usersId", usersId);
        return "Login/Resultpwd";
    }

	@GetMapping("/Resultpwd")
	public String showResultpwd(Model model) {
		return "Login/Resultpwd";
	}
	
	//비밀번호 재설정
	@PostMapping("Resultpwd")
	public String processResultPwd(
		@RequestParam String usersId,
	    @RequestParam String newPwd,
	    @RequestParam String confirmPwd,
	    Model model
		) {
		// (1) 새 비밀번호/확인 일치 체크 (서버측 이중검증)
	    if (!newPwd.equals(confirmPwd)) {
	        model.addAttribute("usersId", usersId);
	        model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
	        return "Login/Resetpwd";
	    }

	    // (2) 사용자 로드 및 비밀번호 업데이트
	    User u = repo.findById(usersId)
	                 .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자"));
	    u.setUsersPwd(newPwd);
	    repo.save(u);

	    // (3) 완료 후 로그인 페이지로
	    return "redirect:/Login/Login";
	}

	@GetMapping("/checkId")
	@ResponseBody
	public ResponseEntity<Boolean> checkId(@RequestParam String usersId) {
		boolean exists = repo.existsById(usersId);
		return ResponseEntity.ok(!exists);
	}

	@PostMapping("/sendEmailCode")
	@ResponseBody
	public ResponseEntity<String> sendEmailCode(@RequestParam String usersEmail, HttpSession session) {
		String code = emailService.generateCode();
		try {
			emailService.sendVerificationCode(usersEmail, code);
			// 세션에 이메일과 코드를 저장
			session.setAttribute("emailToVerify", usersEmail);
			session.setAttribute("emailCode", code);
			return ResponseEntity.ok("sent");
		} catch (MessagingException e) {
			return ResponseEntity.status(500).body("error");
		}
	}

	@PostMapping("/verifyEmailCode")
	@ResponseBody
	public ResponseEntity<Boolean> verifyEmailCode(@RequestParam String code, HttpSession session) {
		String saved = (String) session.getAttribute("emailCode");
		// 인증 성공 시 세션에서 제거(Optional)
		boolean ok = saved != null && saved.equals(code);
		if (ok) {
			// 인증 완료 플래그만 남기고, 코드 자체는 삭제하지 않습니다.
		    session.setAttribute("emailVerified", true);
	   }
		return ResponseEntity.ok(ok);
	}

}
