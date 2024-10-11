package com.quocbao.projectmanager.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.quocbao.projectmanager.common.DataResponse;
import com.quocbao.projectmanager.service.MessageService;

@RestController
@RequestMapping("/users/{userId}/groupId/{groupId}")
public class MessageController {

	private MessageService messageService;

	public MessageController(MessageService messageService) {
		this.messageService = messageService;
	}

	@MessageMapping("/group/{groupId}/sendMessage")
	@SendTo("/topic/group/{groupId}")
	public String createMessage(@PathVariable Long userId, @PathVariable Long groupId, @RequestParam String message) {
		messageService.createMessage(groupId, userId, message);
		return message;
	}

	@GetMapping("/message")
	public ResponseEntity<DataResponse> getMessagesByGroupId(@PathVariable Long groupId) {
		return new ResponseEntity<>(new DataResponse(HttpStatus.OK.value(),
				messageService.getMessagesByGroupId(groupId), "Retrieve messages of group"), HttpStatus.OK);
	}

	@DeleteMapping("/message/{messageId}")
	public ResponseEntity<DataResponse> deleteMessage(@PathVariable Long userId, @PathVariable Long messageId) {
		return new ResponseEntity<>(
				new DataResponse(HttpStatus.OK.value(), null, messageService.deleteMessageByUserId(messageId, userId)),
				HttpStatus.OK);
	}
}
