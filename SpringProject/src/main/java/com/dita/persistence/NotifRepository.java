package com.dita.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dita.domain.Notif;
import com.dita.domain.User;

public interface NotifRepository extends JpaRepository<Notif, Integer> {
	List<Notif> findByUserOrderByCreatedAtDesc(User user);
	List<Notif> findAllByUserUsersIdOrderByCreatedAtDesc(String usersId);
}
