package com.quocbao.projectmanager.payload.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class UserRequest {

	@JsonProperty("first_name")
	private String firstName;

	@JsonProperty("last_name")
	private String lastName;

	@JsonProperty("phone_number")
	private String phoneNumber;

	@JsonProperty("email")
	private String email;
}
