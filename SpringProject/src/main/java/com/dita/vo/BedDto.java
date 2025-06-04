package com.dita.vo;

import lombok.Data;

@Data
public class BedDto {
	private int bedId;
    private String bedNumber;
    private String bedStatus; // enum 문자열 (예: "사용 가능", "사용 중")
    private int wardId;
}
