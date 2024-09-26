package com.quocbao.projectmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.quocbao.projectmanager.entity.Friend;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long>, JpaSpecificationExecutor<Friend> {

}
