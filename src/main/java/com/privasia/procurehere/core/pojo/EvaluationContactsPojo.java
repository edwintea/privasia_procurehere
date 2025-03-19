package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

/**
 * @author Giridhar
 */
public class EvaluationContactsPojo implements Serializable {

	private static final long serialVersionUID = -2823474238826159152L;
	private String title;
	private String contactName;
	private String comunicationEmail;
	private String designation;
	private String contactNumber;
	private String mobileNumber;

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the contactName
	 */
	public String getContactName() {
		return contactName;
	}

	/**
	 * @param contactName the contactName to set
	 */
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	/**
	 * @return the comunicationEmail
	 */
	public String getComunicationEmail() {
		return comunicationEmail;
	}

	/**
	 * @param comunicationEmail the comunicationEmail to set
	 */
	public void setComunicationEmail(String comunicationEmail) {
		this.comunicationEmail = comunicationEmail;
	}

	/**
	 * @return the designation
	 */
	public String getDesignation() {
		return designation;
	}

	/**
	 * @param designation the designation to set
	 */
	public void setDesignation(String designation) {
		this.designation = designation;
	}

	/**
	 * @return the contactNumber
	 */
	public String getContactNumber() {
		return contactNumber;
	}

	/**
	 * @param contactNumber the contactNumber to set
	 */
	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	/**
	 * @return the mobileNumber
	 */
	public String getMobileNumber() {
		return mobileNumber;
	}

	/**
	 * @param mobileNumber the mobileNumber to set
	 */
	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

}
