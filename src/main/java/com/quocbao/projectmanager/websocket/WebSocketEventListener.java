package com.quocbao.projectmanager.websocket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

@Component
public class WebSocketEventListener extends TextWebSocketHandler {

	protected static final Logger LOGGER = LoggerFactory.getLogger(WebSocketEventListener.class);

	private final Map<String, String> userSessionMap = new ConcurrentHashMap<>();

	private UserStatusService userStatusService;

	public WebSocketEventListener(UserStatusService userStatusService) {
		this.userStatusService = userStatusService;
	}

	// Handle web socket connection event
	@EventListener
	public void handleWebSocketConnectListener(SessionConnectedEvent sessionConnectedEvent) {
		userStatusService.addOnlineUser(sessionConnectedEvent.getUser());
	}

	// Handle web socket disconnection events
	@EventListener
	public void handleWebSocketDisconnectionListener(SessionDisconnectEvent sessionDisconnectEvent) {
		userStatusService.removeOnlineUser(sessionDisconnectEvent.getUser());
	}

	@EventListener
	public void handleSubscribeEvent(SessionSubscribeEvent sessionSubscribeEvent) {
		String subscribedChannel = (String) sessionSubscribeEvent.getMessage().getHeaders().get("simpDestination");
		String simpSessionId = (String) sessionSubscribeEvent.getMessage().getHeaders().get("simpSessionId");
		if (subscribedChannel == null) {
			LOGGER.error("SUBSCRIBED TO NULL?? WAT?!");
			return;
		}
		userSessionMap.put(simpSessionId, subscribedChannel);
		userStatusService.addUserSubscribed(sessionSubscribeEvent.getUser(), subscribedChannel);
	}

	@EventListener
	public void handleUnSubscribeEvent(SessionUnsubscribeEvent sessionUnsubscribeEvent) {
		String simpSessionId = (String) sessionUnsubscribeEvent.getMessage().getHeaders().get("simpSessionId");
		String unSubscribedChannel = userSessionMap.get(simpSessionId);
		userStatusService.removeUserSubscribed(sessionUnsubscribeEvent.getUser(), unSubscribedChannel);
	}
}