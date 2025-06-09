package com.dita.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.dita.domain.Appt;
<<<<<<< HEAD
import com.dita.domain.Grade;
import com.dita.domain.PatientType;
import com.dita.domain.Status;
=======

>>>>>>> branch 'main' of https://github.com/Jangton/2025_SpringProject.git
import com.dita.domain.User;
import com.dita.persistence.ApptRepository;
import com.dita.service.NotifService;
import com.fasterxml.jackson.core.JsonProcessingException;

import jakarta.servlet.http.HttpSession;
import lombok.extern.java.Log;

@Controller
@Log
@RequestMapping("/hospital")
public class HospitalPageController {
<<<<<<< HEAD
=======
	
    private final NotifService notifService;
	private final ApptRepository ApptRepository;
	
	public HospitalPageController(ApptRepository apptRepository, NotifService notifService) {
        this.ApptRepository = apptRepository;
        this.notifService = notifService;
	}
>>>>>>> branch 'main' of https://github.com/Jangton/2025_SpringProject.git

    private final ApptRepository apptRepository;

    public HospitalPageController(ApptRepository apptRepository) {
        this.apptRepository = apptRepository;
    }

    /** ── 메인(의사 전용) ── 
     * GET /hospital/hospital_home → templates/hospital/hospital_home.html
     */
    @GetMapping("/hospital_home")
    public String showHospitalHome(HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/Login/Login";
        }
        if (!Grade.의사.equals(loginUser.getGrade())) {
            return "redirect:/Login/Login?error=not_doctor";
        }
        model.addAttribute("displayName", loginUser.getUsersName());
        return "hospital/hospital_home";
    }

<<<<<<< HEAD
    /** ── 알림 ──
     * GET /hospital/notification → templates/hospital/notification.html
     */
    @GetMapping("/notification")
    public String getTodayNotifications(HttpSession session, Model model) {
        // (원한다면 세션·등급 체크 추가)
=======
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


    @GetMapping("/hospital/hospital_home")
    public String showhospital_homePage() {
    	return "/hospital/hospital_home";
    }
    
    @GetMapping("/hospital/notification")
    public String getTodayNotifications(Model model) {
>>>>>>> branch 'main' of https://github.com/Jangton/2025_SpringProject.git
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.atTime(23, 59, 59);

        List<Appt> todayAppts = apptRepository.findAll().stream()
            .filter(a -> a.getScheduledAt() != null
                      && !a.getScheduledAt().isBefore(start)
                      && !a.getScheduledAt().isAfter(end))
            .sorted(Comparator.comparing(Appt::getScheduledAt))
            .toList();

        model.addAttribute("todayAppts", todayAppts);
        return "hospital/notification";
    }

    /** ── 예약 ── GET /hospital/reservation → reservation.html */
    @GetMapping("/reservation")
    public String showReservationPage(HttpSession session, Model model) {
    	User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/Login/Login";
        }
        if (!Grade.의사.equals(loginUser.getGrade())) {
            return "redirect:/Login/Login?error=not_doctor";
        }
        model.addAttribute("displayName", loginUser.getUsersName());
        return "hospital/reservation";
    }

    /** ── 진료 ── GET /hospital/treatment → treatment.html */
    @GetMapping("/treatment")
    public String showTreatmentPage(HttpSession session, Model model) {
    	User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/Login/Login";
        }
        if (!Grade.의사.equals(loginUser.getGrade())) {
            return "redirect:/Login/Login?error=not_doctor";
        }
        
        List<PatientType> types = List.of(PatientType.진료대기, PatientType.진료중);
        List<Appt> appts = apptRepository.findAllByStatusAndPatient_PatientTypeIn(Status.확정, types);

        Map<String, List<Appt>> byRoom = appts.stream().collect(Collectors.groupingBy(Appt::getRoom));
        
        model.addAttribute("appointmentsByRoom", byRoom);
        model.addAttribute("displayName", loginUser.getUsersName());
        model.addAttribute("waitingCount", appts.stream().filter(a -> a.getPatient().getPatientType()==PatientType.진료대기).count());
        model.addAttribute("displayName", loginUser.getUsersName());
        return "hospital/treatment";
    }

<<<<<<< HEAD
    /** ── 차트 ── GET /hospital/chart → chart.html */
    @GetMapping("/chart")
    public String showChartPage(HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null || !Grade.의사.equals(loginUser.getGrade())) {
            return "redirect:/Login/Login";
        }
        model.addAttribute("displayName", loginUser.getUsersName());

        // 1) 방 목록 선언
        List<String> rooms = List.of("진료실1","진료실2","진료실3","진료실4");
        model.addAttribute("rooms", rooms);

        // 2) 빈 맵 초기화
        Map<String, Integer> countByRoom = new LinkedHashMap<>();
        rooms.forEach(r -> countByRoom.put(r, 0));

        // 3) DB에서 확정 상태 예약 가져오기
        List<Appt> confirmed = apptRepository.findByStatus(Status.확정);

        // 4) 방별로 카운트
        for (Appt a : confirmed) {
            String room = a.getRoom();
            // 방에 미리 키가 세팅되어 있다면
            if (countByRoom.containsKey(room)) {
                countByRoom.put(room, countByRoom.get(room) + 1);
            }
        }
        model.addAttribute("countByRoom", countByRoom);

        // 5) (필요하다면) 기다리는/진행중 맵도 넘겨주되, 
        //    템플릿에서는 countByRoom만 쓰면 됩니다.
        Map<String,List<Appt>> waitingByRoom   = rooms.stream()
            .collect(Collectors.toMap(r->r, r->new ArrayList<>(), (a,b)->a, LinkedHashMap::new));
        Map<String,List<Appt>> inProgressByRoom = rooms.stream()
            .collect(Collectors.toMap(r->r, r->new ArrayList<>(), (a,b)->a, LinkedHashMap::new));
        for (Appt a : confirmed) {
            String room = a.getRoom();
            if (!waitingByRoom.containsKey(room)) continue;
            if (a.getPatient().getPatientType()==PatientType.진료대기) {
                waitingByRoom.get(room).add(a);
            } else {
                inProgressByRoom.get(room).add(a);
            }
        }
        model.addAttribute("waitingByRoom", waitingByRoom);
        model.addAttribute("inProgressByRoom", inProgressByRoom);

=======
    @GetMapping("/hospital/chart")
    public String showChartPage(Model model) throws JsonProcessingException {
       
>>>>>>> branch 'main' of https://github.com/Jangton/2025_SpringProject.git
        return "hospital/chart";
    }

<<<<<<< HEAD
    /** ── 통계 ── GET /hospital/statistics → statistics.html */
    @GetMapping("/statistics")
    public String showStatisticsPage(HttpSession session, Model model) {
    	User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/Login/Login";
        }
        if (!Grade.의사.equals(loginUser.getGrade())) {
            return "redirect:/Login/Login?error=not_doctor";
        }
        model.addAttribute("displayName", loginUser.getUsersName());
=======

    @GetMapping("/hospital/statistics")
    public String showStatisticsPage() {
        // templates/hospital/statistics.html
>>>>>>> branch 'main' of https://github.com/Jangton/2025_SpringProject.git
        return "hospital/statistics";
    }
    
}
