package com.dita.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
import com.dita.domain.Patient;
import com.dita.domain.PatientType;
import com.dita.domain.Status;
import com.dita.domain.User;
import com.dita.domain.Vital_sign;
import com.dita.persistence.ApptRepository;
import com.dita.persistence.DiseaseRepository;
import com.dita.persistence.DrugRepository;
import com.dita.persistence.PatientRepository;
import com.dita.persistence.UserRepository;
import com.dita.persistence.VitalRepository;
import com.dita.service.MedRecService;
import com.dita.vo.PatientWithVitalDTO;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

@Controller
@Log
@RequestMapping("/hospital")
@RequiredArgsConstructor
public class HospitalPageController {

    /* ── Repository & Service ─────────────────────────────── */
    private final UserRepository userRepository;
    private final ApptRepository apptRepository;
    private final MedRecService medRecService;
    private final DrugRepository drugRepository;
    private final DiseaseRepository diseaseRepository;
    private final PatientRepository  patientRepository;
    private final VitalRepository   vitalRepository;


    /* ─────────────────────────────────────────────────────── */

    /** 메인(의사 전용) */
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

    /** 오늘 알림 */
    @GetMapping("/notification")
    public String getTodayNotifications(HttpSession session, Model model) {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end   = today.atTime(23, 59, 59);

        List<Appt> todayAppts = apptRepository.findAll().stream()
            .filter(a -> a.getScheduledAt() != null
                      && !a.getScheduledAt().isBefore(start)
                      && !a.getScheduledAt().isAfter(end))
            .sorted(Comparator.comparing(Appt::getScheduledAt))
            .toList();

        model.addAttribute("todayAppts", todayAppts);
        return "hospital/notification";
    }

    /** 예약 화면 */
    @GetMapping("/reservation")
    public String showReservationPage(HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null)      return "redirect:/Login/Login";
        if (!Grade.의사.equals(loginUser.getGrade()))
            return "redirect:/Login/Login?error=not_doctor";

