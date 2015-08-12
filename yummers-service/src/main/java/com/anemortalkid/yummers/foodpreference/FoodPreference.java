package com.anemortalkid.yummers.foodpreference;

import org.springframework.data.annotation.Id;

import com.anemortalkid.yummers.associates.Associate;

/**
 * A mapping of an associate to food preference
 * 
 * @author JMonterrubio
 *
 */
public class FoodPreference {

	@Id
	private String id;

	private Associate associate;
	private FoodPreferenceType preferenceType;

	public FoodPreference(Associate associate, FoodPreferenceType preferenceType) {
		this.associate = associate;
		this.preferenceType = preferenceType;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Associate getAssociate() {
		return associate;
	}

	public void setAssociate(Associate associate) {
		this.associate = associate;
	}

	public FoodPreferenceType getPreferenceType() {
		return preferenceType;
	}

	public void setPreferenceType(FoodPreferenceType preferenceType) {
		this.preferenceType = preferenceType;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FoodPreference [id=");
		builder.append(id);
		builder.append(", associate=");
		builder.append(associate);
		builder.append(", preferenceType=");
		builder.append(preferenceType);
		builder.append("]");
		return builder.toString();
	}

}
