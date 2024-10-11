package com.quocbao.projectmanager.specification;

import org.springframework.data.jpa.domain.Specification;

import com.quocbao.projectmanager.entity.Group;
import com.quocbao.projectmanager.entity.Group_;
import com.quocbao.projectmanager.entity.Project_;
import com.quocbao.projectmanager.entity.User;
import com.quocbao.projectmanager.entity.User_;

import jakarta.persistence.criteria.Join;

public class GroupSpecification {

	private GroupSpecification() {

	}

	public static Specification<Group> getGroupsByUserId(User user) {
		return (root, query, criteriaBuilder) -> {
			Join<Group, User> member = root.join(Group_.users);
			return criteriaBuilder.equal(member.get(User_.id), user.getId());
		};
	}

	public static Specification<Group> search(String keySearch) {
		return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(Project_.name), keySearch);
	}
}
