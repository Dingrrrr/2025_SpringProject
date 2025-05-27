package com.dita.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
	 @Column(name = "user_id", length = 50, nullable = false)
	 private String userId;

	 @Column(name = "user_pwd", length = 255, nullable = false)
	 private String userPwd;

	 @Column(name = "user_name", length = 50, nullable = false)
	 private String userName;

	 @Column(name = "user_email", length = 50, nullable = false)
	 private String userEmail;

	 @Column(name = "user_phone", length = 50, nullable = false)
	 private String userPhone;

	 @Enumerated(EnumType.STRING)
	 @Column(nullable = false)
	 private Grade grade;
}
