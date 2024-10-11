package com.quocbao.projectmanager.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.quocbao.projectmanager.common.DataResponse;
import com.quocbao.projectmanager.common.LinkHateoas;
import com.quocbao.projectmanager.common.PaginationResponse;
import com.quocbao.projectmanager.payload.request.LoginRequest;
import com.quocbao.projectmanager.payload.request.UserRequest;
import com.quocbao.projectmanager.payload.response.UserResponse;
import com.quocbao.projectmanager.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {

	private UserService userService;
	private LinkHateoas linkHateoas;

	public UserController(UserService userService, LinkHateoas linkHateoas) {
		this.userService = userService;
		this.linkHateoas = linkHateoas;
	}

	@GetMapping()
	public ResponseEntity<DataResponse> getUser(@RequestParam Long userId) {
		EntityModel<UserResponse> entityModel = EntityModel.of(userService.getUser(userId));
		entityModel.add(linkHateoas.linkUpdateUser(userId)).add(linkHateoas.linkGetFriendsByUserId(userId))
				.add(linkHateoas.linkCreateProject(userId)).add(linkHateoas.linkGetProjectsByUserId(userId, "PLANNING"))
				.add(linkHateoas.linkGetGroupByUserId(userId));
		return new ResponseEntity<>(
				new DataResponse(HttpStatus.OK.value(), entityModel, "User creation request successful."),
				HttpStatus.OK);
	}

	@PutMapping("/{userId}")
	public ResponseEntity<DataResponse> updateUser(@Valid @PathVariable Long userId,
			@Valid @RequestBody UserRequest userRequest) {
		EntityModel<UserResponse> entityModel = EntityModel.of(userService.updateUser(userId, userRequest));
		entityModel.add(linkHateoas.linkGetUser(userId));
		DataResponse dataResponse = new DataResponse(HttpStatus.OK.value(), entityModel,
				"User information update successful.");
		return new ResponseEntity<>(dataResponse, HttpStatus.OK);
	}

	@GetMapping("/login")
	public ResponseEntity<DataResponse> login(@RequestBody LoginRequest loginRequest) {
		UserResponse userResponse = userService.loginUser(loginRequest);
		EntityModel<UserResponse> entityModel = EntityModel.of(userResponse)
				.add(linkHateoas.linkGetUser(userResponse.getId()))
				.add(linkHateoas.linkGetFriendsByUserId(userResponse.getId()))
				.add(linkHateoas.linkCreateProject(userResponse.getId()))
				.add(linkHateoas.linkGetProjectsByUserId(userResponse.getId(), "PLANNING"))
				.add(linkHateoas.linkGetGroupByUserId(userResponse.getId()));
		return new ResponseEntity<>(new DataResponse(HttpStatus.ACCEPTED.value(), entityModel, "Login successful."),
				HttpStatus.ACCEPTED);
	}

	@PostMapping("/register")
	public ResponseEntity<DataResponse> register(@Valid @RequestBody UserRequest userRequest) {
		UserResponse userResponse = userService.registerUser(userRequest);
		EntityModel<UserResponse> entityModel = EntityModel.of(userResponse);
		entityModel.add(linkHateoas.linkGetUser(userResponse.getId()))
				.add(linkHateoas.linkGetFriendsByUserId(userResponse.getId()))
				.add(linkHateoas.linkCreateProject(userResponse.getId()))
				.add(linkHateoas.linkGetProjectsByUserId(userResponse.getId(), "PLANNING"));
		return new ResponseEntity<>(
				new DataResponse(HttpStatus.OK.value(), entityModel, "Account creation request successful."),
				HttpStatus.OK);
	}

	@PatchMapping("/updatePassword")
	public ResponseEntity<DataResponse> updatePassword(@Valid @RequestBody LoginRequest loginRequest) {
		return new ResponseEntity<>(new DataResponse(HttpStatus.OK.value(), userService.updatePassword(loginRequest),
				"User password update request success."), HttpStatus.OK);
	}

	@PostMapping("/confirm-friend-request")
	public ResponseEntity<DataResponse> confirmFriendRequest(@RequestParam Long fromUserId,
			@RequestParam Long toUserId) {
		return new ResponseEntity<>(new DataResponse(HttpStatus.OK.value(),
				userService.confirmFriendRequest(fromUserId, toUserId), "Friend request successfull."), HttpStatus.OK);
	}

	@GetMapping("/{userId}/friends")
	public ResponseEntity<PaginationResponse<EntityModel<UserResponse>>> getFriendsByUserId(@PathVariable Long userId,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
		Page<UserResponse> userResponsePage = userService.getFriendsByUserId(userId, PageRequest.of(page, size));
		List<EntityModel<UserResponse>> entityModels = userResponsePage.getContent().stream()
				.map(userResponse -> EntityModel.of(userResponse).add(linkHateoas.linkGetUser(userResponse.getId())))
				.toList();
		PaginationResponse<EntityModel<UserResponse>> paginationResponse = new PaginationResponse<>(HttpStatus.OK,
				entityModels, userResponsePage.getPageable().getPageNumber(), userResponsePage.getSize(),
				userResponsePage.getTotalElements(), userResponsePage.getTotalPages(),
				userResponsePage.getSort().isSorted(), userResponsePage.getSort().isUnsorted(),
				userResponsePage.getSort().isEmpty());
		return new ResponseEntity<>(paginationResponse, HttpStatus.OK);
	}
}
