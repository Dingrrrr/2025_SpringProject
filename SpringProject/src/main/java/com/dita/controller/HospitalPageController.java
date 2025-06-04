package com.dita.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HospitalPageController {

    // ── 외부 진입점 ──
    @GetMapping("/home")
    public String hospitalLanding() {
        // templates/hospital/hospital_home.html
        return "hospital/hospital_home";
    }

    // ── /hospital 이하 매핑 ──

    @GetMapping("/hospital/home")
    public String showDashboard() {
        // templates/hospital/home.html
        return "hospital/home";
    }

    @GetMapping("/hospital/notification")
    public String showNotification() {
        // templates/hospital/notification.html
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
