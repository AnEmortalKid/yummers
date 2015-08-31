package com.anemortalkid.yummers.postoffice;

import java.util.List;

/**
 * Represents the data needed to send an Email to some recipients.
 * 
 * @author jmonterrubio
 *
 */
public class EmailData {

	private List<String> recipients;
	private String subject;
	private String content;

	public EmailData() {
		// for json only
	}

	/**
	 * @return a list of emails that should receive this email
	 */
	public List<String> getRecipients() {
		return recipients;
	}

	/**
	 * @param recipients
	 *            a list of emails that should receive this email
	 */
	public void setRecipients(List<String> recipients) {
		this.recipients = recipients;
	}

	/**
	 * @return the content of the email
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content
	 *            the content of the email
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * @return the subject of the email
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * @param subject
	 *            the subject of the email
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

}
