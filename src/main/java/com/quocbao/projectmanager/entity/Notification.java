package com.quocbao.projectmanager.entity;

import java.io.Serializable;
import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "notification")
public class Notification implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	private Long id;

	@Column(name = "user_id")
	private Long recipientId;

	@Column(name = "message")
	private String message;

	@Column(name = "status")
	private boolean read;

	@Column(name = "type")
	private String type;

	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_at")
	private Date createdAt;

	public Notification() {

	}

	public Notification(Long recipientId, String message, String type) {
		this.recipientId = recipientId;
		this.message = message;
		this.type = type;
		this.read = false;
	}
}
