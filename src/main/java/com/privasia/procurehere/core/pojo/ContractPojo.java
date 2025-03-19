package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.ContractStatus;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author pavan
 */
public class ContractPojo implements Serializable {

	private static final long serialVersionUID = -8596436519002175404L;

	private String id;

	private String contractReferenceNumber;

	private String groupCode;

	private Date contractStartDate;

	private Date contractEndDate;

	private BigDecimal contractValue;

	private ContractStatus status;

	private String createdBy;

	private String vendorCode;

	private String modifiedBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date createdDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date modifiedDate;

	private String businessUnit;

	private String eventIds;

	private String referencenumber;

	private String groupcode;

	private String supplier;

	private String createdDateStr;

	private String modifiedDateStr;

	private String modifiedby;
	
	private String creatorEmail;
	
	private String contractId;
	
	private String decimal;
	
	private String groupCodeStr;
	
	private String currencyCode;
	
	private String companyName;
	
	private String unitCode;
	
	private String contractName;
	
	private String eventId;
	
	private String previousContractNo;
	
	private String supplierCode;
	
	private Boolean renewalContract;
	
	private String renewalContractStr;
	
	private String procurementCategory;
	
	private String agreementType;
	
	private String contractReminderDates;
	
	private Date loaDate;
	
	private String loaDateStr;
	
	private Date agreementDate;
	
	private String agreementDateStr;
	
	private Boolean loaCompleted = Boolean.FALSE;
	
	private String loaCompletedStr;
	
	private String agreementCompletedStr;
	
	private Boolean agreementCompleted = Boolean.FALSE;
	
	public ContractPojo() {

	}

	/**
	 * @param id
	 * @param contractReferenceNumber
	 * @param groupCode
	 * @param contractStartDate
	 * @param contractEndDate
	 * @param contractValue
	 * @param status
	 * @param createdBy
	 * @param vendorCode
	 * @param modifiedBy
	 * @param createdDate
	 * @param modifiedDate
	 */
	public ContractPojo(String id, String contractReferenceNumber, String groupCode, Date contractStartDate, Date contractEndDate, BigDecimal contractValue, ContractStatus status, Date createdDate, Date modifiedDate, String createBy, String modifiedBy, String companyName, String businessUnit, String creatorEmail) {
		this.id = id;
		this.contractReferenceNumber = contractReferenceNumber;
		this.groupCode = groupCode;
		this.contractStartDate = contractStartDate;
		this.contractEndDate = contractEndDate;
		this.contractValue = contractValue;
		this.status = status;
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
		this.createdBy = createBy;
		this.modifiedBy = modifiedBy;
		this.vendorCode = companyName;
		this.businessUnit = businessUnit;
		this.creatorEmail = creatorEmail;

	}
	
	public ContractPojo(String id, String contractReferenceNumber, String groupCodeStr, Date contractStartDate, Date contractEndDate, BigDecimal contractValue, ContractStatus status, Date createdDate, Date modifiedDate, String createBy, String modifiedBy, String companyName, String businessUnit, String creatorEmail, String decimal, String contractId, String currencyCode) {
		this.id = id;
		this.contractReferenceNumber = contractReferenceNumber;
		this.groupCodeStr = groupCodeStr;
		this.contractStartDate = contractStartDate;
		this.contractEndDate = contractEndDate;
		this.contractValue = contractValue;
		this.status = status;
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
		this.createdBy = createBy;
		this.modifiedBy = modifiedBy;
		this.vendorCode = companyName;
		this.businessUnit = businessUnit;
		this.creatorEmail = creatorEmail;
		this.decimal = decimal;
		this.contractId = contractId;
		this.currencyCode = currencyCode;
	}
	
