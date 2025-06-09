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
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.dita.domain.Appt;
import com.dita.domain.Grade;
import com.dita.domain.PatientType;
import com.dita.domain.Status;
import com.dita.domain.User;
import com.dita.persistence.ApptRepository;
import com.dita.persistence.DiseaseRepository;
import com.dita.persistence.DrugRepository;
import com.dita.persistence.UserRepository;
import com.dita.service.MedRecService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

@Controller
@Log
@RequestMapping("/hospital")
@RequiredArgsConstructor
public class HospitalPageController {

    private final UserRepository userRepository;

    private final ApptRepository apptRepository;
    private final MedRecService medRecService;
    private final DrugRepository drugRepository;
    private final DiseaseRepository diseaseRepository;

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

    /** ── 알림 ──
     * GET /hospital/notification → templates/hospital/notification.html
     */
    @GetMapping("/notification")
    public String getTodayNotifications(HttpSession session, Model model) {
        // (원한다면 세션·등급 체크 추가)
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

    /** ── 차트 ── GET /hospital/chart → chart.html */
    @GetMapping("/chart")
    public String showChartPage(HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null || !Grade.의사.equals(loginUser.getGrade())) {
            return "redirect:/Login/Login";
        }
        model.addAttribute("displayName", loginUser.getUsersName());

        // 1) 방 목록
        List<String> rooms = List.of("진료실1", "진료실2", "진료실3", "진료실4");
        model.addAttribute("rooms", rooms);

        // 2) DB에서 "확정" 상태인 모든 예약 가져오기
        List<Appt> confirmed = apptRepository.findByStatus(Status.확정);

        // 3) 방별로 묶어서 맵 생성
        Map<String, List<Appt>> appointmentsByRoom = rooms.stream()
            .collect(Collectors.toMap(
                Function.identity(),                        // key = "진료실1" 등
                room -> confirmed.stream()                  // value = confirmed 중에서
                           .filter(a -> room.equals(a.getRoom()))
                           .collect(Collectors.toList()),
                (oldList, newList) -> oldList,              // 병합 로직 (중복 키가 없으므로 사용 안 됨)
                LinkedHashMap::new                          // 순서를 보장할 LinkedHashMap
            ));
        model.addAttribute("appointmentsByRoom", appointmentsByRoom);
        model.addAttribute("diseases", diseaseRepository.findAll());
        model.addAttribute("drugs",    drugRepository.findAll());
        model.addAttribute("doctors",  userRepository.findByGrade(Grade.의사));
        return "hospital/chart";
    }

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
        return "hospital/statistics";
    }
    
    @PostMapping("/chart")
    public String saveChartRecord(
    		@RequestParam Integer apptId,
            @RequestParam Integer patientId,
            @RequestParam String  doctorId,
            @RequestParam String  chiefComplaint,
            @RequestParam Long    diseaseId,
            @RequestParam Integer drugId,
            @RequestParam String  notes
    		) {
    		medRecService.saveRecord(
            apptId, patientId, doctorId,
            chiefComplaint, diseaseId, drugId, notes
        );
    	return "redirect:/hospital/chart";
    }
    
}