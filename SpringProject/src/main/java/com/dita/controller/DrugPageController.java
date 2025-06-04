package com.dita.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.dita.persistence.DrugRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dita.domain.Drug;

@Controller
@RequestMapping("/Drug")
public class DrugPageController {

    private final DrugRepository drugRepository;

    DrugPageController(DrugRepository drugRepository) {
        this.drugRepository = drugRepository;
    }

    @GetMapping("/Drug")
    public String showDrugPage() { //Drug페이지
        return "/Drug/Drug";  
    }
    
    @GetMapping("/Add")
    public String showAddDrugPopup() { // AddDrug 팝업 페이지
        return "/Drug/AddDrug";
    }
    
    @GetMapping("/api/drugs/search")
    @ResponseBody
    public List<Map<String, Object>> searchDrugs(@RequestParam String keyword) {
        return drugRepository.findByDrugNameContaining(keyword).stream().map(drug -> {
            Map<String, Object> map = new HashMap<>();
            map.put("drugId", drug.getDrugId());
            map.put("drugName", drug.getDrugName());
            map.put("formType", drug.getFormType());
            map.put("drugCode", drug.getDrugCode());
            return map;
        }).collect(Collectors.toList());
    }

}
