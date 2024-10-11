package com.quocbao.projectmanager.serviceimpl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.quocbao.projectmanager.common.MethodGeneral;
import com.quocbao.projectmanager.elasticsearch.entity.ProjectES;
import com.quocbao.projectmanager.elasticsearch.repository.ProjectESRepository;
import com.quocbao.projectmanager.entity.Project;
import com.quocbao.projectmanager.entity.Role;
import com.quocbao.projectmanager.entity.User;
import com.quocbao.projectmanager.exception.ResourceNotFoundException;
import com.quocbao.projectmanager.payload.request.ProjectRequest;
import com.quocbao.projectmanager.payload.response.ProjectResponse;
import com.quocbao.projectmanager.repository.ProjectRepository;
import com.quocbao.projectmanager.repository.RoleRepository;
import com.quocbao.projectmanager.repository.UserRepository;
import com.quocbao.projectmanager.service.ProjectService;
import com.quocbao.projectmanager.specification.ProjectSpecification;

@Service
public class ProjectServiceImpl implements ProjectService {

	private ProjectRepository projectRepository;
	private ProjectESRepository projectESRepository;
	private UserRepository userRepository;
	private RoleRepository roleRepository;
	private MethodGeneral methodGeneral;

	public ProjectServiceImpl(ProjectRepository projectRepository, UserRepository userRepository,
			RoleRepository roleRepository, MethodGeneral methodGeneral, ProjectESRepository projectESRepository) {
		this.projectRepository = projectRepository;
		this.projectESRepository = projectESRepository;
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.methodGeneral = methodGeneral;
	}

	@Override
	public ProjectResponse createProject(Long userId, ProjectRequest projectRequest) {
		User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
		Project project = new Project(projectRequest);
		project.setUser(user);
		assignRoleIfNotPresent(user);
		project = projectRepository.save(project);
		projectESRepository.save(new ProjectES(project));
		return new ProjectResponse(project);
	}

	private void assignRoleIfNotPresent(User user) {
		List<Role> roles = user.getRoles();
		Role role = roleRepository.findById(2L).orElseThrow(() -> new ResourceNotFoundException("Role not found"));
		if (!roles.contains(role)) {
			user.getRoles().add(role);
			userRepository.save(user);
		}
	}

	@Override
	public ProjectResponse getProject(Long userId, Long projectId) {
		return projectRepository.findById(projectId).map(ProjectResponse::new)
				.orElseThrow(() -> new ResourceNotFoundException("Project not found"));
	}

	@Override
	public ProjectResponse updateProject(Long userId, Long projectId, ProjectRequest projectRequest) {
		return projectRepository.findById(projectId).map(p -> {
			methodGeneral.validatePermission(userId, p.getUser().getId());
			p = p.updateProject(projectRequest);
			projectRepository.save(p);
			updateProjectES(p);
			return new ProjectResponse(p);
		}).orElseThrow(() -> new ResourceNotFoundException("Project not found"));
	}

	private void updateProjectES(Project project) {
		ProjectES projectES = projectESRepository.findById(project.getId().toString()).get();
		projectESRepository.save(projectES.updateProjectES(project));
	}

	@Override
	public String deleteProject(Long userId, Long projectId) {
		Project project = projectRepository.findById(projectId)
				.orElseThrow(() -> new ResourceNotFoundException("Project not found"));
		methodGeneral.validatePermission(userId, project.getUser().getId());
		projectRepository.delete(project);
		deleteProjectES(projectId);
		return "Delete Successfully";
	}

	private void deleteProjectES(Long projectId) {
		ProjectES projectES = projectESRepository.findById(projectId.toString()).get();
		projectESRepository.delete(projectES);
	}

	@Override
	public Page<ProjectResponse> getProjectsByUserIdAndStatus(Long userId, String status, String keySearch,
			Pageable pageable) {
		Specification<Project> spec;
		if (keySearch != null && !keySearch.isEmpty()) {
			spec = Specification.where(ProjectSpecification.findByUserId(User.builder().id(userId).build()))
					.or(ProjectSpecification.findProject(User.builder().id(userId).build()))
					.and(ProjectSpecification.findByStatus(status)).and(ProjectSpecification.search(keySearch));
		} else {
			spec = Specification.where(ProjectSpecification.findByUserId(User.builder().id(userId).build()))
					.or(ProjectSpecification.findProject(User.builder().id(userId).build()))
					.and(ProjectSpecification.findByStatus(status));
		}
		return projectRepository.findAll(spec, pageable).map(ProjectResponse::new);
	}

	@Override
	public Page<ProjectES> findByUserIdAndStatusAndKeySearch(Long userId, String status, String keySearch,
			Pageable pageable) {
		if(keySearch == null || keySearch.isEmpty()) {
			return projectESRepository.findByUserIdAndStatusAndPageable(userId.toString(), status, pageable);
		}
		return projectESRepository.findByUserIdAndStatusAndPageable(userId.toString(), status, keySearch, pageable);
	}
}
