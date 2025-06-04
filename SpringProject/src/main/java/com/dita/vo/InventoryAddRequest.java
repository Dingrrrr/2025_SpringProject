package com.dita.vo;

import com.dita.domain.ChangeType;
import lombok.Data;

@Data
public class InventoryAddRequest {
    private Integer drugId;
    private String name;
    private String code;
    private String location;
    private int quantity;
    private String type; // Form_Type의 문자열
    private ChangeType changeType;
}
