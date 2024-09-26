package com.quocbao.projectmanager.serviceimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.quocbao.projectmanager.entity.Role;
import com.quocbao.projectmanager.entity.User;
import com.quocbao.projectmanager.entity.Friend;
import com.quocbao.projectmanager.exception.DuplicateException;
import com.quocbao.projectmanager.exception.ResourceNotFoundException;
import com.quocbao.projectmanager.payload.request.LoginRequest;
import com.quocbao.projectmanager.payload.request.UserRequest;
import com.quocbao.projectmanager.payload.response.UserResponse;
import com.quocbao.projectmanager.repository.RoleRepository;
import com.quocbao.projectmanager.repository.FriendRepository;
import com.quocbao.projectmanager.repository.UserRepository;
import com.quocbao.projectmanager.security.jwt.JwtTokenProvider;
import com.quocbao.projectmanager.service.UserService;
import com.quocbao.projectmanager.specification.UserSpecification;

import jakarta.transaction.Transactional;

@Service
public class UserServiceImpl implements UserService {

	private static final String USERUNCORRECT = "Incorrect username or password";

	private UserRepository userRepository;
	private RoleRepository roleRepository;
	private FriendRepository friendRepository;
	private JwtTokenProvider jwtTokenProvider;
	private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

	public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
			JwtTokenProvider jwtTokenProvider, FriendRepository friendRepository) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.jwtTokenProvider = jwtTokenProvider;
		this.friendRepository = friendRepository;
	}

	@Override
	public UserResponse getUser(Long userId) {
		return userRepository.findById(userId).map(UserResponse::new)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with id:" + userId.toString()));
	}

	@Override
	@Transactional
	public UserResponse updateUser(Long userId, UserRequest userRequest) {
		userRepository.findById(userId).ifPresent(u -> {
			u.updateUser(userRequest);
			userRepository.save(u);
		});
		UserResponse userResponse = new UserResponse(new User(userRequest));
		userResponse.setId(userId);
		return userResponse;
	}

	@Override
	@Transactional
	public UserResponse loginUser(LoginRequest loginRequest) {
		User user = getUserByPhoneNumber(loginRequest.getUsername())
				.orElseThrow(() -> new ResourceNotFoundException(USERUNCORRECT));
		if (!bCryptPasswordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
			throw new ResourceNotFoundException(USERUNCORRECT);
		}
		loadUserByUsername(loginRequest.getUsername());
		UserResponse userResponse = new UserResponse(user);
		userResponse.setToken(jwtTokenProvider.generateToken(user.userDetails(user)));
		return userResponse;
	}

	@Override
	@Transactional
	public UserResponse registerUser(LoginRequest loginRequest) {
		if (userRepository
				.count(Specification.where(UserSpecification.hasPhoneNumber(loginRequest.getUsername()))) > 0) {
			throw new DuplicateException("Username already exists");
		}
		User user = new User();
		user.setPhoneNumber(loginRequest.getUsername());
		user.setPassword(bCryptPasswordEncoder.encode(loginRequest.getPassword()));
		user.setRoles(getRoles((long) 1));
		userRepository.save(user);
		UserResponse userResponse = new UserResponse(user);
		userResponse.setToken(jwtTokenProvider.generateToken(user.userDetails(user)));
		return userResponse;
	}

	@Override
	@Transactional
	@Cacheable(value = "username", key = "#username")
	public UserDetails loadUserByUsername(String username) {
		User user = getUserByPhoneNumber(username).orElseThrow(() -> new ResourceNotFoundException(USERUNCORRECT));
		return user.userDetails(user);
	}

	@Override
	@Transactional
	public String confirmFriendRequest(Long fromUser, Long toUser) {
		friendRepository.save(new Friend(fromUser, toUser));
		return "Success";
	}

	@Override
	public List<UserResponse> getFriendsByUserId(Long userId) {
		Specification<User> specUser = Specification.where(UserSpecification.propertiesByUserId(userId));
		List<User> users = userRepository.findAll(specUser);
		return users.stream().map(UserResponse::new).toList();
	}

	private Optional<User> getUserByPhoneNumber(String phoneNumber) {
		Specification<User> spec = Specification.where(UserSpecification.hasPhoneNumber(phoneNumber));
		return userRepository.findOne(spec);
	}

	private List<Role> getRoles(Long roleId) {
		List<Role> roles = new ArrayList<>();
		roles.add(roleRepository.findById(roleId).orElse(Role.builder().id((roleId)).build()));
		return roles;
	}

}
