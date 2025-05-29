package com.dita.util;

import com.dita.domain.Drug;
import com.dita.domain.Drug_category;
import com.dita.domain.Form_Type;
import com.dita.service.DrugCsvService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

@Component
@RequiredArgsConstructor
public class DrugCsvLoader implements CommandLineRunner {

    private final DrugCsvService drugCsvService;

    @Override
    public void run(String... args) throws Exception {
    	InputStream is = getClass().getClassLoader().getResourceAsStream("templates/Drug/DrugData.csv");
        if (is == null) {
            System.err.println("❌ DrugData.csv not found in resources/Drug/");
            return;
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"))) {
            String line;
            br.readLine(); // 헤더 스킵

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length < 5) continue;

                String drugCode = parts[0].trim();
                String drugName = parts[1].trim();
                String mainIngredient = parts[2].trim().isEmpty() ? "미상" : parts[2].trim();
                String categoryRaw = parts[3].trim();
                String formTypeRaw = parts[4].trim();

                try {
                    Drug drug = Drug.builder()
                            .drugCode(drugCode)
                            .drugName(drugName)
                            .mainIngredient(mainIngredient)
                            .category(parseCategory(categoryRaw))
                            .formType(parseFormType(formTypeRaw))
                            .build();

                    drugCsvService.saveIfNotExists(drug);
                    System.out.println(" 저장 성공: " + drugCode);

                } catch (IllegalArgumentException e) {
                    System.err.println("❌ Enum 매핑 실패 → drugCode: " + drugCode +
                            ", category: " + categoryRaw +
                            ", formType: " + formTypeRaw);
                }
            }
        }
    }

    private Drug_category parseCategory(String raw) {
        if (raw == null || raw.isBlank()) return Drug_category.미분류;
        return switch (raw) {
            case "향정", "향정신성의약품" -> Drug_category.향정;
            case "대마" -> Drug_category.대마;
            case "일반" -> Drug_category.일반;
            default -> Drug_category.미분류;
        };
    }

    private Form_Type parseFormType(String raw) {
        if (raw == null || raw.isBlank()) return Form_Type.기타;
        return switch (raw) {
            case "내복", "내복약" -> Form_Type.내복약;
            case "외용", "외용약" -> Form_Type.외용약;
            case "주사" -> Form_Type.주사;
            case "수액", "수액제" -> Form_Type.수액;
            default -> Form_Type.기타;
        };
    }
}
