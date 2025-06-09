package com.dita.vo;


import java.time.LocalDateTime;

import com.dita.domain.Status;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AppointmentDto {

	private String name;
	private String gender; 
	private String rrn;
	private String phone;
	private LocalDateTime date;
	private LocalDateTime originalDate;
	private String room;
	private String disease;
	private String doctor;
	private Status status;
}
