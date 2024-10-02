package com.quocbao.projectmanager.payload.response;

import java.time.LocalDate;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.quocbao.projectmanager.entity.Task;

import lombok.Getter;
import lombok.Setter;

@Getter
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
	private Date createdAt;

	@JsonProperty("date_end")
	private LocalDate dateEnd;

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
