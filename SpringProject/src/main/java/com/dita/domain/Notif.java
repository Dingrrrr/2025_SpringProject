package com.dita.domain;


import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "notif")// 알림
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Notif {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "notif_id", nullable = false)
	private int notifId; //알림 아이디
	
	@ManyToOne
	@JoinColumn(name ="user_id", nullable= false)
	private User user;// 유저 아이디
	
	@ManyToOne
	@JoinColumn(name ="appt_id", nullable= false)
	private Appt appt;// 예약 아이디
	
	@Column(length = 50, nullable = false)
	private String type;// 알림 종류
	
	@Column(nullable = false)
	private String message;// 알림 내용
	
	@Column(name="is_read", nullable = false)
	private boolean isRead;// 알림 확인 여부
	
	@CreationTimestamp
	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;//생성 일시
}
