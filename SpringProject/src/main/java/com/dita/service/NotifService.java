// ── com.dita.service.NotificationService.java ──
package com.dita.service;

import com.dita.domain.Grade;
import com.dita.domain.Notif;
import com.dita.domain.Patient;
import com.dita.domain.User;
import com.dita.persistence.*;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotifService {

    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final NotifRepository notifRepository;

    /**
     * 실제 알림 생성 로직 (공통)
     * 
     * @param timeLabel  - "아침 9시", "점심 12시", "저녁 18시", "야간 24시"
     */
    private void createVitalReminderNotifications(String timeLabel) {
        // 1) DB에서 전체 환자 목록 조회
        List<Patient> patientList = patientRepository.findAll();

        // 2) DB에서 grade='간호사' 인 모든 User 조회
        List<User> nurseList = userRepository.findByGrade(Grade.간호사);

        // 현재 시점 (알림 생성 시각)
        LocalDateTime now = LocalDateTime.now();

        // 3) 환자별, 간호사별로 Notif 엔티티 생성 후 저장
        for (Patient patient : patientList) {
            String patientName = patient.getPatientName();
            // 예: "곧 홍길동 환자분의 건강상태 점검 시간입니다. (아침 9시)"
            String message = String.format("곧 %s 환자분의 건강상태 점검 시간입니다. (%s)", patientName, timeLabel);

            for (User nurse : nurseList) {
                Notif notif = Notif.builder()
                        .user(nurse)             // ManyToOne으로 User 엔티티를 직접 연결
                        .type("vital_reminder")  // 알림 타입 (원하시는 대로 바꿀 수 있음)
                        .message(message)
                        .isRead(false)           // 처음 생성 시에는 읽음 처리되지 않음
                        .createdAt(now)          // 생성 시각
                        .build();

                notifRepository.save(notif);
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // 1) 아침 알림 스케줄 08:50 (아침 9시 측정 10분 전)
    // ─────────────────────────────────────────────────────────────────────────────
    @Scheduled(cron = "0 50 8 * * *", zone = "Asia/Seoul")
    public void sendMorningVitalReminder() {
        createVitalReminderNotifications("아침 9시");
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // 2) 점심 알림 스케줄 11:50 (점심 12시 측정 10분 전)
    // ─────────────────────────────────────────────────────────────────────────────
    @Scheduled(cron = "0 50 11 * * *", zone = "Asia/Seoul")
    public void sendLunchVitalReminder() {
        createVitalReminderNotifications("점심 12시");
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // 3) 저녁 알림 스케줄 17:50 (저녁 18시 측정 10분 전)
    // ─────────────────────────────────────────────────────────────────────────────
    @Scheduled(cron = "0 50 17 * * *", zone = "Asia/Seoul")
    public void sendDinnerVitalReminder() {
        createVitalReminderNotifications("저녁 18시");
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // 4) 야간 알림 스케줄 23:50 (야간 24시 측정 10분 전)
    // ─────────────────────────────────────────────────────────────────────────────
    @Scheduled(cron = "0 50 23 * * *", zone = "Asia/Seoul")
    public void sendNightVitalReminder() {
        createVitalReminderNotifications("야간 24시");
    }
    
 // 개별 알림 읽음 처리
    @Transactional
    public void markAsRead(Integer notifId) {
        Notif notif = notifRepository.findById(notifId)
                                     .orElseThrow(() -> new RuntimeException("알림을 찾을 수 없습니다."));
        if (!notif.isRead()) {
            notif.setRead(true);
            notifRepository.save(notif);
        }
    }

    // 로그인한 사용자의 모든 알림을 읽음 처리
    @Transactional
    public void markAllAsRead(String usersId) {
        List<Notif> list = notifRepository.findByUser_UsersIdAndIsReadFalse(usersId);
        for (Notif n : list) {
            n.setRead(true);
            notifRepository.save(n);
        }
    }

    // 해당 사용자의 모든 알림 조회
    @Transactional(readOnly = true)
    public List<Notif> getNotificationsForUser(String usersId) {
        // 예: 최신순으로 정렬해서 가져오려면 추가 조건을 줄 수 있습니다.
        return notifRepository.findByUser_UsersIdOrderByCreatedAtDesc(usersId);
    }
    
}
