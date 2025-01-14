package com.quocbao.projectmanager.common;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.UUID;

import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import com.quocbao.projectmanager.controller.GroupController;
import com.quocbao.projectmanager.controller.MessageController;
import com.quocbao.projectmanager.controller.ProjectController;
import com.quocbao.projectmanager.controller.TaskController;
import com.quocbao.projectmanager.controller.UserController;
import com.quocbao.projectmanager.payload.request.ProjectRequest;
import com.quocbao.projectmanager.payload.request.TaskRequest;
import com.quocbao.projectmanager.payload.request.UserRequest;

@Component
public class LinkHateoas {

	private LinkHateoas() {

	}

	// Link user
	public Link linkGetUser(UUID userId) {
		return linkTo(methodOn(UserController.class).getUser(userId)).withRel("get")
				.withTitle("View user requested information").withType("GET");
	}

	public Link linkUpdateUser(UUID userId) {
		return linkTo(methodOn(UserController.class).updateUser(userId, new UserRequest())).withRel("update")
				.withTitle("Update this user").withType("PUT");
	}

	public Link linkCreateProject(UUID userId) {
		return linkTo(methodOn(ProjectController.class).createProject(userId, new ProjectRequest())).withRel("projects")
				.withTitle("Create a new project.").withType("POST");
	}

	// Link project
	public Link linkGetProject(UUID userId, UUID projectId) {
		return linkTo(methodOn(ProjectController.class).getProject(userId, projectId)).withRel("get")
				.withTitle("Retrieve info project").withType("GET");
	}

	public Link linkUpdateProject(UUID userId, UUID projectId) {
		return linkTo(methodOn(ProjectController.class).updateProject(userId, projectId, new ProjectRequest()))
				.withRel("put").withTitle("Update info project").withType("PUT");
	}

	public Link linkDeleteProject(UUID userId, UUID projectId) {
		return linkTo(methodOn(ProjectController.class).deleteProject(userId, projectId)).withRel("del")
				.withTitle("Delete a project").withType("DELETE");
	}

	public Link linkGetProjectsByUserId(UUID userId) {
		return linkTo(methodOn(ProjectController.class).getProjectsByUserIdByStatus(userId,
				StatusEnum.PENDING.toString(), 0, 10, null, null)).withRel("projects")
				.withTitle("Get list projects by user id").withType("GET");
	}

	public Link linkCreateTask(UUID projectId) {
		return linkTo(methodOn(TaskController.class).createTask(projectId, null)).withRel("task")
				.withTitle("Create a new task").withType("POST");
	}

	// Link task
	public Link linkGetTask(UUID projectId, UUID taskId) {
		return linkTo(methodOn(TaskController.class).getTask(projectId, taskId)).withRel("get")
				.withTitle("Retrieve info task").withType("GET");
	}

	public Link linkUpdateTask(UUID projectId, UUID taskId, UUID userId) {
		return linkTo(methodOn(TaskController.class).updateTask(projectId, taskId, userId, new TaskRequest()))
				.withRel("update").withTitle("Update info task").withType("UPDATE");
	}

	public Link linkDeleteTask(UUID projectId, UUID userId, UUID taskId) {
		return linkTo(methodOn(TaskController.class).deleteTask(projectId, userId, taskId)).withRel("delete")
				.withTitle("Delete a task").withType("DELETE");
	}

	public Link linkGetTasksByUserIdAndProjectIdAndStatusAndDate(UUID userId, UUID projectId, String status) {
		return linkTo(methodOn(TaskController.class).getTasks(userId, projectId, status, null, null, 0, 10))
				.withRel("Task").withTitle("Retrieve tasks of project by ID user, ID project and status")
				.withType("GET");
	}

	public Link linkCommitContent(UUID projectId, UUID taskId) {
		return linkTo(methodOn(TaskController.class).commitContent(projectId, taskId, new TaskRequest()))
				.withRel("TASK").withTitle("Commit content task").withType("UPDATE");
	}

	// Link of group
	public Link linkGetGroupByUserId(UUID userId) {
		return linkTo(methodOn(GroupController.class).getGroupsByUserId(userId, 0, 10, null)).withRel("GROUP")
				.withTitle("Retrieve groups of user").withType("GET");
	}

	public Link linkUpdateGroup(UUID userId, UUID groupId, String name) {
		return linkTo(methodOn(GroupController.class).updateGroup(userId, groupId, name)).withRel("GROUP")
				.withTitle("Update info group").withType("PUT");
	}

	public Link linkGetMembersInGroup(UUID groupId) {
		return linkTo(methodOn(GroupController.class).getUsersInGroup(groupId, groupId, 0, 10, null)).withRel("GROUP")
				.withTitle("Retrieve members in group").withType("GET");
	}

	public Link linkAddMemberToGroup(UUID userId, UUID groupId, UUID memberId) {
		return linkTo(methodOn(GroupController.class).addMember(userId, groupId, memberId)).withRel("GROUP")
				.withTitle("Add a user to group").withType("POST");
	}

	public Link linkRemoveMemberOfGroup(UUID userId, UUID groupId, UUID memberId) {
		return linkTo(methodOn(GroupController.class).delMember(userId, groupId, memberId)).withRel("GROUP")
				.withTitle("Delete a user of group").withType("DELETE");
	}

	// Link of message

	public Link linkGetMessageInGroup(UUID groupId) {
		return linkTo(methodOn(MessageController.class).getMessagesByGroupId(groupId, 0, 10)).withRel("MESSAGE")
				.withTitle("Retrieve messages in group").withType("GET");
	}

	public Link linkDelMessage(UUID userId, UUID messageId) {
		return linkTo(methodOn(MessageController.class).deleteMessage(userId, messageId)).withRel("MESSAGE")
				.withTitle("Delete message in group").withType("DELETE");
	}
}
