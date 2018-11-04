package org.resign.backend;

import java.util.Map;

public class GatewayResponse {

	private String body;
	private Map<String, String> headers;
	private int statusCode;
	private boolean base64Encoded;
	
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public Map<String, String> getHeaders() {
		return headers;
	}
	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}
	public int getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	public boolean isBase64Encoded() {
		return base64Encoded;
	}
	public void setBase64Encoded(boolean base64Encoded) {
		this.base64Encoded = base64Encoded;
	}

}
