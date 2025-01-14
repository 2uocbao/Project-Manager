package com.quocbao.projectmanager.serviceimpl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.quocbao.projectmanager.entity.Group;
import com.quocbao.projectmanager.entity.Member;
import com.quocbao.projectmanager.entity.Message;
import com.quocbao.projectmanager.entity.User;
import com.quocbao.projectmanager.exception.ResourceNotFoundException;
import com.quocbao.projectmanager.payload.request.ChatMessage;
import com.quocbao.projectmanager.payload.response.MessageResponse;
import com.quocbao.projectmanager.repository.MemberRepository;
import com.quocbao.projectmanager.repository.MessageRepository;
import com.quocbao.projectmanager.service.MessageService;
import com.quocbao.projectmanager.specification.MemberSpecification;
import com.quocbao.projectmanager.specification.MessageSpecification;
import com.quocbao.projectmanager.websocket.UserStatusService;

import jakarta.persistence.Tuple;

@Service
public class MessageServiceImpl implements MessageService {

	private MessageRepository messageRepository;

	private MemberRepository memberRepository;

	private UserStatusService userStatusService;

	private SimpMessagingTemplate simpMessagingTemplate;

	public MessageServiceImpl(MessageRepository messageRepository, MemberRepository memberRepository,
			UserStatusService userStatusService, SimpMessagingTemplate simpMessagingTemplate) {
		this.messageRepository = messageRepository;
		this.memberRepository = memberRepository;
		this.userStatusService = userStatusService;
		this.simpMessagingTemplate = simpMessagingTemplate;
	}

	@Override
	public void sendMessageToGroupId(ChatMessage chatMessage) {
		List<UUID> groupIds = new ArrayList<>();
		groupIds.add(chatMessage.getReceiver());
		Specification<Member> spec = MemberSpecification.findMemberByGroupId(groupIds)
				.and(MemberSpecification.excludeUserId(chatMessage.getSender()));
		List<Member> members = memberRepository.findAll(spec);
		for (Member member : members) {
			if (!userStatusService.isUserOnline(member.getUser().getId())) {
				messageRepository.save(
						new Message(chatMessage.getReceiver(), chatMessage.getSender(), chatMessage.getContent()));
				break;
			}
			if (userStatusService.isUserSubscribe(member.getUser().getId(),
					"/queue/message-user" + chatMessage.getReceiver())) {
				simpMessagingTemplate.convertAndSend("/queue/message-user" + chatMessage.getReceiver(), chatMessage);
			} else {
				simpMessagingTemplate.convertAndSend("/queue/message-notifi" + member.getUser().getId(), chatMessage);
			}
			messageRepository
					.save(new Message(chatMessage.getReceiver(), chatMessage.getSender(), chatMessage.getContent()));
		}
	}

	@Override
	public void createMessage(UUID groupId, UUID userId, String message) {
		Message newMessage = new Message(groupId, userId, message);
		messageRepository.save(newMessage);
	}

	@Override
	public Page<MessageResponse> getMessagesByGroupId(UUID groupId, Pageable pageable) {
		Specification<Message> specification = MessageSpecification
				.getMessagesByGroupId(Group.builder().id(groupId).build());
		return messageRepository.findAll(specification, pageable).map(MessageResponse::new);
	}

	@Override
	public String deleteMessageByUserId(UUID messageId, UUID userId) {
		return messageRepository.findById(userId).map(t -> {
			if (t.getUser().getId().equals(userId)) {
				messageRepository.delete(t);
			}
			return "Successful";
		}).orElseThrow(() -> new ResourceNotFoundException("Resource can not delete."));
	}

	public User getUser() {
		Object object = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return (User) object;
	}

}
