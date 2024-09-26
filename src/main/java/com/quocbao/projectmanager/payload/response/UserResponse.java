package com.quocbao.projectmanager.payload.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.quocbao.projectmanager.entity.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponse {

	@JsonProperty("id")
	private Long id;

	@JsonProperty("first_name")
	private String firstName;

	@JsonProperty("last_name")
	private String lastName;

	@JsonProperty("phone_number")
	private String phoneNumber;

	@JsonProperty("email")
	private String email;

	@JsonProperty("token")
	private String token;

	public UserResponse() {

	}

	public UserResponse(User user) {
		this.id = user.getId();
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName() == null ? null : user.getLastName();
		this.phoneNumber = user.getPhoneNumber() == null ? null : user.getPhoneNumber();
		this.email = user.getEmail() == null ? null : user.getEmail();
	}
}
