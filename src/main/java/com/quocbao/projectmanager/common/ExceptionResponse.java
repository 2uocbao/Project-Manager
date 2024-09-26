package com.quocbao.projectmanager.common;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ExceptionResponse {

	@JsonProperty("status")
	private int status;
	@JsonProperty("message")
	private String message;
	@JsonProperty("timestamp")
	private LocalDateTime timestamp;
	@JsonProperty("details")
	private String details;

	public ExceptionResponse(int status, String message, LocalDateTime timestamp, String details) {
		this.status = status;
		this.message = message;
		this.timestamp = timestamp;
		this.details = details;
	}
}
