package com.dita.util;

import com.dita.domain.Disease;
import com.dita.persistence.DiseaseRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
@RequiredArgsConstructor
public class DiseaseExcelLoader implements CommandLineRunner {

    private final DiseaseRepository diseaseRepository;

    @Override
    public void run(String... args) throws Exception {
        InputStream is = getClass().getClassLoader().getResourceAsStream("templates/hospital/질병분류코드.xlsx");
        if (is == null) {
            System.err.println("❌ 질병분류코드.xlsx not found");
            return;
        }

        try (Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);

            int rowCount = 0;
            for (Row row : sheet) {
                rowCount++;
                if (rowCount < 3) continue;

                Cell nameCell = row.getCell(4); // E열
                if (nameCell == null) continue;

                nameCell.setCellType(CellType.STRING);
                String name = nameCell.getStringCellValue().trim();
                if (name.isEmpty()) continue;

                Disease disease = Disease.builder()
                        .name(name)
                        .build();

                // 중복 방지 (동일 name이 이미 있으면 저장 안 함)
                if (!diseaseRepository.existsByName(name)) {
                    diseaseRepository.save(disease);
                    System.out.println("✔ 저장 완료: " + name);
                }
            }
        }
    }
}
