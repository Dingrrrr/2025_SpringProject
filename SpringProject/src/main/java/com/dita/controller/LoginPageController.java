package com.dita.controller;

import com.dita.domain.Grade;
import com.dita.domain.User;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.java.Log;

@Controller
@Log
@RequestMapping("/Login")
public class LoginPageController {

	private final LoginPageRepository repo;
	private final EmailService emailService;
	 private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	public LoginPageController(LoginPageRepository repo, EmailService emailService) {
		this.repo = repo;
		this.emailService = emailService;
	}

	@GetMapping("/Login")
	public String showLoginPage(Model model,
			 					@RequestParam(value = "error", required = false) String error,
			 					@RequestParam(value = "logout", required = false) String logout) {
		//화면에 에러 메시지를 띄우고 싶을 때
		if (error != null) {
			model.addAttribute("loginError", "아이디 또는 비밀번호가 올바르지 않습니다.");
		}
		if (logout != null) {
			model.addAttribute("logoutMsg", "로그아웃되었습니다.");
		}
			 						
		return "Login/Login";
	}
	
	@PostMapping("/Login")
	public String processLogin(
	        HttpServletRequest request,
	        @RequestParam String usersId,
	        @RequestParam String usersPwd,
	        Model model) {

	    // ✅ 0) 관리자 하드코딩 로그인 (DB 조회 없이 우선 처리)
	    if (usersId.equals("admin") && usersPwd.equals("1234")) {
	        HttpSession session = request.getSession(true);
	        User adminUser = new User();
	        adminUser.setUsersId("admin");
	        adminUser.setUsersName("관리자");
	        session.setAttribute("loginUser", adminUser);
	        return "redirect:/admin/adminMemberManage";
	    }

	    // 1) 아이디 존재 확인
	    Optional<User> opt = repo.findById(usersId);
	    if (opt.isEmpty()) {
	        return "redirect:/Login/Login?error";
	    }

	    User user = opt.get();

	    // 2) 비밀번호 확인
	    if (!passwordEncoder.matches(usersPwd, user.getUsersPwd())) {
	        return "redirect:/Login/Login?error";
	    }

	    // 3) 세션에 로그인 사용자 저장
	    HttpSession session = request.getSession(true);
	    session.setAttribute("loginUser", user);

	    // 4) 등급별 분기
	    if (Grade.의사.equals(user.getGrade())) {
	        return "redirect:/hospital/hospital_home";
	    } else if (Grade.간호사.equals(user.getGrade())) {
	        return "redirect:/nurse/NurseHome";
	    } else if(Grade.수납.equals(user.getGrade())){
	    	 return "redirect:/acceptance/acceptanceHome";
	    }else {
	        model.addAttribute("loginError", "권한이 없습니다.");
	        return "Login/Login";
	    }
	}
	
	@GetMapping("/Logout")
    public String processLogout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/Login/Login?logout";
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
		String encodedPwd = passwordEncoder.encode(usersPwd);
		User u = new User();
		u.setUsersId(usersId);
		u.setUsersPwd(encodedPwd);
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
	
	@GetMapping("/nurse/NurseHome")
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
