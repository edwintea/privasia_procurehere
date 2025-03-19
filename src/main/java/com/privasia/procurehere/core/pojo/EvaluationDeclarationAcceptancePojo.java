/**
 * 
 */
package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

/**
 * @author pooja
 */
public class EvaluationDeclarationAcceptancePojo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1826626554182383847L;

	private String evaluationCommittee;

	private String username;

	private String userLoginEmail;

	private String acceptedDate;

	/**
	 * @return the evaluationCommittee
	 */
	public String getEvaluationCommittee() {
		return evaluationCommittee;
	}

	/**
	 * @param evaluationCommittee the evaluationCommittee to set
	 */
	public void setEvaluationCommittee(String evaluationCommittee) {
		this.evaluationCommittee = evaluationCommittee;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the userLoginEmail
	 */
	public String getUserLoginEmail() {
		return userLoginEmail;
	}

	/**
	 * @param userLoginEmail the userLoginEmail to set
	 */
	public void setUserLoginEmail(String userLoginEmail) {
		this.userLoginEmail = userLoginEmail;
	}

	/**
	 * @return the acceptedDate
	 */
	public String getAcceptedDate() {
		return acceptedDate;
	}

	/**
	 * @param acceptedDate the acceptedDate to set
	 */
	public void setAcceptedDate(String acceptedDate) {
		this.acceptedDate = acceptedDate;
	}

}
