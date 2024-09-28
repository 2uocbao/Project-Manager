package com.quocbao.projectmanager.service;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.quocbao.projectmanager.payload.request.LoginRequest;
import com.quocbao.projectmanager.payload.request.UserRequest;
import com.quocbao.projectmanager.payload.response.UserResponse;

public interface UserService extends UserDetailsService {

	public UserResponse getUser(Long userId);

	public UserResponse updateUser(Long userId, UserRequest userRequest);

	public UserResponse loginUser(LoginRequest loginRequest);

	public UserResponse registerUser(UserRequest userRequest);

	public String confirmFriendRequest(Long fromUser, Long toUser);

	public List<UserResponse> getFriendsByUserId(Long userId);
	
	public String updatePassword(LoginRequest loginRequest);
}