        model.addAttribute("displayName", loginUser.getUsersName());
        return "hospital/reservation";
    }

    /** 진료 대기/진행 화면 */
    @GetMapping("/treatment")
    public String showTreatmentPage(HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null)      return "redirect:/Login/Login";
        if (!Grade.의사.equals(loginUser.getGrade()))
            return "redirect:/Login/Login?error=not_doctor";

        List<PatientType> types = List.of(PatientType.진료대기, PatientType.진료중, PatientType.수납);
        List<Appt> appts = apptRepository.findAllByStatusAndPatient_PatientTypeIn(Status.확정, types);

        Map<String, List<Appt>> byRoom =
            appts.stream().collect(Collectors.groupingBy(Appt::getRoom));

        model.addAttribute("appointmentsByRoom", byRoom);
        model.addAttribute("waitingCount",
                           appts.stream().filter(a -> a.getPatient().getPatientType()==PatientType.진료대기).count());
        model.addAttribute("displayName", loginUser.getUsersName());
        return "hospital/treatment";
    }

    /** 진료 기록 작성 (미사용 시 삭제 가능) */
    @GetMapping("/record/write")
    public String showRecordWritePage(@RequestParam Integer apptId, Model model) {
        Appt appt = apptRepository.findById(apptId).orElse(null);
        if (appt == null) return "redirect:/hospital/treatment";

        model.addAttribute("appt", appt);
        return "hospital/record_write";
    }

    /** 차트 화면 */
    @GetMapping("/chart")
    public String showChartPage(HttpSession session,
                                Model model,
                                @RequestParam(required = false) Integer apptId,
                                @RequestParam(required = false) String error) {

        /* ── 로그인/등급 검사 ───────────────────────────── */
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null || !Grade.의사.equals(loginUser.getGrade())) {
            return "redirect:/Login/Login";
        }
        model.addAttribute("displayName", loginUser.getUsersName());
        model.addAttribute("doctorId",   loginUser.getUsersId());
        model.addAttribute("error",      error);
        /* ─────────────────────────────────────────────── */

        /* ── 특정 예약(apptId)로 진입한 경우 ────────────── */
        if (apptId != null) {
            Optional<Appt> optionalAppt = apptRepository.findById(apptId);
            if (optionalAppt.isPresent()) {
                Appt appt = optionalAppt.get();

                /* 다른 의사의 예약이면 차단 */
                if (!appt.getDoctor().getUsersId().equals(loginUser.getUsersId())) {
                    return "redirect:/hospital/chart?error=unauthorized_access";
                }
                model.addAttribute("selectedAppt", appt);

                /* 차트 존재 여부 */
                boolean alreadyWritten = medRecService.existsByApptId(apptId);
                model.addAttribute("alreadyWritten", alreadyWritten);
                model.addAttribute("chartExists",   alreadyWritten);

                /* 전체 진료 기록 (해당 환자) */
                List<Med_rec> medRecords = medRecService.findRecordsByPatient(appt.getPatient());
                model.addAttribute("medRecords", medRecords);

                /* 🔄 수정 : 최신 진료 기록 한 건 (메모 유무 관계없음) */
                Med_rec latestRecord = medRecService.findLatestRecordByApptId(apptId);
                model.addAttribute("latestRecord", latestRecord);

                /* 최근 메모가 달린 기록 2건 */
                List<Med_rec> recentNotes = medRecords.stream()
                    .filter(rec -> rec.getNotes() != null && !rec.getNotes().isBlank())
                    .sorted(Comparator.comparing(Med_rec::getCreatedAt).reversed())
                    .limit(2)
                    .toList();
                model.addAttribute("recentMedRecs", recentNotes);
            }
        }
        /* ─────────────────────────────────────────────── */

        /* 진료실별 확정 예약 맵 */
        List<String> rooms = List.of("진료실1", "진료실2", "진료실3", "진료실4");
        List<Appt> confirmed = apptRepository.findByStatus(Status.확정);
        Map<String, List<Appt>> appointmentsByRoom = rooms.stream()
            .collect(Collectors.toMap(
                Function.identity(),
                room -> confirmed.stream()
                                 .filter(a -> room.equals(a.getRoom()))
                                 .collect(Collectors.toList()),
                (oldVal, newVal) -> oldVal,
                LinkedHashMap::new
            ));
        model.addAttribute("appointmentsByRoom", appointmentsByRoom);

        /* 진단명, 약물 목록 */
        model.addAttribute("diseases", diseaseRepository.findAll());
        model.addAttribute("drugs",    drugRepository.findAll());

        return "hospital/chart";
    }

    /** 통계 화면 */
    @GetMapping("/statistics")
    public String showStatisticsPage(HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/Login/Login";
        if (!Grade.의사.equals(loginUser.getGrade()))
            return "redirect:/Login/Login?error=not_doctor";

        model.addAttribute("displayName", loginUser.getUsersName());
        return "hospital/statistics";
    }

    /* ── 차트 + 처방 저장 ─────────────────────────────── */
    @PostMapping("/chart")
    public String saveMedRecord(@RequestParam Integer apptId,
                                @RequestParam Integer patientId,
                                @RequestParam String doctorId,
                                @RequestParam String chiefComplaint,
                                @RequestParam Long diseaseId,
                                @RequestParam(required = false) String notes,
                                @RequestParam List<Integer> drugIds,
                                @RequestParam List<String> dosages,
                                @RequestParam List<String> frequencies,
                                @RequestParam List<String> durations,
                                @RequestParam String patientType) {

        /* 1) Med_rec 저장 (첫 번째 약물은 saveRecord 내부에서 연결) */
        Med_rec saved = medRecService.saveRecord(
            apptId, patientId, doctorId, chiefComplaint,
            diseaseId, drugIds.get(0), notes
        );

        /* 2) Prescription 다건 저장 */
        for (int i = 0; i < drugIds.size(); i++) {
            medRecService.savePrescription(
                saved, drugIds.get(i),
                dosages.get(i), frequencies.get(i), durations.get(i)
            );
        }

        /* 3) 환자 상태(수납/입원) 업데이트 */
        apptRepository.findById(apptId).ifPresent(appt -> {
            appt.getPatient().setPatientType(PatientType.valueOf(patientType));
            apptRepository.save(appt);
        });

        return "redirect:/hospital/chart?apptId=" + apptId;
    }

    /* ── 메모 저장 ───────────────────────────────────── */
    @PostMapping("/chart/note")
    public String updateNote(@RequestParam Integer recordId,
                             @RequestParam String notes) {

        Med_rec rec = medRecService.findById(recordId);
        if (rec == null) return "redirect:/hospital/chart";

        rec.setNotes(notes);
        medRecService.save(rec);

        Integer apptId = rec.getApptId().getApptId();
        return "redirect:/hospital/chart?apptId=" + apptId;
    }
    
    /* -- 환자 정보 검색 --------------------------------------*/
    @GetMapping("/search")
    public String searchPatient(@RequestParam String keyword,
                                HttpSession session,
                                Model model) {

        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null || !Grade.의사.equals(loginUser.getGrade()))
            return "redirect:/Login/Login";

        keyword = keyword.trim();
        List<Patient> patients = keyword.isBlank()
            ? List.of()
            : patientRepository.findAllByPatientName(keyword);   // ← 인스턴스 메서드

        // Patient + Vital → DTO 매핑
        List<PatientWithVitalDTO> dtoList = patients.stream()
            .map(p -> {
                Vital_sign v = vitalRepository          // ← 인스턴스 메서드
                    .findFirstByPatient_PatientIdOrderByRecordedAtDesc(p.getPatientId())
                    .orElse(null);
                return new PatientWithVitalDTO(p, v);   // ← DTO 생성자
            })
            .toList();

        model.addAttribute("patientResults", dtoList);

        // 나머지 공통 모델 값 세팅을 위해 기존 메서드 호출
        return showTreatmentPage(session, model);
    }
}
