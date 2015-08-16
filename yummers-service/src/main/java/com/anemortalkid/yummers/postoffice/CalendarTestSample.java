package com.anemortalkid.yummers.postoffice;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;

import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

public class CalendarTestSample {

	private JavaMailSenderImpl mailSender;

	public CalendarTestSample(String userName, String password) {

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

	public void sendInvite(List<String> recipients, String subject, CalendarInviteData calendarInviteData) throws MessagingException, IOException, URISyntaxException, ParseException {
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
		helper.setTo((String[]) recipients.toArray());
		helper.setSubject(subject);
		OutlookCalendarInvite oci = new OutlookCalendarInvite(calendarInviteData);
		String calendarData = oci.getDataString();
		mimeMessage.setDataHandler(new DataHandler(new ByteArrayDataSource(calendarData, "text/calendar")));
		try {
			this.mailSender.send(mimeMessage);
		} catch (MailException ex) {

		}
	}
}
