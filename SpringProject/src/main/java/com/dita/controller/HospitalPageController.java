package com.dita.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
import com.dita.domain.Med_rec;
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
        
        List<PatientType> types = List.of(PatientType.진료대기, PatientType.진료중, PatientType.수납);
        List<Appt> appts = apptRepository.findAllByStatusAndPatient_PatientTypeIn(Status.확정, types);

        Map<String, List<Appt>> byRoom = appts.stream().collect(Collectors.groupingBy(Appt::getRoom));
        
        model.addAttribute("appointmentsByRoom", byRoom);
        model.addAttribute("displayName", loginUser.getUsersName());
        model.addAttribute("waitingCount", appts.stream().filter(a -> a.getPatient().getPatientType()==PatientType.진료대기).count());
        model.addAttribute("displayName", loginUser.getUsersName());
        return "hospital/treatment";
    }
    
    /** ── 진료 기록 작성 ── GET /hospital/record/write?apptId= */
    @GetMapping("/record/write")
    public String showRecordWritePage(@RequestParam Integer apptId, Model model) {
        Appt appt = apptRepository.findById(apptId).orElse(null);
        if (appt == null) {
            return "redirect:/hospital/treatment";
        }

        model.addAttribute("appt", appt);
        return "hospital/record_write"; // templates/hospital/record_write.html
    }

    /** ── 차트 ── GET /hospital/chart → chart.html */
    @GetMapping("/chart")
    public String showChartPage(
        HttpSession session, Model model,
        @RequestParam(required = false) Integer apptId
    ) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null || !Grade.의사.equals(loginUser.getGrade())) {
            return "redirect:/Login/Login";
        }

        model.addAttribute("displayName", loginUser.getUsersName());
        model.addAttribute("doctorId", loginUser.getUsersId());

        // apptId 넘어온 경우 → 환자 상세 차트 작성으로 진입한 것
        if (apptId != null) {
            Optional<Appt> optionalAppt = apptRepository.findById(apptId);
            if (optionalAppt.isPresent()) {
                Appt appt = optionalAppt.get();

                // ✅ 다른 의사가 예약한 환자일 경우 차단
                if (!appt.getDoctor().getUsersId().equals(loginUser.getUsersId())) {
                    return "redirect:/hospital/chart?error=unauthorized_access";
                }

                model.addAttribute("selectedAppt", appt);

                // 진료 기록 존재 여부 (차트 작성 여부)
                boolean alreadyWritten = medRecService.existsByApptId(apptId);
                model.addAttribute("alreadyWritten", alreadyWritten);

                // 환자 과거 진료 기록 + 메모
                model.addAttribute("medRecords", medRecService.findRecordsByPatient(appt.getPatient()));
                model.addAttribute("recentMedRecs", medRecService.findTop2RecordsByPatient(appt.getPatient()));

                boolean chartExists = !medRecService.findByAppt(appt).isEmpty();
                model.addAttribute("chartExists", chartExists);
            }
        }

        // 진료실 목록 + 확정 예약
        List<String> rooms = List.of("진료실1", "진료실2", "진료실3", "진료실4");
        List<Appt> confirmed = apptRepository.findByStatus(Status.확정);
        Map<String, List<Appt>> appointmentsByRoom = rooms.stream()
            .collect(Collectors.toMap(
                Function.identity(),
                room -> confirmed.stream().filter(a -> room.equals(a.getRoom())).collect(Collectors.toList()),
                (oldVal, newVal) -> oldVal,
                LinkedHashMap::new
            ));
        model.addAttribute("appointmentsByRoom", appointmentsByRoom);

        // 진단명 + 약물 목록
        model.addAttribute("diseases", diseaseRepository.findAll());
        model.addAttribute("drugs", drugRepository.findAll());

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

    /**
     * ── 진료 기록 저장 처리 ── 
     * POST /hospital/chart → 진료기록 및 처방약 저장
     * 
     * ✔ 선택된 예약(apptId), 환자(patientId), 의사(doctorId), 증상(chiefComplaint),
     *   진단(diseaseId), 메모(notes), 복수 약물(drugIds + 각 용량/투여정보)을 받아
     *   Med_rec 및 여러 Prescription 레코드 저장
     * ✔ 저장 후 차트 페이지로 리다이렉트
     */
    @PostMapping("/chart")
    public String saveMedRecord(
        @RequestParam Integer apptId,
        @RequestParam Integer patientId,
        @RequestParam String doctorId,
        @RequestParam String chiefComplaint,
        @RequestParam Long diseaseId,
        @RequestParam(required = false) String notes,

        @RequestParam List<Integer> drugIds,
        @RequestParam List<String> dosages,
        @RequestParam List<String> frequencies,
        @RequestParam List<String> durations
    ) {
        Med_rec saved = medRecService.saveRecord(apptId, patientId, doctorId, chiefComplaint, diseaseId, drugIds.get(0), notes);

        for (int i = 0; i < drugIds.size(); i++) {
            medRecService.savePrescription(
                saved,
                drugIds.get(i),
                dosages.get(i),
                frequencies.get(i),
                durations.get(i)
            );
        }

        return "redirect:/hospital/chart";
    }
    
    @PostMapping("/chart/note")
    public String updateNote(
        @RequestParam Integer recordId,
        @RequestParam String notes
    ) {
        Med_rec rec = medRecService.findById(recordId);
        if (rec == null) {
            return "redirect:/hospital/chart";
        }

        rec.setNotes(notes);         // 메모 내용 설정
        medRecService.save(rec);     // 저장

        Integer apptId = rec.getApptId().getApptId();
        return "redirect:/hospital/chart?apptId=" + apptId;  // 차트 페이지로 리다이렉트
    }  
}
