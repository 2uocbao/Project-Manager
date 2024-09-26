package com.quocbao.projectmanager.common;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Setter;

@Setter
public class ErrorResponse {

	@JsonProperty("status")
	private int status;

	@JsonProperty("error")
	private Error error;

	public ErrorResponse() {

	}

	public ErrorResponse(int status, Error error) {
		this.status = status;
		this.error = error;
	}

	@Setter
	public class Error {

		@JsonProperty("code")
		private String code;

		@JsonProperty("message")
		private String message;

		@JsonProperty("detail")
		private Detail detail;

		public Error() {

		}

		public Error(String code, String message, Detail detail) {
			this.code = code;
			this.message = message;
			this.detail = detail;
		}

		@Setter
		public class Detail {

			@JsonProperty("field")
			private String field;

			@JsonProperty("issue")
			private String issue;

			public Detail() {

			}

			public Detail(String field, String issue) {
				this.field = field;
				this.issue = issue;
			}
		}
	}
}
