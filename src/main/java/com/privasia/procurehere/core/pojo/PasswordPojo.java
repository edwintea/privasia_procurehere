package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class PasswordPojo implements Serializable {

	private static final long serialVersionUID = 1726380585706014325L;

	@NotNull(message = "{user.password.not.empty}")
	@Size(min = 5, max = 64, message = "{user.password.length}")
	private String newPassword;

	@NotNull(message = "{user.password.not.empty}")
	@Size(min = 5, max = 64, message = "{user.password.length}")
	private String confirmPassword;

	@NotNull(message = "{user.password.not.empty}")
	@Size(min = 5, max = 64, message = "{user.password.length}")
	private String oldPassword;
	
	private String username;
	
	private String loginEmail;

	
	public PasswordPojo() {
	}
	
	public PasswordPojo(String username, String loginEmail) {
		this.username = username;
		this.loginEmail = loginEmail;
	}
	
	/**
	 * @return the newPassword
	 */
	public String getNewPassword() {
		return newPassword;
	}

	/**
	 * @param newPassword the newPassword to set
	 */
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	/**
	 * @return the confirmPassword
	 */
	public String getConfirmPassword() {
		return confirmPassword;
	}

	/**
	 * @param confirmPassword the confirmPassword to set
	 */
	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	/**
	 * @return the oldPassword
	 */
	public String getOldPassword() {
		return oldPassword;
	}

	/**
	 * @param oldPassword the oldPassword to set
	 */
	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
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
	 * @return the loginEmail
	 */
	public String getLoginEmail() {
		return loginEmail;
	}

	/**
	 * @param loginEmail the loginEmail to set
	 */
	public void setLoginEmail(String loginEmail) {
		this.loginEmail = loginEmail;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toLogString() {
		return "PasswordPojo [newPassword=" + newPassword + ", confirmPassword=" + confirmPassword + ", oldPassword=" + oldPassword + "]";
	}

}
