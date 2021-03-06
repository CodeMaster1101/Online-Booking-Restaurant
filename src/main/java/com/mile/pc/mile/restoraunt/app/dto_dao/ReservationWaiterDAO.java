package com.mile.pc.mile.restoraunt.app.dto_dao;

import java.time.LocalTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Component
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationWaiterDAO {
	
	private long tableid;
	private String firstName;
	private String username;
	private String period;
	@DateTimeFormat(iso = ISO.TIME)
	private LocalTime time;
	private boolean arrived;
	private String note;
}
