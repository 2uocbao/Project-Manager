package com.quocbao.projectmanager.serviceimpl;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.quocbao.projectmanager.common.ConvertData;
import com.quocbao.projectmanager.common.MethodGeneral;
import com.quocbao.projectmanager.common.RoleEnum;
import com.quocbao.projectmanager.common.StatusEnum;
//import com.quocbao.projectmanager.elasticsearch.entity.ProjectES;
//import com.quocbao.projectmanager.elasticsearch.repository.ProjectESRepository;
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
import com.quocbao.projectmanager.specification.RoleSpecification;

@Service
public class ProjectServiceImpl implements ProjectService {

	private ProjectRepository projectRepository;

//	private ProjectESRepository projectESRepository;

	private UserRepository userRepository;

	private RoleRepository roleRepository;

	private MethodGeneral methodGeneral;

	public ProjectServiceImpl(ProjectRepository projectRepository, UserRepository userRepository,
			RoleRepository roleRepository, MethodGeneral methodGeneral
//			, ProjectESRepository projectESRepository
	) {
		this.projectRepository = projectRepository;
//		this.projectESRepository = projectESRepository;
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.methodGeneral = methodGeneral;
	}

	@Override
	public ProjectResponse createProject(UUID userId, ProjectRequest projectRequest) {
		User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
		Project project = new Project(projectRequest);
		project.setUser(user);
		assignRoleIfNotPresent(user);
		project = projectRepository.save(project);
//		projectESRepository.save(new ProjectES(project));
		return new ProjectResponse(project);
	}

	@Override
	public ProjectResponse getProject(UUID userId, UUID projectId) {
		return new ProjectResponse(retrieveProject(projectId));
	}

	@Override
	public ProjectResponse updateProject(UUID userId, UUID projectId, ProjectRequest projectRequest) {
		return projectRepository.findById(projectId).map(p -> {
			methodGeneral.validatePermission(userId, p.getUser().getId());
			p = p.updateProject(projectRequest);
			projectRepository.save(p);
//			updateProjectES(p);
			return new ProjectResponse(p);
		}).orElseThrow(() -> new ResourceNotFoundException("Project not found"));
	}

	@Override
	public String deleteProject(UUID userId, UUID projectId) {
		Project project = retrieveProject(projectId);
		methodGeneral.validatePermission(userId, project.getUser().getId());
		projectRepository.delete(project);
//		deleteProjectES(projectId);
		return "Delete Successfully";
	}

	@Override
	public Page<ProjectResponse> getProjects(UUID userId, String status, String startDate, String endDate,
			Pageable pageable) {
		Timestamp start = ConvertData.toTimestamp(startDate);
		Timestamp end = ConvertData.toTimestamp(endDate);
		Specification<Project> spec;

		spec = Specification.where(ProjectSpecification.findByUserId(userId))
				.or(ProjectSpecification.findProject(userId)).and(ProjectSpecification.findByStatus(status))
				.and(ProjectSpecification.byDate(start, end));

		return projectRepository.findAll(spec, pageable).map(ProjectResponse::new);
	}

	@Override
	public String getGDPProjectStatus(UUID userId, String startDate, String endDate) {
		Timestamp start = ConvertData.toTimestamp(startDate);
		Timestamp end = ConvertData.toTimestamp(endDate);
		Specification<Project> spec;
		spec = byStatus(userId, StatusEnum.DONE).and(ProjectSpecification.byDate(start, end));
		long done = projectRepository.count(spec);
		spec = byStatus(userId, StatusEnum.PENDING).and(ProjectSpecification.byDate(start, end));
		long pending = projectRepository.count(spec);
		spec = byStatus(userId, StatusEnum.INPROGRESS).and(ProjectSpecification.byDate(start, end));
		long todo = projectRepository.count(spec);

		return "Done: " + done + "pending: " + pending + "todo: " + todo;
	}

	private Specification<Project> byStatus(UUID userId, StatusEnum status) {
		return Specification.where(ProjectSpecification.findByUserId(userId))
				.or(ProjectSpecification.findProject(userId)).and(ProjectSpecification.findByStatus(status.toString()));
	}
//	@Override
//	public Page<String> findByUserIdAndStatusAndKeySearch(UUID userId, String status, String keySearch,
//			Pageable pageable) {
//		return null;
//		if(keySearch == null || keySearch.isEmpty()) {
//			return projectESRepository.findByUserIdAndStatusAndPageable(userId.toString(), status, pageable);
//		}
//		return projectESRepository.findByUserIdAndStatusAndPageable(userId.toString(), status, keySearch, pageable);
//	}

	private Project retrieveProject(UUID projectId) {
		return projectRepository.findById(projectId)
				.orElseThrow(() -> new ResourceNotFoundException("Project not found"));
	}

	private void assignRoleIfNotPresent(User user) {
		List<Role> roles = user.getRoles();
		Role role = roleRepository
				.findOne(Specification.where(RoleSpecification.findByName(RoleEnum.MANAGER.toString())))
				.orElseThrow(() -> new ResourceNotFoundException("Role not found"));
		if (!roles.contains(role)) {
			user.getRoles().add(role);
			userRepository.save(user);
		}
	}

//	private void updateProjectES(Project project) {
//	ProjectES projectES = projectESRepository.findById(project.getId().toString()).get();
//	projectESRepository.save(projectES.updateProjectES(project));
//}

//	private void deleteProjectES(UUID projectId) {
//	ProjectES projectES = projectESRepository.findById(projectId.toString()).get();
//	projectESRepository.delete(projectES);
//}
}
