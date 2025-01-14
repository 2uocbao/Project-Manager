package com.quocbao.projectmanager.websocket;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.quocbao.projectmanager.entity.Notification;
import com.quocbao.projectmanager.payload.request.NotifiRequest;
import com.quocbao.projectmanager.payload.response.NotifiResponse;
import com.quocbao.projectmanager.repository.NotificationRepository;
import com.quocbao.projectmanager.specification.NotificationSpecification;

@Service
public class PushNotificationService {

	private SimpMessagingTemplate simpMessagingTemplate;
	private NotificationRepository notificationRepository;
	private UserStatusService userStatusService;

	public PushNotificationService(SimpMessagingTemplate simpMessagingTemplate,
			UserStatusService userStatusService,
			NotificationRepository notificationRepository) {
		this.simpMessagingTemplate = simpMessagingTemplate;
		this.notificationRepository = notificationRepository;
		this.userStatusService = userStatusService;
	}

	public void pushNotification(NotifiRequest notifiRequest) {
		if(userStatusService.isUserOnline(notifiRequest.getReceiver())) {
			simpMessagingTemplate.convertAndSend("/queue/notifi-user" + notifiRequest.getReceiver(), notifiRequest);
		}
		notificationRepository.save(new Notification(notifiRequest));
	}

	public Page<NotifiResponse> getNotificationsByUserId(UUID userId, Pageable pageable) {
		Specification<Notification> specification = Specification
				.where(NotificationSpecification.getNotificationsByUserId(userId));
		return notificationRepository.findAll(specification, pageable).map(NotifiResponse::new);

	}

}
