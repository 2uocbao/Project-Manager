package com.quocbao.projectmanager.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.quocbao.projectmanager.entity.Notification;

@Repository
public interface NotificationRepository
		extends JpaRepository<Notification, UUID>, JpaSpecificationExecutor<Notification> {

}
