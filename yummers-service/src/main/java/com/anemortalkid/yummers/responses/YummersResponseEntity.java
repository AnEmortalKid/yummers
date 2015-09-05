package com.anemortalkid.yummers.responses;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Represents a custom {@link ResponseEntity} to send through the tubes
 * 
 * @author JMonterrubio
 *
 * @param <T>
 */
public class YummersResponseEntity<T> extends ResponseEntity<T> {

	/**
	 * Constructs a {@link YummersResponseEntity} with the given headers and
	 * status code
	 * 
	 * @param headers
	 *            the {@link HttpHeaders}
	 * @param statusCode
	 *            the {@link HttpStatus}
	 */
	public YummersResponseEntity(HttpHeaders headers, HttpStatus statusCode) {
		super(headers, statusCode);
	}

	/**
	 * Constructs a {@link YummersResponseEntity} with a response body, headers
	 * and status code
	 * 
	 * @param body
	 *            the body of the response
	 * @param headers
	 *            the {@link HttpHeaders}
	 * @param statusCode
	 *            the {@link HttpStatus}
	 */
	public YummersResponseEntity(T body, HttpHeaders headers, HttpStatus statusCode) {
		super(body, headers, statusCode);
	}

}
