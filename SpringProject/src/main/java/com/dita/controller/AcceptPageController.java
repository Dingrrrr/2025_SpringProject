package com.dita.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.java.Log;

@Controller
@Log
@RequestMapping("/acceptance/")
public class AcceptPageController {
	
	@GetMapping("/acceptanceHome")
    public String showAcceptanceHomePage(Model model) {
		// 필요 시 model에 데이터 추가 가능
        return "acceptance/acceptanceHome"; 
    }
	@GetMapping("/acceptanceDoctor")
    public String AcceptanceDoctor(Model model) {
		// 필요 시 model에 데이터 추가 가능
        return "acceptance/acceptanceDoctor"; 
    }
	
	@GetMapping("/acceptanceCondition")
    public String AcceptanceCondition(Model model) {
		// 필요 시 model에 데이터 추가 가능
        return "acceptance/acceptanceCondition"; 
	}
	@GetMapping("/AcceptanceReceipt")
    public String showAcceptanceReceiptPage(Model model) {
		// 필요 시 model에 데이터 추가 가능
        return "acceptance/AcceptanceReceipt"; 

    }
	
}
