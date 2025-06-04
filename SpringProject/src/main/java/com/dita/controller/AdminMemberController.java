// AdminMemberController.java
package com.dita.controller;

import com.dita.domain.Grade;
import com.dita.domain.Sched;
import com.dita.domain.Type;
import com.dita.domain.User;
import com.dita.persistence.AdminMemberRepository;
import com.dita.persistence.UserRepository;
import com.dita.vo.SchedDto;
import com.dita.vo.UserDto;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/adminMemberManage")
public class AdminMemberController {

    @Autowired
    private UserRepository userRepository;
    private final AdminMemberRepository adminMemberRepository;

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
} 
