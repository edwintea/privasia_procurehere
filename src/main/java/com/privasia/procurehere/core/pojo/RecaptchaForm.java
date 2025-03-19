/**
 * 
 */
package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Nitin Otageri
 *
 */
public class RecaptchaForm implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5842064172841859990L;
	@JsonProperty("g-recaptcha-response")
	@Transient
	private String recaptchaResponse;

	/**
	 * @return the recaptchaResponse
	 */
	public String getRecaptchaResponse() {
		return recaptchaResponse;
	}

	/**
	 * @param recaptchaResponse the recaptchaResponse to set
	 */
	public void setRecaptchaResponse(String recaptchaResponse) {
		this.recaptchaResponse = recaptchaResponse;
	}
	
}
