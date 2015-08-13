package com.anemortalkid.yummers.postoffice;

import java.util.Properties;

import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

public class EmailTestSample {

	private MailSender mailSender;

	public EmailTestSample(String userName, String password) {
		JavaMailSenderImpl javaMailSenderImpl = new JavaMailSenderImpl();
		/*
		 * properties.put("mail.smtp.auth", "true");
		 * properties.put("mail.smtp.starttls.enable", "true");
		 * properties.put("mail.smtp.host", "smtp.gmail.com");
		 * properties.put("mail.smtp.port", "587");
		 */
		javaMailSenderImpl.setJavaMailProperties(getGoogleProperties());
		javaMailSenderImpl.setUsername(userName);
		javaMailSenderImpl.setPassword(password);
		this.mailSender = javaMailSenderImpl;
	}

	private Properties getGoogleProperties() {
		Properties properties = System.getProperties();
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.host", "smtp.gmail.com");
		properties.put("mail.smtp.port", "587");
		return properties;
	}

	public void sendEmail() {
		SimpleMailMessage msg = new SimpleMailMessage();
		msg.setText("Testing email through spring");
		msg.setTo("janmonterrubio+receive@gmail.com");
		try {
			this.mailSender.send(msg);
		} catch (MailException ex) {

		}
	}

}
