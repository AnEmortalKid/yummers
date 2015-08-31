package com.anemortalkid.yummers.postoffice;

import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.anemortalkid.yummers.responses.ResponseFactory;
import com.anemortalkid.yummers.responses.YummersResponseEntity;

@RestController
@RequestMapping("/postman")
public class PostmanController {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private JavaMailSenderImpl sender;

	@Value("${yummers.prod.mail.mode}")
	private boolean productionMode;

	@Value("${yummers.mail.smtp.auth}")
	private String smtp_auth;

	@Value("${yummers.mail.smtp.starttls.enable}")
	private String smtp_starttls_enable;

	@Value("${yummers.mail.smtp.host}")
	private String smtp_host;

	@Value("${yummers.prod.mail.smtp.host}")
	private String prod_smtp_host;

	@Value("${yummers.mail.smtp.port}")
	private String smtp_port;

	@Value("${yummers.mail.smtp.user}")
	private String user;

	@Value("${yummers.mail.smtp.password}")
	private String password;

	private JavaMailSenderImpl getSender() {
		if (sender == null) {
			sender = new JavaMailSenderImpl();
			logger.info("Prod mode = " + productionMode);
			Properties properties = System.getProperties();
			properties.put("mail.smtp.host", productionMode ? prod_smtp_host : smtp_host);
			if (!productionMode) {
				properties.put("mail.smtp.auth", smtp_auth);
				properties.put("mail.smtp.starttls.enable", smtp_starttls_enable);
				properties.put("mail.smtp.port", smtp_port);
				sender.setUsername(user);
				sender.setPassword(password);
			}
			sender.setJavaMailProperties(properties);
		}
		return sender;
	}

	@PreAuthorize("hasRole('ROLE_SUPER')")
	@RequestMapping(value = "/sendEmail", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public YummersResponseEntity<Boolean> sendEmail(@RequestBody EmailData mailData) {
		String callingPath = "/postman/sendEmail";
		SendableEmail sed = new SendableEmail(mailData);

		try {
			sed.send(getSender());
			return ResponseFactory.respondOK(callingPath, true);
		} catch (Exception e) {
			return ResponseFactory.respondFail(callingPath, e.getMessage());
		}
	}
	
	public boolean sendEmailData(EmailData emailData)
	{
		SendableEmail se = new SendableEmail(emailData);
		try
		{
			se.send(getSender());
			return true;
		}
		catch(Exception e)
		{
			logger.error("Failed to send email", e);
			return false;
		}
	}

	@PreAuthorize("hasRole('ROLE_SUPER')")
	@RequestMapping(value = "/sendInvite", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public YummersResponseEntity<Boolean> sendInvite(@RequestBody CalendarInviteData calendarData) {
		String callingPath = "/postman/sendInvite";
		SendableCalendarInvite sci = new SendableCalendarInvite(calendarData.getRecipients(),
				calendarData.getSubject(), calendarData.getEventData());
		System.out.println(sci);
		try {
			sci.send(getSender());
			return ResponseFactory.respondOK(callingPath, true);
		} catch (Exception e) {
			return ResponseFactory.respondFail(callingPath, e.getMessage());
		}
	}

	public Boolean sendCalendarInviteData(List<String> recipientEmails, String subject, EventData eventData) {
		SendableCalendarInvite sci = new SendableCalendarInvite(recipientEmails, subject, eventData);
		try {
			sci.send(getSender());
			return true;
		} catch (Exception e) {
			logger.error("Could not send invite", e);
			return false;
		}
	}

}
