package com.quocbao.projectmanager.elasticsearch.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import com.quocbao.projectmanager.common.ConvertData;
import com.quocbao.projectmanager.entity.Project;

import lombok.Getter;
import lombok.Setter;

@Document(indexName = "projects")
@Getter
@Setter
public class ProjectES {

	@Id
	private String id;

	private String name;

	private String status;

	private String createdAt;

	private String dateEnd;

	private String userId;

	public ProjectES() {

	}

	public ProjectES(Project project) {
		this.id = project.getId().toString();
		this.name = project.getName();
		this.status = project.getStatus();
		this.createdAt = new ConvertData().toLocalDate(project.getCreatedAt());
		this.dateEnd = project.getDateEnd().toString();
		this.userId = project.getUser().getId().toString();
	}

	public ProjectES updateProjectES(Project project) {
		this.name = project.getName();
		this.status = project.getStatus();
		this.createdAt = new ConvertData().toLocalDate(project.getCreatedAt());
		this.dateEnd = project.getDateEnd().toString();
		return this;
	}
}
