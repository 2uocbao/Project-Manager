package com.quocbao.projectmanager.specification;

import org.springframework.data.jpa.domain.Specification;

import com.quocbao.projectmanager.entity.Project;
import com.quocbao.projectmanager.entity.Project_;
import com.quocbao.projectmanager.entity.Task;
import com.quocbao.projectmanager.entity.Task_;
import com.quocbao.projectmanager.entity.User;

import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

public class ProjectSpecification {

	private ProjectSpecification() {

	}

	public static Specification<Project> findByUserId(User user) {
		return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Project_.user), user);
	}

	public static Specification<Project> findByStatus(String status) {
		return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Project_.status), status);
	}

	public static Specification<Project> findProject(User user) {
		return (root, query, criteriaBuilder) -> {
			@SuppressWarnings("null")
			Subquery<Long> subQuery = query.subquery(Long.class);
			Root<Task> rootTask = subQuery.from(Task.class);
			subQuery.select(rootTask.get(Task_.project).get(Project_.id));
			subQuery.where(criteriaBuilder.equal(rootTask.get(Task_.user), user));
			return criteriaBuilder.in(root.get(Project_.id)).value(subQuery);
		};
	}
}
