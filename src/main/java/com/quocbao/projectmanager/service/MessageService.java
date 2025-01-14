package com.quocbao.projectmanager.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.quocbao.projectmanager.payload.request.ChatMessage;
import com.quocbao.projectmanager.payload.response.MessageResponse;

public interface MessageService {

	public void sendMessageToGroupId(ChatMessage chatMessage);

	public void createMessage(UUID groupId, UUID userId, String message);

	public Page<MessageResponse> getMessagesByGroupId(UUID groupId, Pageable pageable);

	public String deleteMessageByUserId(UUID messageId, UUID userId);
}
