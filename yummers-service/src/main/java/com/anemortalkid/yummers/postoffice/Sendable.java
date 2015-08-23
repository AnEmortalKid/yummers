package com.anemortalkid.yummers.postoffice;

import java.io.IOException;

import javax.mail.MessagingException;

import org.springframework.mail.javamail.JavaMailSenderImpl;

/**
 * Represents something that can be sent through the tubes
 * 
 * @author janmonterrubio
 *
 */
public interface Sendable {

	final String YUMMERS_SENDER_MAIL = "postman@yummers-rest.com";

	/**
	 * Sends this Sendable
	 * 
	 * @param senderImpl
	 *            the mail sender to use
	 * @throws IOException
	 *             when something fails in the mail sending
	 * @throws MessagingException
	 *             when something fails in the mail sending
	 */
	void send(JavaMailSenderImpl senderImpl) throws IOException, MessagingException;
}
