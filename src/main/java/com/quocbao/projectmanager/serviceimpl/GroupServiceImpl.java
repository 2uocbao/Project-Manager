package com.quocbao.projectmanager.serviceimpl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.quocbao.projectmanager.common.RoleEnum;
import com.quocbao.projectmanager.entity.Group;
import com.quocbao.projectmanager.entity.Member;
import com.quocbao.projectmanager.entity.Role;
import com.quocbao.projectmanager.entity.User;
import com.quocbao.projectmanager.exception.ResourceNotFoundException;
import com.quocbao.projectmanager.payload.response.GroupResponse;
import com.quocbao.projectmanager.payload.response.UserResponse;
import com.quocbao.projectmanager.repository.GroupRepository;
import com.quocbao.projectmanager.repository.MemberRepository;
import com.quocbao.projectmanager.repository.RoleRepository;
import com.quocbao.projectmanager.repository.UserRepository;
import com.quocbao.projectmanager.service.GroupService;
import com.quocbao.projectmanager.specification.MemberSpecification;
import com.quocbao.projectmanager.specification.RoleSpecification;

import jakarta.persistence.Tuple;
import jakarta.transaction.Transactional;

@Service
public class GroupServiceImpl implements GroupService {

	private GroupRepository groupRepository;
	private MemberRepository memberRepository;
	private UserRepository userRepository;
	private RoleRepository roleRepository;

	public GroupServiceImpl(GroupRepository groupRepository, MemberRepository memberRepository,
			RoleRepository roleRepository, UserRepository userRepository) {
		this.groupRepository = groupRepository;
		this.memberRepository = memberRepository;
		this.roleRepository = roleRepository;
		this.userRepository = userRepository;
	}

	@Override
	@Transactional
	public GroupResponse createGroup(UUID userId, UUID withUser) {
		assignRoleIfNotPresent(User.builder().id(userId).build());
		Set<Member> members = new HashSet<>();
		Group group = groupRepository.save(new Group("null", members));
		members.add(new Member(group.getId(), userId));
		members.add(new Member(group.getId(), withUser));
		return new GroupResponse(group);
	}

	// Optimize, JPASpecificationExcuter
	@Override
	public Page<GroupResponse> getGroupsByUserId(UUID userId, Pageable pageable, String keySearch) {
		Page<Tuple> result = groupRepository.getGroupsOfUser(userId.toString(), pageable);
		return result.map(
				t -> new GroupResponse((String) t.get(0), (String) t.get(1), (String) t.get(2), (Timestamp) t.get(3)));
	}

	@Override
	public Group updateGroup(UUID userId, UUID groupId, String name) {
		Group group = groupRepository.findById(groupId)
				.orElseThrow(() -> new ResourceNotFoundException("Request failed."));
		Specification<Member> specification;
		List<UUID> groupIds = new ArrayList<>();
		groupIds.add(groupId);
		specification = MemberSpecification.findMemberByGroupId(groupIds)
				.and(MemberSpecification.findMemberByUserId(userId));
		Member member = memberRepository.findOne(specification).get();
		if (member.getUser().getRoles().stream().anyMatch(role -> role.getName().equals(RoleEnum.ADMIN.toString()))) {
			group.setName(name);
			return groupRepository.save(group);
		}
		return null;

	}

	@Override
	public String deleteGroup(UUID userId, UUID groupId) {
		Group group = groupRepository.findById(groupId)
				.orElseThrow(() -> new ResourceNotFoundException("Request failed."));
		Specification<Member> specification;
		List<UUID> groupIds = new ArrayList<>();
		groupIds.add(groupId);
		specification = MemberSpecification.findMemberByGroupId(groupIds)
				.and(MemberSpecification.findMemberByUserId(userId));
		Member member = memberRepository.findOne(specification).get();
		if (member.getUser().getRoles().stream().anyMatch(role -> role.getName().equals(RoleEnum.ADMIN.toString()))) {
			groupRepository.delete(group);
			return "Successful";
		}
		return null;
	}

	@Override
	public String addUserToGroup(UUID userId, UUID memberId, UUID groupId) {
		Specification<Member> specification;
		List<UUID> groupIds = new ArrayList<>();
		groupIds.add(groupId);
		specification = MemberSpecification.findMemberByGroupId(groupIds)
				.and(MemberSpecification.findMemberByUserId(memberId));
		if (!memberRepository.findOne(specification).isEmpty()) {
			return "Successful";
		}
		specification = MemberSpecification.findMemberByGroupId(groupIds)
				.and(MemberSpecification.findMemberByUserId(userId));
		Member member = memberRepository.findOne(specification).get();
		if (member.getUser().getRoles().stream().anyMatch(role -> role.getName().equals(RoleEnum.ADMIN.toString()))) {
			memberRepository.save(new Member(groupId, memberId));
			return "Successful";
		}
		return "Request failed";
	}

	@Override
	public String removeUserInGroup(UUID userId, UUID memberId, UUID groupId) {
		Specification<Member> specification;
		List<UUID> groupIds = new ArrayList<>();
		groupIds.add(groupId);
		specification = MemberSpecification.findMemberByGroupId(groupIds)
				.and(MemberSpecification.findMemberByUserId(userId));
		Member member = memberRepository.findOne(specification).get();
		if (member.getUser().getRoles().stream().anyMatch(role -> role.getName().equals(RoleEnum.ADMIN.toString()))) {
			memberRepository.delete(MemberSpecification.findMemberByUserId(memberId));
			return "Successful";
		}
		return "Request failed";
	}

	@Override
	public Page<UserResponse> getUsersInGroup(UUID groupId, Pageable pageable, String keySearch) {
		Specification<Member> specification;
		List<UUID> groupIds = new ArrayList<>();
		groupIds.add(groupId);
		specification = MemberSpecification.findMemberByGroupId(groupIds);
		Page<Member> members = memberRepository.findAll(specification, pageable);
		Page<User> users = members.map(Member::getUser);
		return users.map(UserResponse::new);
	}

	@Override
	public Page<UserResponse> getUsersConnect(UUID userId, Pageable pageable, String keySearch) {
		Specification<Member> specification = MemberSpecification.findMemberByUserId(userId);
		Page<Member> members = memberRepository.findAll(specification, pageable);
		List<UUID> groupIds = members.stream().map(Member::getGroup).map(Group::getId).distinct().toList();
		Specification<Member> groupMembersSpecification = MemberSpecification.findMemberByGroupId(groupIds)
				.and(MemberSpecification.excludeUserId(userId));
		Page<Member> groupMembers = memberRepository.findAll(groupMembersSpecification, pageable);
		return groupMembers.map(t -> new UserResponse(t.getUser()));
	}

	private void assignRoleIfNotPresent(User user) {

		User userInfo = userRepository.findById(user.getId()).get();
		List<Role> roles = userInfo.getRoles();

		Role role = roleRepository.findOne(Specification.where(RoleSpecification.findByName(RoleEnum.ADMIN.toString())))
				.orElseThrow(() -> new ResourceNotFoundException("Role not found"));

		Boolean isExistRole = false;
		for (Role r : roles) {
			if (r.getName().contains(role.getName())) {
				isExistRole = true;
				break;
			}
		}

		if (Boolean.FALSE.equals(isExistRole)) {
			userInfo.getRoles().add(role);
		}

	}

}
