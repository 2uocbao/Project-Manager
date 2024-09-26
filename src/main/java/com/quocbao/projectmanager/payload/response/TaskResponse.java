package com.quocbao.projectmanager.payload.response;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.quocbao.projectmanager.entity.Task;

import lombok.Setter;

@Setter
public class TaskResponse {

	@JsonProperty("id")
	private Long id;

	@JsonProperty("name")
	private String name;

	@JsonProperty("description")
	private String description;

	@JsonProperty("type")
	private String type;

	@JsonProperty("content_submit")
	private String contentSubmit;

	@JsonProperty("status")
	private String status;

	@JsonProperty("created_at")
	private Timestamp createdAt;

	@JsonProperty("date_end")
	private Timestamp dateEnd;

	public TaskResponse() {

	}

	public TaskResponse(Task task) {
		this.id = task.getId();
		this.name = task.getName();
		this.description = task.getDescription();
		this.type = task.getType();
		this.contentSubmit = task.getContentSubmit();
		this.status = task.getStatus();
		this.createdAt = task.getCreatedAt();
		this.dateEnd = task.getDateEnd();
	}
}
