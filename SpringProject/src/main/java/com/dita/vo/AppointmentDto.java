package com.dita.vo;

import java.time.LocalDateTime;

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
	private String room;
	private String disease;
	private String doctor;
}
