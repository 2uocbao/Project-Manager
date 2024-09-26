package com.quocbao.projectmanager.entity;

import java.io.Serializable;
import java.util.List;

import com.quocbao.projectmanager.payload.request.RoleRequest;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Entity
@Getter
@Table(name = "role")
@Setter
public class Role implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name")
	private String name;

	public Role() {

	}

	public Role(Long id, String name) {
		this.id = id;
		this.name = name;
	}

	public Role(RoleRequest roleRequest) {
		this.name = roleRequest.getName();
	}

	public List<Role> roles(List<Long> idRoles) {
		return idRoles.stream().map(idR -> Role.builder().id(idR).build()).toList();
	}
}