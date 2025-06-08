// AdminMemberController.java
package com.dita.controller;

import com.dita.domain.Grade;
import com.dita.domain.Patient;
import com.dita.domain.PatientType;
import com.dita.domain.Sched;
import com.dita.domain.Type;
import com.dita.domain.User;
import com.dita.persistence.AdminMemberRepository;
import com.dita.persistence.ApptRepository;
import com.dita.persistence.PatientRepository;
import com.dita.persistence.UserRepository;
import com.dita.vo.PatientDto;
import com.dita.vo.SchedDto;
import com.dita.vo.UserDto;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/adminMemberManage")
public class AdminMemberController {

    @Autowired
    private UserRepository userRepository;
    private final AdminMemberRepository adminMemberRepository;
    private final PatientRepository patientRepository;
    private final ApptRepository apptRepository;

   
    @GetMapping
    public String showUserManage(Model model) {
        var doctors = userRepository.findByGrade(Grade.의사);
        model.addAttribute("doctor", doctors);
        model.addAttribute("nurse", userRepository.findByGrade(Grade.간호사));
        model.addAttribute("billing", userRepository.findByGrade(Grade.수납));
        model.addAttribute("patient", userRepository.findByGrade(null));
        return "admin/adminMemberManage";
    }

    @PostMapping("/update")
    @ResponseBody
    public String updateUser(@RequestBody UserDto dto) {
        Optional<User> opt = userRepository.findById(dto.getUsersId());
        if (opt.isPresent()) {
            User user = opt.get();
            user.setUsersName(dto.getUsersName());
            user.setUsersPhone(dto.getUsersPhone());
            user.setUsersEmail(dto.getUsersEmail());
            user.setUsersAddress(dto.getUsersAddress());
            user.setUsersBirth(dto.getUsersBirth());
            user.setUsersGender(dto.getUsersGender());
            user.setUsersIdcard(dto.getUsersIdcard());
            userRepository.save(user);
            return "수정 완료";
        }
        return "수정 실패: 사용자 없음";
    }

    @DeleteMapping("/delete/{userId}")
    @ResponseBody
    public String deleteUser(@PathVariable String userId) {
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
            return "삭제 완료";
        }
        return "삭제 실패: 해당 사용자 없음";
    }
    
    // [GET] 환자 목록
    @GetMapping("/patients")
    @ResponseBody
    public List<PatientDto> getPatients() {
        return patientRepository.findAll()
                .stream()
                .map(PatientDto::new) // Patient → PatientDto
                .collect(Collectors.toList());
    }
    
    // [POST] 환자 정보 수정
    @PostMapping("/patients/update")
    @ResponseBody
    public String updatePatient(@RequestBody PatientDto dto) {
        Optional<Patient> opt = patientRepository.findById(dto.getPatientId());
        if (opt.isPresent()) {
            Patient patient = opt.get();
            patient.setPatientName(dto.getName());
            patient.setPatientGender(dto.getGender());
            patient.setPatientBirth(dto.getBirth());
            patient.setPatientPhone(dto.getPhone());
            patient.setPatientAddress(dto.getAddress());
            patient.setPatientSymptom(dto.getSymptom());
            patient.setPatientType(dto.getType());
            patientRepository.save(patient);
            return "환자 정보가 수정되었습니다.";
        }
        return "수정 실패: 환자 없음";
    }

    // [DELETE] 환자 삭제
    @DeleteMapping("/patients/delete/{id}")
    @ResponseBody
    public String deletePatient(@PathVariable int id) {
        Optional<Patient> opt = patientRepository.findById(id);
        if (opt.isPresent()) {
            Patient patient = opt.get();

            apptRepository.deleteAllByPatient(patient); // 예약 먼저 삭제
            patientRepository.delete(patient);          // 그 후 환자 삭제

            return "환자 정보가 삭제되었습니다.";
        }
        return "삭제 실패: 해당 환자 없음";
    }

} 
