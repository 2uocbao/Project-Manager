package com.quocbao.projectmanager.service;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.quocbao.projectmanager.entity.Task;
import com.quocbao.projectmanager.payload.request.TaskRequest;
import com.quocbao.projectmanager.payload.response.TaskResponse;

public interface TaskService {

	public TaskResponse createTask(Long projectId, TaskRequest taskRequest);

	public Task getTask(Long taskId);

	public TaskResponse updateTask(Long taskId, Long userId, TaskRequest taskRequest);

	public TaskResponse commitContentTask(Long taskId, TaskRequest taskRequest);

	public String deleteTask(Long userId, Long taskId);

	public Page<TaskResponse> getTaskByUserIdAndProjectIdAndStatusAndDate(Long userId, Long projectId, String status,
			LocalDate date, Pageable pageable);
}
