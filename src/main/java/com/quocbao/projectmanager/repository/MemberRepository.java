package com.quocbao.projectmanager.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.quocbao.projectmanager.entity.Member;
@Repository
public interface MemberRepository extends JpaRepository<Member, UUID>, JpaSpecificationExecutor<Member>{

}
