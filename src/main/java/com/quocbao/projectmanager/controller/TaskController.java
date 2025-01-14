package com.quocbao.projectmanager.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.hateoas.EntityModel;
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

import com.quocbao.projectmanager.common.DataResponse;
import com.quocbao.projectmanager.common.LinkHateoas;
import com.quocbao.projectmanager.common.PaginationResponse;
import com.quocbao.projectmanager.payload.request.TaskRequest;
import com.quocbao.projectmanager.payload.response.TaskResponse;
import com.quocbao.projectmanager.service.TaskService;

@RestController
@RequestMapping("/projects/{projectId}")
public class TaskController {

	private TaskService taskService;
	private LinkHateoas linkHateoas;

	public TaskController(TaskService taskSerivce, LinkHateoas linkHateoas) {
		this.taskService = taskSerivce;
		this.linkHateoas = linkHateoas;
	}

	@PostMapping("/tasks")
	public ResponseEntity<DataResponse> createTask(@PathVariable UUID projectId, @RequestBody TaskRequest taskRequest) {
		TaskResponse taskResponse = taskService.createTask(projectId, taskRequest);
		EntityModel<TaskResponse> entityModel = EntityModel.of(taskResponse);
		entityModel.add(linkHateoas.linkGetTask(projectId, taskResponse.getId())).add(linkHateoas
				.linkGetTasksByUserIdAndProjectIdAndStatusAndDate(taskRequest.getUserId(), projectId, "PLANNING"));
		return new ResponseEntity<>(
				new DataResponse(HttpStatus.OK.value(), entityModel, "Task creation request successful."),
				HttpStatus.OK);
	}

	@GetMapping("/tasks/{taskId}")
	public ResponseEntity<DataResponse> getTask(@PathVariable UUID projectId, @PathVariable UUID taskId) {

		TaskResponse taskResponse = taskService.getTask(taskId);

		EntityModel<TaskResponse> entityModel = EntityModel.of(taskResponse);
		entityModel.add(linkHateoas.linkUpdateTask(projectId, taskId, taskResponse.getUserId()))
				.add(linkHateoas.linkDeleteTask(projectId, taskResponse.getUserId(), taskId))
				.add(linkHateoas.linkGetUser(taskResponse.getUserId()))
				.add(linkHateoas.linkCommitContent(projectId, taskId));
		return new ResponseEntity<>(new DataResponse(HttpStatus.OK.value(), entityModel, "Retrieve info Task."),
				HttpStatus.OK);
	}

	@PutMapping("/users/{userId}/tasks/{taskId}")
	public ResponseEntity<DataResponse> updateTask(@PathVariable UUID projectId, @PathVariable UUID taskId,
			@PathVariable UUID userId, @RequestBody TaskRequest taskRequest) {
		TaskResponse taskResponse = taskService.updateTask(taskId, userId, taskRequest);
		EntityModel<TaskResponse> entityModel = EntityModel.of(taskResponse);
		entityModel.add(linkHateoas.linkGetTask(taskId, projectId));
		return new ResponseEntity<>(
				new DataResponse(HttpStatus.OK.value(), entityModel, "Update task request successful."), HttpStatus.OK);
	}

	@PutMapping("/tasks/{taskId}/commit_content")
	public ResponseEntity<DataResponse> commitContent(@PathVariable UUID projectId, @PathVariable UUID taskId,
			@RequestBody TaskRequest taskRequest) {
		TaskResponse taskResponse = taskService.commitContentTask(taskId, taskRequest);
		EntityModel<TaskResponse> entityModel = EntityModel.of(taskResponse);
		entityModel.add(linkHateoas.linkGetTask(taskId, projectId));
		return new ResponseEntity<>(new DataResponse(HttpStatus.OK.value(), entityModel, "Commit contenet successful."),
				HttpStatus.OK);
	}

	@DeleteMapping("/users/{userId}/tasks/{taskId}")
	public ResponseEntity<DataResponse> deleteTask(@PathVariable UUID projectId, @PathVariable UUID userId,
			@PathVariable UUID taskId) {
		return new ResponseEntity<>(
				new DataResponse(HttpStatus.OK.value(), taskService.deleteTask(userId, taskId), "Request Successful."),
				HttpStatus.OK);
	}

	@GetMapping("/users/{userId}/tasks")
	public ResponseEntity<PaginationResponse<EntityModel<TaskResponse>>> getTasks(@PathVariable UUID userId,
			@PathVariable UUID projectId, @RequestParam(required = false, defaultValue = "TODO") String status,
			@RequestParam(defaultValue = "name") String sortField,
			@RequestParam(defaultValue = "asc") String sortDirection, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {

		Direction direction = Direction.fromString(sortDirection.toUpperCase());

		Page<TaskResponse> taskPage = taskService.getTasks(userId, projectId, status,
				PageRequest.of(page, size, Sort.by(direction, sortField)));

		List<EntityModel<TaskResponse>> entityModels = taskPage.getContent().stream()
				.map(t -> EntityModel.of(t).add(linkHateoas.linkGetTask(projectId, t.getId()))).toList();

		PaginationResponse<EntityModel<TaskResponse>> paginationResponse = new PaginationResponse<>(HttpStatus.OK,
				entityModels, taskPage.getPageable().getPageNumber(), taskPage.getSize(), taskPage.getTotalElements(),
				taskPage.getTotalPages(), taskPage.getSort().isSorted(), taskPage.getSort().isUnsorted(),
				taskPage.getSort().isEmpty());

		return new ResponseEntity<>(paginationResponse, HttpStatus.OK);
	}

	@GetMapping("/users/{userId}/tasks/GDP_task_status")
	public ResponseEntity<DataResponse> getGDPTaskByStatus(@PathVariable UUID userId, @PathVariable UUID projectId) {
		String gdpTask = taskService.getGDPTask(userId, projectId);
		return new ResponseEntity<>(new DataResponse(HttpStatus.OK.value(), null, gdpTask), HttpStatus.OK);
	}

	@PutMapping("/users/{userId}/tasks/{taskId}/toUser/{toUserId}")
	public ResponseEntity<DataResponse> asignTaskToUser(@PathVariable UUID projectId, @PathVariable UUID userId,
			@PathVariable UUID taskId, @PathVariable UUID toUserId) {
		taskService.assignToUser(taskId, userId, toUserId);
		return new ResponseEntity<>(new DataResponse(HttpStatus.OK.value(), null, "Successfull"), HttpStatus.OK);
	}

}
