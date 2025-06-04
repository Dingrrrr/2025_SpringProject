// ── com.dita.controller.NotifController.java ──
package com.dita.controller;

import com.dita.domain.Notif;
import com.dita.domain.User;
import com.dita.domain.Grade;
import com.dita.persistence.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/notif")
@RequiredArgsConstructor
public class NotifController {

    private final NotifRepository notifRepository;
    private final UserRepository userRepository;

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
    public void markAsRead(@PathVariable Integer notifId) {
        notifRepository.findById(notifId).ifPresent(notif -> {
            if (!notif.isRead()) {
                notif.setRead(true);
                notifRepository.save(notif);
            }
        });
    }
}
