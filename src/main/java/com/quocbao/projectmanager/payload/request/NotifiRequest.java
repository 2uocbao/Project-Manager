package com.quocbao.projectmanager.payload.request;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotifiRequest {

	private String type;
	private UUID sender;
	private UUID receiver;
	private String from;

}
