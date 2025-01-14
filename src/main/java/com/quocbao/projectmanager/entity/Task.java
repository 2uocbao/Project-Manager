package com.quocbao.projectmanager.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import com.quocbao.projectmanager.common.ConvertData;
import com.quocbao.projectmanager.common.StatusEnum;
import com.quocbao.projectmanager.payload.request.TaskRequest;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "task")
@Setter
@AllArgsConstructor
public class Task implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@NotBlank(message = "Name can not be blank")
	@Column(name = "name")
	private String name;

	@NotBlank(message = "Description can not be blank")
	@Column(name = "description")
	private String description;

	@Column(name = "type")
	private String type;

	@Column(name = "content_submit")
	private String contentSubmit;

	@Column(name = "status")
	@Enumerated(EnumType.STRING)
	private StatusEnum status;

	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_at")
	private Timestamp createdAt;

	@Column(name = "date_end")
	private Timestamp dateEnd;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
	@JoinColumn(name = "project_id", nullable = false)
	private Project project;

	public Task() {

	}

	public Task(TaskRequest taskRequest) {
		this.name = taskRequest.getName();
		this.description = taskRequest.getDescription();
		this.dateEnd = ConvertData.toTimestamp(taskRequest.getDateEnd());
		this.status = StatusEnum.PENDING;
		this.type = taskRequest.getType() == null ? "NONSTATUS" : taskRequest.getType();
		this.contentSubmit = "null";
	}

	public Task updateTask(TaskRequest taskRequest) {
		this.name = taskRequest.getName();
		this.description = taskRequest.getDescription();
		this.dateEnd = ConvertData.toTimestamp(taskRequest.getDateEnd());
		this.type = taskRequest.getType();
		this.contentSubmit = taskRequest.getContentSubmit();
		return this;
	}
}
