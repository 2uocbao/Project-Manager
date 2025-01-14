package com.quocbao.projectmanager.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.quocbao.projectmanager.payload.request.LoginRequest;
import com.quocbao.projectmanager.payload.request.UserRequest;
import com.quocbao.projectmanager.payload.response.UserResponse;

public interface UserService extends UserDetailsService {

	public UserResponse getUser(UUID userId);

	public UserResponse updateUser(UUID userId, UserRequest userRequest);

	public UserResponse loginUser(LoginRequest loginRequest);

	public UserResponse registerUser(UserRequest userRequest);

	public UserResponse updatePassword(LoginRequest loginRequest);

	public Page<UserResponse> searchUser(UUID userId, String keySearch, Pageable pageable);

	public Page<UserResponse> findFriend(UUID userId, String keySearch, Pageable pageable);
}
