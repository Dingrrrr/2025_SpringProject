package com.dita.service;

import com.dita.domain.Notif;
import com.dita.domain.Patient;
import com.dita.domain.PatientType;
import com.dita.domain.User;
import com.dita.domain.Grade;
import com.dita.persistence.NotifRepository;
import com.dita.persistence.PatientRepository;
import com.dita.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientService {

    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final NotifRepository notifRepository;

    @Transactional
    public Patient admitPatient(Patient patient) {
        // 1) 기존에 DB에 저장된 환자를 조회 (업데이트 비교 목적)
        Patient existing = null;
        if (patient.getPatientId() != null) {
            existing = patientRepository.findById(patient.getPatientId()).orElse(null);
        }

        // 2) 환자 저장(INSERT 또는 UPDATE)
        Patient savedPatient = patientRepository.save(patient);

        // 3) “입원 알림 생성 로직” 조건 체크
        boolean wasNotInHospital = (existing == null) 
                                   || (existing.getPatientType() != PatientType.입원중);
        boolean isNowInHospital  = (savedPatient.getPatientType() == PatientType.입원중);

        log.info("[PatientService] 기존환자상태={}, 저장후환자상태={}",
                (existing == null ? "신규" : existing.getPatientType()),
                savedPatient.getPatientType());
        log.info("[PatientService] wasNotInHospital={}, isNowInHospital={}",
                wasNotInHospital, isNowInHospital);

        if (isNowInHospital && wasNotInHospital) {
            // 4) “입원 알림” 생성: Grade.간호사인 사용자 모두에게 Notif 저장
            String message = savedPatient.getPatientName() + "님이 입원하셨습니다.";
            List<User> nurseList = userRepository.findByGrade(Grade.간호사);
            log.info("[PatientService] 간호사 수={}", nurseList.size());

            for (User nurse : nurseList) {
                Notif notif = Notif.builder()
                                   .user(nurse)
                                   .type("입원알림")
                                   .message(message)
                                   .isRead(false)
                                   .build();
                Notif savedNotif = notifRepository.save(notif);
                log.info("[PatientService] 생성된 NotifID={}, to={}",
                        savedNotif.getNotifId(), nurse.getUsersId());
            }
        }

        return savedPatient;
    }
}