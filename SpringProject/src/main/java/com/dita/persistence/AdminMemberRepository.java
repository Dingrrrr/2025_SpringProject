package com.dita.persistence;

import java.time.DayOfWeek;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dita.domain.Grade;
import com.dita.domain.Sched;
import com.dita.domain.User;

public interface AdminMemberRepository extends JpaRepository<Sched, Integer> {

    // ✅ 특정 직원의 모든 스케줄 조회
    List<Sched> findByUser(User user);

    // ✅ 특정 요일에 근무하는 스케줄 조회
    List<Sched> findByWorkDaysContaining(DayOfWeek dayOfWeek);

    // ✅ 특정 직원의 스케줄 중 특정 요일 포함
    List<Sched> findByUserAndWorkDaysContaining(User user, DayOfWeek dayOfWeek);

    // ✅ enum Grade 타입에 맞게 수정
    List<Sched> findByUserGrade(Grade grade);

    List<Sched> findByUserGradeAndWorkDaysContaining(Grade grade, DayOfWeek dayOfWeek);
}
