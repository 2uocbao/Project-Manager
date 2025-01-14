package com.quocbao.projectmanager.controller;

import java.util.List;
import java.util.UUID;

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

import com.quocbao.projectmanager.common.DataResponse;
import com.quocbao.projectmanager.common.LinkHateoas;
import com.quocbao.projectmanager.common.PaginationResponse;
//import com.quocbao.projectmanager.elasticsearch.entity.ProjectES;
import com.quocbao.projectmanager.payload.request.ProjectRequest;
import com.quocbao.projectmanager.payload.response.ProjectResponse;
import com.quocbao.projectmanager.service.ProjectService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users/{userId}/projects")
public class ProjectController {

	private ProjectService projectService;
	private LinkHateoas linkHateoas;

	public ProjectController(ProjectService projectService, LinkHateoas linkHateoas) {
		this.projectService = projectService;
		this.linkHateoas = linkHateoas;
	}

	@PostMapping()
	public ResponseEntity<DataResponse> createProject(@Valid @PathVariable UUID userId,
			@RequestBody ProjectRequest projectRequest) {
		ProjectResponse projectResponse = projectService.createProject(userId, projectRequest);
		EntityModel<ProjectResponse> entityModel = EntityModel.of(projectResponse);
		entityModel.add(linkHateoas.linkGetProjectsByUserId(userId));
		return new ResponseEntity<>(
				new DataResponse(HttpStatus.OK.value(), entityModel, "Project creation request successful."),
				HttpStatus.OK);
	}

	@GetMapping("/{projectId}")
	public ResponseEntity<DataResponse> getProject(@PathVariable UUID userId, @PathVariable UUID projectId) {
		EntityModel<ProjectResponse> entityModel = EntityModel.of(projectService.getProject(userId, projectId));
		entityModel.add(linkHateoas.linkUpdateProject(userId, projectId))
				.add(linkHateoas.linkGetProjectsByUserId(userId)).add(linkHateoas.linkDeleteProject(userId, projectId))
				.add(linkHateoas.linkCreateTask(projectId))
				.add(linkHateoas.linkGetTasksByUserIdAndProjectIdAndStatusAndDate(userId, projectId, "PENDING"));
		return new ResponseEntity<>(
				new DataResponse(HttpStatus.OK.value(), entityModel, "Request successful information project."),
				HttpStatus.OK);
	}

	@PutMapping("/{projectId}")
	public ResponseEntity<DataResponse> updateProject(@PathVariable UUID userId, @Valid @PathVariable UUID projectId,
			@RequestBody ProjectRequest projectRequest) {
		EntityModel<ProjectResponse> entityModel = EntityModel
				.of(projectService.updateProject(userId, projectId, projectRequest));
		entityModel.add(linkHateoas.linkGetProject(userId, projectId));
		return new ResponseEntity<>(
				new DataResponse(HttpStatus.OK.value(), entityModel, "Project information update successful."),
				HttpStatus.OK);
	}

	@GetMapping()
	public ResponseEntity<PaginationResponse<EntityModel<ProjectResponse>>> getProjectsByUserIdByStatus(
			@PathVariable UUID userId, @RequestParam(required = false, defaultValue = "INPROGRESS") String status,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
			@RequestParam String startDate, @RequestParam String endDate) {

		Page<ProjectResponse> pageProject = projectService.getProjects(userId, status, startDate, endDate,
				PageRequest.of(page, size));

		List<EntityModel<ProjectResponse>> entityModels = pageProject.getContent().stream()
				.map(p -> EntityModel.of(p).add(linkHateoas.linkGetProject(userId, p.getId()))).toList();

		PaginationResponse<EntityModel<ProjectResponse>> paginationResponse = new PaginationResponse<>(HttpStatus.OK,
				entityModels, pageProject.getPageable().getPageNumber(), pageProject.getSize(),
				pageProject.getTotalElements(), pageProject.getTotalPages(), pageProject.getSort().isSorted(),
				pageProject.getSort().isUnsorted(), pageProject.getSort().isEmpty());

		return new ResponseEntity<>(paginationResponse, HttpStatus.OK);
	}

	@GetMapping("/GDP_Project_status")
	public ResponseEntity<DataResponse> getGDPProjectStatus(@PathVariable UUID userId,
			@RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate) {
		return new ResponseEntity<>(new DataResponse(HttpStatus.OK.value(), null,
				projectService.getGDPProjectStatus(userId, startDate, endDate)), HttpStatus.OK);
	}

//	@GetMapping()
//	public ResponseEntity<PaginationResponse<EntityModel<ProjectES>>> findProjectByUserIdAndStatusAndSearch(
//			@PathVariable UUID userId, @RequestParam(required = false, defaultValue = "PLANNING") String status,
//			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
//			@RequestParam(defaultValue = "name") String sortField,
//			@RequestParam(defaultValue = "asc") String sortDirection, @RequestParam(required = false, defaultValue = "") String search) {
//
//		Page<ProjectES> pageProjectES = projectService.findByUserIdAndStatusAndKeySearch(userId, status, search,
//				PageRequest.of(page, size, Sort.by(Direction.fromString(sortDirection), sortField)));
//
//		List<EntityModel<ProjectES>> entityModels = pageProjectES.getContent().stream()
//				.map(p -> EntityModel.of(p).add(linkHateoas.linkGetProject(userId, UUID.parseUUID(p.getId()))))
//				.toList();
//
//		PaginationResponse<EntityModel<ProjectES>> paginationResponse = new PaginationResponse<>(HttpStatus.OK,
//				entityModels, pageProjectES.getPageable().getPageNumber(), pageProjectES.getSize(),
//				pageProjectES.getTotalElements(), pageProjectES.getTotalPages(), pageProjectES.getSort().isSorted(),
//				pageProjectES.getSort().isUnsorted(), pageProjectES.getSort().isEmpty());
//
//		return new ResponseEntity<>(paginationResponse, HttpStatus.OK);
//	}

	@DeleteMapping("/{projectId}")
	public ResponseEntity<DataResponse> deleteProject(@PathVariable UUID userId, @PathVariable UUID projectId) {
		return new ResponseEntity<>(new DataResponse(HttpStatus.OK.value(),
				projectService.deleteProject(userId, projectId), "Request Successful"), HttpStatus.OK);
	}
}
