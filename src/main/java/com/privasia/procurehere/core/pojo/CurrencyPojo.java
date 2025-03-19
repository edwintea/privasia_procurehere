package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.util.Date;



import com.fasterxml.jackson.annotation.JsonFormat;
import com.privasia.procurehere.core.entity.Currency;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.utils.Global;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Javed Ahmed
 *
 */
public class CurrencyPojo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6735538759827762158L;
	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	private String id;
	private String currencyCode;
	private String currencyName;
	private Status status;

	private String createdBy;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm a")
	private Date createdDate;

	private String modifiedBy;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm a")
	private Date modifiedDate;

	public CurrencyPojo(Currency currency) {

		this.id = currency.getId();
		this.currencyCode = currency.getCurrencyCode();
		this.currencyName = currency.getCurrencyName();
		LOG.info("in the pojo: name:"+currency.getCurrencyName());
		this.status = currency.getStatus();

		this.createdBy = currency.getCreatedBy() != null ? currency.getCreatedBy().getLoginId() : null;
		this.createdDate = currency.getCreatedDate();
		this.modifiedBy = currency.getModifiedBy() != null ? currency.getModifiedBy().getLoginId() : null;
		this.modifiedDate = currency.getModifiedDate();

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public String getCurrencyName() {
		return currencyName;
	}

	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

}
