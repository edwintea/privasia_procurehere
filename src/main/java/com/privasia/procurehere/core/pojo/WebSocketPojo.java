/**
 * 
 */
package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.math.BigDecimal;

import com.privasia.procurehere.core.enums.EventStatus;

/**
 * @author kapil
 */
public class WebSocketPojo implements Serializable {

	private static final long serialVersionUID = -2632299518145704003L;

	private String infoMessage;

	private String errorMessage;

	private String data;
	
	private BigDecimal currentStepAmount;
	
	private int currentStep;
	
	private EventStatus eventStatus;

	
	
	public WebSocketPojo(String infoMessage, String errorMessage, String data) {
		super();
		this.infoMessage = infoMessage;
		this.errorMessage = errorMessage;
		this.data = data;
	}
	
	

	public WebSocketPojo(BigDecimal currentStepAmount, int currentStep, EventStatus eventStatus) {
		super();
		this.currentStepAmount = currentStepAmount;
		this.currentStep = currentStep;
		this.eventStatus = eventStatus;
	}



	/**
	 * @return the infoMessage
	 */
	public String getInfoMessage() {
		return infoMessage;
	}

	/**
	 * @param infoMessage the infoMessage to set
	 */
	public void setInfoMessage(String infoMessage) {
		this.infoMessage = infoMessage;
	}

	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * @param errorMessage the errorMessage to set
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	/**
	 * @return the data
	 */
	public String getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(String data) {
		this.data = data;
	}



	/**
	 * @return the currentStepAmount
	 */
	public BigDecimal getCurrentStepAmount() {
		return currentStepAmount;
	}



	/**
	 * @param currentStepAmount the currentStepAmount to set
	 */
	public void setCurrentStepAmount(BigDecimal currentStepAmount) {
		this.currentStepAmount = currentStepAmount;
	}



	/**
	 * @return the currentStep
	 */
	public int getCurrentStep() {
		return currentStep;
	}



	/**
	 * @param currentStep the currentStep to set
	 */
	public void setCurrentStep(int currentStep) {
		this.currentStep = currentStep;
	}



	/**
	 * @return the eventStatus
	 */
	public EventStatus getEventStatus() {
		return eventStatus;
	}



	/**
	 * @param eventStatus the eventStatus to set
	 */
	public void setEventStatus(EventStatus eventStatus) {
		this.eventStatus = eventStatus;
	}

}
