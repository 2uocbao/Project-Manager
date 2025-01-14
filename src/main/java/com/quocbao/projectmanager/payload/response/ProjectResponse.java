package com.quocbao.projectmanager.payload.response;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.quocbao.projectmanager.common.ConvertData;
import com.quocbao.projectmanager.entity.Project;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectResponse {

	@JsonProperty("id")
	private UUID id;
	
	@JsonProperty("user_id")
	private UUID userId;

	@JsonProperty("name")
	private String name;

	@JsonProperty("description")
	private String description;

	@JsonProperty("tasks_complete")
	private double tasks;

	@JsonProperty("status")
	private String status;

	@JsonProperty("created_at")
	private String createdAt;

	@JsonProperty("date_end")
	private String dateEnd;

	public ProjectResponse() {

	}

	public ProjectResponse(Project project) {
		this.id = project.getId();
		this.userId = project.getUser().getId();
		this.name = project.getName();
		this.description = project.getDescription();
		this.status = project.getStatus().toString();
		this.createdAt = ConvertData.timeStampToString(project.getCreatedAt());
		this.dateEnd = ConvertData.timeStampToString(project.getDateEnd());
		this.tasks = project.getPerTaskComplete();
	}
}
