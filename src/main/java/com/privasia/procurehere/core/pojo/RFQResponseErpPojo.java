package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * @author pooja
 *
 */
public class RFQResponseErpPojo implements Serializable {

	private static final long serialVersionUID = -377316209664730661L;

	private String appId;

	private String prReqNo;
	@JsonProperty("rfxDocNo")
	private String RFQ_docNo;

	private String status;

	private String type;

	private String message;

	/**
	 * @return the appId
	 */
	public String getAppId() {
		return appId;
	}

	/**
	 * @param appId the appId to set
	 */
	public void setAppId(String appId) {
		this.appId = appId;
	}

	/**
	 * @return the prReqNo
	 */
	public String getPrReqNo() {
		return prReqNo;
	}

	/**
	 * @param prReqNo the prReqNo to set
	 */
	public void setPrReqNo(String prReqNo) {
		this.prReqNo = prReqNo;
	}

	/**
	 * @return the rFQ_docNo
	 */
	public String getRFQ_docNo() {
		return RFQ_docNo;
	}

	/**
	 * @param rFQ_docNo the rFQ_docNo to set
	 */
	public void setRFQ_docNo(String rFQ_docNo) {
		RFQ_docNo = rFQ_docNo;
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
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

}
