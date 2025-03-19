package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

/**
 * @author Giridhar
 */
public class EvaluationTeamsPojo implements Serializable {

	private static final long serialVersionUID = 448627936828264259L;
	private String owner;
	private String email;
	private String teamMemberType;

	/**
	 * @return the owner
	 */
	public String getOwner() {
		return owner;
	}

	/**
	 * @param owner the owner to set
	 */
	public void setOwner(String owner) {
		this.owner = owner;
	}

	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * @return the teamMemberType
	 */
	public String getTeamMemberType() {
		return teamMemberType;
	}

	/**
	 * @param teamMemberType the teamMemberType to set
	 */
	public void setTeamMemberType(String teamMemberType) {
		this.teamMemberType = teamMemberType;
	}

}
