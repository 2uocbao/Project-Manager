package com.quocbao.projectmanager.common;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ConvertData {

	public ConvertData() {

	}

	public LocalDate toDate(String dateString) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // Define the date format
		return LocalDate.parse(dateString, formatter);
	}

}
