package com.anemortalkid.yummers.associates;

import org.springframework.data.annotation.Id;

/**
 * Data model for an Associate
 * 
 * @author JMonterrubio
 *
 */
public class Associate {

	@Id
	private String associateId;
	private String firstName;
	private String lastName;

	public Associate() {
		// auto json
	}

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

	public String getEmail() {
		return firstName + "." + lastName + "@domain.com";
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
		builder.append("]");
		return builder.toString();
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if (!(obj instanceof Associate)) {
			return false;
		}
		Associate other = (Associate) obj;
		return this.getAssociateId().equals(other.getAssociateId());
	}

}
