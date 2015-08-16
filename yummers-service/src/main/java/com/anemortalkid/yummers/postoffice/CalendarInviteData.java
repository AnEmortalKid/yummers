package com.anemortalkid.yummers.postoffice;

import org.joda.time.DateTime;

/**
 * Data object for the required fields we need when generating an
 * {@link OutlookCalendarInvite}
 * 
 * @author JM034719
 *
 */
public class CalendarInviteData {

	private String mailto;
	private DateTime dateStart;
	private DateTime dateEnd;
	private String location;
	private String description;
	private String summary;

	public String getMailto() {
		return mailto;
	}

	public void setMailto(String mailto) {
		this.mailto = mailto;
	}

	public DateTime getDateStart() {
		return dateStart;
	}

	public void setDateStart(DateTime dateStart) {
		this.dateStart = dateStart;
	}

	public DateTime getDateEnd() {
		return dateEnd;
	}

	public void setDateEnd(DateTime dateEnd) {
		this.dateEnd = dateEnd;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

}
