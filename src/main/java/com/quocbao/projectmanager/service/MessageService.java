package com.quocbao.projectmanager.service;

import java.util.List;

import com.quocbao.projectmanager.entity.Message;

public interface MessageService {

	public void createMessage(Long groupId, Long userId, String message);

	public List<Message> getMessagesByGroupId(Long groupId);

	public String deleteMessageByUserId(Long messageId, Long userId);
}
