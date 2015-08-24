package com.anemortalkid.yummers.associates;

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
