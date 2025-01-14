package com.quocbao.projectmanager.payload.request;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ChatMessage {

	private String content;
	private UUID sender;
	private UUID receiver;
	private String userSender;

}
