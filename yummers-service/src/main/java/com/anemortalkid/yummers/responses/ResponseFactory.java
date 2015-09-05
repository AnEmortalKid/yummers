package com.anemortalkid.yummers.responses;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

/**
 * A Factory for {@link YummersResponseEntity} with different helper methods
 * 
 * @author JMonterrubio
 *
 */
public class ResponseFactory {

	/**
	 * Responds that the request failed with I_AM_A_TEAPOT and "reason" header
	 * 
	 * @param location
	 *            the resource where the error happened
	 * @param errorMessage
	 *            the error message
	 * @return a {@link YummersResponseEntity} with a failed error
	 */
	public static <T> YummersResponseEntity<T> respondFail(String location, String errorMessage) {
		HttpHeaders headers = getHeaders(location);
		headers.set("reason", errorMessage);
		return new YummersResponseEntity<T>(headers, HttpStatus.I_AM_A_TEAPOT);
	}

	/**
	 * Responds with HttpStatus.Created
	 * 
	 * @param location
	 *            the resource where the creation happened
	 * @param t
	 *            the object that was created
	 * @return a {@link YummersResponseEntity} with created code and the created
	 *         object
	 */
	public static <T> YummersResponseEntity<T> respondCreated(String location, T t) {
		return new YummersResponseEntity<T>(t, getHeaders(location), HttpStatus.CREATED);
	}

	/**
	 * Responds with HttpStatus.Found
	 * 
	 * @param location
	 *            the resource where the finding happened
	 * @param t
	 *            the object that was found
	 * @return a {@link YummersResponseEntity} with found code and the found
	 *         object
	 */
	public static <T> YummersResponseEntity<T> respondFound(String location, T body) {
		return new YummersResponseEntity<T>(body, getHeaders(location), HttpStatus.FOUND);
	}

	/**
	 * Responds with HttpStatus.Ok
	 * 
	 * @param location
	 *            the resource where the creation happened
	 * @param t
	 *            the object for the request
	 * @return a {@link YummersResponseEntity} with OK and the object requested
	 */
	public static <T> YummersResponseEntity<T> respondOK(String location, T body) {
		return new YummersResponseEntity<T>(body, getHeaders(location), HttpStatus.OK);
	}

	private static HttpHeaders getHeaders(String location) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("requestURL", location);
		return headers;
	}

}
