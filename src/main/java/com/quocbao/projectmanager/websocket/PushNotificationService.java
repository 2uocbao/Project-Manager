package com.quocbao.projectmanager.websocket;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.quocbao.projectmanager.entity.Notification;
import com.quocbao.projectmanager.repository.NotificationRepository;
import com.quocbao.projectmanager.specification.NotificationSpecification;

@Service
public class PushNotificationService {

	private SimpMessagingTemplate simpMessagingTemplate;
	private WebSocketEventListener webSocketEventListener;
	private NotificationRepository notificationRepository;

	public PushNotificationService(SimpMessagingTemplate simpMessagingTemplate,
			WebSocketEventListener webSocketEventListener, NotificationRepository notificationRepository) {
		this.simpMessagingTemplate = simpMessagingTemplate;
		this.webSocketEventListener = webSocketEventListener;
		this.notificationRepository = notificationRepository;
	}

	public void pushNotification(Long memberId, String destination, String message, String type) {
		if (webSocketEventListener.isUserActive(memberId.toString())) {
			simpMessagingTemplate.convertAndSend(destination, message);
		} else {
			Notification notification = new Notification(memberId, message, type);
			notificationRepository.save(notification);
		}
	}

	public List<Notification> getNotificationsByUserId(Long userId) {
		Specification<Notification> specification = Specification
				.where(NotificationSpecification.getNotificationsByUserId(userId));
		return notificationRepository.findAll(specification);

	}

}
