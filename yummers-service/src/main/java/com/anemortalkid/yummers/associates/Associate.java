package com.anemortalkid.yummers.associates;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Id;

/**
 * Data model for an Associate, which is someone who can participate in the food
 * friday stuff
 * 
 * @author JMonterrubio
 *
 */
public class Associate {

	@Id
	private String associateId;
	private String firstName;
	private String lastName;

	@Value("${yummers.mail.domain}")
	private String emailDomain;

	public Associate() {
		// auto json
	}

	/**
	 * Creates an associate with the given id, first name and last name
	 * 
	 * @param associateId
	 *            the unique identifier for the associate
	 * @param firstName
	 *            the associate's first name
	 * @param lastName
	 *            the associate's last name
	 */
	public Associate(String associateId, String firstName, String lastName) {
		this.associateId = associateId;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public String getAssociateId() {
		return associateId;
	}

	public void setAssociateId(String id) {
		this.associateId = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * Returns an email constructed from the first and last name of the
	 * associate and the emailDomain, ie firstName.lastName@emailDomain
	 * 
	 * @return an email constructed from the first and last name of the
	 *         associate
	 */
	public String getEmail() {
		return firstName + "." + lastName + "@" + emailDomain;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Associate [associateId=");
		builder.append(associateId);
		builder.append(", firstName=");
		builder.append(firstName);
		builder.append(", lastName=");
		builder.append(lastName);
		builder.append(", email=");
		builder.append(getEmail());
		builder.append("]");
		return builder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((associateId == null) ? 0 : associateId.hashCode());
		result = prime * result + ((emailDomain == null) ? 0 : emailDomain.hashCode());
		result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Associate other = (Associate) obj;
		if (associateId == null) {
			if (other.associateId != null)
				return false;
		} else if (!associateId.equals(other.associateId))
			return false;
		if (emailDomain == null) {
			if (other.emailDomain != null)
				return false;
		} else if (!emailDomain.equals(other.emailDomain))
			return false;
		if (firstName == null) {
			if (other.firstName != null)
				return false;
		} else if (!firstName.equals(other.firstName))
			return false;
		if (lastName == null) {
			if (other.lastName != null)
				return false;
		} else if (!lastName.equals(other.lastName))
			return false;
		return true;
	}

}
