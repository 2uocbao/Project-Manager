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
import com.quocbao.projectmanager.payload.request.UserRequest;
import com.quocbao.projectmanager.payload.response.UserResponse;
import com.quocbao.projectmanager.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {

	private UserService userService;

	static final String REQUESTSUCCESS = "Request Successfully";

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping()
	public ResponseEntity<DataResponse> getUser(@RequestParam Long userId) {
		EntityModel<UserResponse> entityModel = EntityModel.of(userService.getUser(userId));
		entityModel.add(linkUpdateUser(userId, new UserRequest())).add(linkGetFriendsByUserId(userId));
		return new ResponseEntity<>(new DataResponse(HttpStatus.OK.value(), entityModel, REQUESTSUCCESS),
				HttpStatus.OK);
	}

	@PutMapping("/{userId}")
	public ResponseEntity<DataResponse> updateUser(@Valid @PathVariable Long userId,
			@Valid @RequestBody UserRequest userRequest) {
		EntityModel<UserResponse> entityModel = EntityModel.of(userService.updateUser(userId, userRequest))
				.add(linkGetUser(userId)).add(linkGetFriendsByUserId(userId));
		DataResponse dataResponse = new DataResponse(HttpStatus.OK.value(), entityModel, REQUESTSUCCESS);
		return new ResponseEntity<>(dataResponse, HttpStatus.OK);
	}

	@GetMapping("/login")
	public ResponseEntity<DataResponse> login(@RequestBody LoginRequest loginRequest) {
		UserResponse userResponse = userService.loginUser(loginRequest);
		EntityModel<UserResponse> entityModel = EntityModel.of(userResponse).add(linkGetUser(userResponse.getId()))
				.add(linkUpdateUser(userResponse.getId(), new UserRequest()))
				.add(linkGetFriendsByUserId(userResponse.getId()));
		return new ResponseEntity<>(new DataResponse(HttpStatus.ACCEPTED.value(), entityModel, REQUESTSUCCESS),
				HttpStatus.ACCEPTED);
	}

	@PostMapping("/register")
	public ResponseEntity<DataResponse> register(@Valid @RequestBody UserRequest userRequest) {
		UserResponse userResponse = userService.registerUser(userRequest);
		EntityModel<UserResponse> entityModel = EntityModel.of(userResponse)
				.add(linkUpdateUser(userResponse.getId(), new UserRequest()));
		return new ResponseEntity<>(new DataResponse(HttpStatus.OK.value(), entityModel, REQUESTSUCCESS),
				HttpStatus.OK);
	}

	@PutMapping("/updatePassword")
	public ResponseEntity<DataResponse> updatePassword(@Valid @RequestBody LoginRequest loginRequest) {
		return new ResponseEntity<>(
				new DataResponse(HttpStatus.OK.value(), userService.updatePassword(loginRequest), REQUESTSUCCESS),
				HttpStatus.OK);
	}

	@PostMapping("/confirm-friend-request")
	public ResponseEntity<DataResponse> confirmFriendRequest(@RequestParam Long fromUserId,
			@RequestParam Long toUserId) {
		return new ResponseEntity<>(new DataResponse(HttpStatus.OK.value(),
				userService.confirmFriendRequest(fromUserId, toUserId), REQUESTSUCCESS), HttpStatus.OK);
	}

	@GetMapping("/{userId}/friends")
	public ResponseEntity<DataResponse> getFriendsByUserId(@PathVariable Long userId) {
		List<EntityModel<UserResponse>> entityModels = userService.getFriendsByUserId(userId).stream()
				.map(userResponse -> EntityModel.of(userResponse).add(linkGetUser(userResponse.getId()))).toList();
		return new ResponseEntity<>(new DataResponse(HttpStatus.OK.value(), entityModels, REQUESTSUCCESS),
				HttpStatus.OK);
	}

	@MessageMapping("/friend.request")
	@SendTo("/topic/{userId}/friend-request")
	public ResponseEntity<DataResponse> friendRequest(@DestinationVariable Long userId,
			@Payload FriendRequest friendRequest) {
		DataResponse dataResponse = new DataResponse(HttpStatus.OK.value(),
				"You have friend request from " + friendRequest.getFromUserName() + " " + friendRequest.getFromUser(),
				REQUESTSUCCESS);
		return new ResponseEntity<>(dataResponse, HttpStatus.OK);
	}

	private Link linkGetUser(Long userId) {
		return linkTo(methodOn(UserController.class).getUser(userId)).withRel("get")
				.withTitle("View user requested information").withType("GET");
	}

	private Link linkUpdateUser(Long userId, UserRequest userRequest) {
		return linkTo(methodOn(UserController.class).updateUser(userId, userRequest)).withRel("update")
				.withTitle("Update this user").withType("UPDATE");
	}

	private Link linkGetFriendsByUserId(Long userId) {
		return linkTo(methodOn(UserController.class).getFriendsByUserId(userId)).withRel("get")
				.withTitle("Get list friends of user by user id").withType("GET");
	}
}
