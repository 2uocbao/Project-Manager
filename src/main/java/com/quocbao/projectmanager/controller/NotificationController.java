package com.quocbao.projectmanager.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.quocbao.projectmanager.common.DataResponse;
import com.quocbao.projectmanager.common.PaginationResponse;
import com.quocbao.projectmanager.payload.request.NotifiRequest;
import com.quocbao.projectmanager.payload.response.NotifiResponse;
import com.quocbao.projectmanager.websocket.PushNotificationService;

@RestController
@RequestMapping("/users/{userId}")
public class NotificationController {

	private PushNotificationService notificationService;

	public NotificationController(PushNotificationService notificationService) {
		this.notificationService = notificationService;
	}

	@GetMapping("/notifications")
	public ResponseEntity<PaginationResponse<EntityModel<NotifiResponse>>> getNotificationsByUserId(
			@PathVariable UUID userId, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		Page<NotifiResponse> notifications = notificationService.getNotificationsByUserId(userId,
				PageRequest.of(page, size));
		List<EntityModel<NotifiResponse>> entityModels = notifications.getContent().stream().map(EntityModel::of)
				.toList();
		PaginationResponse<EntityModel<NotifiResponse>> paginationResponse = new PaginationResponse<>(HttpStatus.OK,
				entityModels, notifications.getPageable().getPageNumber(), notifications.getSize(),
				notifications.getTotalElements(), notifications.getTotalPages(), notifications.getSort().isSorted(),
				notifications.getSort().isUnsorted(), notifications.getSort().isEmpty());

		return new ResponseEntity<>(paginationResponse, HttpStatus.OK);
	}

	@MessageMapping("/notifi")
	public ResponseEntity<DataResponse> notifiRequest(@Payload NotifiRequest notifiRequest) {
		notificationService.pushNotification(notifiRequest);
		return new ResponseEntity<>(new DataResponse(HttpStatus.OK.value(), null, "Successful"), HttpStatus.OK);
	}
}
