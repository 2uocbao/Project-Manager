package com.quocbao.projectmanager.specification;

import org.springframework.data.jpa.domain.Specification;

import com.quocbao.projectmanager.entity.Notification;
import com.quocbao.projectmanager.entity.Notification_;

public class NotificationSpecification {

	private NotificationSpecification() {

	}

	public static Specification<Notification> getNotificationsByUserId(Long userId) {
		return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Notification_.recipientId), userId);
	}

}
