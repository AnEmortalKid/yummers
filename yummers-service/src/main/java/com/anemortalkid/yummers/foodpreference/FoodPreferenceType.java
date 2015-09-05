package com.anemortalkid.yummers.foodpreference;

/**
 * Enumerates the different types of preference an associate can have
 * 
 * @author JMonterrubio
 *
 */
public enum FoodPreferenceType {

	/**
	 * 
	 */
	BREAKFAST("Breakfast"),

	/**
	 * 
	 */
	SNACK("Snack");

	private String display;

	private FoodPreferenceType(String display) {
		this.display = display;
	}

	public String getDisplay() {
		return display;
	}

	public String toString() {
		return display;
	}

	public static FoodPreferenceType parseString(String display) {
		String upperCased = display.toUpperCase();
		switch (upperCased) {
		case "BREAKFAST":
			return BREAKFAST;
		case "SNACK":
			return SNACK;
		default:
			return null;
		}
	}
}
