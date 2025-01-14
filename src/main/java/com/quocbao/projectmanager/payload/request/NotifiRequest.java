package com.quocbao.projectmanager.payload.request;

import lombok.Getter;

@Getter
public class FriendRequest {

	private Long fromUser;
	
	private String fromUserName;

	private Long toUser;

}
