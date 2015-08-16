package com.anemortalkid.yummers.postoffice;

import java.io.IOException;
import java.util.List;

import javax.activation.DataHandler;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;

import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.anemortalkid.yummers.responses.YummersResponseEntity;

public class SendableCalendarInvite implements Emailable {

	private CalendarInviteData inviteData;
	private String subject;
	private String[] recipients;

	public SendableCalendarInvite(List<String> recipientEmails, String subject, CalendarInviteData inviteData) {
		recipients = new String[recipientEmails.size()];
		recipientEmails.toArray(recipients);
		this.subject = subject;
		this.inviteData = inviteData;
	}

	@Override
	public void send(JavaMailSenderImpl senderImpl) throws MessagingException, IOException {
		MimeMessage mimeMessage = senderImpl.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
		helper.setTo(recipients);
		helper.setSubject(subject);
		OutlookCalendarInvite oci = new OutlookCalendarInvite(inviteData);
		String calendarData = oci.getDataString();
		mimeMessage.setDataHandler(new DataHandler(new ByteArrayDataSource(calendarData, "text/calendar")));
		senderImpl.send(mimeMessage);
	}
}
