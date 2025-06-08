package com.dita.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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

    // ✅ 1. 입원 중 환자 리스트
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

    // ✅ 2. 입원 처리
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

    // ✅ 3. 입원 대기 환자 리스트
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
    
    public void updatePatientStatus(int patientId, String status, String symptom, String admittedAt, String doctorId) {
        Optional<Admission> admissionOpt = admissionRepo.findByPatientId(patientId);

        if (admissionOpt.isPresent()) {
            Admission admission = admissionOpt.get();

            // 상태 업데이트
            admission.getPatient().setPatientType(PatientType.valueOf(status));

            // 진단명 업데이트
            admission.getPatient().setPatientSymptom(symptom);

            // 입원일 업데이트
            admission.setAdmittedAt(LocalDate.parse(admittedAt).atStartOfDay());

            // 담당의 업데이트
            User doctor = userRepository.findById(String.valueOf(doctorId))
                    .orElseThrow(() -> new IllegalArgumentException("의사를 찾을 수 없습니다."));


            patientRepository.save(admission.getPatient());
            admissionRepo.save(admission);
        }
    }

    public void deleteAdmission(int patientId) {
        Optional<Admission> opt = admissionRepo.findByPatientId(patientId);

        if (opt.isPresent()) {
            Admission admission = opt.get();

            // 1. 환자 상태를 '입원대기'로 변경
            Patient patient = admission.getPatient();
            patient.setPatientType(PatientType.입원대기);
            patientRepository.save(patient);

            // 2. 침대 상태를 '사용가능'으로 변경
            Bed bed = admission.getBed();
            if (bed != null) {
                bed.setBedstatus(StatusBed.사용가능);  // ✅ enum으로 정확히 설정
                bedRepository.save(bed);
            }

            // 3. 입원 정보 삭제
            admissionRepo.delete(admission);
        }
    }


    
}
