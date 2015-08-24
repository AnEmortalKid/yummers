package com.anemortalkid.yummers.responses;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class YummersResponseEntity<T> extends ResponseEntity<T> {

	public YummersResponseEntity(HttpHeaders headers,
			HttpStatus statusCode) {
		super(headers, statusCode);
	}

	public YummersResponseEntity(T body, HttpHeaders headers,
			HttpStatus statusCode) {
		super(body, headers, statusCode);
	}

}
