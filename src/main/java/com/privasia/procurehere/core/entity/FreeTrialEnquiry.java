package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

/**
 * @author yogesh
 */
@Entity
@Table(name = "PROC_FREE_TRIAL_ENQUIRY")
public class FreeTrialEnquiry implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1637896644200118214L;

	public interface FreeTrialEnquiryInfo {

	}

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64, nullable = false)
	private String id;

	@NotNull(message = "{free.trial.user.empty}", groups = { FreeTrialEnquiryInfo.class })
	@Column(name = "USER_NAME", length = 250)
	private String userName;

	@NotNull(message = "{free.trial.email.empty}", groups = { FreeTrialEnquiryInfo.class })
	@Column(name = "EMAIL_ID", length = 250)
	private String emailId;

	@Column(name = "IP_ADDRESS", length = 500)
	private String ipAddress;

	@Column(name = "CREATED_DATE")
	private Date createdDate;

	@Column(name = "SIGNUP_DATE")
	private Date signupDate;

	@Column(name = "FOLLOWUP_EMAIL_SENT")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean followupEmailSent = Boolean.FALSE;

	@Column(name = "FOLLOWUP_EMAIL_DATE")
	private Date followupEmailDate;

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return the emailId
	 */
	public String getEmailId() {
		return emailId;
	}

	/**
	 * @param emailId the emailId to set
	 */
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	/**
	 * @return the ipAddress
	 */
	public String getIpAddress() {
		return ipAddress;
	}

	/**
	 * @param ipAddress the ipAddress to set
	 */
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	/**
	 * @return the createdDate
	 */
	public Date getCreatedDate() {
		return createdDate;
	}

	/**
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * @return the signupDate
	 */
	public Date getSignupDate() {
		return signupDate;
	}

	/**
	 * @param signupDate the signupDate to set
	 */
	public void setSignupDate(Date signupDate) {
		this.signupDate = signupDate;
	}

	/**
	 * @return the followupEmailSent
	 */
	public Boolean getFollowupEmailSent() {
		return followupEmailSent;
	}

	/**
	 * @param followupEmailSent the followupEmailSent to set
	 */
	public void setFollowupEmailSent(Boolean followupEmailSent) {
		this.followupEmailSent = followupEmailSent;
	}

	/**
	 * @return the followupEmailDate
	 */
	public Date getFollowupEmailDate() {
		return followupEmailDate;
	}

	/**
	 * @param followupEmailDate the followupEmailDate to set
	 */
	public void setFollowupEmailDate(Date followupEmailDate) {
		this.followupEmailDate = followupEmailDate;
	}

}
