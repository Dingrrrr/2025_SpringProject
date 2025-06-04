package com.dita.vo;

import com.dita.domain.Form_Type;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DrugInventoryDto {
    private int drugId;
    private String name;
    private Form_Type type; 
    private String code;
    private String location;
    private int stock;
}


