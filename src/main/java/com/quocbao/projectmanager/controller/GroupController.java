package com.quocbao.projectmanager.controller;

import java.util.List;

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
	public ResponseEntity<DataResponse> createGroup(@PathVariable Long userId, @RequestParam String name) {
		Group group = groupService.createGroup(userId, name);
		return new ResponseEntity<>(new DataResponse(HttpStatus.OK.value(), group, "Group creation successful."),
				HttpStatus.OK);
	}

	@GetMapping("/groups")
	public ResponseEntity<PaginationResponse<EntityModel<Group>>> getGroupsByUserId(@PathVariable Long userId,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
			@RequestParam(required = false) String search) {
		Page<Group> pageGroup = groupService.getGroupsByUserId(userId, PageRequest.of(page, size), search);
		List<EntityModel<Group>> entityModel = pageGroup.getContent().stream().map(t -> EntityModel.of(t)
				.add(linkHateoas.linkGetMembersInGroup(t.getId())).add(linkHateoas.linkGetMembersInGroup(t.getId())))
				.toList();
		PaginationResponse<EntityModel<Group>> paginationResponse = new PaginationResponse<>(HttpStatus.OK, entityModel,
				pageGroup.getPageable().getPageNumber(), pageGroup.getSize(), pageGroup.getTotalElements(),
				pageGroup.getTotalPages(), pageGroup.getSort().isSorted(), pageGroup.getSort().isUnsorted(),
				pageGroup.getSort().isEmpty());
		return new ResponseEntity<>(paginationResponse, HttpStatus.OK);
	}

	@DeleteMapping("/groups/{groupId}")
	public ResponseEntity<DataResponse> deleteGroup(@PathVariable Long userId, @PathVariable Long groupId) {
		return new ResponseEntity<>(
				new DataResponse(HttpStatus.OK.value(), null, groupService.deleteGroup(userId, groupId)),
				HttpStatus.OK);
	}

	@PutMapping("/groups/{groupId}")
	public ResponseEntity<DataResponse> updateGroup(@PathVariable Long userId, @PathVariable Long groupId,
			@RequestParam String name) {
		return new ResponseEntity<>(
				new DataResponse(HttpStatus.OK.value(), groupService.updateGroup(userId, groupId, name), "Successful"),
				HttpStatus.OK);
	}

	@GetMapping("/groups/{groupId}")
	public ResponseEntity<PaginationResponse<EntityModel<UserResponse>>> getUsersInGroup(@PathVariable Long groupId,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
			@RequestParam(required = false) String search) {
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
	public ResponseEntity<DataResponse> addMember(@PathVariable Long userId, @PathVariable Long groupId,
			@RequestParam Long memberId) {
		return new ResponseEntity<>(
				new DataResponse(HttpStatus.OK.value(), null, groupService.addUserToGroup(memberId, groupId)),
				HttpStatus.OK);
	}

	@DeleteMapping("/groups/{groupId}/del")
	public ResponseEntity<DataResponse> delMember(@PathVariable Long userId, @PathVariable Long groupId,
			@RequestParam Long memberId) {
		return new ResponseEntity<>(
				new DataResponse(HttpStatus.OK.value(), null, groupService.removeUserInGroup(memberId, groupId)),
				HttpStatus.OK);
	}
}
