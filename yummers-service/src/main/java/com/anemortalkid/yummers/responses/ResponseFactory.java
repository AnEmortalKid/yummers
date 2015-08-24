package com.anemortalkid.yummers.responses;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

public class ResponseFactory {

	public static <T> YummersResponseEntity<T> respondFail(String location,
			String errorMessage) {
		HttpHeaders headers = getHeaders(location);
		headers.set("reason", errorMessage);
		return new YummersResponseEntity<T>(headers, HttpStatus.I_AM_A_TEAPOT);
	}

	public static <T> YummersResponseEntity<T> respondCreated(String location,
			T t) {

		return new YummersResponseEntity<T>(t, getHeaders(location),
				HttpStatus.CREATED);
	}

	public static <T> YummersResponseEntity<T> respondFound(String location,
			T body) {
		return new YummersResponseEntity<T>(body, getHeaders(location),
				HttpStatus.FOUND);
	}

	public static <T> YummersResponseEntity<T> respondOK(String location, T body) {
		return new YummersResponseEntity<T>(body, getHeaders(location),
				HttpStatus.OK);
	}

	private static HttpHeaders getHeaders(String location) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("requestURL", location);
		return headers;
	}

}
