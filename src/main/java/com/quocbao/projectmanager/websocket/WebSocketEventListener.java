package com.quocbao.projectmanager.websocket;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener extends TextWebSocketHandler {

	protected static final Logger LOGGER = LoggerFactory.getLogger(WebSocketEventListener.class);

	// Store active users in a set
	protected static Set<String> userActive = new HashSet<>();

	// Handle web socket connection event
	@EventListener
	public void handleWebSocketConnectListener(SessionConnectedEvent sessionConnectedEvent) {
		StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.wrap(sessionConnectedEvent.getMessage());
		String userId = getUserId(stompHeaderAccessor);
		if (userId != null) {
			userActive.add(userId);
			LOGGER.info("User {} connected", userId);
		} else {
			LOGGER.info("User {} connection failed", userId);
		}
	}

	// Handle web socket disconnection events
	@EventListener
	public void handleWebSocketDisconnectionListener(SessionDisconnectEvent sessionDisconnectEvent) {
		StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.wrap(sessionDisconnectEvent.getMessage());
		String userId = getUserId(stompHeaderAccessor);
		if (userId != null) {
			userActive.remove(userId);
			LOGGER.info("User {} Disconnected", userId);
		}
	}

	private String getUserId(StompHeaderAccessor accessor) {
		GenericMessage<?> generic = (GenericMessage<?>) accessor
				.getHeader(SimpMessageHeaderAccessor.CONNECT_MESSAGE_HEADER);
		if (generic != null) {
			SimpMessageHeaderAccessor nativeAccessor = SimpMessageHeaderAccessor.wrap(generic);
			List<String> userIdValue = nativeAccessor.getNativeHeader("userId");

			return userIdValue == null ? null : userIdValue.stream().findFirst().orElse(null);
		}

		return null;
	}

	public boolean isUserActive(String username) {
		return userActive.contains(username);
	}
}