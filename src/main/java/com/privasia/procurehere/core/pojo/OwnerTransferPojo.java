/**
 * 
 */
package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

/**
 * @author ravi
 */
public class OwnerTransferPojo implements Serializable {

	private static final long serialVersionUID = -7338555284255224442L;

	private String fromUserId;

	private String toUserId;

	private String loggedInUser;

	public OwnerTransferPojo() {
	}

	public OwnerTransferPojo(String fromUserId, String toUserId, String loggedInUser) {
		super();
		this.fromUserId = fromUserId;
		this.toUserId = toUserId;
		this.loggedInUser = loggedInUser;
	}

	/**
	 * @return the fromUserId
	 */
	public String getFromUserId() {
		return fromUserId;
	}

	/**
	 * @param fromUserId the fromUserId to set
	 */
	public void setFromUserId(String fromUserId) {
		this.fromUserId = fromUserId;
	}

	/**
	 * @return the toUserId
	 */
	public String getToUserId() {
		return toUserId;
	}

	/**
	 * @param toUserId the toUserId to set
	 */
	public void setToUserId(String toUserId) {
		this.toUserId = toUserId;
	}

	/**
	 * @return the loggedInUser
	 */
	public String getLoggedInUser() {
		return loggedInUser;
	}

	/**
	 * @param loggedInUser the loggedInUser to set
	 */
	public void setLoggedInUser(String loggedInUser) {
		this.loggedInUser = loggedInUser;
	}

}
