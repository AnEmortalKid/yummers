package com.anemortalkid.yummers.rotation;

import java.util.List;

import org.springframework.data.annotation.Id;

/**
 * A rotation represents an order of how events will be scheduled. At most there
 * will be one active one
 * 
 * @author JMonterrubio
 *
 */
public class Rotation {

	@Id
	private String id;

	private List<String> breakfastAssociates;
	private List<String> snackAssociates;
	private String nextBreakfastStarter;
	private String nextSnackStarter;
	private boolean active;

	public Rotation() {
		// free jason
	}

	/**
	 * @param breakfastAssociates
	 * @param snackAssociates
	 * @param nextBreakfastStarter
	 * @param nextSnackStarter
	 * @param active
	 */
	public Rotation(List<String> breakfastAssociates, List<String> snackAssociates, String nextBreakfastStarter, String nextSnackStarter, boolean active) {
		this.breakfastAssociates = breakfastAssociates;
		this.snackAssociates = snackAssociates;
		this.nextBreakfastStarter = nextBreakfastStarter;
		this.nextSnackStarter = nextSnackStarter;
		this.active = active;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * List of associate ids who had snack preference, in order
	 * 
	 * @return
	 */
	public List<String> getBreakfastAssociates() {
		return breakfastAssociates;
	}

	public void setBreakfastAssociates(List<String> breakfastAssociates) {
		this.breakfastAssociates = breakfastAssociates;
	}

	/**
	 * List of Associate Ids that had snack, order preserved
	 * 
	 * @return
	 */
	public List<String> getSnackAssociates() {
		return snackAssociates;
	}

	public void setSnackAssociates(List<String> snackAssociates) {
		this.snackAssociates = snackAssociates;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getNextBreakfastStarter() {
		return nextBreakfastStarter;
	}

	public void setNextBreakfastStarter(String nextBreakfastStarter) {
		this.nextBreakfastStarter = nextBreakfastStarter;
	}

	public String getNextSnackStarter() {
		return nextSnackStarter;
	}

	public void setNextSnackStarter(String nextSnackStarter) {
		this.nextSnackStarter = nextSnackStarter;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Rotation [id=");
		builder.append(id);
		builder.append(", breakfastAssociates=");
		builder.append(breakfastAssociates);
		builder.append(", snackAssociates=");
		builder.append(snackAssociates);
		builder.append(", nextBreakfastStarter=");
		builder.append(nextBreakfastStarter);
		builder.append(", nextSnackStarter=");
		builder.append(nextSnackStarter);
		builder.append(", active=");
		builder.append(active);
		builder.append("]");
		return builder.toString();
	}

}
