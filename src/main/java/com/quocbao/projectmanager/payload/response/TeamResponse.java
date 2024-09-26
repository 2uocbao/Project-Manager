package com.quocbao.projectmanager.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.quocbao.projectmanager.entity.Team;

import lombok.Setter;

@Setter
public class TeamResponse {

	@JsonProperty("id")
	private Long id;

	@JsonProperty("name")
	private String name;

	public TeamResponse(Team team) {
		this.id = team.getId();
		this.name = team.getName();
	}
}
