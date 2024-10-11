package com.quocbao.projectmanager.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.quocbao.projectmanager.entity.Group;
import com.quocbao.projectmanager.payload.response.UserResponse;

public interface GroupService {

	public Group createGroup(Long userId, String name);

	public Page<Group> getGroupsByUserId(Long userId, Pageable pageable, String keySearch);

	public Group updateGroup(Long userId, Long groupId, String name);

	public String deleteGroup(Long userId, Long groupId);

	public String addUserToGroup(Long userId, Long groupId);

	public String removeUserInGroup(Long userId, Long groupId);

	public Page<UserResponse> getUsersInGroup(Long groupId, Pageable pageable, String keySearch);
}
