package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.entity.PoFinanceRequest;
import com.privasia.procurehere.core.enums.PoStatus;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;

/**
 * @author pooja
 */
public class PoSupplierPojo implements Serializable {

	private static final long serialVersionUID = 1929218342022468298L;

	private String id;

	private String referenceNumber;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date createdDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date modifiedDate;

	private String name;

	private String createdBy;

	private String currency;

	private String decimal;

	private String businessUnit;

	private BigDecimal grandTotal;

	private String poNumber;

	private PoStatus status;

	private String description;

	private String buyerCompanyName;

	private boolean finanshereRequest = false;

	private String finanshereRequestStatus;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date acceptRejectDate;

	private int srNo;

	private String poname;

	private String buyer;

	private String poCreatedby;

	private PoStatus poStatus;

	private String unitName;

	private Date actionDate;

	private String poIds;

	private String createdDateStr;

	private String acceptRejectdateStr;

	private Boolean revised;

	public PoSupplierPojo() {
		super();
	}

	public PoSupplierPojo(String id, String name, Date modifiedDate, BigDecimal grandTotal, Date createdDate, String decimal, String referenceNumber, String description, String poNumber, PoStatus status, String businessUnit, String currency, String companyName, Date acceptRejectDate, PoFinanceRequest poFinanceRequest) {
		this.id = id;
		this.name = name;
		this.modifiedDate = modifiedDate;
		this.grandTotal = grandTotal;
		this.createdDate = createdDate;
		this.decimal = decimal;
		this.referenceNumber = referenceNumber;
		this.description = description;
		this.poNumber = poNumber;
		this.status = status;
		this.businessUnit = businessUnit;
		this.currency = currency;
		this.buyerCompanyName = companyName;
		this.acceptRejectDate = acceptRejectDate;
		try {
			if (poFinanceRequest != null) {
				finanshereRequest = true;
				finanshereRequestStatus = poFinanceRequest.getRequestStatus().name();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public PoSupplierPojo(String id, String name, Date modifiedDate, BigDecimal grandTotal, Date createdDate, String decimal, String referenceNumber, String description, String poNumber, PoStatus status, String businessUnit, String currency, String companyName, Date acceptRejectDate, PoFinanceRequest poFinanceRequest, Boolean revised) {
		this.id = id;
		this.name = name;
		this.modifiedDate = modifiedDate;
		this.grandTotal = grandTotal;
		this.createdDate = createdDate;
		this.decimal = decimal;
		this.referenceNumber = referenceNumber;
		this.description = description;
		this.poNumber = poNumber;
		if (Boolean.TRUE == revised) {
			this.status = PoStatus.REVISE;
		} else {
			this.status = status;
		}
		this.businessUnit = businessUnit;
		this.currency = currency;
		this.buyerCompanyName = companyName;
		this.acceptRejectDate = acceptRejectDate;
		try {
			if (poFinanceRequest != null) {
				finanshereRequest = true;
				finanshereRequestStatus = poFinanceRequest.getRequestStatus().name();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.revised = revised;
	}

	/**
	 * @return the srNo
	 */
	public int getSrNo() {
		return srNo;
	}

	/**
	 * @param srNo the srNo to set
	 */
	public void setSrNo(int srNo) {
		this.srNo = srNo;
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
	 * @return the referenceNumber
	 */
	public String getReferenceNumber() {
		return referenceNumber;
	}

	/**
	 * @param referenceNumber the referenceNumber to set
	 */
	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
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

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
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
	 * @return the currency
	 */
	public String getCurrency() {
		return currency;
	}

	/**
	 * @param currency the currency to set
	 */
	public void setCurrency(String currency) {
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
	 * @return the businessUnit
	 */
	public String getBusinessUnit() {
		return businessUnit;
	}

	/**
	 * @param businessUnit the businessUnit to set
	 */
	public void setBusinessUnit(String businessUnit) {
		this.businessUnit = businessUnit;
	}

	/**
	 * @return the grandTotal
	 */
	public BigDecimal getGrandTotal() {
		return grandTotal;
	}

	/**
	 * @param grandTotal the grandTotal to set
	 */
	public void setGrandTotal(BigDecimal grandTotal) {
		this.grandTotal = grandTotal;
	}

	/**
	 * @return the poNumber
	 */
	public String getPoNumber() {
		return poNumber;
	}

	/**
	 * @param poNumber the poNumber to set
	 */
	public void setPoNumber(String poNumber) {
		this.poNumber = poNumber;
	}

	/**
	 * @return the status
	 */
	public PoStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(PoStatus status) {
		this.status = status;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the buyerCompanyName
	 */
	public String getBuyerCompanyName() {
		return buyerCompanyName;
	}

	/**
	 * @param buyerCompanyName the buyerCompanyName to set
	 */
	public void setBuyerCompanyName(String buyerCompanyName) {
		this.buyerCompanyName = buyerCompanyName;
	}

	/**
	 * @return the acceptRejectDate
	 */
	public Date getAcceptRejectDate() {
		return acceptRejectDate;
	}

	/**
	 * @param acceptRejectDate the acceptRejectDate to set
	 */
	public void setAcceptRejectDate(Date acceptRejectDate) {
		this.acceptRejectDate = acceptRejectDate;
	}

	/**
	 * @return the poname
	 */
	public String getPoname() {
		return poname;
	}

	/**
	 * @param poname the poname to set
	 */
	public void setPoname(String poname) {
		this.poname = poname;
	}

	/**
	 * @return the buyer
	 */
	public String getBuyer() {
		return buyer;
	}

	/**
	 * @param buyer the buyer to set
	 */
	public void setBuyer(String buyer) {
		this.buyer = buyer;
	}

	/**
	 * @return the poStatus
	 */
	public PoStatus getPostatus() {
		return poStatus;
	}

	/**
	 * @param postatus the poStatus to set
	 */
	public void setPostatus(PoStatus poStatus) {
		this.poStatus = poStatus;
	}

	/**
	 * @return the poCreatedby
	 */
	public String getPoCreatedby() {
		return poCreatedby;
	}

	/**
	 * @param poCreatedby the poCreatedby to set
	 */
	public void setPoCreatedby(String poCreatedby) {
		this.poCreatedby = poCreatedby;
	}

	/**
	 * @return the unitName
	 */
	public String getUnitName() {
		return unitName;
	}

	/**
	 * @param description the description to set
	 */
	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	/**
	 * @return the actionDate
	 */

	public void setActionDate(Date actionDate) {
		this.actionDate = actionDate;
	}

	/**
	 * @param actionDate the actionDate to set
	 */
	public Date getActionDate() {
		return actionDate;
	}

	/**
	 * @return the poIds
	 */
	public String getPoIds() {
		return poIds;
	}

	/**
	 * @param poIds the poIds to set
	 */
	public void setPoIds(String poIds) {
		this.poIds = poIds;
	}

	public PoStatus getPoStatus() {
		return poStatus;
	}

	public void setPoStatus(PoStatus poStatus) {
		this.poStatus = poStatus;
	}

	public String getCreatedDateStr() {
		return createdDateStr;
	}

	public void setCreatedDateStr(String createdDateStr) {
		this.createdDateStr = createdDateStr;
	}

	public String getAcceptRejectdateStr() {
		return acceptRejectdateStr;
	}

	public void setAcceptRejectdateStr(String acceptRejectdateStr) {
		this.acceptRejectdateStr = acceptRejectdateStr;
	}

	/**
	 * @return the finanshereRequest
	 */
	public boolean isFinanshereRequest() {
		return finanshereRequest;
	}

	/**
	 * @param finanshereRequest the finanshereRequest to set
	 */
	public void setFinanshereRequest(boolean finanshereRequest) {
		this.finanshereRequest = finanshereRequest;
	}

	/**
	 * @return the finanshereRequestStatus
	 */
	public String getFinanshereRequestStatus() {
		return finanshereRequestStatus;
	}

	/**
	 * @param finanshereRequestStatus the finanshereRequestStatus to set
	 */
	public void setFinanshereRequestStatus(String finanshereRequestStatus) {
		this.finanshereRequestStatus = finanshereRequestStatus;
	}

	/**
	 * @return the revised
	 */
	public Boolean getRevised() {
		return revised;
	}

	/**
	 * @param revised the revised to set
	 */
	public void setRevised(Boolean revised) {
		this.revised = revised;
	}

}
