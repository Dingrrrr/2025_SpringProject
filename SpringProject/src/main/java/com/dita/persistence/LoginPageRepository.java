package com.dita.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dita.domain.User;

public interface LoginPageRepository extends JpaRepository<User, String> {
	Optional<User> findByUsersNameAndUsersEmail(String usersName, String UsersEmail);
}
