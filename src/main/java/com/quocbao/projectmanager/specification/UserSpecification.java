package com.quocbao.projectmanager.specification;

import org.springframework.data.jpa.domain.Specification;

import com.quocbao.projectmanager.entity.Friend;
import com.quocbao.projectmanager.entity.Friend_;
import com.quocbao.projectmanager.entity.User;
import com.quocbao.projectmanager.entity.User_;

import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import jakarta.persistence.metamodel.SingularAttribute;

public class UserSpecification {

	private UserSpecification() {

	}

	public static Specification<User> findByColumn(SingularAttribute<User, String> column, String value) {
		return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(column), value);
	}

	public static Specification<User> propertiesByUserId(Long userId) {
		return (root, query, criteriaBuilder) -> {
			@SuppressWarnings("null")
			Subquery<Long> subquery = query.subquery(Long.class);
			Root<Friend> rootFriend = subquery.from(Friend.class);
			subquery.select(rootFriend.get(Friend_.friendId));
			subquery.where(criteriaBuilder.equal(rootFriend.get(Friend_.userId), userId));
			Predicate predicate = criteriaBuilder.in(root.get(User_.id)).value(subquery);
			query.where(predicate);
			return predicate;
		};
	}
}
