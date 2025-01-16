package com.quocbao.projectmanager.specification;

import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;

import com.quocbao.projectmanager.entity.Project_;
import com.quocbao.projectmanager.entity.Task;
import com.quocbao.projectmanager.entity.Task_;
import com.quocbao.projectmanager.entity.User_;

public class TaskSpecification {

	private TaskSpecification() {

	}

	public static Specification<Task> getTaskDetailCustom(UUID taskId) {
		return (root, query, criteriaBuilder) -> {
			root.fetch(Task_.user);
			query.multiselect(root.get(Task_.user).get(User_.firstName),
					root.get(Task_.user).get(User_.firstName), root.get(Task_.user).get(User_.id));
			return criteriaBuilder.equal(root.get(Task_.id), taskId);
		};
	}

	public static Specification<Task> getTaskByUserId(UUID user) {
		return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Task_.user).get(User_.id), user);
	}

	public static Specification<Task> getTaskByProjectId(UUID project) {
		return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Task_.project).get(Project_.id),
				project);
	}

	public static Specification<Task> getTaskByStatus(String status) {
		return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Task_.status), status);
	}

	public static Specification<Task> getTaskByType(String type) {
		return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Task_.type), type);
	}

}
