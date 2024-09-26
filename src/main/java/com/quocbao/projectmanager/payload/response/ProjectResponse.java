package com.quocbao.projectmanager.payload.response;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.quocbao.projectmanager.entity.Project;

import lombok.Setter;

@Setter
public class ProjectResponse {

	@JsonProperty("id")
	private Long id;

	@JsonProperty("name")
	private String name;

	@JsonProperty("description")
	private String description;

	@JsonProperty("status")
	private String status;

	@JsonProperty("created_at")
	private Timestamp createdAt;

	@JsonProperty("date_end")
	private Timestamp dateEnd;

	public ProjectResponse() {

	}

	public ProjectResponse(Project project) {
		this.id = project.getId();
		this.name = project.getName();
		this.description = project.getDescription();
		this.status = project.getStatus();
		this.createdAt = project.getCreatedAt();
		this.dateEnd = project.getDateEnd();
	}
}
