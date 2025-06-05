// ── com.dita.controller.NotifController.java ──
package com.dita.controller;

import com.dita.domain.Notif;
import com.dita.domain.User;
import com.dita.domain.Grade;
import com.dita.persistence.*;
import com.dita.service.NotifService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/notif")
@RequiredArgsConstructor
public class NotifController {

    private final NotifRepository notifRepository;
    private final UserRepository userRepository;
    private final NotifService notifService;

    /**
     * 1) 특정 간호사 ID(usersId)로 요청하면, 해당 사용자의 알림 전체를 최신순으로 반환
     *    GET /api/notif/{usersId}
     */
    @GetMapping("/{usersId}")
    public List<Notif> getNotifsByUser(@PathVariable String usersId) {
        Optional<User> opt = userRepository.findById(usersId);
        if (opt.isPresent() && opt.get().getGrade().equals(Grade.간호사)) {
            User nurse = opt.get();
            return notifRepository.findByUserOrderByCreatedAtDesc(nurse);
        }
        // 존재하지 않거나 간호사 등급이 아닌 경우 빈 리스트 반환
        return List.of();
    }

    /**
     * 2) 개별 알림 읽음 처리 (PATCH)
     *    PATCH /api/notif/read/{notifId}
     */
    @PatchMapping("/read/{notifId}")
    public ResponseEntity<Void> markAsRead(@PathVariable Integer notifId) {
        Optional<Notif> opt = notifRepository.findById(notifId);
        if (opt.isPresent() && !opt.get().isRead()) {
            Notif n = opt.get();
            n.setRead(true);
            notifRepository.save(n);
        }
        // 존재하지 않거나 이미 읽음인 경우에도 200 OK 반환
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/mark-all-read")
    public ResponseEntity<Void> markAllAsRead(HttpSession session) {
        // (1) 세션에서 "loginUser" 꺼내기
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            // 세션에 로그인 정보가 없으면 그냥 200 OK 반환
            return ResponseEntity.ok().build();
        }

        // (2) 세션에 저장된 User가 간호사 등급인지 확인
        if (!loginUser.getGrade().equals(Grade.간호사)) {
            // 간호사가 아니면 아무것도 하지 않고 200 OK 반환
            return ResponseEntity.ok().build();
        }

        // (3) 간호사 ID(이 예시에서는 usersId)만 NotifService에 넘겨서 전체 읽음 처리
        String nurseId = loginUser.getUsersId();
        notifService.markAllAsRead(nurseId);

        return ResponseEntity.ok().build();
    }
}
