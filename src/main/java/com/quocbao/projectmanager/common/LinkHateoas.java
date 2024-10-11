package com.quocbao.projectmanager.common;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.time.ZoneId;
import java.util.Date;

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
	public Link linkGetUser(Long userId) {
		return linkTo(methodOn(UserController.class).getUser(userId)).withRel("get")
				.withTitle("View user requested information").withType("GET");
	}

	public Link linkUpdateUser(Long userId) {
		return linkTo(methodOn(UserController.class).updateUser(userId, new UserRequest())).withRel("update")
				.withTitle("Update this user").withType("PUT");
	}

	public Link linkGetFriendsByUserId(Long userId) {
		return linkTo(methodOn(UserController.class).getFriendsByUserId(userId, 0, 10)).withRel("get")
				.withTitle("Get list friends of user by user id").withType("GET");
	}

	public Link linkCreateProject(Long userId) {
		return linkTo(methodOn(ProjectController.class).createProject(userId, new ProjectRequest())).withRel("projects")
				.withTitle("Create a new project.").withType("POST");
	}

	// Link project
	public Link linkGetProject(Long userId, Long projectId) {
		return linkTo(methodOn(ProjectController.class).getProject(userId, projectId)).withRel("get")
				.withTitle("Retrieve info project").withType("GET");
	}

	public Link linkUpdateProject(Long userId, Long projectId) {
		return linkTo(methodOn(ProjectController.class).updateProject(userId, projectId, new ProjectRequest()))
				.withRel("put").withTitle("Update info project").withType("PUT");
	}

	public Link linkDeleteProject(Long userId, Long projectId) {
		return linkTo(methodOn(ProjectController.class).deleteProject(userId, projectId)).withRel("del")
				.withTitle("Delete a project").withType("DELETE");
	}

	public Link linkGetProjectsByUserId(Long userId, String status) {
		return linkTo(methodOn(ProjectController.class).findProjectByUserIdAndStatusAndSearch(userId, status, 0, 10, "name", "asc", null))
				.withRel("projects").withTitle("Get list projects by user id").withType("GET");
	}

	public Link linkCreateTask(Long projectId) {
		return linkTo(methodOn(TaskController.class).createTask(projectId, null)).withRel("task")
				.withTitle("Create a new task").withType("POST");
	}

	// Link task
	public Link linkGetTask(Long projectId, Long taskId) {
		return linkTo(methodOn(TaskController.class).getTask(projectId, taskId)).withRel("get")
				.withTitle("Retrieve info task").withType("GET");
	}

	public Link linkUpdateTask(Long projectId, Long taskId, Long userId) {
		return linkTo(methodOn(TaskController.class).updateTask(projectId, taskId, userId, new TaskRequest()))
				.withRel("update").withTitle("Update info task").withType("UPDATE");
	}

	public Link linkDeleteTask(Long projectId, Long userId, Long taskId) {
		return linkTo(methodOn(TaskController.class).deleteTask(projectId, userId, taskId)).withRel("delete")
				.withTitle("Delete a task").withType("DELETE");
	}

	public Link linkGetTasksByUserIdAndProjectIdAndStatusAndDate(Long userId, Long projectId, String status) {
		return linkTo(methodOn(TaskController.class).getTasksByUserIdAndProjectAndStatusAndDate(userId, projectId,
				status, new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString(), 0, 10))
				.withRel("Task").withTitle("Retrieve tasks of project by ID user, ID project and status")
				.withType("GET");
	}

	public Link linkCommitContent(Long projectId, Long taskId) {
		return linkTo(methodOn(TaskController.class).commitContent(projectId, taskId, new TaskRequest()))
				.withRel("TASK").withTitle("Commit content task").withType("UPDATE");
	}

	// Link of group
	public Link linkGetGroupByUserId(Long userId) {
		return linkTo(methodOn(GroupController.class).getGroupsByUserId(userId, 0, 10, null)).withRel("GROUP")
				.withTitle("Retrieve groups of user").withType("GET");
	}

	public Link linkUpdateGroup(Long userId, Long groupId, String name) {
		return linkTo(methodOn(GroupController.class).updateGroup(userId, groupId, name)).withRel("GROUP")
				.withTitle("Update info group").withType("PUT");
	}

	public Link linkGetMembersInGroup(Long groupId) {
		return linkTo(methodOn(GroupController.class).getUsersInGroup(groupId, 0, 10, null)).withRel("GROUP")
				.withTitle("Retrieve members in group").withType("GET");
	}

	public Link linkAddMemberToGroup(Long userId, Long groupId, Long memberId) {
		return linkTo(methodOn(GroupController.class).addMember(userId, groupId, memberId)).withRel("GROUP")
				.withTitle("Add a user to group").withType("POST");
	}

	public Link linkRemoveMemberOfGroup(Long userId, Long groupId, Long memberId) {
		return linkTo(methodOn(GroupController.class).delMember(userId, groupId, memberId)).withRel("GROUP")
				.withTitle("Delete a user of group").withType("DELETE");
	}

	// Link of message

	public Link linkGetMessageInGroup(Long groupId) {
		return linkTo(methodOn(MessageController.class).getMessagesByGroupId(groupId)).withRel("MESSAGE")
				.withTitle("Retrieve messages in group").withType("GET");
	}

	public Link linkDelMessage(Long userId, Long messageId) {
		return linkTo(methodOn(MessageController.class).deleteMessage(userId, messageId)).withRel("MESSAGE")
				.withTitle("Delete message in group").withType("DELETE");
	}
}
