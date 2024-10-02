package com.quocbao.projectmanager.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.quocbao.projectmanager.common.ConvertData;
import com.quocbao.projectmanager.common.DataResponse;
import com.quocbao.projectmanager.entity.Task;
import com.quocbao.projectmanager.payload.request.TaskRequest;
import com.quocbao.projectmanager.payload.response.TaskResponse;
import com.quocbao.projectmanager.service.TaskService;

@RestController
@RequestMapping("/projects/{projectId}")
public class TaskController {

	private TaskService taskService;

	public TaskController(TaskService taskSerivce) {
		this.taskService = taskSerivce;
	}

	@PostMapping("/task")
	public ResponseEntity<DataResponse> createTask(@PathVariable Long projectId, @RequestBody TaskRequest taskRequest) {
		TaskResponse taskResponse = taskService.createTask(projectId, taskRequest);
		EntityModel<TaskResponse> entityModel = EntityModel.of(taskResponse);
		entityModel.add(linkGetTask(projectId, taskResponse.getId()))
				.add(linkGetTasksByUserIdAndProjectIdAndStatusAndDate(taskRequest.getUserId(), projectId, "NONSTATUS"));
		return new ResponseEntity<>(
				new DataResponse(HttpStatus.OK.value(), entityModel, "Task creation request successful."),
				HttpStatus.OK);
	}

	@GetMapping("/task/{taskId}")
	public ResponseEntity<DataResponse> getTask(@PathVariable Long projectId, @PathVariable Long taskId) {
		Task task = taskService.getTask(taskId);
		Long userId = task.getUser().getId();
		TaskResponse taskResponse = new TaskResponse(task);
		EntityModel<TaskResponse> entityModel = EntityModel.of(taskResponse);
		entityModel.add(linkUpdateTask(projectId, taskId, null)).add(linkDeleteTask(taskId)).add(linkGetUser(userId))
				.add(linkGetTasksByUserIdAndProjectIdAndStatusAndDate(userId, projectId, task.getStatus()));
		return new ResponseEntity<>(new DataResponse(HttpStatus.OK.value(), entityModel, "Retrieve info Task."),
				HttpStatus.OK);
	}

	@PutMapping("/task/{taskId}")
	public ResponseEntity<DataResponse> updateTask(@PathVariable Long projectId, @PathVariable Long taskId,
			@RequestBody TaskRequest taskRequest) {
		TaskResponse taskResponse = taskService.updateTask(taskId, taskRequest);
		EntityModel<TaskResponse> entityModel = EntityModel.of(taskResponse);
		entityModel.add(linkGetTask(projectId, taskId));
		return new ResponseEntity<>(
				new DataResponse(HttpStatus.OK.value(), entityModel, "Update infomation request successful."),
				HttpStatus.OK);
	}

	@DeleteMapping("/task/{taskId}")
	public ResponseEntity<DataResponse> deleteTask(@PathVariable Long taskId) {
		return new ResponseEntity<>(
				new DataResponse(HttpStatus.OK.value(), taskService.deleteTask(taskId), "Request Successful."),
				HttpStatus.OK);
	}

	@GetMapping("/users/{userId}/tasks")
	public ResponseEntity<DataResponse> getTasksByUserIdAndProjectAndStatusAndDate(@PathVariable Long userId,
			@PathVariable Long projectId, @RequestParam String status, @RequestParam String date) {
		LocalDate dateConvert = new ConvertData().toDate(date);
		List<EntityModel<TaskResponse>> entityModels = taskService
				.getTaskByUserIdAndProjectIdAndStatusAndDate(userId, projectId, status, dateConvert).stream()
				.map(t -> EntityModel.of(t).add(linkGetTask(projectId, t.getId()))).toList();
		return new ResponseEntity<>(
				new DataResponse(HttpStatus.OK.value(), entityModels, "Get tasks by ID user, status, and date."),
				HttpStatus.OK);
	}

	private Link linkGetUser(Long userId) {
		return linkTo(methodOn(UserController.class).getUser(userId)).withRel("USER").withTitle("Get info user")
				.withType("GET");
	}

	private Link linkGetTask(Long projectId, Long taskId) {
		return linkTo(methodOn(TaskController.class).getTask(projectId, taskId)).withRel("get")
				.withTitle("Retrieve info task").withType("GET");
	}

	private Link linkUpdateTask(Long projectId, Long taskId, TaskRequest taskRequest) {
		return linkTo(methodOn(TaskController.class).updateTask(projectId, taskId, taskRequest)).withRel("update")
				.withTitle("Update info task").withType("UPDATE");
	}

	private Link linkDeleteTask(Long taskId) {
		return linkTo(methodOn(TaskController.class).deleteTask(taskId)).withRel("delete").withTitle("Delete a task")
				.withType("DELETE");
	}

	private Link linkGetTasksByUserIdAndProjectIdAndStatusAndDate(Long userId, Long projectId, String status) {
		return linkTo(methodOn(TaskController.class).getTasksByUserIdAndProjectAndStatusAndDate(userId, projectId,
				status, new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString())).withRel("Task")
				.withTitle("Retrieve tasks of project by ID user, ID project and status").withType("GET");
	}
}
