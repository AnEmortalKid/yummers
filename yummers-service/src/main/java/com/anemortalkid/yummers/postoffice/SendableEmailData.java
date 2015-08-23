package com.anemortalkid.yummers.postoffice;

import java.io.IOException;

import javax.mail.MessagingException;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

public class SendableEmailData implements Sendable {

	private EmailData emailData;

	/**
	 * @param emailData
	 */
	public SendableEmailData(EmailData emailData) {
		this.emailData = emailData;
	}

	@Override
	public void send(JavaMailSenderImpl senderImpl) throws MessagingException, MailException, IOException {
		SimpleMailMessage smm = new SimpleMailMessage();
		String[] toRecipientsArray = new String[emailData.getToRecipients().size()];
		emailData.getToRecipients().toArray(toRecipientsArray);
		smm.setTo(toRecipientsArray);
		smm.setText(emailData.getText());
		smm.setSubject(emailData.getSubject());
		// TODO: PROBABLY WANT THIS in the email data?
		smm.setFrom("postman@yummers.com");
		senderImpl.send(smm);
	}

}
