package com.quocbao.projectmanager.serviceimpl;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.quocbao.projectmanager.entity.Group;
import com.quocbao.projectmanager.entity.Message;
import com.quocbao.projectmanager.exception.ResourceNotFoundException;
import com.quocbao.projectmanager.repository.MessageRepository;
import com.quocbao.projectmanager.service.MessageService;
import com.quocbao.projectmanager.specification.MessageSpecification;

@Service
public class MessageServiceImpl implements MessageService {

	private MessageRepository messageRepository;

	public MessageServiceImpl(MessageRepository messageRepository) {
		this.messageRepository = messageRepository;
	}

	@Override
	public void createMessage(Long groupId, Long userId, String message) {
		Message newMessage = new Message(groupId, userId, message);
		messageRepository.save(newMessage);
	}

	@Override
	public List<Message> getMessagesByGroupId(Long groupId) {
		Specification<Message> specification = MessageSpecification
				.getMessagesByGroupId(Group.builder().id(groupId).build());
		return messageRepository.findAll(specification);
	}

	@Override
	public String deleteMessageByUserId(Long messageId, Long userId) {
		return messageRepository.findById(userId).map(t -> {
			if (t.getUser().getId().equals(userId)) {
				messageRepository.delete(t);
			}
			return "Successful";
		}).orElseThrow(() -> new ResourceNotFoundException("Resource can not delete."));
	}

}
