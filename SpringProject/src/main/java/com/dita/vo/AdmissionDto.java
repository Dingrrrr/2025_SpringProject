package com.dita.vo;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdmissionDto {
    private String patientId;
    private String doctorId;
    private int bedId;
    private String reason;
}
