package com.quocbao.projectmanager.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.time.LocalDate;

import org.hibernate.annotations.CreationTimestamp;

import com.quocbao.projectmanager.common.ConvertData;
import com.quocbao.projectmanager.payload.request.ProjectRequest;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Entity
@Getter
@Table(name = "project")
@Setter
@AllArgsConstructor
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

	@Column(name = "status")
	private String status;

	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_at")
	private Date createdAt;

	@Column(name = "date_end")
	private LocalDate dateEnd;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Task> tasks;

	public Project() {

	}

	public Project(ProjectRequest projectRequest) {
		this.name = projectRequest.getName();
		this.description = projectRequest.getDescription();
		this.dateEnd = new ConvertData().toDate(projectRequest.getDateEnd());
		this.status = "PLANNING";
	}

	public Project updateProject(ProjectRequest projectRequest) {
		this.name = projectRequest.getName();
		this.description = projectRequest.getDescription();
		this.dateEnd = new ConvertData().toDate(projectRequest.getDateEnd());
		return this;
	}

}
