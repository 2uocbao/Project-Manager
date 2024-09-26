package com.quocbao.projectmanager.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.quocbao.projectmanager.payload.request.ProjectRequest;
import com.quocbao.projectmanager.payload.response.ProjectResponse;

@Service
public interface ProjectService {

	public void createProject(ProjectRequest projectRequest);

	public ProjectResponse getProject(Long projectId);

	public void updateProject(Long projectId, ProjectRequest projectRequest);

	public void deleteProject(Long projectId);

	public List<ProjectResponse> getAllProjects(Long userId);

	public List<ProjectResponse> getProjectsByUserIdAndStatus(Long userId, String status);
}
