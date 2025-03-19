/**
 * 
 */
package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author ravi
 */
public class EngineMailerResponseResult implements Serializable {

	private static final long serialVersionUID = -3573585095890029160L;

	@JsonProperty("StatusCode")
	private String statusCode;

	@JsonProperty("Status")
	private String status;

	@JsonProperty("TransactionID")
	private String transactionId;

	@JsonProperty("ErrorMessage")
	private String errorMessage;

	/**
	 * @return the statusCode
	 */
	public String getStatusCode() {
		return statusCode;
	}

	/**
	 * @param statusCode the statusCode to set
	 */
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	/**
	 * @return the transactionId
	 */
	public String getTransactionId() {
		return transactionId;
	}

	/**
	 * @param transactionId the transactionId to set
	 */
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	/**
	 * @return the trrorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * @param errorMessage the trrorMessage to set
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String toLogString() {
		return "EngineMailerResponse [statusCode=" + statusCode + ", status=" + status + ", transactionId=" + transactionId + ", errorMessage=" + errorMessage + "]";
	}

}
