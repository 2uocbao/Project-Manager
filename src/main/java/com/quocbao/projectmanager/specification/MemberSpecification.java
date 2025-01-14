package com.quocbao.projectmanager.specification;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;

import com.quocbao.projectmanager.entity.Group_;
import com.quocbao.projectmanager.entity.Member;
import com.quocbao.projectmanager.entity.Member_;
import com.quocbao.projectmanager.entity.User;

public class MemberSpecification {

	private MemberSpecification() {

	}

	public static Specification<Member> findMemberByGroupId(List<UUID> groupIds) {
		return (root, query, criteriaBuilder) -> root.get(Member_.group).get(Group_.id).in(groupIds);
	}

	public static Specification<Member> findMemberByUserId(UUID userId) {
		return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Member_.user),
				User.builder().id(userId).build());
	}

	public static Specification<Member> excludeUserId(UUID userId) {
		return (root, query, criteriaBuilder) -> criteriaBuilder.notEqual(root.get(Member_.user),
				User.builder().id(userId).build());
	}

}
