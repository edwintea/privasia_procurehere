package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.privasia.procurehere.core.entity.BuyerSettings;
import com.privasia.procurehere.core.entity.Currency;
import com.privasia.procurehere.core.entity.TimeZone;

public class BuyerSettingsPojo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6633795391873106505L;

	private String id;

	private TimeZone timeZone;

	private Currency currency;

	private String decimal;

	private String createdBy;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm a")
	private Date createdDate;

	private String modifiedBy;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm a")
	private Date modifiedDate;
	
	private String mode;

	public BuyerSettingsPojo(BuyerSettings buyerSettings) {

		this.id=buyerSettings.getId();
		this.timeZone = buyerSettings.getTimeZone();
		this.currency = buyerSettings.getCurrency();
		this.decimal = buyerSettings.getDecimal();
		this.createdBy = buyerSettings.getCreatedBy() != null ? buyerSettings.getCreatedBy().getLoginId() : null;
		this.createdDate = buyerSettings.getCreatedDate();
		this.modifiedBy = buyerSettings.getModifiedBy() != null ? buyerSettings.getModifiedBy().getLoginId() : null;
		this.modifiedDate = buyerSettings.getModifiedDate();
		this.mode=buyerSettings.getMode();
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
	 * @return the timeZone
	 */
	public TimeZone getTimeZone() {
		return timeZone;
	}

	/**
	 * @param timeZone the timeZone to set
	 */
	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
	}

	/**
	 * @return the currency
	 */
	public Currency getCurrency() {
		return currency;
	}

	/**
	 * @param currency the currency to set
	 */
	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	/**
	 * @return the decimal
	 */
	public String getDecimal() {
		return decimal;
	}

	/**
	 * @param decimal the decimal to set
	 */
	public void setDecimal(String decimal) {
		this.decimal = decimal;
	}

	/**
	 * @return the createdBy
	 */
	public String getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * @return the createdDate
	 */
	public Date getCreatedDate() {
		return createdDate;
	}

	/**
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * @return the modifiedBy
	 */
	public String getModifiedBy() {
		return modifiedBy;
	}

	/**
	 * @param modifiedBy the modifiedBy to set
	 */
	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	/**
	 * @return the modifiedDate
	 */
	public Date getModifiedDate() {
		return modifiedDate;
	}

	/**
	 * @param modifiedDate the modifiedDate to set
	 */
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	

}
