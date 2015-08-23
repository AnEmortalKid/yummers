package com.anemortalkid.yummers.associates;

/**
 * A load object to encapsulate the required things when registering associates
 * with preference and preferences
 * 
 * @author JM034719
 *
 */
public class AssociateWithPreference {

	private String associateId;
	private String firstName;
	private String lastName;
	private String foodPreference;

	public AssociateWithPreference() {
		// free json
	}

	/**
	 * @param associateId
	 * @param firstName
	 * @param lastName
	 * @param foodPreference
	 */
	public AssociateWithPreference(String associateId, String firstName,
			String lastName, String foodPreference) {
		this.associateId = associateId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.foodPreference = foodPreference;
	}

	public String getAssociateId() {
		return associateId;
	}

	public void setAssociateId(String associateId) {
		this.associateId = associateId;
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

	public String getFoodPreference() {
		return foodPreference;
	}

	public void setFoodPreference(String foodPreference) {
		this.foodPreference = foodPreference;
	}

}
