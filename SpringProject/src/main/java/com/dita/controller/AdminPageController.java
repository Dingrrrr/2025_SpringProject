package com.dita.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.java.Log;

@Controller
@Log
@RequestMapping("/admin/")
public class AdminPageController {
	
	@GetMapping("/adminMemberManage")
    public void showAttendancePage(Model model) {
        
    }
	
}
