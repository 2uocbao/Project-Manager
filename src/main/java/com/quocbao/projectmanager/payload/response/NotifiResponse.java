package com.quocbao.projectmanager.payload.response;

import java.util.Date;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.quocbao.projectmanager.entity.Notification;

import lombok.Setter;

@Setter
public class NotifiResponse {

	@JsonProperty("id")
	private UUID id;

	@JsonProperty("userId")
	private UUID userId;
	
	@JsonProperty("senderId")
	private UUID senderId;

	@JsonProperty("message")
	private String message;

	@JsonProperty("status")
	private Boolean status;

	@JsonProperty("type")
	private String type;

	@JsonProperty("createdAt")
	private Date createdAt;

	public NotifiResponse(Notification notification) {
		this.id = notification.getId();
		this.userId = notification.getRecipientId();
		if(notification.getType().equals("PROJECT")) {
			this.message = notification.getProject().getName();
		} else {
			this.senderId = notification.getUser().getId();
			this.message = notification.getUser().getFirstName() + notification.getUser().getLastName();
		}
		this.status = notification.isRead();
		this.type = notification.getType();
		this.createdAt = notification.getCreatedAt();
	}

}
