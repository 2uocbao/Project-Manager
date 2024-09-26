package com.quocbao.projectmanager.payload.request;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class TaskRequest {

	@JsonProperty("name")
	private String name;

	@JsonProperty("description")
	private String description;

	@JsonProperty("date_end")
	private Timestamp dateEnd;
}
