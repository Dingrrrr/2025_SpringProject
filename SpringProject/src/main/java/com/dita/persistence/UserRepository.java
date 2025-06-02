package com.dita.persistence;

import com.dita.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserRepository extends JpaRepository<User, String> {
	
	List<User> findByGrade(String grade);
}
