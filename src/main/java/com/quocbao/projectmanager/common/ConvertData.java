package com.quocbao.projectmanager.common;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class ConvertData {

	public ConvertData() {

	}

	public LocalDate toDate(String dateString) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // Define the date format
		return LocalDate.parse(dateString, formatter);
	}

	public String toLocalDate(Date date) {
		LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()) // or specify a specific zone
				.toLocalDate();
		return localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	}
}
