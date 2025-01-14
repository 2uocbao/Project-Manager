package com.quocbao.projectmanager.common;

import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.quocbao.projectmanager.exception.UnauthorizedException;

@Component
public class MethodGeneral {

	private MethodGeneral() {

	}

	public void validatePermission(UUID userId, UUID currentUserId) {
//		System.out.println(userId);
//		System.out.println(currentUserId);
		if (!Objects.equals(userId, currentUserId)) {
			throw new UnauthorizedException("User does not have permission.");
		}
	}
}
