package com.anemortalkid.yummers.responses;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class YummersResponseEntity<T> extends ResponseEntity<T> {

	private String errorMessage;

	public YummersResponseEntity(String errorMessage, T body, HttpStatus statusCode) {
		super(body, statusCode);
		this.errorMessage = errorMessage;
	}

	public YummersResponseEntity(String errorMessage, HttpHeaders headers, HttpStatus statusCode) {
		super(headers, statusCode);
		this.errorMessage = errorMessage;
	}

	public YummersResponseEntity(T body, HttpHeaders headers, HttpStatus statusCode) {
		super(body, headers, statusCode);
	}

}
