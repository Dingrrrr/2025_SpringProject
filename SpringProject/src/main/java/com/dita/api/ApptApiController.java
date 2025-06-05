package com.dita.api;

import com.dita.domain.Appt;
import com.dita.persistence.ApptRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class ApptApiController {

    private final ApptRepository apptRepository;

    public ApptApiController(ApptRepository apptRepository) {
        this.apptRepository = apptRepository;
    }

    @GetMapping("/api/reservations")
    public List<Map<String, String>> getMonthlyAppointments(
            @RequestParam int year,
            @RequestParam int month
    ) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDateTime start = ym.atDay(1).atStartOfDay();
        LocalDateTime end = ym.atEndOfMonth().atTime(23, 59, 59);

        List<Appt> appts = apptRepository.findAll().stream()
                .filter(a -> a.getScheduledAt() != null &&
                        !a.getScheduledAt().isBefore(start) &&
                        !a.getScheduledAt().isAfter(end))
                .collect(Collectors.toList());

        return appts.stream()
        		.map(appt -> Map.of(
        			    "date", appt.getScheduledAt().toLocalDate().toString(),
        			    "time", appt.getScheduledAt().toLocalTime().toString().substring(0,5), // "HH:mm" 포맷
        			    "name", appt.getPatient().getPatientName(),
        			    "reason", appt.getStatus().toString()
        			))
                .collect(Collectors.toList());
    }
}
