package com.quocbao.projectmanager.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.quocbao.projectmanager.elasticsearch.entity.ProjectES;
import com.quocbao.projectmanager.payload.request.ProjectRequest;
import com.quocbao.projectmanager.payload.response.ProjectResponse;

public interface ProjectService {

	public ProjectResponse createProject(Long userId, ProjectRequest projectRequest);

	public ProjectResponse getProject(Long userId, Long projectId);

	public ProjectResponse updateProject(Long userId, Long projectId, ProjectRequest projectRequest);

	public String deleteProject(Long userId, Long projectId);

	public Page<ProjectResponse> getProjectsByUserIdAndStatus(Long userId, String status, String keySearch,
			Pageable pageable);

	public Page<ProjectES> findByUserIdAndStatusAndKeySearch(Long userId, String status, String keySearch,
			Pageable pageable);
}
