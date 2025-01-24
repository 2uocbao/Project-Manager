package com.quocbao.projectmanager.serviceimpl;

import java.util.Objects;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.quocbao.projectmanager.common.MethodGeneral;
import com.quocbao.projectmanager.common.StatusEnum;
import com.quocbao.projectmanager.entity.Project;
import com.quocbao.projectmanager.entity.Task;
import com.quocbao.projectmanager.entity.User;
import com.quocbao.projectmanager.exception.ResourceNotFoundException;
import com.quocbao.projectmanager.payload.request.NotifiRequest;
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
	public TaskResponse createTask(UUID projectId, TaskRequest taskRequest) {
		Project project = projectRepository.findById(projectId)
				.orElseThrow(() -> new ResourceNotFoundException("Project not found"));
		methodGeneral.validatePermission(taskRequest.getUserId(), project.getUser().getId());
		Task task = new Task(taskRequest);
		task.setProject(project);
		task.setUser(project.getUser());
		return new TaskResponse(taskRepository.save(task));
	}

	@Override
	public TaskResponse getTask(UUID taskId) {
		Task task = taskRepository.findOne(Specification.where(TaskSpecification.getTaskDetailCustom(taskId)))
				.orElseThrow(() -> new ResourceNotFoundException("Task not found"));
		TaskResponse taskResponse = new TaskResponse(task);
		taskResponse.setUserId(task.getUser().getId());
		taskResponse.setUsername(task.getUser().getFirstName() + " " + task.getUser().getLastName());
		return taskResponse;
	}

	@Override
	public TaskResponse updateTask(UUID taskId, UUID userId, TaskRequest taskRequest) {
		return taskRepository.findById(taskId).map(t -> {
			t.updateTask(taskRequest);
			return new TaskResponse(taskRepository.save(t));
		}).orElseThrow(() -> new ResourceNotFoundException("Task not found for update."));
	}

	@Override
	public TaskResponse commitContentTask(UUID taskId, TaskRequest taskRequest) {
		return taskRepository.findById(taskId).map(t -> {
			// Make sure the user in charge of the task
			methodGeneral.validatePermission(taskRequest.getUserId(), t.getUser().getId());
			t.setContentSubmit(taskRequest.getContentSubmit());
			t.setStatus(StatusEnum.DONE);
			return new TaskResponse(taskRepository.save(t));
		}).orElseThrow(() -> new ResourceNotFoundException("Not found task need update."));
	}

	@Override
	public String deleteTask(UUID userId, UUID taskId) {
		return taskRepository.findById(taskId).map(t -> {
			// Make sure the user is an administrator of the project
			methodGeneral.validatePermission(userId, t.getProject().getUser().getId());
			taskRepository.delete(Task.builder().id(taskId).build());
			return "Request Successful";
		}).orElseThrow(() -> new ResourceNotFoundException("Not found task need delete."));
	}

	@Override
	public Page<TaskResponse> getTasks(UUID userId, UUID projectId, String status, Pageable pageable) {
		Project project = projectRepository.findById(projectId)
				.orElseThrow(() -> new ResourceNotFoundException("This project does not exist"));
		if (Boolean.TRUE.equals(isAdminProject(userId, project.getUser().getId()))) {
			return listTasksByAdminProject(projectId, status, pageable).map(TaskResponse::new);
		}
		return listTasksByUser(userId, projectId, status, pageable).map(TaskResponse::new);
	}

	@Override
	public String getGDPTask(UUID userId, UUID projectId) {
		Specification<Task> spec;
		spec = byStatus(userId, projectId, StatusEnum.DONE);
		long done = taskRepository.count(spec);
		spec = byStatus(userId, projectId, StatusEnum.PENDING);
		long pending = taskRepository.count(spec);
		spec = byStatus(userId, projectId, StatusEnum.INPROGRESS);
		long todo = taskRepository.count(spec);
		return "Done: " + done + "pending: " + pending + "todo: " + todo;
	}

	@Override
	public void assignToUser(UUID taskId, UUID userId, UUID toUserId) {
		taskRepository.findById(taskId).ifPresent(task -> {
			methodGeneral.validatePermission(userId, task.getProject().getUser().getId());
			if (userId.equals(toUserId)) {
				task.setUser(User.builder().id(userId).build());
				task.setStatus(StatusEnum.PENDING);
				taskRepository.save(task);
			}
			assignTaskToNewUser(task, toUserId);
		});
	}

	private Specification<Task> byStatus(UUID userId, UUID projectId, StatusEnum status) {
		UUID userIdAdminProject = projectRepository.findById(projectId)
				.orElseThrow(() -> new ResourceNotFoundException("This project does not exist")).getUser().getId();

		if (Boolean.TRUE.equals(isAdminProject(userId, userIdAdminProject))) {
			return Specification.where(TaskSpecification.getTaskByProjectId(projectId))
					.and(TaskSpecification.getTaskByStatus(status.toString()));
		}
		return Specification.where(TaskSpecification.getTaskByUserId(userId))
				.and(TaskSpecification.getTaskByProjectId(projectId))
				.and(TaskSpecification.getTaskByStatus(status.toString()));
	}

	private Task assignTaskToNewUser(Task task, UUID toUserId) {
		task.setUser(User.builder().id(toUserId).build());
		task.setStatus(StatusEnum.INPROGRESS);
		taskRepository.save(task);
		// Send notification to the assigned user.
		pushNotificationService.pushNotification(NotifiRequest.builder().receiver(toUserId).type("PROJECT")
				.from(task.getProject().getName()).sender(task.getProject().getId()).build());
		return task;
	}

	private Boolean isAdminProject(UUID userId, UUID project) {
		return Objects.equals(userId, project);
	}

	private Page<Task> listTasksByAdminProject(UUID project, String status, Pageable pageable) {
		Specification<Task> spec = Specification.where(TaskSpecification.getTaskByProjectId(project))
				.and(TaskSpecification.getTaskByStatus(status));
		return taskRepository.findAll(spec, pageable);
	}

	private Page<Task> listTasksByUser(UUID userId, UUID projectId, String status, Pageable pageable) {
		Specification<Task> spec;

		spec = Specification.where(TaskSpecification.getTaskByUserId(userId)
				.and(TaskSpecification.getTaskByProjectId(projectId)).and(TaskSpecification.getTaskByStatus(status)));

		return taskRepository.findAll(spec, pageable);
	}
}
