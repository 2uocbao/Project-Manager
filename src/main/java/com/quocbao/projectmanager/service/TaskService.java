package com.quocbao.projectmanager.service;

import java.time.LocalDate;
import java.util.List;

import com.quocbao.projectmanager.entity.Task;
import com.quocbao.projectmanager.payload.request.TaskRequest;
import com.quocbao.projectmanager.payload.response.TaskResponse;

public interface TaskService {

	public TaskResponse createTask(Long projectId, TaskRequest taskRequest);

	public Task getTask(Long taskId);

	public TaskResponse updateTask(Long taskId, TaskRequest taskRequest);

	public String deleteTask(Long taskId);

	public List<TaskResponse> getTaskByUserIdAndProjectIdAndStatusAndDate(Long userId, Long projectId, String status,
			LocalDate date);
}
