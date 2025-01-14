package com.quocbao.projectmanager.specification;

import java.sql.Timestamp;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;

import com.quocbao.projectmanager.entity.Project;
import com.quocbao.projectmanager.entity.Project_;
import com.quocbao.projectmanager.entity.Task;
import com.quocbao.projectmanager.entity.Task_;
import com.quocbao.projectmanager.entity.User_;

import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

public class ProjectSpecification {

	private ProjectSpecification() {

	}

	public static Specification<Project> findByUserId(UUID user) {
		return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Project_.user).get(User_.id), user);
	}

	public static Specification<Project> findByStatus(String status) {
		return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Project_.status), status);
	}

	public static Specification<Project> findProject(UUID user) {
		return (root, query, criteriaBuilder) -> {
			Subquery<UUID> subQuery = query.subquery(UUID.class);
			Root<Task> rootTask = subQuery.from(Task.class);
			subQuery.select(rootTask.get(Task_.project).get(Project_.id));
			subQuery.where(criteriaBuilder.equal(rootTask.get(Task_.user).get(User_.id), user));
			return criteriaBuilder.in(root.get(Project_.id)).value(subQuery);
		};
	}

	public static Specification<Project> byDate(Timestamp start, Timestamp end) {
		return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get(Project_.dateEnd), start, end);
	}
}
