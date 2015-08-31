package com.anemortalkid.yummers.postoffice;

import java.io.IOException;

import javax.mail.MessagingException;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

public class SendableEmail implements Sendable {

	private EmailData emailData;

	/**
	 * @param emailData
	 */
	public SendableEmail(EmailData emailData) {
		this.emailData = emailData;
	}

	@Override
	public void send(JavaMailSenderImpl senderImpl) throws MessagingException, MailException, IOException {
		SimpleMailMessage smm = new SimpleMailMessage();
		String[] toRecipientsArray = new String[emailData.getRecipients().size()];
		emailData.getRecipients().toArray(toRecipientsArray);
		smm.setTo(toRecipientsArray);
		smm.setText(emailData.getContent());
		smm.setSubject(emailData.getSubject());
		smm.setFrom(YUMMERS_SENDER_MAIL);
		senderImpl.send(smm);
	}

}
