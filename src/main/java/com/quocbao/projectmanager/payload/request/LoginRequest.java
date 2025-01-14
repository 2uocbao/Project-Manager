package com.quocbao.projectmanager.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class LoginRequest {

	@JsonProperty("username")
	private String username;

	@JsonProperty("password")
	private String password;
	
	@JsonProperty("newPassword")
	private String newPassword;
	
	public LoginRequest(String username, String password) {
		this.username = username;
		this.password = password;
	}

}
