package com.quocbao.projectmanager.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.quocbao.projectmanager.payload.request.TaskRequest;
import com.quocbao.projectmanager.payload.response.TaskResponse;

public interface TaskService {

	public TaskResponse createTask(UUID projectId, TaskRequest taskRequest);

	public TaskResponse getTask(UUID taskId);

	public TaskResponse updateTask(UUID taskId, UUID userId, TaskRequest taskRequest);

	public TaskResponse commitContentTask(UUID taskId, TaskRequest taskRequest);

	public String deleteTask(UUID userId, UUID taskId);

	public Page<TaskResponse> getTasks(UUID userId, UUID projectId, String status,
			Pageable pageable);
	
	public String getGDPTask(UUID userId, UUID projectId);
	
	public void assignToUser(UUID taskId, UUID userId, UUID toUserId);
}
