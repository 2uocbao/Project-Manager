package com.quocbao.projectmanager.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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

import com.quocbao.projectmanager.common.DataResponse;
import com.quocbao.projectmanager.payload.request.ProjectRequest;
import com.quocbao.projectmanager.payload.response.ProjectResponse;
import com.quocbao.projectmanager.service.ProjectService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users/{userId}/projects")
public class ProjectController {

	private ProjectService projectService;

	public ProjectController(ProjectService projectService) {
		this.projectService = projectService;
	}

	@PostMapping()
	public ResponseEntity<DataResponse> createProject(@Valid @PathVariable Long userId,
			@RequestBody ProjectRequest projectRequest) {
		ProjectResponse projectResponse = projectService.createProject(userId, projectRequest);
		EntityModel<ProjectResponse> entityModel = EntityModel.of(projectResponse);
		entityModel.add(linkProjectsByUserId(userId, "INPROGRESS"));
		return new ResponseEntity<>(
				new DataResponse(HttpStatus.OK.value(), entityModel, "Project creation request successful."),
				HttpStatus.OK);
	}

	@GetMapping("/{projectId}")
	public ResponseEntity<DataResponse> getProject(@PathVariable Long userId, @PathVariable Long projectId) {
		EntityModel<ProjectResponse> entityModel = EntityModel.of(projectService.getProject(userId, projectId));
		entityModel.add(linkUpdateProject(userId, projectId, new ProjectRequest()))
				.add(linkProjectsByUserId(userId, "INPRGRESS")).add(linkDeleteProject(userId, projectId))
				.add(linkCreateTask(projectId))
				.add(linkGetTasksByUserIdAndProjectIdAndStatusAndDate(userId, projectId, "INPROCESS"));
		return new ResponseEntity<>(
				new DataResponse(HttpStatus.OK.value(), entityModel, "Request successful information project."),
				HttpStatus.OK);
	}

	@PutMapping("/{projectId}")
	public ResponseEntity<DataResponse> updateProject(@PathVariable Long userId, @Valid @PathVariable Long projectId,
			@RequestBody ProjectRequest projectRequest) {
		EntityModel<ProjectResponse> entityModel = EntityModel
				.of(projectService.updateProject(projectId, projectRequest));
		entityModel.add(linkGetProject(userId, projectId));
		return new ResponseEntity<>(
				new DataResponse(HttpStatus.OK.value(), entityModel, "Project information update successful."),
				HttpStatus.OK);
	}

	@GetMapping()
	public ResponseEntity<DataResponse> getProjectsByStatus(@PathVariable Long userId,
			@RequestParam(required = false, defaultValue = "INPROGRESS") String status) {
		List<EntityModel<ProjectResponse>> entityModels = projectService.getProjectsByUserIdAndStatus(userId, status)
				.stream().map(p -> EntityModel.of(p).add(linkGetProject(userId, p.getId()))).toList();
		return new ResponseEntity<>(
				new DataResponse(HttpStatus.OK.value(), entityModels, "Retrieve projects of user by condition."),
				HttpStatus.OK);
	}

	@DeleteMapping("/{projectId}")
	public ResponseEntity<DataResponse> deleteProject(@PathVariable Long userId, @PathVariable Long projectId) {
		return new ResponseEntity<>(
				new DataResponse(HttpStatus.OK.value(), projectService.deleteProject(projectId), "Request Successful"),
				HttpStatus.OK);
	}

	private Link linkGetProject(Long userId, Long projectId) {
		return linkTo(methodOn(ProjectController.class).getProject(userId, projectId)).withRel("get")
				.withTitle("Retrieve info project").withType("GET");
	}

	private Link linkUpdateProject(Long userId, Long projectId, ProjectRequest projectRequest) {
		return linkTo(methodOn(ProjectController.class).updateProject(userId, projectId, projectRequest)).withRel("put")
				.withTitle("Update info project").withType("PUT");
	}

	private Link linkDeleteProject(Long userId, Long projectId) {
		return linkTo(methodOn(ProjectController.class).deleteProject(userId, projectId)).withRel("del")
				.withTitle("Delete a project").withType("DELETE");
	}

	private Link linkProjectsByUserId(Long userId, String status) {
		return linkTo(methodOn(ProjectController.class).getProjectsByStatus(userId, status)).withRel("projects")
				.withTitle("Get list projects by user id").withType("GET");
	}

	private Link linkCreateTask(Long projectId) {
		return linkTo(methodOn(TaskController.class).createTask(projectId, null)).withRel("task")
				.withTitle("Create a new task").withType("POST");
	}

	private Link linkGetTasksByUserIdAndProjectIdAndStatusAndDate(Long userId, Long projectId, String status) {
		return linkTo(methodOn(TaskController.class).getTasksByUserIdAndProjectAndStatusAndDate(userId, projectId,
				status, new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString())).withRel("Task")
				.withTitle("Retrieve tasks of project by ID user, ID project and status").withType("GET");
	}
}
