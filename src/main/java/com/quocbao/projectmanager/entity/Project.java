package com.quocbao.projectmanager.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.beans.factory.annotation.Value;

import com.quocbao.projectmanager.payload.request.ProjectRequest;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Table(name = "project")
@Setter
public class Project implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "Project name cannot be blank")
	@Column(name = "name")
	private String name;

	@Column(name = "description")
	private String description;

	@Value("working")
	@Column(name = "status")
	private String status;

	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_at")
	private Timestamp createdAt;

	@Column(name = "date_end")
	private Timestamp dateEnd;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	public Project() {

	}

	public Project(ProjectRequest projectRequest) {
		this.name = projectRequest.getName();
		this.description = projectRequest.getDescription();
		this.dateEnd = projectRequest.getDateEnd();
	}

}
