/**
 * 
 */
package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

/**
 * @author RT-Kapil
 */
public class AuctionConsolePojo implements Serializable {

	private static final long serialVersionUID = 3245692140830586207L;

	private Integer currentStepNo;

	private String currentStatus;

	/**
	 * @return the currentStepNo
	 */
	public Integer getCurrentStepNo() {
		return currentStepNo;
	}

	/**
	 * @param currentStepNo the currentStepNo to set
	 */
	public void setCurrentStepNo(Integer currentStepNo) {
		this.currentStepNo = currentStepNo;
	}

	/**
	 * @return the currentStatus
	 */
	public String getCurrentStatus() {
		return currentStatus;
	}

	/**
	 * @param currentStatus the currentStatus to set
	 */
	public void setCurrentStatus(String currentStatus) {
		this.currentStatus = currentStatus;
	}

}
