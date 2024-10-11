package com.quocbao.projectmanager.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.quocbao.projectmanager.common.DataResponse;
import com.quocbao.projectmanager.entity.Notification;
import com.quocbao.projectmanager.payload.request.FriendRequest;
import com.quocbao.projectmanager.websocket.PushNotificationService;

@RestController
@RequestMapping("/users/{userId}")
public class NotificationController {

	private PushNotificationService notificationService;

	public NotificationController(PushNotificationService notificationService) {
		this.notificationService = notificationService;
	}

	@GetMapping("/notifications")
	public ResponseEntity<DataResponse> getNotificationsByUserId(@PathVariable Long userId) {
		List<Notification> notifications = notificationService.getNotificationsByUserId(userId);
		return new ResponseEntity<>(
				new DataResponse(HttpStatus.OK.value(), notifications, "Retrieve notifications by userId successful."),
				HttpStatus.OK);
	}

	@MessageMapping("/friend.request")
	@SendTo("/topic/{userId}/friend-request")
	public ResponseEntity<DataResponse> friendRequest(@DestinationVariable Long userId,
			@Payload FriendRequest friendRequest) {
		notificationService.pushNotification(userId, "/topic/" + userId + "/friend-request",
				"You have friend request from " + friendRequest.getFromUserName() + " " + friendRequest.getFromUser(),
				"Friend Request");
		return new ResponseEntity<>(new DataResponse(HttpStatus.OK.value(), null, "Successful"), HttpStatus.OK);
	}
}
