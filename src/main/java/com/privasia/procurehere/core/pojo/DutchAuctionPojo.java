/**
 * 
 */
package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

/**
 * @author user
 */
/**
 * @author RT-Kapil
 */
public class DutchAuctionPojo implements Serializable {

	private static final long serialVersionUID = -629027136357118472L;

	private String id;

	private String currentStepAmount;

	private String currentSlotTime;

	private Integer currentStep;

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
	 * @return the currentStepAmount
	 */
	public String getCurrentStepAmount() {
		return currentStepAmount;
	}

	/**
	 * @param currentStepAmount the currentStepAmount to set
	 */
	public void setCurrentStepAmount(String currentStepAmount) {
		this.currentStepAmount = currentStepAmount;
	}

	/**
	 * @return the currentSlotTime
	 */
	public String getCurrentSlotTime() {
		return currentSlotTime;
	}

	/**
	 * @param currentSlotTime the currentSlotTime to set
	 */
	public void setCurrentSlotTime(String currentSlotTime) {
		this.currentSlotTime = currentSlotTime;
	}

	/**
	 * @return the currentStep
	 */
	public Integer getCurrentStep() {
		return currentStep;
	}

	/**
	 * @param currentStep the currentStep to set
	 */
	public void setCurrentStep(Integer currentStep) {
		this.currentStep = currentStep;
	}

	/* (non-Javadoc)
	 * LOGSTRING
	 */
	public String toLogString() {
		return "DutchAuctionPojo [currentStepAmount=" + currentStepAmount + ", currentSlotTime=" + currentSlotTime + ", currentStep=" + currentStep + "]";
	}
	
	

}
