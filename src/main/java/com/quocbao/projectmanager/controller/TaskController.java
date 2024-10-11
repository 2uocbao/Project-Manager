package com.quocbao.projectmanager.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

import com.quocbao.projectmanager.common.ConvertData;
import com.quocbao.projectmanager.common.DataResponse;
import com.quocbao.projectmanager.common.LinkHateoas;
import com.quocbao.projectmanager.common.PaginationResponse;
import com.quocbao.projectmanager.entity.Task;
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

	@PostMapping("/task")
	public ResponseEntity<DataResponse> createTask(@PathVariable Long projectId, @RequestBody TaskRequest taskRequest) {
		TaskResponse taskResponse = taskService.createTask(projectId, taskRequest);
		EntityModel<TaskResponse> entityModel = EntityModel.of(taskResponse);
		entityModel.add(linkHateoas.linkGetTask(taskResponse.getId(), projectId)).add(linkHateoas
				.linkGetTasksByUserIdAndProjectIdAndStatusAndDate(taskRequest.getUserId(), projectId, "PLANNING"));
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
		entityModel.add(linkHateoas.linkUpdateTask(projectId, taskId, userId))
				.add(linkHateoas.linkDeleteTask(projectId, task.getUser().getId(), taskId))
				.add(linkHateoas.linkGetUser(userId)).add(linkHateoas.linkCommitContent(projectId, taskId));
		return new ResponseEntity<>(new DataResponse(HttpStatus.OK.value(), entityModel, "Retrieve info Task."),
				HttpStatus.OK);
	}

	@PutMapping("/users/{userId}/task/{taskId}")
	public ResponseEntity<DataResponse> updateTask(@PathVariable Long projectId, @PathVariable Long taskId,
			@PathVariable Long userId, @RequestBody TaskRequest taskRequest) {
		TaskResponse taskResponse = taskService.updateTask(taskId, userId, taskRequest);
		EntityModel<TaskResponse> entityModel = EntityModel.of(taskResponse);
		entityModel.add(linkHateoas.linkGetTask(taskId, projectId));
		return new ResponseEntity<>(
				new DataResponse(HttpStatus.OK.value(), entityModel, "Update infomation request successful."),
				HttpStatus.OK);
	}

	@PutMapping("/task/{taskId}/commit_content")
	public ResponseEntity<DataResponse> commitContent(@PathVariable Long projectId, @PathVariable Long taskId,
			@RequestBody TaskRequest taskRequest) {
		TaskResponse taskResponse = taskService.commitContentTask(taskId, taskRequest);
		EntityModel<TaskResponse> entityModel = EntityModel.of(taskResponse);
		entityModel.add(linkHateoas.linkGetTask(taskId, projectId));
		return new ResponseEntity<>(new DataResponse(HttpStatus.OK.value(), entityModel, "Commit contenet successful."),
				HttpStatus.OK);
	}

	@DeleteMapping("/users/{userId}/task/{taskId}")
	public ResponseEntity<DataResponse> deleteTask(@PathVariable Long projectId, @PathVariable Long userId,
			@PathVariable Long taskId) {
		return new ResponseEntity<>(
				new DataResponse(HttpStatus.OK.value(), taskService.deleteTask(userId, taskId), "Request Successful."),
				HttpStatus.OK);
	}

	@GetMapping("/users/{userId}/tasks")
	public ResponseEntity<PaginationResponse<EntityModel<TaskResponse>>> getTasksByUserIdAndProjectAndStatusAndDate(
			@PathVariable Long userId, @PathVariable Long projectId,
			@RequestParam(required = false, defaultValue = "PLANNING") String status, @RequestParam String date,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
		LocalDate dateConvert = new ConvertData().toDate(date);
		Page<TaskResponse> taskPage = taskService.getTaskByUserIdAndProjectIdAndStatusAndDate(userId, projectId, status,
				dateConvert, PageRequest.of(page, size));
		List<EntityModel<TaskResponse>> entityModels = taskPage.getContent().stream()
				.map(t -> EntityModel.of(t).add(linkHateoas.linkGetTask(projectId, t.getId()))).toList();
		PaginationResponse<EntityModel<TaskResponse>> paginationResponse = new PaginationResponse<>(HttpStatus.OK,
				entityModels, taskPage.getPageable().getPageNumber(), taskPage.getSize(), taskPage.getTotalElements(),
				taskPage.getTotalPages(), taskPage.getSort().isSorted(), taskPage.getSort().isUnsorted(),
				taskPage.getSort().isEmpty());
		return new ResponseEntity<>(paginationResponse, HttpStatus.OK);
	}
}
