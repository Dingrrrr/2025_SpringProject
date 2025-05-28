package com.dita.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")// 유저
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
	
	 @Id
	 @Column(name = "users_id", length = 50, nullable = false)
	 private String usersId;

	 @Column(name = "users_pwd", length = 255, nullable = false)
	 private String usersPwd;

	 @Column(name = "users_name", length = 50, nullable = false)
	 private String usersName;

	 @Column(name = "users_email", length = 50, nullable = false)
	 private String usersEmail;

	 @Column(name = "users_phone", length = 50, nullable = false)
	 private String usersPhone;
	 
	 @Column(name = "users_birth", length = 20, nullable = false)
	 private String usersBirth;

	 @Column(name = "users_address", length = 50, nullable = false)
	 private String usersAddress;
	 
	 @Enumerated(EnumType.STRING)
	 @Column(nullable = false)
	 private Grade grade;
	 
	 @OneToOne
	 @JoinColumn(name = "dept_id")
	 private Dept deptId;
}
