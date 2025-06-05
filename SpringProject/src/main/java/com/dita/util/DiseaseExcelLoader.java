/*
 * package com.dita.util;
 * 
 * import com.dita.domain.Disease; import com.dita.service.DiseaseService;
 * import lombok.RequiredArgsConstructor; import
 * org.apache.poi.hssf.usermodel.HSSFWorkbook; // ✅ .xls import
 * org.apache.poi.ss.usermodel.*; import
 * org.springframework.boot.CommandLineRunner; import
 * org.springframework.stereotype.Component;
 * 
 * import java.io.InputStream;
 * 
 * @Component
 * 
 * @RequiredArgsConstructor public class DiseaseExcelLoader implements
 * CommandLineRunner {
 * 
 * private final DiseaseService diseaseService;
 * 
 * @Override public void run(String... args) throws Exception { InputStream is =
 * getClass().getClassLoader().getResourceAsStream(
 * "templates/hospital/질병분류코드.xls"); if (is == null) {
 * System.err.println("❌ 질병분류코드.xls not found in resources/templates/hospital/"
 * ); return; }
 * 
 * try (Workbook workbook = new HSSFWorkbook(is)) { Sheet sheet =
 * workbook.getSheetAt(0); int rowCount = 0;
 * 
 * for (Row row : sheet) { if (rowCount++ == 0) continue; // 첫 줄(헤더) 스킵
 * 
 * Cell codeCell = row.getCell(0); // 질병코드 Cell nameCell = row.getCell(1); //
 * 질병명
 * 
 * if (codeCell == null || nameCell == null) continue;
 * 
 * codeCell.setCellType(CellType.STRING); nameCell.setCellType(CellType.STRING);
 * 
 * String code = codeCell.getStringCellValue().trim(); String name =
 * nameCell.getStringCellValue().trim();
 * 
 * if (code.isEmpty() || name.isEmpty()) continue;
 * 
 * Disease disease = Disease.builder() .code(code) .name(name) .build();
 * 
 * diseaseService.saveIfNotExists(disease); System.out.println("✔ 저장 완료: " +
 * code + " / " + name); } } } }
 */