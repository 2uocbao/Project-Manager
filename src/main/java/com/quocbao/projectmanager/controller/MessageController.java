package com.quocbao.projectmanager.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.quocbao.projectmanager.common.DataResponse;
import com.quocbao.projectmanager.common.PaginationResponse;
import com.quocbao.projectmanager.payload.request.ChatMessage;
import com.quocbao.projectmanager.payload.response.MessageResponse;
import com.quocbao.projectmanager.service.MessageService;

@Controller
public class MessageController {

	private MessageService messageService;

	public MessageController(MessageService messageService) {
		this.messageService = messageService;
	}

	@MessageMapping("/chat")
	public ChatMessage sendSpecific(@Payload ChatMessage message) {
		messageService.sendMessageToGroupId(message);
		return message;
	}

	@GetMapping("/message/{groupId}")
	public ResponseEntity<PaginationResponse<EntityModel<MessageResponse>>> getMessagesByGroupId(
			@PathVariable UUID groupId, @RequestParam(defaultValue =  "page") int page, @RequestParam(defaultValue = "size") int size) {
		Direction direction = Direction.DESC;
		Page<MessageResponse> messageResponse = messageService.getMessagesByGroupId(groupId,
				PageRequest.of(page, size, direction, "createdAt"));
		List<EntityModel<MessageResponse>> entityModels = messageResponse.getContent().stream()
				.map(EntityModel::of).toList();
		PaginationResponse<EntityModel<MessageResponse>> paginationResponse = new PaginationResponse<>(HttpStatus.OK,
				entityModels, messageResponse.getPageable().getPageNumber(), messageResponse.getSize(),
				messageResponse.getTotalElements(), messageResponse.getTotalPages(),
				messageResponse.getSort().isSorted(), messageResponse.getSort().isUnsorted(),
				messageResponse.getSort().isEmpty());
		return new ResponseEntity<>(paginationResponse, HttpStatus.OK);
	}

	@DeleteMapping("/message/{messageId}")
	public ResponseEntity<DataResponse> deleteMessage(@PathVariable UUID userId, @PathVariable UUID messageId) {
		return new ResponseEntity<>(
				new DataResponse(HttpStatus.OK.value(), null, messageService.deleteMessageByUserId(messageId, userId)),
				HttpStatus.OK);
	}
}
