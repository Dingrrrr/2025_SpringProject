package com.dita.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.dita.domain.Appt;
import com.dita.domain.Status;
import com.dita.domain.User;
import com.dita.persistence.ApptRepository;

import jakarta.servlet.http.HttpSession;

@Controller
public class HospitalPageController {
	private final ApptRepository ApptRepository;
	public HospitalPageController(ApptRepository apptRepository) {
        this.ApptRepository = apptRepository;
	}

    // ── 외부 진입점 ──
    @GetMapping("/home")
    public String hospitalLanding() {
        // templates/hospital/hospital_home.html
        return "hospital/hospital_home";
    }

    // ── /hospital 이하 매핑 ──

    @GetMapping("/hospital/home")
    public String showDashboard(HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/Login/Login"; // 로그인 안했으면 로그인으로
        }
        model.addAttribute("displayName", loginUser.getUsersName()); // ✅ 이름 넘기기
        return "hospital/home";
    }



    @GetMapping("/hospital/notification")
    public String getTodayNotifications(Model model) {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.atTime(23, 59, 59);

        List<Appt> todayAppts = ApptRepository.findAll().stream()
            .filter(appt -> appt.getScheduledAt() != null &&
                            !appt.getScheduledAt().isBefore(start) &&
                            !appt.getScheduledAt().isAfter(end))
            .sorted(Comparator.comparing(Appt::getScheduledAt)) // ✅ 시간순 정렬 추가
            .toList();

        model.addAttribute("todayAppts", todayAppts);
        return "hospital/notification"; 
    }


    @GetMapping("/hospital/reservation")
    public String showReservationPage() {
        // templates/hospital/reservation.html
        return "hospital/reservation";
    }

    @GetMapping("/hospital/treatment")
    public String showTreatmentPage() {
        // templates/hospital/treatment.html
        return "hospital/treatment";
    }

    @GetMapping("/hospital/chart")
    public String showChartPage() {
        // templates/hospital/chart.html
        return "hospital/chart";
    }

    @GetMapping("/hospital/statistics")
    public String showStatisticsPage() {
        // templates/hospital/statistics.html
        return "hospital/statistics";
    }
}
