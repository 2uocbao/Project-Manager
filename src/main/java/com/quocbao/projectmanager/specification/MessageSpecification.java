package com.quocbao.projectmanager.specification;

import org.springframework.data.jpa.domain.Specification;

import com.quocbao.projectmanager.entity.Group;
import com.quocbao.projectmanager.entity.Message;
import com.quocbao.projectmanager.entity.Message_;

public class MessageSpecification {

	private MessageSpecification() {

	}

	public static Specification<Message> getMessagesByGroupId(Group group) {
		return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Message_.group), group);
	}
}
