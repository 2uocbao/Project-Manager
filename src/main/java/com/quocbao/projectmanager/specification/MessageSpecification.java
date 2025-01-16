package com.quocbao.projectmanager.specification;

import org.springframework.data.jpa.domain.Specification;

import com.quocbao.projectmanager.entity.Group;
import com.quocbao.projectmanager.entity.Message;
import com.quocbao.projectmanager.entity.Message_;
import com.quocbao.projectmanager.entity.User;

public class MessageSpecification {

	private MessageSpecification() {

	}

	public static Specification<Message> getMessagesByGroupId(Group group) {
		return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Message_.group), group);
	}

	public static Specification<Message> getMessageByUserId(User user) {
		return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Message_.user), user);
	}

}
