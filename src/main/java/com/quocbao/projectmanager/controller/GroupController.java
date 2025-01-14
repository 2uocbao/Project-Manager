package com.quocbao.projectmanager.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.quocbao.projectmanager.common.DataResponse;
import com.quocbao.projectmanager.common.LinkHateoas;
import com.quocbao.projectmanager.common.PaginationResponse;
import com.quocbao.projectmanager.entity.Group;
import com.quocbao.projectmanager.payload.response.GroupResponse;
import com.quocbao.projectmanager.payload.response.UserResponse;
import com.quocbao.projectmanager.service.GroupService;

@RestController
@RequestMapping("/users/{userId}")
public class GroupController {

	private GroupService groupService;
	private LinkHateoas linkHateoas;

	public GroupController(GroupService groupService, LinkHateoas linkHateoas) {
		this.groupService = groupService;
		this.linkHateoas = linkHateoas;
	}

	@PostMapping("/groups")
	public ResponseEntity<DataResponse> createGroup(@PathVariable UUID userId, @RequestParam UUID withUser) {
		Group group = groupService.createGroup(userId, withUser);
		return new ResponseEntity<>(new DataResponse(HttpStatus.OK.value(), group, "Group creation successful."),
				HttpStatus.OK);
	}

	@GetMapping("/groups")
	public ResponseEntity<PaginationResponse<EntityModel<GroupResponse>>> getGroupsByUserId(@PathVariable UUID userId,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
			@RequestParam(required = false) String search) {
		Page<GroupResponse> pageGroup = groupService.getGroupsByUserId(userId, PageRequest.of(0, 10), null);
		List<EntityModel<GroupResponse>> entityModel = pageGroup.stream()
				.map(t -> EntityModel.of(t).add(linkHateoas.linkGetMessageInGroup(t.getId()))).toList();
		PaginationResponse<EntityModel<GroupResponse>> paginationResponse = new PaginationResponse<>(HttpStatus.OK,
				entityModel, pageGroup.getPageable().getPageNumber(), pageGroup.getSize(), pageGroup.getTotalElements(),
				pageGroup.getTotalPages(), pageGroup.getSort().isSorted(), pageGroup.getSort().isUnsorted(),
				pageGroup.getSort().isEmpty());
		return new ResponseEntity<>(paginationResponse, HttpStatus.OK);
	}

	@DeleteMapping("/groups/{groupId}")
	public ResponseEntity<DataResponse> deleteGroup(@PathVariable UUID userId, @PathVariable UUID groupId) {
		return new ResponseEntity<>(
				new DataResponse(HttpStatus.OK.value(), null, groupService.deleteGroup(userId, groupId)),
				HttpStatus.OK);
	}

	@PutMapping("/groups/{groupId}")
	public ResponseEntity<DataResponse> updateGroup(@PathVariable UUID userId, @PathVariable UUID groupId,
			@RequestParam String name) {
		return new ResponseEntity<>(
				new DataResponse(HttpStatus.OK.value(), groupService.updateGroup(userId, groupId, name), "Successful"),
				HttpStatus.OK);
	}

	@GetMapping("/groups/{groupId}")
	public ResponseEntity<PaginationResponse<EntityModel<UserResponse>>> getUsersInGroup(@PathVariable UUID userId,
			@PathVariable UUID groupId, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, @RequestParam(required = false) String search) {
		Page<UserResponse> userResponse = groupService.getUsersInGroup(groupId, PageRequest.of(page, size), search);
		List<EntityModel<UserResponse>> entityModels = userResponse.getContent().stream()
				.map(t -> EntityModel.of(t).add(linkHateoas.linkGetUser(t.getId()))).toList();
		PaginationResponse<EntityModel<UserResponse>> paginationResponse = new PaginationResponse<>(HttpStatus.OK,
				entityModels, userResponse.getNumber(), userResponse.getTotalPages(), userResponse.getTotalElements(),
				userResponse.getSize(), userResponse.getSort().isSorted(), userResponse.getSort().isUnsorted(),
				userResponse.getSort().isEmpty());
		return new ResponseEntity<>(paginationResponse, HttpStatus.OK);
	}

	@PostMapping("/groups/{groupId}/add")
	public ResponseEntity<DataResponse> addMember(@PathVariable UUID userId, @PathVariable UUID groupId,
			@RequestParam UUID memberId) {
		return new ResponseEntity<>(
				new DataResponse(HttpStatus.OK.value(), null, groupService.addUserToGroup(userId, memberId, groupId)),
				HttpStatus.OK);
	}

	@DeleteMapping("/groups/{groupId}/del")
	public ResponseEntity<DataResponse> delMember(@PathVariable UUID userId, @PathVariable UUID groupId,
			@RequestParam UUID memberId) {
		return new ResponseEntity<>(new DataResponse(HttpStatus.OK.value(), null,
				groupService.removeUserInGroup(userId, memberId, groupId)), HttpStatus.OK);
	}

	@GetMapping("/friends")
	public ResponseEntity<PaginationResponse<EntityModel<UserResponse>>> getFriendsOfUser(@PathVariable UUID userId,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
			@RequestParam(defaultValue = "null") String keySearch) {
		Page<UserResponse> usersPage = groupService.getUsersConnect(userId, PageRequest.of(page, size), keySearch);
		List<EntityModel<UserResponse>> entityModels = usersPage.getContent().stream().map(EntityModel::of)
				.toList();
		PaginationResponse<EntityModel<UserResponse>> paginationResponse = new PaginationResponse<>(HttpStatus.OK,
				entityModels, usersPage.getNumber(), usersPage.getTotalPages(), usersPage.getTotalElements(),
				usersPage.getSize(), usersPage.getSort().isSorted(), usersPage.getSort().isUnsorted(),
				usersPage.getSort().isEmpty());
		return new ResponseEntity<>(paginationResponse, HttpStatus.OK);
	}
}
