package com.quocbao.projectmanager.specification;

import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;

import com.quocbao.projectmanager.entity.Group_;
import com.quocbao.projectmanager.entity.Member;
import com.quocbao.projectmanager.entity.Member_;
import com.quocbao.projectmanager.entity.User;
import com.quocbao.projectmanager.entity.User_;

import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import jakarta.persistence.metamodel.SingularAttribute;

public class UserSpecification {

	private UserSpecification() {

	}

	public static Specification<User> searchUser(SingularAttribute<User, String> column, String value) {
		return (root, query, criteriaBulider) -> criteriaBulider.like(root.get(column), value);
	}

	public static Specification<User> findByColumn(SingularAttribute<User, String> column, String value) {
		return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(column), value);
	}

	public static Specification<User> excludeUserId(UUID userId) {
		return (root, query, criteriaBuilder) -> criteriaBuilder.notEqual(root.get(User_.id), userId);
	}

	public static Specification<User> searchUser(UUID userId, Boolean isFind) {
		return (root, query, criteriaBuilder) -> {
			
			Subquery<UUID> groupSubquery = query.subquery(UUID.class);
			Root<Member> groupRoot = groupSubquery.from(Member.class);
			
			Subquery<UUID> userGroupSubquery = query.subquery(UUID.class);
			Root<Member> userGroupRoot = userGroupSubquery.from(Member.class);
			
			// query select group ID by userId
			userGroupSubquery.select(userGroupRoot.get(Member_.group).get(Group_.id))
					.where(criteriaBuilder.equal(userGroupRoot.get(Member_.user).get(User_.id), userId));
			
			// query select user in group ID
			groupSubquery.select(groupRoot.get(Member_.user).get(User_.id))
					.where(groupRoot.get(Member_.group).get(Group_.id).in(userGroupSubquery));
			
			// case for find users with the same group
			if(Boolean.TRUE.equals(isFind)) {
				return criteriaBuilder.and(root.get(User_.id).in(groupSubquery));
			}
			return criteriaBuilder.not(root.get(User_.id).in(groupSubquery));
		};
	}
}
