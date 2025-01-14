package com.quocbao.projectmanager.controller;

import java.util.List;
import java.util.UUID;

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

	@GetMapping("/{userId}")
	public ResponseEntity<DataResponse> getUser(@PathVariable UUID userId) {
		EntityModel<UserResponse> entityModel = EntityModel.of(userService.getUser(userId));
		entityModel.add(linkHateoas.linkUpdateUser(userId)).add(linkHateoas.linkCreateProject(userId))
				.add(linkHateoas.linkGetProjectsByUserId(userId))
				.add(linkHateoas.linkGetGroupByUserId(userId));
		return new ResponseEntity<>(
				new DataResponse(HttpStatus.OK.value(), entityModel, "User creation request successful."),
				HttpStatus.OK);
	}

	@PutMapping("/{userId}")
	public ResponseEntity<DataResponse> updateUser(@Valid @PathVariable UUID userId,
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
				.add(linkHateoas.linkCreateProject(userResponse.getId()))
				.add(linkHateoas.linkGetProjectsByUserId(userResponse.getId()))
				.add(linkHateoas.linkGetGroupByUserId(userResponse.getId()));
		return new ResponseEntity<>(new DataResponse(HttpStatus.ACCEPTED.value(), entityModel, "Login successful."),
				HttpStatus.ACCEPTED);
	}

	@PostMapping("/register")
	public ResponseEntity<DataResponse> register(@Valid @RequestBody UserRequest userRequest) {
		UserResponse userResponse = userService.registerUser(userRequest);
		EntityModel<UserResponse> entityModel = EntityModel.of(userResponse);
		entityModel.add(linkHateoas.linkGetUser(userResponse.getId()))

				.add(linkHateoas.linkCreateProject(userResponse.getId()))
				.add(linkHateoas.linkGetProjectsByUserId(userResponse.getId()));
		return new ResponseEntity<>(
				new DataResponse(HttpStatus.OK.value(), entityModel, "Account creation request successful."),
				HttpStatus.OK);
	}

	@PatchMapping("/updatePassword")
	public ResponseEntity<DataResponse> updatePassword(@Valid @RequestBody LoginRequest loginRequest) {
		return new ResponseEntity<>(new DataResponse(HttpStatus.OK.value(), userService.updatePassword(loginRequest),
				"User password update request success."), HttpStatus.OK);
	}

	@GetMapping("/{userId}/search")
	public ResponseEntity<PaginationResponse<EntityModel<UserResponse>>> searchUser(@PathVariable UUID userId,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
			@RequestParam String search) {
		Page<UserResponse> userResponse = userService.searchUser(userId, search, PageRequest.of(page, size));
		List<EntityModel<UserResponse>> entityModels = userResponse.getContent().stream().map(EntityModel::of).toList();
		PaginationResponse<EntityModel<UserResponse>> paginationResponse = new PaginationResponse<>(HttpStatus.OK,
				entityModels, userResponse.getPageable().getPageNumber(), userResponse.getSize(),
				userResponse.getTotalElements(), userResponse.getTotalPages(), userResponse.getSort().isSorted(),
				userResponse.getSort().isUnsorted(), userResponse.getSort().isEmpty());
		return new ResponseEntity<>(paginationResponse, HttpStatus.OK);
	}
	
	@GetMapping("/{userId}/find")
	public ResponseEntity<PaginationResponse<EntityModel<UserResponse>>> findFriend(@PathVariable UUID userId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
			@RequestParam(required = false) String search) {
		Page<UserResponse> userResponse = userService.findFriend(userId, search, PageRequest.of(page, size));
		List<EntityModel<UserResponse>> entityModels = userResponse.getContent().stream().map(EntityModel::of).toList();
		PaginationResponse<EntityModel<UserResponse>> paginationResponse = new PaginationResponse<>(HttpStatus.OK,
				entityModels, userResponse.getPageable().getPageNumber(), userResponse.getSize(),
				userResponse.getTotalElements(), userResponse.getTotalPages(), userResponse.getSort().isSorted(),
				userResponse.getSort().isUnsorted(), userResponse.getSort().isEmpty());
		return new ResponseEntity<>(paginationResponse, HttpStatus.OK);
	}
}
