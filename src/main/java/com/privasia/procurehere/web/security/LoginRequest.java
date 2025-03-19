/**
 * 
 */
package com.privasia.procurehere.web.security;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Nitin Otageri
 */
public class LoginRequest {
	private String username;
	private String password;
	private String deviceId;

	@JsonCreator
	public LoginRequest(@JsonProperty("username") String username, @JsonProperty("password") String password, @JsonProperty("deviceId") String deviceId) {
		this.username = username;
		this.password = password;
		this.deviceId = deviceId;
	}

	/**
	 * @return
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @return
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @return the deviceId
	 */
	public String getDeviceId() {
		return deviceId;
	}

}
