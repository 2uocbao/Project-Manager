package com.quocbao.projectmanager.payload.request;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class TaskRequest {

	@JsonProperty("user_id")
	private UUID userId;

	@JsonProperty("name")
	private String name;

	@JsonProperty("description")
	private String description;

	@JsonProperty("content_submit")
	private String contentSubmit;

	@JsonProperty("type")
	private String type;

	@JsonProperty("status")
	private String status;

	@JsonProperty("date_end")
	private String dateEnd;
}
