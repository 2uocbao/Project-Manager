package com.quocbao.projectmanager.payload.response;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.quocbao.projectmanager.common.ConvertData;
import com.quocbao.projectmanager.entity.Task;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskResponse {

	@JsonProperty("id")
	private UUID id;
	
	@JsonProperty("user_id")
	private UUID userId;
	
	@JsonProperty("username")
	private String username;

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
	private String createdAt;

	@JsonProperty("date_end")
	private String dateEnd;

	public TaskResponse() {

	}

	public TaskResponse(Task task) {
		this.id = task.getId();
		this.userId = task.getUser().getId();
		this.name = task.getName();
		this.description = task.getDescription();
		this.type = task.getType();
		this.contentSubmit = task.getContentSubmit();
		this.status = task.getStatus().toString();
		this.createdAt = ConvertData.timeStampToString(task.getCreatedAt());
		this.dateEnd = ConvertData.timeStampToString(task.getDateEnd());
	}
}
