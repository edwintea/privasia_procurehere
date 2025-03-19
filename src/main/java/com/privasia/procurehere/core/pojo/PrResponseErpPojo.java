package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

/**
 * @author parveen
 */
public class PrResponseErpPojo implements Serializable {

	private static final long serialVersionUID = -377316209664730661L;

	private String appId;
	
	private String id;

	private String prReqNo;

	private String status;

	private String buzei;

	private String type;

	private String message;

	private String headerNote;

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
	 * @return the buzei
	 */
	public String getBuzei() {
		return buzei;
	}

	/**
	 * @param buzei the buzei to set
	 */
	public void setBuzei(String buzei) {
		this.buzei = buzei;
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

	/**
	 * @return the headerNote
	 */
	public String getHeaderNote() {
		return headerNote;
	}

	/**
	 * @param headerNote the headerNote to set
	 */
	public void setHeaderNote(String headerNote) {
		this.headerNote = headerNote;
	}

}
