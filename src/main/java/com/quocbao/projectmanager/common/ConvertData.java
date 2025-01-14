package com.quocbao.projectmanager.common;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public abstract class ConvertData {

	ConvertData() {
	}

	public static String timeStampToString(Timestamp timestamp) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		return timestamp.toLocalDateTime().format(formatter);
	}

	public static Timestamp toTimestamp(String dateString) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm"); // Define the date format
		try {
			Date parseDate = formatter.parse(dateString);
			return new Timestamp(parseDate.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String toLocalDate(Date date) {
		LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()) // or specify a specific zone
				.toLocalDate();
		return localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-d"));
	}

	public static String formatHexToUUID(String hex) {
		return hex.replaceFirst("([0-9a-fA-F]{8})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{12})",
				"$1-$2-$3-$4-$5");
	}
}
