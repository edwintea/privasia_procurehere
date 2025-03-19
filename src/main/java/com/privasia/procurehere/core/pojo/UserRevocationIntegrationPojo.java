package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author Nana
 */
public class UserRevocationIntegrationPojo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9209619501887324007L;

	@ApiModelProperty(notes = "User Email Id", required = true)
	private String emailId;

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public String toLogString() {
		return "UserRevocationIntegrationPojo [emailId=" + emailId + "]";
	}

	
}
