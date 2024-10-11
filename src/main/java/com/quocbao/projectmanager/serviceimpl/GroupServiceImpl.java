package com.quocbao.projectmanager.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.quocbao.projectmanager.entity.Group;
import com.quocbao.projectmanager.entity.Role;
import com.quocbao.projectmanager.entity.User;
import com.quocbao.projectmanager.exception.ResourceNotFoundException;
import com.quocbao.projectmanager.payload.response.UserResponse;
import com.quocbao.projectmanager.repository.GroupRepository;
import com.quocbao.projectmanager.repository.RoleRepository;
import com.quocbao.projectmanager.repository.UserRepository;
import com.quocbao.projectmanager.service.GroupService;
import com.quocbao.projectmanager.specification.GroupSpecification;

@Service
public class GroupServiceImpl implements GroupService {

	private GroupRepository groupRepository;
	private UserRepository userRepository;
	private RoleRepository roleRepository;

	public GroupServiceImpl(GroupRepository groupRepository, UserRepository userRepository,
			RoleRepository roleRepository) {
		this.groupRepository = groupRepository;
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
	}

	@Override
	public Group createGroup(Long userId, String name) {
		User user = userRepository.findById(userId).get();
		Group group = new Group();
		group.setName(name);
		group.setUser(user);
		Role role = roleRepository.findById(3L).get();
		if (!user.getRoles().contains(role)) {
			user.getRoles().add(role);
			userRepository.save(user);
		}
		return groupRepository.save(group);
	}

	@Override
	public Page<Group> getGroupsByUserId(Long userId, Pageable pageable, String keySearch) {
		Specification<Group> specification;
		if (keySearch != null && !keySearch.isEmpty()) {
			specification = GroupSpecification.getGroupsByUserId(User.builder().id(userId).build())
					.and(GroupSpecification.search(keySearch));
		} else {
			specification = GroupSpecification.getGroupsByUserId(User.builder().id(userId).build());
		}
		return groupRepository.findAll(specification, pageable);
	}

	@Override
	public Group updateGroup(Long userId, Long groupId, String name) {
		return groupRepository.findById(groupId).map(t -> {
			if (t.getUser().getId().equals(userId)) {
				t.setName(name);
			}
			return groupRepository.save(t);
		}).orElseThrow(() -> new ResourceNotFoundException("Request failed."));
	}

	@Override
	public String deleteGroup(Long userId, Long groupId) {
		return groupRepository.findById(groupId).map(t -> {
			if (t.getUser().getId().equals(userId)) {
				groupRepository.delete(t);
			}
			return "Successful";
		}).orElseThrow(() -> new ResourceNotFoundException("Request failed."));
	}

	@Override
	public String addUserToGroup(Long userId, Long groupId) {
		return groupRepository.findById(groupId).map(t -> {
			if (!t.getUsers().contains(User.builder().id(userId).build())) {
				t.getUsers().add(User.builder().id(userId).build());
				groupRepository.save(t);
			}
			return "Successful";
		}).orElseThrow(() -> new ResourceNotFoundException("Request failed."));
	}

	@Override
	public String removeUserInGroup(Long userId, Long groupId) {
		return groupRepository.findById(groupId).map(t -> {
			if (t.getUsers().contains(User.builder().id(userId).build())) {
				t.getUsers().remove(User.builder().id(userId).build());
				groupRepository.save(t);
			}
			return "Successful";
		}).orElseThrow(() -> new ResourceNotFoundException("Request failed."));
	}

	@Override
	public Page<UserResponse> getUsersInGroup(Long groupId, Pageable pageable, String keySearch) {
		return groupRepository.findById(groupId).map(group -> {
			// Get the list of users in the group
			List<User> users;

			if (keySearch != null && !keySearch.isEmpty()) {
				users = group.getUsers().stream().filter(t -> t.getFirstName().contains(keySearch))
						.filter(t -> t.getLastName().contains(keySearch)).toList();
			} else {
				users = group.getUsers();
			}
			// Convert List<User> to List<UserResponse>
			List<UserResponse> userResponses = users.stream().map(UserResponse::new).toList();

			// Manually handle pagination
			int start = (int) pageable.getOffset();
			int end = Math.min((start + pageable.getPageSize()), userResponses.size());

			if (start > end) {
				return new PageImpl<UserResponse>(new ArrayList<>(), pageable, userResponses.size());
			}

			List<UserResponse> paginatedList = userResponses.subList(start, end);
			return new PageImpl<>(paginatedList, pageable, userResponses.size());
		}).orElseThrow(() -> new ResourceNotFoundException("Group not found with id: " + groupId));
	}
}
