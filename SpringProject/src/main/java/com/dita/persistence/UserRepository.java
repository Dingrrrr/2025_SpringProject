package com.dita.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dita.domain.User;

public interface UserRepository extends JpaRepository<User, String> {
    List<User> findByGrade(String grade);
}
