package com.dita.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.dita.domain.Admission;
import com.dita.domain.Bed;
import com.dita.domain.Patient;
import com.dita.domain.PatientType;
import com.dita.domain.StatusBed;
import com.dita.domain.User;
import com.dita.persistence.AdmissionRepository;
import com.dita.persistence.BedRepository;
import com.dita.persistence.PatientRepository;
import com.dita.persistence.UserRepository;
import com.dita.vo.AdmittedPatientDto;
import com.dita.vo.PatientDto;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdmissionService {

    private final AdmissionRepository admissionRepo;
    private final PatientRepository patientRepository;
    private final BedRepository bedRepository;
    private final UserRepository userRepository;

    // 1. 입원 중 환자 리스트
    public List<AdmittedPatientDto> getAdmittedPatients() {
        return admissionRepo.findAll().stream()
            .map(adm -> {
                Patient p = adm.getPatient();
                Bed b = adm.getBed();
                return AdmittedPatientDto.builder()
                    .patientId(p.getPatientId())
                    .name(p.getPatientName())
                    .gender(p.getPatientGender())
                    .age(p.getAge())
                    .diagnosis(p.getPatientSymptom())
                    .date(adm.getAdmittedAt() != null ? adm.getAdmittedAt().toString() : "")
                    .status(p.getPatientType() != null ? p.getPatientType().name() : "")
                    .doctor(adm.getDoctor() != null ? adm.getDoctor().getUsersName() : "미정")
                    .bedNumber(b != null ? b.getBedNumber() : "미정")
                    .ward(b != null && b.getWard() != null ? b.getWard().getName() : "미정")
                    .build();
            })
            .collect(Collectors.toList());
    }

    // 2. 입원 처리
    @Transactional
    public void admit(int patientId, String doctorId, int bedId) {
        Patient patient = patientRepository.findById(patientId)
            .orElseThrow(() -> new IllegalArgumentException("환자 없음"));
        User doctor = userRepository.findById(doctorId)
            .orElseThrow(() -> new IllegalArgumentException("의사 없음"));
        Bed bed = bedRepository.findById(bedId)
            .orElseThrow(() -> new IllegalArgumentException("병상 없음"));

        Admission admission = Admission.builder()
            .patient(patient)
            .doctor(doctor)
            .bed(bed)
            .admittedAt(LocalDateTime.now())
            .build();
        admissionRepo.save(admission);

        patient.setPatientType(PatientType.입원중);
        patientRepository.save(patient);

        bed.setBedstatus(StatusBed.사용중);
        bedRepository.save(bed);
    }

    // 3. 입원 대기 환자 리스트
    public List<PatientDto> getWaitingPatients() {
        return patientRepository.findByPatientType(PatientType.입원대기).stream()
            .map((Patient p) -> PatientDto.builder()
                .patientId(p.getPatientId())
                .name(p.getPatientName())
                .gender(p.getPatientGender())
                .age(p.getAge())
                .symptom(p.getPatientSymptom())
                .build())
            .collect(Collectors.toList());
    }
    
    //5. 수정
    public void updatePatientStatus(int patientId, String status, String symptom, String admittedAt, String doctorId) {
        List<Admission> admissions = admissionRepo.findByPatientId(patientId);

        if (!admissions.isEmpty()) {
            Admission admission = admissions.get(0); // 가장 최신 입원 기록

            // 1. 환자 상태 변경
            admission.getPatient().setPatientType(PatientType.valueOf(status));

            // 2. 증상 업데이트
            admission.getPatient().setPatientSymptom(symptom);

            // 3. 입원일(admittedAt)은 그대로 유지
            // (이미 admission.getAdmittedAt()에 들어있으므로 따로 건드릴 필요 없음)

            // 4. 퇴원 처리
            if ("퇴원".equals(status)) {
                // 4-1. 퇴원 시간 설정
                admission.setDischargeAt(LocalDateTime.now());

                // 4-2. 침대 비우기
                Bed bed = admission.getBed();
                if (bed != null) {
                    bed.setBedstatus(StatusBed.사용가능);
                    bedRepository.save(bed);
                }
            }

            // 5. 담당의사 변경
            User doctor = userRepository.findById(doctorId)
                    .orElseThrow(() -> new IllegalArgumentException("의사를 찾을 수 없습니다."));
            admission.setDoctor(doctor);

            // 6. 저장
            patientRepository.save(admission.getPatient());
            admissionRepo.save(admission);
         // 로그 확인
         
        }
    }

    
    //4. 삭제
    public void deleteAdmission(int patientId) {
        List<Admission> admissions = admissionRepo.findByPatientId(patientId);

        if (!admissions.isEmpty()) {
            Admission admission = admissions.get(0); // 기준 필요 시 최신만 선택

            Patient patient = admission.getPatient();
            patient.setPatientType(PatientType.입원대기);
            patientRepository.save(patient);

            Bed bed = admission.getBed();
            if (bed != null) {
                bed.setBedstatus(StatusBed.사용가능);
                bedRepository.save(bed);
            }

            admissionRepo.delete(admission);
        }
    }

    //통계

    public Map<String, Map<String, Long>> getAdmissionStatistics(String startDate, String endDate) {
        LocalDateTime start = (startDate != null) ? LocalDate.parse(startDate).atStartOfDay()
                : LocalDate.now().minusDays(6).atStartOfDay();
        LocalDateTime end = (endDate != null) ? LocalDate.parse(endDate).atTime(23, 59, 59)
                : LocalDate.now().atTime(23, 59, 59);

        List<Admission> allAdmissions = admissionRepo.findAllWithDetails(); // 퇴원 포함 전체

        // ✅ 입원/퇴원 시점에 존재한 상태 기준 통계
        Map<String, Long> statusStats = allAdmissions.stream()
        	    .map(Admission::getPatient)
        	    .filter(p -> p != null && p.getPatientType() != null)
        	    .collect(Collectors.groupingBy(
        	        p -> p.getPatientType().name(),
        	        Collectors.counting()
        	    ));


        // ✅ 연령대 분포: 입원 시점 기준 (퇴원 포함)
        Map<String, Long> ageStats = allAdmissions.stream()
                .filter(a -> a.getAdmittedAt() != null && !a.getAdmittedAt().isBefore(start) && !a.getAdmittedAt().isAfter(end))
                .map(Admission::getPatient)
                .filter(p -> p != null && p.getAge() > 0)
                .collect(Collectors.groupingBy(
                        p -> {
                            int age = p.getAge();
                            if (age < 20) return "10대 이하";
                            else if (age < 30) return "20대";
                            else if (age < 40) return "30대";
                            else if (age < 50) return "40대";
                            else if (age < 60) return "50대";
                            else return "60대 이상";
                        },
                        Collectors.counting()
                ));

        // ✅ 의사별 환자 수
        Map<String, Long> doctorStats = allAdmissions.stream()
                .filter(a -> a.getAdmittedAt() != null && !a.getAdmittedAt().isBefore(start) && !a.getAdmittedAt().isAfter(end))
                .filter(a -> a.getDoctor() != null)
                .collect(Collectors.groupingBy(
                        a -> a.getDoctor().getUsersName(),
                        Collectors.counting()
                ));

        // ✅ 입원 추이
        Map<String, Long> admitTrend = allAdmissions.stream()
                .filter(a -> a.getAdmittedAt() != null && !a.getAdmittedAt().isBefore(start) && !a.getAdmittedAt().isAfter(end))
                .collect(Collectors.groupingBy(
                        a -> a.getAdmittedAt().toLocalDate().toString(),
                        TreeMap::new,
                        Collectors.counting()
                ));

        // ✅ 퇴원 추이
        Map<String, Long> dischargeTrend = allAdmissions.stream()
                .filter(a -> a.getDischargeAt() != null && !a.getDischargeAt().isBefore(start) && !a.getDischargeAt().isAfter(end))
                .collect(Collectors.groupingBy(
                        a -> a.getDischargeAt().toLocalDate().toString(),
                        TreeMap::new,
                        Collectors.counting()
                ));

        // ✅ 진단명별
        Map<String, Long> reasonStats = allAdmissions.stream()
                .map(Admission::getAdmissionReason)
                .filter(reason -> reason != null && !reason.isBlank())
                .collect(Collectors.groupingBy(
                        r -> r,
                        Collectors.counting()
                ));

        Map<String, Map<String, Long>> result = new HashMap<>();
        result.put("상태별통계", statusStats);
        result.put("연령대통계", ageStats);
        result.put("의사별환자수", doctorStats);
        result.put("입원추이", admitTrend);
        result.put("퇴원추이", dischargeTrend);
        result.put("진단명통계", reasonStats);

        return result;
    }

}
