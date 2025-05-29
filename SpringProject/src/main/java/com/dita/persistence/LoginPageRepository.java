package com.dita.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dita.domain.User;

public interface LoginPageRepository extends JpaRepository<User, String> {

}
