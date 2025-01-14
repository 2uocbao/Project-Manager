package com.quocbao.projectmanager.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

//import com.quocbao.projectmanager.elasticsearch.entity.ProjectES;
import com.quocbao.projectmanager.payload.request.ProjectRequest;
import com.quocbao.projectmanager.payload.response.ProjectResponse;

public interface ProjectService {

	public ProjectResponse createProject(UUID userId, ProjectRequest projectRequest);

	public ProjectResponse getProject(UUID userId, UUID projectId);

	public ProjectResponse updateProject(UUID userId, UUID projectId, ProjectRequest projectRequest);

	public String deleteProject(UUID userId, UUID projectId);

	public Page<ProjectResponse> getProjects(UUID userId, String status, String startDate, String endDate,
			Pageable pageable);

//	public Page<String> findByUserIdAndStatusAndKeySearch(UUID userId, String status, String keySearch,
//			Pageable pageable);

	public String getGDPProjectStatus(UUID userId, String startDate, String endDate);
}
