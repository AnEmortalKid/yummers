package com.anemortalkid.yummers.postoffice;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.activation.DataHandler;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

public class SendableCalendarInvite implements Sendable {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private EventData eventData;
	private String subject;
	private String[] recipients;

	public SendableCalendarInvite(CalendarInviteData calendarData) {
		this(calendarData.getRecipients(), calendarData.getSubject(), calendarData.getEventData());
	}

	public SendableCalendarInvite(List<String> recipientEmails, String subject, EventData inviteData) {
		recipients = new String[recipientEmails.size()];
		recipientEmails.toArray(recipients);
		this.subject = subject;
		this.eventData = inviteData;
	}

	@Override
	public void send(JavaMailSenderImpl senderImpl) throws MessagingException, IOException {
		MimeMessage mimeMessage = senderImpl.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
		helper.setTo(recipients);
		helper.setSubject(subject);
		OutlookCalendarInvite oci = new OutlookCalendarInvite(eventData);
		String calendarData = oci.getDataString();
		mimeMessage.setDataHandler(new DataHandler(new ByteArrayDataSource(calendarData, "text/calendar")));
		senderImpl.send(mimeMessage);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SendableCalendarInvite [eventData=");
		builder.append(eventData);
		builder.append(", subject=");
		builder.append(subject);
		builder.append(", recipients=");
		builder.append(Arrays.toString(recipients));
		builder.append("]");
		return builder.toString();
	}

}
