package com.anemortalkid.yummers.postoffice;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.mail.MessagingException;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.anemortalkid.yummers.responses.ResponseFactory;
import com.anemortalkid.yummers.responses.YummersResponseEntity;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

//TODO figure out the date time passing issue, mayhaps we want it to just be strings and yolo?
@RestController
@RequestMapping("/postman")
public class PostmanController {

	// TODO - calendar invite work around for now?

	private JavaMailSenderImpl sender;

	@Value("${yummers.mail.smtp.auth}")
	private String smtp_auth;

	@Value("${yummers.mail.smtp.starttls.enable}")
	private String smtp_starttls_enable;

	@Value("${yummers.mail.smtp.host}")
	private String smtp_host;

	@Value("${yummers.mail.smtp.port}")
	private String smtp_port;

	@Value("${yummers.mail.smtp.user}")
	private String user;

	@Value("${yummers.mail.smtp.password}")
	private String password;

	@RequestMapping(value = "/sendEmail", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public YummersResponseEntity<Boolean> sendEmail(@RequestBody EmailData mailData)
			throws MailException, MessagingException, IOException {
		String callingPath = "/postman/sendEmail";
		SendableEmailData sed = new SendableEmailData(mailData);

		try {
			sed.send(getSender());
			return ResponseFactory.respondOK(callingPath, true);
		} catch (Exception e) {
			return ResponseFactory.respondFail(callingPath, e.getMessage());
		}
	}

	@RequestMapping(value = "/dateTest", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public void testDate(
			@RequestBody @org.springframework.format.annotation.DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") DateTime dateTime) {
		System.out.println("DateRec:" + dateTime);
	}

	@RequestMapping(value = "/dateTest2", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public void testDate2(@RequestBody @JsonDeserialize(using = DateTimeSerializer.class) DateTime dateTime) {
		System.out.println("DateRec:" + dateTime);
	}

	@RequestMapping(value = "/dateTest3", method = RequestMethod.POST)
	public void testDate3(
			@org.springframework.format.annotation.DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") DateTime dateTime) {
		System.out.println("DateRec3:" + dateTime);
	}

	@RequestMapping(value = "/dateTest4", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public void testDate4(DateTimeWrapper wrapper) {
		System.out.println(wrapper.getDateTime());
	}

	@RequestMapping(value = "/sendInvite", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public YummersResponseEntity<Boolean> sendInvite(@RequestBody List<String> recipientEmails,
			@RequestBody String subject, @RequestBody CalendarInviteData inviteData) {
		String callingPath = "/postman/sendInvite";
		SendableCalendarInvite sci = new SendableCalendarInvite(recipientEmails, subject, inviteData);
		try {
			sci.send(getSender());
			return ResponseFactory.respondOK(callingPath, true);
		} catch (Exception e) {
			return ResponseFactory.respondFail(callingPath, e.getMessage());
		}
	}

	private JavaMailSenderImpl getSender() {
		if (sender == null) {
			sender = new JavaMailSenderImpl();
			Properties properties = System.getProperties();
			properties.put("mail.smtp.auth", smtp_auth);
			properties.put("mail.smtp.starttls.enable", smtp_starttls_enable);
			properties.put("mail.smtp.host", smtp_host);
			properties.put("mail.smtp.port", smtp_port);
			sender.setJavaMailProperties(properties);
			sender.setUsername(user);
			sender.setPassword(password);
		}
		return sender;
	}

	static class DateTimeSerializer extends JsonDeserializer<DateTime> {

		@Override
		public DateTime deserialize(JsonParser jp, DeserializationContext ctxt)
				throws IOException, JsonProcessingException {
			// TODO Auto-generated method stub
			DateTimeFormatter formatter = org.joda.time.format.DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
			return formatter.parseDateTime(jp.getText());
		}
	}

	static class DateTimeWrapper {
		private DateTime dateTime;
		private String string;

		public DateTime getDateTime() {
			return dateTime;
		}

		@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
		public void setDateTime(DateTime dateTime) {
			this.dateTime = dateTime;
		}

		public String getString() {
			return string;
		}

		public void setString(String string) {
			this.string = string;
		}
	}
}
