package com.quocbao.projectmanager.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class TeamRequest {

	@JsonProperty("name")
	private String name;
}
