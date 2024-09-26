package com.quocbao.projectmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.quocbao.projectmanager.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

}
