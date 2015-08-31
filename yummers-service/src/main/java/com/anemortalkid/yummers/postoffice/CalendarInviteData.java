package com.anemortalkid.yummers.postoffice;

import java.util.List;

public class CalendarInviteData {

	private List<String> recipients;
	private String subject;
	private EventData eventData;

	public CalendarInviteData() {
		// free json
	}

	public List<String> getRecipients() {
		return recipients;
	}

	public void setRecipients(List<String> recipientEmails) {
		this.recipients = recipientEmails;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public EventData getEventData() {
		return eventData;
	}

	public void setEventData(EventData calendarInviteData) {
		this.eventData = calendarInviteData;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CalendarData [recipientEmails=");
		builder.append(recipients);
		builder.append(", subject=");
		builder.append(subject);
		builder.append(", eventData=");
		builder.append(eventData);
		builder.append("]");
		return builder.toString();
	}

}
