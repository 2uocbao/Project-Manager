package com.quocbao.projectmanager.payload.response;

import java.util.Date;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.quocbao.projectmanager.entity.Project;

import lombok.Getter;
import lombok.Setter;

@Getter
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
	private Date createdAt;

	@JsonProperty("date_end")
	private LocalDate dateEnd;

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
