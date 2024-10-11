package com.quocbao.projectmanager.common;

import java.util.Objects;

import org.springframework.stereotype.Component;

import com.quocbao.projectmanager.exception.UnauthorizedException;

@Component
public class MethodGeneral {

	private MethodGeneral() {

	}

	public void validatePermission(Long userId, Long currentUserId) {
		if (!Objects.equals(userId, currentUserId)) {
			throw new UnauthorizedException("User does not have permission.");
		}
	}
}
