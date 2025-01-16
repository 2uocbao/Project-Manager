package com.quocbao.projectmanager.serviceimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.quocbao.projectmanager.entity.Role;
import com.quocbao.projectmanager.entity.User;
import com.quocbao.projectmanager.entity.User_;
import com.quocbao.projectmanager.exception.DuplicateException;
import com.quocbao.projectmanager.exception.ResourceNotFoundException;
import com.quocbao.projectmanager.payload.request.LoginRequest;
import com.quocbao.projectmanager.payload.request.UserRequest;
import com.quocbao.projectmanager.payload.response.UserResponse;
import com.quocbao.projectmanager.repository.RoleRepository;
import com.quocbao.projectmanager.repository.UserRepository;
import com.quocbao.projectmanager.security.jwt.JwtTokenProvider;
import com.quocbao.projectmanager.service.UserService;
import com.quocbao.projectmanager.specification.UserSpecification;

import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;

@Service
@Transactional
public class UserServiceImpl implements UserService {

	private static final String USERUNCORRECT = "Incorrect username or password";

	private UserRepository userRepository;

	private RoleRepository roleRepository;
	private JwtTokenProvider jwtTokenProvider;
	private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

	public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository,
			JwtTokenProvider jwtTokenProvider) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@Override
	public UserResponse getUser(UUID userId) {
		return userRepository.findById(userId).map(UserResponse::new)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with id:" + userId.toString()));
	}

	@Override
	public UserResponse updateUser(UUID userId, UserRequest userRequest) {
		return userRepository.findById(userId).map(u -> {
			if (!userRequest.getEmail().equals(u.getEmail())) {
				isDuplicateEmail(userRequest.getEmail());
			}
			u.updateUser(userRequest);
			userRepository.save(u);
			return new UserResponse(u);
		}).orElseThrow(() -> new ResourceNotFoundException("User not found with id:" + userId.toString()));
	}

	@Override
	public UserResponse loginUser(LoginRequest loginRequest) {
		User user = getUserByEmail(loginRequest.getUsername())
				.orElseThrow(() -> new ResourceNotFoundException(USERUNCORRECT));
		if (!bCryptPasswordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
			throw new ResourceNotFoundException(USERUNCORRECT);
		}
		UserResponse userResponse = new UserResponse(user);
		userResponse.setToken(jwtTokenProvider.generateToken(user.userDetails(user)));
		return userResponse;
	}

	@Override
	@Transactional
	public UserResponse registerUser(UserRequest userRequest) {
		isDuplicateEmail(userRequest.getEmail());
		isValidPassword(userRequest.getPassword());
		User user = new User(userRequest);
		user.setPassword(bCryptPasswordEncoder.encode(userRequest.getPassword()));
		user.setRoles(getRoles(1L));
		UserResponse userResponse = new UserResponse(userRepository.save(user));
		userResponse.setToken(jwtTokenProvider.generateToken(user.userDetails(user)));
		return userResponse;
	}

	@Override
	@Transactional
	@Cacheable(value = "username", key = "#username")
	public UserDetails loadUserByUsername(String username) {
		if(username.contains("@gmail.com")) {
			Optional<User> user = userRepository.findOne(Specification.where(UserSpecification.findByColumn(User_.email, username)));
			if(user.isEmpty()) {
				return null;
			}
			return new User().userDetails(user.get());
		}
		User user = getUserByEmail(username).orElseThrow(() -> new ResourceNotFoundException(USERUNCORRECT));
		return user.userDetails(user);
	}

	@Override
	public UserResponse updatePassword(LoginRequest loginRequest) {
		isValidPassword(loginRequest.getPassword());
		getUserByEmail(loginRequest.getUsername()).map(u -> {
			if(!bCryptPasswordEncoder.matches(loginRequest.getPassword(), u.getPassword())) {
				throw new ResourceNotFoundException("Incorrect password");
			}
			u.setPassword(bCryptPasswordEncoder.encode(loginRequest.getNewPassword()));
			userRepository.save(u);
			return null;
		});
		return null;
	}

	@Override
	public Page<UserResponse> searchUser(UUID userId, String keySearch, Pageable pageable) {
		if (!keySearch.isEmpty()) {
			keySearch += "%";
		}
		Specification<User> spec = Specification.where(UserSpecification.searchUser(User_.firstName, keySearch))
				.or(UserSpecification.searchUser(User_.lastName, keySearch))
				.or(UserSpecification.searchUser(User_.email, keySearch)).and(UserSpecification.excludeUserId(userId))
				.and(UserSpecification.searchUser(userId, false));
		Page<User> users = userRepository.findAll(spec, pageable);
		return users.map(UserResponse::new);
	}

	@Override
	public Page<UserResponse> findFriend(UUID userId, String keySearch, Pageable pageable) {

		keySearch += "%";

		Specification<User> spec = Specification.where(UserSpecification.searchUser(User_.firstName, keySearch))
				.or(UserSpecification.searchUser(User_.lastName, keySearch))
				.or(UserSpecification.searchUser(User_.email, keySearch)).and(UserSpecification.excludeUserId(userId))
				.and(UserSpecification.searchUser(userId, true));
		Page<User> users = userRepository.findAll(spec, pageable);
		return users.map(UserResponse::new);
	}

	private Optional<User> getUserByEmail(String email) {
		Specification<User> spec = Specification.where(UserSpecification.findByColumn(User_.email, email));
		return userRepository.findOne(spec);
	}

	private List<Role> getRoles(Long roleId) {
		List<Role> roles = new ArrayList<>();
		roles.add(roleRepository.findById(roleId).orElse(Role.builder().id((roleId)).build()));
		return roles;
	}

	private void isDuplicateEmail(String email) {
		if (email != null
				&& userRepository.count(Specification.where(UserSpecification.findByColumn(User_.email, email))) > 0) {
			throw new DuplicateException("The email you entered is already registered. Please try a different email.");
		}

	}

	private void isValidPassword(String password) {
		// Regular Expression:
		// Minimum length of 8 characters: {8,}
		// At least one uppercase letter: (?=.*[A-Z])
		// At least one lowercase letter: (?=.*[a-z])
		// At least one digit
		// At least one special character (@$!%*?&).
		String regex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
		if (!password.matches(regex)) {
			// Set the message for this to be received in the exception handler.
			// Because can not add message to set constraint violation.
			throw new ConstraintViolationException("Password cannot be null or contain special characters", null);
		}
	}
}
