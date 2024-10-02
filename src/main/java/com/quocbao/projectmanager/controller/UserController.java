package com.quocbao.projectmanager.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
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
import com.quocbao.projectmanager.payload.request.FriendRequest;
import com.quocbao.projectmanager.payload.request.LoginRequest;
import com.quocbao.projectmanager.payload.request.ProjectRequest;
import com.quocbao.projectmanager.payload.request.UserRequest;
import com.quocbao.projectmanager.payload.response.UserResponse;
import com.quocbao.projectmanager.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {

	private UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping()
	public ResponseEntity<DataResponse> getUser(@RequestParam Long userId) {
		EntityModel<UserResponse> entityModel = EntityModel.of(userService.getUser(userId));
		entityModel.add(linkUpdateUser(userId, new UserRequest())).add(linkGetFriendsByUserId(userId))
				.add(linkCreateProject(userId)).add(linkProjectsByUserId(userId, "INPROGRESS"));
		return new ResponseEntity<>(
				new DataResponse(HttpStatus.OK.value(), entityModel, "User creation request successful."),
				HttpStatus.OK);
	}

	@PutMapping("/{userId}")
	public ResponseEntity<DataResponse> updateUser(@Valid @PathVariable Long userId,
			@Valid @RequestBody UserRequest userRequest) {
		EntityModel<UserResponse> entityModel = EntityModel.of(userService.updateUser(userId, userRequest));
		entityModel.add(linkGetUser(userId));
		DataResponse dataResponse = new DataResponse(HttpStatus.OK.value(), entityModel,
				"User information update successful.");
		return new ResponseEntity<>(dataResponse, HttpStatus.OK);
	}

	@GetMapping("/login")
	public ResponseEntity<DataResponse> login(@RequestBody LoginRequest loginRequest) {
		UserResponse userResponse = userService.loginUser(loginRequest);
		EntityModel<UserResponse> entityModel = EntityModel.of(userResponse).add(linkGetUser(userResponse.getId()))
				.add(linkGetFriendsByUserId(userResponse.getId())).add(linkCreateProject(userResponse.getId()))
				.add(linkProjectsByUserId(userResponse.getId(), "INPROGRESS"));
		return new ResponseEntity<>(new DataResponse(HttpStatus.ACCEPTED.value(), entityModel, "Login successful."),
				HttpStatus.ACCEPTED);
	}

	@PostMapping("/register")
	public ResponseEntity<DataResponse> register(@Valid @RequestBody UserRequest userRequest) {
		UserResponse userResponse = userService.registerUser(userRequest);
		EntityModel<UserResponse> entityModel = EntityModel.of(userResponse);
		entityModel.add(linkGetUser(userResponse.getId())).add(linkGetFriendsByUserId(userResponse.getId()))
				.add(linkCreateProject(userResponse.getId()))
				.add(linkProjectsByUserId(userResponse.getId(), "INPROGRESS"));
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
	public ResponseEntity<DataResponse> getFriendsByUserId(@PathVariable Long userId) {
		List<EntityModel<UserResponse>> entityModels = userService.getFriendsByUserId(userId).stream()
				.map(userResponse -> EntityModel.of(userResponse).add(linkGetUser(userResponse.getId()))).toList();
		return new ResponseEntity<>(new DataResponse(HttpStatus.OK.value(), entityModels, "Friends of user."),
				HttpStatus.OK);
	}

	@MessageMapping("/friend.request")
	@SendTo("/topic/{userId}/friend-request")
	public ResponseEntity<DataResponse> friendRequest(@DestinationVariable Long userId,
			@Payload FriendRequest friendRequest) {
		DataResponse dataResponse = new DataResponse(HttpStatus.OK.value(),
				"You have friend request from " + friendRequest.getFromUserName() + " " + friendRequest.getFromUser(),
				"Notification");
		return new ResponseEntity<>(dataResponse, HttpStatus.OK);
	}

	private Link linkGetUser(Long userId) {
		return linkTo(methodOn(UserController.class).getUser(userId)).withRel("get")
				.withTitle("View user requested information").withType("GET");
	}

	private Link linkUpdateUser(Long userId, UserRequest userRequest) {
		return linkTo(methodOn(UserController.class).updateUser(userId, userRequest)).withRel("update")
				.withTitle("Update this user").withType("PUT");
	}

	private Link linkGetFriendsByUserId(Long userId) {
		return linkTo(methodOn(UserController.class).getFriendsByUserId(userId)).withRel("get")
				.withTitle("Get list friends of user by user id").withType("GET");
	}

	private Link linkProjectsByUserId(Long userId, String status) {
		return linkTo(methodOn(ProjectController.class).getProjectsByStatus(userId, status)).withRel("projects")
				.withTitle("Get list projects by user id").withType("GET");
	}

	private Link linkCreateProject(Long userId) {
		return linkTo(methodOn(ProjectController.class).createProject(userId, new ProjectRequest())).withRel("projects")
				.withTitle("Create a new project.").withType("POST");
	}
}
