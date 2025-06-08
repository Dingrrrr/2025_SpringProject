package com.dita.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


//입원용 dto
@Data
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@lombok.Builder
public class AdmittedPatientDto {
	    private int patientId;      // 환자 PK
	    private String name;        // 환자 이름
	    private String gender;      // 성별
	    private int age;            // 나이
	    private String diagnosis;   // 진단명

	    private String date;        // 입원일 (yyyy-MM-dd)
	    private String status;      // 입원대기, 입원중, 퇴원
	    private String doctor;      // 담당의 이름

	    private String bedNumber;        // 병실 이름 (예: 101호)
	    private String ward;        // 병실 이름 (예: 내과병동)
	
}