	//PH-2691
	public ContractPojo(String id, String contractName, String eventId, String contractReferenceNumber, String groupCodeStr, Date contractStartDate, Date contractEndDate, BigDecimal contractValue, ContractStatus status, Date createdDate, Date modifiedDate, String createBy, String modifiedBy, String companyName,String venderCode, String businessUnit, String unitCode, String creatorEmail, String decimal, String contractId, String currencyCode) {
		this.id = id;
		this.contractName = contractName;
		this.eventId = eventId;
		this.contractReferenceNumber = contractReferenceNumber;
		this.groupCodeStr = groupCodeStr;
		this.contractStartDate = contractStartDate;
		this.contractEndDate = contractEndDate;
		this.contractValue = contractValue;
		this.status = status;
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
		this.createdBy = createBy;
		this.modifiedBy = modifiedBy;
		this.vendorCode = venderCode;
		this.companyName = companyName;
		this.businessUnit = businessUnit;
		this.unitCode = unitCode;
		this.creatorEmail = creatorEmail;
		this.decimal = decimal;
		this.contractId = contractId;
		this.currencyCode = currencyCode;
	}

	// PH-2691 csvDownload
	public ContractPojo(String id, String contractId, String contractName, String eventId, String contractReferenceNumber, String previousContractNo, Boolean renewalContract, String companyName, String vendorCode, String businessUnit, 
			String unitCode, String groupCodeStr, String procurementCategory, String agreementType, String currencyCode, BigDecimal contractValue, Date contractStartDate, Date contractEndDate, String contractReminderDates, Date loaDate, 
			Date agreementDate, String creatorEmail, Date createdDate, String modifiedBy, Date modifiedDate, ContractStatus status, String decimal, String loaFileName, String agreementFileName) {
		this.id = id;
		this.contractId = contractId;
		this.contractName = contractName;
		this.eventId = eventId;
		this.contractReferenceNumber = contractReferenceNumber;
		this.previousContractNo = previousContractNo;
		this.renewalContract = renewalContract;
		this.renewalContractStr = (renewalContract != null && renewalContract) ? "YES" : "NO";
		this.companyName = companyName;
		this.vendorCode = vendorCode;
		this.businessUnit = businessUnit;
		this.unitCode = unitCode;
		this.groupCodeStr = groupCodeStr;
		this.procurementCategory = procurementCategory;
		this.agreementType = agreementType;
		this.currencyCode = currencyCode;
		this.contractValue = contractValue;
		this.contractStartDate = contractStartDate;
		this.contractEndDate = contractEndDate;
		this.contractReminderDates = contractReminderDates;
		this.loaDate = loaDate;
		this.agreementDate = agreementDate;
		this.creatorEmail = creatorEmail;
		this.createdDate = createdDate;
		this.modifiedBy = modifiedBy;
		this.modifiedDate = modifiedDate;
		this.status = status;
		this.decimal = decimal;
		if (StringUtils.checkString(loaFileName).length() > 0) {
			this.loaCompleted = Boolean.TRUE;
		}
		if (StringUtils.checkString(agreementFileName).length() > 0) {
			this.agreementCompleted = Boolean.TRUE;
		}
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
	 * @return the contractReferenceNumber
	 */
	public String getContractReferenceNumber() {
		return contractReferenceNumber;
	}

	/**
	 * @param contractReferenceNumber the contractReferenceNumber to set
	 */
	public void setContractReferenceNumber(String contractReferenceNumber) {
		this.contractReferenceNumber = contractReferenceNumber;
	}

	/**
	 * @return the groupCode
	 */
	public String getGroupCode() {
		return groupCode;
	}

	/**
	 * @param groupCode the groupCode to set
	 */
	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	/**
	 * @return the contractStartDate
	 */
	public Date getContractStartDate() {
		return contractStartDate;
	}

	/**
	 * @param contractStartDate the contractStartDate to set
	 */
	public void setContractStartDate(Date contractStartDate) {
		this.contractStartDate = contractStartDate;
	}

	/**
	 * @return the contractEndDate
	 */
	public Date getContractEndDate() {
		return contractEndDate;
	}

	/**
	 * @param contractEndDate the contractEndDate to set
	 */
	public void setContractEndDate(Date contractEndDate) {
		this.contractEndDate = contractEndDate;
	}

	/**
	 * @return the contractValue
	 */
	public BigDecimal getContractValue() {
		return contractValue;
	}

	/**
	 * @param contractValue the contractValue to set
	 */
	public void setContractValue(BigDecimal contractValue) {
		this.contractValue = contractValue;
	}

	/**
	 * @return the status
	 */
	public ContractStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(ContractStatus status) {
		this.status = status;
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
	 * @return the vendorCode
	 */
	public String getVendorCode() {
		return vendorCode;
	}

	/**
	 * @param vendorCode the vendorCode to set
	 */
	public void setVendorCode(String vendorCode) {
		this.vendorCode = vendorCode;
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
	 * @return the eventIds
	 */
	public String getEventIds() {
		return eventIds;
	}

	/**
	 * @param eventIds the eventIds to set
	 */
	public void setEventIds(String eventIds) {
		this.eventIds = eventIds;
	}

	/**
	 * @return the referencenumber
	 */
	public String getReferencenumber() {
		return referencenumber;
	}

	/**
	 * @param referencenumber the referencenumber to set
	 */
	public void setReferencenumber(String referencenumber) {
		this.referencenumber = referencenumber;
	}

	/**
	 * @return the groupcode
	 */
	public String getGroupcode() {
		return groupcode;
	}

	/**
	 * @param groupcode the groupcode to set
	 */
	public void setGroupcode(String groupcode) {
		this.groupcode = groupcode;
	}

	/**
	 * @return the supplier
	 */
	public String getSupplier() {
		return supplier;
	}

	/**
	 * @param supplier the supplier to set
	 */
	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	/**
	 * @return the createdDateStr
	 */
	public String getCreatedDateStr() {
		return createdDateStr;
	}

	/**
	 * @param createdDateStr the createdDateStr to set
	 */
	public void setCreatedDateStr(String createdDateStr) {
		this.createdDateStr = createdDateStr;
	}

	/**
	 * @return the modifiedDateStr
	 */
	public String getModifiedDateStr() {
		return modifiedDateStr;
	}

	/**
	 * @param modifiedDateStr the modifiedDateStr to set
	 */
	public void setModifiedDateStr(String modifiedDateStr) {
		this.modifiedDateStr = modifiedDateStr;
	}

	/**
	 * @return the modifiedby
	 */
	public String getModifiedby() {
		return modifiedby;
	}

	/**
	 * @param modifiedby the modifiedby to set
	 */
	public void setModifiedby(String modifiedby) {
		this.modifiedby = modifiedby;
	}
	

	public String getCreatorEmail() {
		return creatorEmail;
	}

	public void setCreatorEmail(String creatorEmail) {
		this.creatorEmail = creatorEmail;
	}

	public String toLogString() {
		return "ContractPojo [id=" + id + ", groupCode=" + groupCode + ", status=" + status + ", vendorCode=" + vendorCode + ", modifiedBy=" + modifiedBy + ", eventIds=" + eventIds + ", referencenumber=" + referencenumber + ", groupcode=" + groupcode + ", supplier=" + supplier + "]";
	}

	/**
	 * @return the contractId
	 */
	public String getContractId() {
		return contractId;
	}

	/**
	 * @param contractId the contractId to set
	 */
	public void setContractId(String contractId) {
		this.contractId = contractId;
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
	 * @return the groupCodeStr
	 */
	public String getGroupCodeStr() {
		return groupCodeStr;
	}

	/**
	 * @param groupCodeStr the groupCodeStr to set
	 */
	public void setGroupCodeStr(String groupCodeStr) {
		this.groupCodeStr = groupCodeStr;
	}

	/**
	 * @return the currencyCode
	 */
	public String getCurrencyCode() {
		return currencyCode;
	}

	/**
	 * @param currencyCode the currencyCode to set
	 */
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	/**
	 * @return the companyName
	 */
	public String getCompanyName() {
		return companyName;
	}

	/**
	 * @param companyName the companyName to set
	 */
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	/**
	 * @return the unitCode
	 */
	public String getUnitCode() {
		return unitCode;
	}

	/**
	 * @param unitCode the unitCode to set
	 */
	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
	}

	/**
	 * @return the contractName
	 */
	public String getContractName() {
		return contractName;
	}

	/**
	 * @param contractName the contractName to set
	 */
	public void setContractName(String contractName) {
		this.contractName = contractName;
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
	 * @return the supplierCode
	 */
	public String getSupplierCode() {
		return supplierCode;
	}

	/**
	 * @param supplierCode the supplierCode to set
	 */
	public void setSupplierCode(String supplierCode) {
		this.supplierCode = supplierCode;
	}

	/**
	 * @return the previousContractNo
	 */
	public String getPreviousContractNo() {
		return previousContractNo;
	}

	/**
	 * @param previousContractNo the previousContractNo to set
	 */
	public void setPreviousContractNo(String previousContractNo) {
		this.previousContractNo = previousContractNo;
	}

	/**
	 * @return the renewalContract
	 */
	public Boolean getRenewalContract() {
		return renewalContract;
	}

	/**
	 * @param renewalContract the renewalContract to set
	 */
	public void setRenewalContract(Boolean renewalContract) {
		this.renewalContract = renewalContract;
	}

	/**
	 * @return the procurementCategory
	 */
	public String getProcurementCategory() {
		return procurementCategory;
	}

	/**
	 * @param procurementCategory the procurementCategory to set
	 */
	public void setProcurementCategory(String procurementCategory) {
		this.procurementCategory = procurementCategory;
	}

	/**
	 * @return the agreementType
	 */
	public String getAgreementType() {
		return agreementType;
	}

	/**
	 * @param agreementType the agreementType to set
	 */
	public void setAgreementType(String agreementType) {
		this.agreementType = agreementType;
	}

	/**
	 * @return the contractReminderDates
	 */
	public String getContractReminderDates() {
		return contractReminderDates;
	}

	/**
	 * @param contractReminderDates the contractReminderDates to set
	 */
	public void setContractReminderDates(String contractReminderDates) {
		this.contractReminderDates = contractReminderDates;
	}

	/**
	 * @return the loaCompleted
	 */
	public Boolean getLoaCompleted() {
		return loaCompleted;
	}

	/**
	 * @param loaCompleted the loaCompleted to set
	 */
	public void setLoaCompleted(Boolean loaCompleted) {
		this.loaCompleted = loaCompleted;
	}

	/**
	 * @return the agreementCompleted
	 */
	public Boolean getAgreementCompleted() {
		return agreementCompleted;
	}

	/**
	 * @param agreementCompleted the agreementCompleted to set
	 */
	public void setAgreementCompleted(Boolean agreementCompleted) {
		this.agreementCompleted = agreementCompleted;
	}

	/**
	 * @return the loaDate
	 */
	public Date getLoaDate() {
		return loaDate;
	}

	/**
	 * @param loaDate the loaDate to set
	 */
	public void setLoaDate(Date loaDate) {
		this.loaDate = loaDate;
	}

	/**
	 * @return the agreementDate
	 */
	public Date getAgreementDate() {
		return agreementDate;
	}

	/**
	 * @param agreementDate the agreementDate to set
	 */
	public void setAgreementDate(Date agreementDate) {
		this.agreementDate = agreementDate;
	}

	/**
	 * @return the loaCompletedStr
	 */
	public String getLoaCompletedStr() {
		return loaCompletedStr;
	}

	/**
	 * @param loaCompletedStr the loaCompletedStr to set
	 */
	public void setLoaCompletedStr(String loaCompletedStr) {
		this.loaCompletedStr = loaCompletedStr;
	}

	/**
	 * @return the agreementCompletedStr
	 */
	public String getAgreementCompletedStr() {
		return agreementCompletedStr;
	}

	/**
	 * @param agreementCompletedStr the agreementCompletedStr to set
	 */
	public void setAgreementCompletedStr(String agreementCompletedStr) {
		this.agreementCompletedStr = agreementCompletedStr;
	}

	/**
	 * @return the loaDateStr
	 */
	public String getLoaDateStr() {
		return loaDateStr;
	}

	/**
	 * @param loaDateStr the loaDateStr to set
	 */
	public void setLoaDateStr(String loaDateStr) {
		this.loaDateStr = loaDateStr;
	}

	/**
	 * @return the agreementDateStr
	 */
	public String getAgreementDateStr() {
		return agreementDateStr;
	}

	/**
	 * @param agreementDateStr the agreementDateStr to set
	 */
	public void setAgreementDateStr(String agreementDateStr) {
		this.agreementDateStr = agreementDateStr;
	}

	/**
	 * @return the renewalContractStr
	 */
	public String getRenewalContractStr() {
		return renewalContractStr;
	}

	/**
	 * @param renewalContractStr the renewalContractStr to set
	 */
	public void setRenewalContractStr(String renewalContractStr) {
		this.renewalContractStr = renewalContractStr;
	}


}
