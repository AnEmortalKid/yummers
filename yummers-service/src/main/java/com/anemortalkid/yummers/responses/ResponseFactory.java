package com.anemortalkid.yummers.responses;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

public class ResponseFactory {

	public static <T> YummersResponseEntity<T> respondFail(String location, String errorMessage) {
		return new YummersResponseEntity<T>(errorMessage, getHeaders(location), HttpStatus.I_AM_A_TEAPOT);
	}

	public static <T> YummersResponseEntity<T> respondCreated(String location, T t) {

		return new YummersResponseEntity<T>(t, getHeaders(location), HttpStatus.CREATED);
	}

	private static HttpHeaders getHeaders(String location) {
		URI uri = null;
		try {
			uri = new URI(location);
		} catch (URISyntaxException syntaxException) {
			syntaxException.printStackTrace();
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(uri);
		return headers;
	}

	public static <T> YummersResponseEntity<T> respondFound(String location, T body) {
		return new YummersResponseEntity<T>(body, getHeaders(location), HttpStatus.FOUND);
	}

	public static <T> YummersResponseEntity<T> respondOK(String location, T body) {
		return new YummersResponseEntity<T>(body, getHeaders(location), HttpStatus.OK);
	}
}
