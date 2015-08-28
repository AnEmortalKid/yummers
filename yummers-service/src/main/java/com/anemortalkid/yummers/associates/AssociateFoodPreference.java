package com.anemortalkid.yummers.associates;

/**
 * A wrapper parameter object to be able to take ParamBody strings in the form
 * "foodPreference":"snack". This object is purely for JSON
 * serialization/deserialization
 * 
 * @author JMonterrubio
 *
 */
public class AssociateFoodPreference {

	private String foodPreference;

	public AssociateFoodPreference() {
		// free json
	}

	public String getFoodPreference() {
		return foodPreference;
	}

	public void setFoodPreference(String foodPreference) {
		this.foodPreference = foodPreference;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AssociateFoodPreference [foodPreference=");
		builder.append(foodPreference);
		builder.append("]");
		return builder.toString();
	}

}
