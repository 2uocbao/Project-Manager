package com.quocbao.projectmanager.entity;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.quocbao.projectmanager.payload.request.UserRequest;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Entity
@Getter
@Table(name = "user")
@Setter
@AllArgsConstructor
public class User implements UserDetails {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(name = "first_name")
	private String firstName;

	@Column(name = "last_name")
	private String lastName;

	@Column(name = "email")
	@Email(message = "Invalid email format")
	private String email;

	@Column(name = "password")
	private String password;

	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
	@JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	private List<Role> roles;

	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private List<Member> member;

	public User() {

	}

	public User(UserRequest userRequest) {
		this.firstName = userRequest.getFirstName();
		this.lastName = userRequest.getLastName();
		this.email = userRequest.getEmail();
	}

	public User updateUser(UserRequest userRequest) {
		this.firstName = userRequest.getFirstName();
		this.lastName = userRequest.getLastName();
		this.email = userRequest.getEmail();
		return this;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return roles.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName())).toList();
	}

	@Override
	public String getUsername() {
		return this.email;
	}

	public UserDetails userDetails(User user) {
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
				user.getAuthorities());
	}
}