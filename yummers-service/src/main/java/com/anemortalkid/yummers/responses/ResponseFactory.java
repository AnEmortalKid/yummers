package com.anemortalkid.yummers.responses;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.anemortalkid.yummers.foodpreference.FoodPreference;

public class ResponseFactory {

	public static <T> ResponseEntity<T> respondFail(String location,
			String message) throws URISyntaxException {
		URI uri = new URI(location);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(uri);
		headers.set("I_AM_A_TEAPOT", message);
		return new ResponseEntity<T>(headers, HttpStatus.I_AM_A_TEAPOT);
	}

	public static <T> ResponseEntity<T> respondCreated(T t) {
		return new ResponseEntity<T>(t, HttpStatus.CREATED);
	}

	public static <T> ResponseEntity<T> respondFound(T t) {
		return new ResponseEntity<T>(t, HttpStatus.FOUND);
	}

	public static <T> ResponseEntity<T> respondOK(T t) {
		return new ResponseEntity<T>(t, HttpStatus.OK);
	}

}
