package com.anemortalkid.yummers.postoffice;

import java.io.IOException;

import javax.mail.MessagingException;

import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.anemortalkid.yummers.responses.YummersResponseEntity;

public interface Emailable {

	void send(JavaMailSenderImpl senderImpl) throws IOException, MessagingException;
}
