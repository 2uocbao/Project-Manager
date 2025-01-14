package com.quocbao.projectmanager.websocket;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import com.quocbao.projectmanager.entity.User;
import com.quocbao.projectmanager.repository.UserRepository;

@Component
public class UserInterceptor implements ChannelInterceptor {
	protected static final Logger LOGGER = LoggerFactory.getLogger(UserInterceptor.class);

	private final UserRepository userRepository;

	public UserInterceptor(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
		if (StompCommand.CONNECT.equals(accessor.getCommand())) {
			String username = accessor.getFirstNativeHeader("username");
			LOGGER.info("User Inter {} connected", username);
			User user = userRepository.findById(UUID.fromString(username)).get();
			accessor.setUser(new UsernamePasswordAuthenticationToken(user, null, null));
		}
		return message;
	}

}
