package com.quocbao.projectmanager.payload.response;

import java.sql.Timestamp;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.quocbao.projectmanager.common.ConvertData;
import com.quocbao.projectmanager.entity.Group;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GroupResponse {

	@JsonProperty("id")
	private UUID id;

	@JsonProperty("name")
	private String name;

	@JsonProperty("lastMessage")
	private String lastMessage;

	@JsonProperty("time")
	private Timestamp time;

	public GroupResponse(String id, String name, String message, Timestamp timeMessage) {
		this.id = UUID.fromString(ConvertData.formatHexToUUID(id));
		this.name = name;
		this.lastMessage = message;
		this.time = timeMessage;
	}

	public GroupResponse(Group group) {
		id = group.getId();
		name = group.getName();
		time = group.getCreatedAt();
	}

}
