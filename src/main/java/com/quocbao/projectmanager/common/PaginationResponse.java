package com.quocbao.projectmanager.common;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Setter;

@Setter
public class PaginationResponse {

	@JsonProperty("status")
	private int status;

	@JsonProperty("data")
	private List<Object> data;

	@JsonProperty("pagination")
	private Pagination pagination;

	public PaginationResponse() {

	}

	@Setter
	public class Pagination {

		@JsonProperty("current_page")
		private int currentPage;

		@JsonProperty("per_page")
		private int perPage;

		@JsonProperty("total_pages")
		private int totalPages;

		@JsonProperty("total_items")
		private int totalItems;

		public Pagination() {

		}
	}
}