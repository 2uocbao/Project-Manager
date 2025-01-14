package com.quocbao.projectmanager.websocket;

import java.security.Principal;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.quocbao.projectmanager.entity.User;
import com.quocbao.projectmanager.payload.request.ChatMessage;

@Service
public class UserStatusService {

	private Set<UUID> onlineUsers;
	private Map<UUID, Set<String>> userSubscribed;
	private SimpMessagingTemplate simpMessagingTemplate;

	public UserStatusService(SimpMessagingTemplate simpMessagingTemplate) {
		this.onlineUsers = new ConcurrentSkipListSet<>();
		this.userSubscribed = new ConcurrentHashMap<>();
		this.simpMessagingTemplate = simpMessagingTemplate;
	}

	public void addOnlineUser(Principal principal) {
		if (principal == null)
			return;
		User user = getUserDetails(principal);
		for (UUID id : onlineUsers) {
			simpMessagingTemplate.convertAndSend("/queue/private" + id,
					ChatMessage.builder().receiver(user.getId()).content("ONLINE").build());

		}
		onlineUsers.add(user.getId());
	}

	public void removeOnlineUser(Principal principal) {
		if (principal == null)
			return;
		User user = getUserDetails(principal);
		onlineUsers.remove(user.getId());
		userSubscribed.remove(user.getId());
		for (UUID id : onlineUsers) {
			simpMessagingTemplate.convertAndSend("/queue/private" + id,
					ChatMessage.builder().receiver(user.getId()).content("OFFLINE").build());
		}
	}

	public boolean isUserOnline(UUID userId) {
		return onlineUsers.contains(userId);
	}

	public void addUserSubscribed(Principal principal, String subscribedChannel) {
		User user = getUserDetails(principal);
		Set<String> subscriptions = userSubscribed.getOrDefault(user.getId(), new HashSet<>());
		subscriptions.add(subscribedChannel);
		userSubscribed.put(user.getId(), subscriptions);
	}

	public void removeUserSubscribed(Principal principal, String subscribedChannel) {
		User user = getUserDetails(principal);
		Set<String> subscriptions = userSubscribed.getOrDefault(user.getId(), new HashSet<>());
		subscriptions.remove(subscribedChannel);
		userSubscribed.put(user.getId(), subscriptions);
	}

	public boolean isUserSubscribe(UUID userId, String subscribedChannel) {
		Set<String> subscriptions = userSubscribed.getOrDefault(userId, new HashSet<>());
		return subscriptions.contains(subscribedChannel);
	}

	private User getUserDetails(Principal principal) {
		UsernamePasswordAuthenticationToken userAu = (UsernamePasswordAuthenticationToken) principal;
		Object object = userAu.getPrincipal();
		return (User) object;
	}

}
