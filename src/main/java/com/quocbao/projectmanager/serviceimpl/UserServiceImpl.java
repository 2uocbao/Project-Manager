package com.quocbao.projectmanager.serviceimpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import jakarta.validation.ConstraintViolationException;

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
	public UserResponse updateUser(Long userId, UserRequest userRequest) {
		return userRepository.findById(userId).map(u -> {
			// if the requested data do not constant with data retrieve, check duplicate
			if (!userRequest.getPhoneNumber().equals(u.getPhoneNumber())) {
				isDuplicatePhoneNumber(userRequest.getPhoneNumber());
			}
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
		User user = getUserByPhoneNumber(loginRequest.getUsername())
				.orElseThrow(() -> new ResourceNotFoundException(USERUNCORRECT));
		if (!bCryptPasswordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
			throw new ResourceNotFoundException(USERUNCORRECT);
		}
		UserResponse userResponse = new UserResponse(user);
		userResponse.setToken(jwtTokenProvider.generateToken(user.userDetails(user)));
		return userResponse;
	}

	@Override
	public UserResponse registerUser(UserRequest userRequest) {
		isDuplicatePhoneNumber(userRequest.getPhoneNumber());
		isValidPassword(userRequest.getPassword());
		User user = User.builder().phoneNumber(userRequest.getPhoneNumber()).password(bCryptPasswordEncoder.encode(userRequest.getPassword()))
				.roles(getRoles(1L)).build();
		UserResponse userResponse = new UserResponse(userRepository.save(user));
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
	public String confirmFriendRequest(Long fromUser, Long toUser) {
		friendRepository.save(new Friend(fromUser, toUser));
		return "Successful";
	}

	@Override
	public Page<UserResponse> getFriendsByUserId(Long userId, Pageable pageable) {
		Specification<User> specUser = Specification.where(UserSpecification.propertiesByUserId(userId));
		return userRepository.findAll(specUser, pageable).map(UserResponse::new);
	}

	@Override
	public String updatePassword(LoginRequest loginRequest) {
		isValidPassword(loginRequest.getPassword());
		getUserByPhoneNumber(loginRequest.getUsername()).ifPresent(u -> {
			u.setPassword(bCryptPasswordEncoder.encode(loginRequest.getPassword()));
			userRepository.save(u);
		});
		return "Password update successful";
	}

	private Optional<User> getUserByPhoneNumber(String phoneNumber) {
		Specification<User> spec = Specification.where(UserSpecification.findByColumn(User_.phoneNumber, phoneNumber));
		return userRepository.findOne(spec);
	}

	private List<Role> getRoles(Long roleId) {
		List<Role> roles = new ArrayList<>();
		roles.add(roleRepository.findById(roleId).orElse(Role.builder().id((roleId)).build()));
		return roles;
	}

	private void isDuplicatePhoneNumber(String phoneNumber) {
		if (userRepository
				.count(Specification.where(UserSpecification.findByColumn(User_.phoneNumber, phoneNumber))) > 0) {
			throw new DuplicateException(
					"The phone number you entered is already registered. Please try a different number.");
		}
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
