package com.anemortalkid.yummers.postoffice;

import java.util.List;

public class EmailData {

	private List<String> toRecipients;
	private String subject;
	private String text;

	public List<String> getToRecipients() {
		return toRecipients;
	}

	public void setToRecipients(List<String> toRecipients) {
		this.toRecipients = toRecipients;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

}
