package com.quocbao.projectmanager.specification;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;

import com.quocbao.projectmanager.entity.Project;
import com.quocbao.projectmanager.entity.Task;
import com.quocbao.projectmanager.entity.Task_;
import com.quocbao.projectmanager.entity.User;

public class TaskSpecification {

	private TaskSpecification() {

	}

	public static Specification<Task> getTaskByUserId(User user) {
		return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Task_.user), user);
	}

	public static Specification<Task> getTaskByProjectId(Project project) {
		return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Task_.project), project);
	}

	public static Specification<Task> getTaskByStatus(String status) {
		return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Task_.status), status);
	}

	public static Specification<Task> getTaskByType(String type) {
		return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Task_.type), type);
	}

	public static Specification<Task> getTaskByDate(LocalDate dateEnd) {
		return (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get(Task_.dateEnd), dateEnd);
	}
}
