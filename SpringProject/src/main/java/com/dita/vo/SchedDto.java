package com.dita.vo;

import com.dita.domain.Type;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.DayOfWeek;
import java.util.List;

@Data
public class SchedDto {
    private int scheduleId;
    
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;
    
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;
    
    private Type type;
    private List<String> workDays; // 이제 "월", "화", ... 형태의 문자열 리스트
    private String usersId;
}

