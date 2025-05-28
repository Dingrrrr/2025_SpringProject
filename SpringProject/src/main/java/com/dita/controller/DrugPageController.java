package com.dita.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/Drug")
public class DrugPageController {

    @GetMapping("/Drug")
    public String showDrugPage() { //Drug페이지
        return "/Drug/Drug";  
    }
    
    @GetMapping("/Add")
    public String showAddDrugPopup() { // AddDrug 팝업 페이지
        return "/Drug/AddDrug";
    }
}
