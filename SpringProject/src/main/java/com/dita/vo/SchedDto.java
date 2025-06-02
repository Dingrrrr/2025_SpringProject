package com.dita.vo;

import com.dita.domain.Type;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.DayOfWeek;
import java.util.List;

@Data
public class SchedDto {
    private int scheduleId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Type type; // 정확히 com.dita.domain.Type 임포트 사용
    private List<DayOfWeek> workDays;
}
