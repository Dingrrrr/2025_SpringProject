package com.dita.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/Drug")
public class DrugPageController {

    @GetMapping("/Drug")
    public String showDrugPage() {
        return "/Drug/Drug";  // templates/Drug.html 파일이 필요!
    }
}
