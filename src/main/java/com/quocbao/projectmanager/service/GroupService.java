package com.quocbao.projectmanager.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.quocbao.projectmanager.entity.Group;
import com.quocbao.projectmanager.payload.response.GroupResponse;
import com.quocbao.projectmanager.payload.response.UserResponse;

public interface GroupService {

	public Group createGroup(UUID userId, UUID withUser);

	public Page<GroupResponse> getGroupsByUserId(UUID userId, Pageable pageable, String keySearch);

	public Group updateGroup(UUID userId, UUID groupId, String name);

	public String deleteGroup(UUID userId, UUID groupId);

	public String addUserToGroup(UUID userId, UUID memberId, UUID groupId);

	public String removeUserInGroup(UUID userId, UUID memberId, UUID groupId);

	public Page<UserResponse> getUsersInGroup(UUID groupId, Pageable pageable, String keySearch);
	
	public Page<UserResponse> getUsersConnect(UUID userId, Pageable pageable, String keySearch);
}
