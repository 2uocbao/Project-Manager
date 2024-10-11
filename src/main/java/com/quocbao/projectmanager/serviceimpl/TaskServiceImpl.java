package com.quocbao.projectmanager.serviceimpl;

import java.time.LocalDate;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.quocbao.projectmanager.common.MethodGeneral;
import com.quocbao.projectmanager.entity.Project;
import com.quocbao.projectmanager.entity.Task;
import com.quocbao.projectmanager.entity.User;
import com.quocbao.projectmanager.exception.ResourceNotFoundException;
import com.quocbao.projectmanager.payload.request.TaskRequest;
import com.quocbao.projectmanager.payload.response.TaskResponse;
import com.quocbao.projectmanager.repository.ProjectRepository;
import com.quocbao.projectmanager.repository.TaskRepository;
import com.quocbao.projectmanager.service.TaskService;
import com.quocbao.projectmanager.specification.TaskSpecification;
import com.quocbao.projectmanager.websocket.PushNotificationService;

@Service
public class TaskServiceImpl implements TaskService {

	private TaskRepository taskRepository;

	private ProjectRepository projectRepository;

	private PushNotificationService pushNotificationService;

	private MethodGeneral methodGeneral;

	public TaskServiceImpl(TaskRepository taskRepository, ProjectRepository projectRepository,
			PushNotificationService pushNotificationService, MethodGeneral methodGeneral) {
		this.taskRepository = taskRepository;
		this.projectRepository = projectRepository;
		this.pushNotificationService = pushNotificationService;
		this.methodGeneral = methodGeneral;
	}

	@Override
	public TaskResponse createTask(Long projectId, TaskRequest taskRequest) {
		Project project = projectRepository.findById(projectId)
				.orElseThrow(() -> new ResourceNotFoundException("Project not found"));

		methodGeneral.validatePermission(taskRequest.getUserId(), project.getUser().getId());

		Task task = new Task(taskRequest);
		task.setProject(project);
		task.setUser(project.getUser());
		return new TaskResponse(taskRepository.save(task));
	}

	@Override
	public Task getTask(Long taskId) {
		return taskRepository.findById(taskId).map(t -> t)
				.orElseThrow(() -> new ResourceNotFoundException("Task information can not found"));
	}

	@Override
	public TaskResponse updateTask(Long taskId, Long userId, TaskRequest taskRequest) {
		return taskRepository.findById(taskId).map(t -> {

			methodGeneral.validatePermission(userId, t.getProject().getUser().getId());

			// If true, check if admin is assigning task to member or updating task
			if (isTaskAssignedToNewUser(t, userId)) {
				assignTaskToNewUser(t, taskRequest);
				updateStatusProject(t.getProject());
			}

			return new TaskResponse(taskRepository.save(t));
		}).orElseThrow(() -> new ResourceNotFoundException("Task not found for update."));
	}

	private boolean isTaskAssignedToNewUser(Task task, Long userId) {
		return !Objects.equals(task.getUser().getId(), userId);
	}

	private void assignTaskToNewUser(Task task, TaskRequest taskRequest) {
		task.updateTask(taskRequest);
		task.setUser(User.builder().id(taskRequest.getUserId()).build());
		task.setStatus("WORKING");
		// Send push notification to the assigned user.
		pushNotificationService.pushNotification(taskRequest.getUserId(),
				"/topic/" + taskRequest.getUserId().toString() + "/notification",
				"You have a new task from project: " + task.getProject().getName(), "New Task");
	}

	private void updateStatusProject(Project project) {
		project.setStatus("INPROGRESS");
		projectRepository.save(project);
	}

	@Override
	public TaskResponse commitContentTask(Long taskId, TaskRequest taskRequest) {
		return taskRepository.findById(taskId).map(t -> {

			methodGeneral.validatePermission(taskRequest.getUserId(), t.getUser().getId());

			t.setContentSubmit(taskRequest.getContentSubmit());
			return new TaskResponse(taskRepository.save(t));
		}).orElseThrow(() -> new ResourceNotFoundException("Not found task need update."));
	}

	@Override
	public String deleteTask(Long userId, Long taskId) {
		return taskRepository.findById(taskId).map(t -> {
			methodGeneral.validatePermission(userId, t.getProject().getUser().getId());
			taskRepository.delete(Task.builder().id(taskId).build());
			return "Request Successful";
		}).orElseThrow(() -> new ResourceNotFoundException("Not found task need delete."));
	}

	@Override
	public Page<TaskResponse> getTaskByUserIdAndProjectIdAndStatusAndDate(Long userId, Long projectId, String status,
			LocalDate date, Pageable pageable) {
		Project project = projectRepository.findById(projectId)
				.orElseThrow(() -> new ResourceNotFoundException("Project not found"));

		if (Boolean.TRUE.equals(isAdminProject(userId, project))) {
			return listTasksByAdminProject(project, status, pageable).map(TaskResponse::new);
		}

		return listTasksByUser(userId, projectId, status, date, pageable).map(TaskResponse::new);
	}

	private Boolean isAdminProject(Long userId, Project project) {
		return Objects.equals(userId, project.getUser().getId());
	}

	private Page<Task> listTasksByAdminProject(Project project, String status, Pageable pageable) {
		Specification<Task> spec = Specification.where(TaskSpecification.getTaskByProjectId(project))
				.and(TaskSpecification.getTaskByStatus(status));
		return taskRepository.findAll(spec, pageable);
	}

	private Page<Task> listTasksByUser(Long userId, Long projectId, String status, LocalDate date, Pageable pageable) {
		Specification<Task> spec = Specification
				.where(TaskSpecification.getTaskByUserId(User.builder().id(userId).build())
						.and(TaskSpecification.getTaskByProjectId(Project.builder().id(projectId).build()))
						.and(TaskSpecification.getTaskByStatus(status)).and(TaskSpecification.getTaskByDate(date)));
		return taskRepository.findAll(spec, pageable);
	}
}
