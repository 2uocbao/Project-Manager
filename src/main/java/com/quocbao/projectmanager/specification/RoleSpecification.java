package com.quocbao.projectmanager.specification;

import org.springframework.data.jpa.domain.Specification;

import com.quocbao.projectmanager.entity.Role;
import com.quocbao.projectmanager.entity.Role_;

public class RoleSpecification {

	private RoleSpecification() {

	}

	public static Specification<Role> findByName(String name) {
		return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Role_.name), name);
	}
}
