package com.quocbao.projectmanager.service;

import java.util.List;

import com.quocbao.projectmanager.payload.request.ProjectRequest;
import com.quocbao.projectmanager.payload.response.ProjectResponse;

public interface ProjectService {

	public ProjectResponse createProject(Long userId, ProjectRequest projectRequest);

	public ProjectResponse getProject(Long userId, Long projectId);

	public ProjectResponse updateProject(Long projectId, ProjectRequest projectRequest);

	public String deleteProject(Long projectId);

	public List<ProjectResponse> getProjectsByUserIdAndStatus(Long userId, String status);
}
