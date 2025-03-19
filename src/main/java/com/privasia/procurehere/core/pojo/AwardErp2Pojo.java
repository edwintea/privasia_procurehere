package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.privasia.procurehere.core.enums.SapDocType;

/**
 * @author parveen
 */
public class AwardErp2Pojo implements Serializable {

	private static final long serialVersionUID = -2857085978691429602L;

	@JsonProperty("SapRefNo")
	private String sapRefNo;

	@JsonProperty("TransactionType")
	private SapDocType sapDocType;

	@JsonProperty("TransactionId")
	private String eventId;

	@JsonProperty("LOGIN_ID")
	private String loginEmail;

	@JsonProperty("ITEM")
	private List<AwardDetailsErp2Pojo> bqItems;

	/**
	 * @return the sapRefNo
	 */
	public String getSapRefNo() {
		return sapRefNo;
	}

	/**
	 * @param sapRefNo the sapRefNo to set
	 */
	public void setSapRefNo(String sapRefNo) {
		this.sapRefNo = sapRefNo;
	}

	/**
	 * @return the sapDocType
	 */
	public SapDocType getSapDocType() {
		return sapDocType;
	}

	/**
	 * @param sapDocType the sapDocType to set
	 */
	public void setSapDocType(SapDocType sapDocType) {
		this.sapDocType = sapDocType;
	}

	/**
	 * @return the bqItems
	 */
	public List<AwardDetailsErp2Pojo> getBqItems() {
		return bqItems;
	}

	/**
	 * @param bqItems the bqItems to set
	 */
	public void setBqItems(List<AwardDetailsErp2Pojo> bqItems) {
		this.bqItems = bqItems;
	}

	/**
	 * @return the eventId
	 */
	public String getEventId() {
		return eventId;
	}

	/**
	 * @param eventId the eventId to set
	 */
	public void setEventId(String eventId) {
		this.eventId = eventId;
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
}
