package com.quocbao.projectmanager.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import com.quocbao.projectmanager.payload.request.NotifiRequest;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Entity
@Table(name = "notification")
@Getter
@Builder
@AllArgsConstructor
public class Notification implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(name = "user_id")
	private UUID recipientId;

	@Column(name = "status")
	private boolean read;

	@Column(name = "type")
	private String type;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "project_id", nullable = true)
	private Project project;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "sender_id", nullable = true)
	private User user;

	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_at")
	private Date createdAt;

	public Notification() {

	}
	
	public Notification(NotifiRequest notifiRequest) {
		this.recipientId = notifiRequest.getReceiver();
		this.type = notifiRequest.getType();
		if (notifiRequest.getType().equals("PROJECT")) {
			this.project = Project.builder().id(notifiRequest.getSender()).build();
		} else {
			this.user = User.builder().id(notifiRequest.getSender()).build();
		}
		this.read = false;
	}
}
