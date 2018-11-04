package org.resign.backend.domain;

import com.fasterxml.jackson.annotation.JsonInclude;

public class ApiResponse {

	@JsonInclude(JsonInclude.Include.NON_NULL)
	String error;

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
	
}
