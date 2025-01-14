package com.quocbao.projectmanager.specification;

import org.springframework.data.jpa.domain.Specification;

import com.quocbao.projectmanager.entity.Group;
import com.quocbao.projectmanager.entity.Group_;

public class GroupSpecification {

	private GroupSpecification() {

	}

	public static Specification<Group> search(String keySearch) {
		return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get(Group_.name), keySearch);
	}
}
