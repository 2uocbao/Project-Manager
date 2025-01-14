package com.quocbao.projectmanager.payload.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.quocbao.projectmanager.entity.Message;

import lombok.Setter;

@Setter
public class MessageResponse {

	@JsonProperty("id")
	private UUID id;

	@JsonProperty("userId")
	private UUID userId;

	@JsonProperty("message")
	private String message;

	@JsonProperty("createdAt")
	private LocalDateTime createdAt;

	public MessageResponse(Message message) {
		this.id = message.getId();
		this.userId = message.getUser().getId();
		this.message = message.getMessageSent();
		this.createdAt = message.getCreatedAt();
	}

}
