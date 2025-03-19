package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.ContractStatus;
import com.privasia.procurehere.core.enums.OperationType;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;
import com.privasia.procurehere.core.utils.StringUtils;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author pavan
 */
public class ProductContractPojo implements Serializable {

	private static final long serialVersionUID = -7094290956150607806L;

	@ApiModelProperty(required = false, hidden = true)
	private String id;

	@ApiModelProperty(notes = "Contract Reference Number", allowableValues = "range[1, 10]", required = true)
	private String contractReferenceNumber;

	@ApiModelProperty(notes = "Purchasing Group Code", allowableValues = "range[1, 9]", required = true)
	private String groupCode;

	@ApiModelProperty(notes = "Start Date in YYYYMMDD format", required = true, hidden = false)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date contractStartDate;

	@ApiModelProperty(notes = "End Date in YYYYMMDD  format", required = true, hidden = false)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date contractEndDate;

	@ApiModelProperty(notes = "Contract Value", allowableValues = "range[1, 15]", required = true)
	private BigDecimal contractValue;

	@ApiModelProperty(notes = "Status", required = true)
	private ContractStatus status;

	@ApiModelProperty(required = false, hidden = true)
	private String createdBy;

	@ApiModelProperty(required = false, notes = "Contract Creator Email")
	private String contractCreator;

	@ApiModelProperty(notes = "Operation", required = true)
	private OperationType operation;

	@ApiModelProperty(notes = "Vendor Code", required = false, hidden = false)
	private String vendorCode;

	@ApiModelProperty(required = false, hidden = true)
	private String modifiedBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@ApiModelProperty(required = false, hidden = true)
	private Date createdDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@ApiModelProperty(required = false, hidden = true)
	private Date modifiedDate;

	@ApiModelProperty(notes = "Business Unit Code", required = false, hidden = false)
	private String businessUnit;

	@ApiModelProperty(notes = "List of Items", required = true, hidden = false)
	private List<ProductContractItemsPojo> itemList;

	private String eventIds;

	private String referencenumber;

	private String groupcode;

	private String supplier;

	private String createdDateStr;

	private String modifiedDateStr;

	private String creatorEmail;
	
	private String decimal;
	
	private String contractId;
	
	private String groupCodeStr;
	
	private String currencyCode;
	
	private String contractName;
	
	private String companyName;
	
	private String supplierCode;
	
	private String eventId;
	
	private String unitCode;

	private String previousContractNo;
	
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
	// PH-2055
	public ProductContractPojo(String id, String contractReferenceNumber, String groupCode, Date contractStartDate, Date contractEndDate, BigDecimal contractValue, ContractStatus status, Date createdDate, Date modifiedDate, String createBy, String modifiedBy, String companyName, String creatorEmail, String businessUnit) {
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
		this.supplier = companyName;
		this.creatorEmail = creatorEmail;
		this.businessUnit = businessUnit;

	}

	public ProductContractPojo(String id, String contractReferenceNumber, String groupCode, Date contractStartDate, Date contractEndDate, BigDecimal contractValue, ContractStatus status, Date createdDate, Date modifiedDate, String createBy, String modifiedBy, String companyName) {
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
		this.supplier = companyName;

	}
	
	//PH-2431
	public ProductContractPojo(String id, String contractReferenceNumber, String groupCodeStr, Date contractStartDate, Date contractEndDate, BigDecimal contractValue, ContractStatus status, Date createdDate, Date modifiedDate, String createBy, String modifiedBy, String companyName, String creatorEmail, String businessUnit, String decimal, String contractId, String currencyCode) {
		this.id = id;
		this.contractReferenceNumber = contractReferenceNumber;
		this.groupCodeStr= groupCodeStr;
		this.contractStartDate = contractStartDate;
		this.contractEndDate = contractEndDate;
		this.contractValue = contractValue;
		this.status = status;
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
		this.createdBy = createBy;
		this.modifiedBy = modifiedBy;
		this.vendorCode = companyName;
		this.supplier = companyName;
		this.creatorEmail = creatorEmail;
		this.businessUnit = businessUnit;
		this.decimal = decimal;
		this.contractId = contractId;
		this.currencyCode = currencyCode;
	}
	
	//2691
	public ProductContractPojo(String id, String contractName, String eventId, String contractReferenceNumber, String groupCodeStr, Date contractStartDate, Date contractEndDate, BigDecimal contractValue, ContractStatus status, Date createdDate, Date modifiedDate, String createBy, String modifiedBy, String companyName, String supplierCode, String creatorEmail, String businessUnit, String unitCode, String decimal, String contractId, String currencyCode) {
		this.id = id;
		this.contractName = contractName;
		this.eventId = eventId;
		this.contractReferenceNumber = contractReferenceNumber;
		this.groupCodeStr= groupCodeStr;
		this.contractStartDate = contractStartDate;
		this.contractEndDate = contractEndDate;
		this.contractValue = contractValue;
		this.status = status;
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
		this.createdBy = createBy;
		this.modifiedBy = modifiedBy;
		this.vendorCode = companyName;
		this.supplier = companyName;
		this.companyName = companyName;
		this.supplierCode = supplierCode;
		this.creatorEmail = creatorEmail;
		this.businessUnit = businessUnit;
		this.unitCode = unitCode;
		this.decimal = decimal;
		this.contractId = contractId;
		this.currencyCode = currencyCode;
	}
	
	//PH-2691 csvDownload
	public ProductContractPojo(String id, String contractId, String contractName, String eventId, String contractReferenceNumber, String previousContractNo, Boolean renewalContract, String companyName, String vendorCode, String businessUnit, String unitCode, String groupCodeStr, String procurementCategory, String agreementType, String currencyCode, BigDecimal contractValue, Date contractStartDate, Date contractEndDate, String contractReminderDates, Date loaDate, Date agreementDate, String creatorEmail, Date createdDate, String modifiedBy, Date modifiedDate, ContractStatus status, String decimal, String loaFileName, String agreementFileName) {
		this.id = id;
		this.contractId = contractId;
		this.contractName = contractName;
		this.eventId = eventId;
		this.contractReferenceNumber = contractReferenceNumber;
		this.previousContractNo = previousContractNo;
		this.renewalContract = renewalContract;
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
		this.createdBy = creatorEmail;
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
	
	public ProductContractPojo(String id, String contractId, String contractName, String eventId, String contractReferenceNumber, String groupCodeStr, Date contractStartDate, Date contractEndDate, String companyName, String supplierCode, String businessUnit, String unitCode, String currencyCode, BigDecimal contractValue, String contractCreator, Date createdDate, ContractStatus status) {
		this.id = id;
		this.contractId = contractId;
		this.contractName = contractName;
		this.eventId = eventId;
		this.contractReferenceNumber = contractReferenceNumber;
		this.groupCodeStr= groupCodeStr;
		this.contractStartDate = contractStartDate;
		this.contractEndDate = contractEndDate;
		this.vendorCode = companyName;
		this.supplier = companyName;
		this.supplierCode = supplierCode;
		this.businessUnit = businessUnit;
		this.unitCode = unitCode;
		this.currencyCode = currencyCode;
		this.contractValue = contractValue;
		this.contractCreator = contractCreator;
		this.createdDate = createdDate;
		this.status = status;
	}


	/**
	 * @return the itemList
	 */
	public List<ProductContractItemsPojo> getItemList() {
		return itemList;
	}

	/**
	 * @param itemList the itemList to set
	 */
	public void setItemList(List<ProductContractItemsPojo> itemList) {
		this.itemList = itemList;
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
	 * @return the operation
	 */
	public OperationType getOperation() {
		return operation;
	}

	/**
	 * @param operation the operation to set
	 */
	public void setOperation(OperationType operation) {
		this.operation = operation;
	}

	public ProductContractPojo() {
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ContractStatus getStatus() {
		return status;
	}

	public void setStatus(ContractStatus status) {
		this.status = status;
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
	 * @param groupCode the groupCode to set
	 */
	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
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
	 * @param referencenumber the eventIds to set
	 */
	public void setReferencenumber(String eventIds) {
		this.referencenumber = eventIds;
	}

	/**
	 * @return the referencenumber
	 */
	public String getReferencenumber() {
		return referencenumber;
	}

	/**
	 * @param groupcode the eventIds to set
	 */
	public void setGroupcode(String eventIds) {
		this.groupcode = eventIds;
	}

	/**
	 * @return the groupcode
	 */
	public String getGroupcode() {
		return groupcode;
	}

	/**
	 * @param supplier the eventIds to set
	 */
	public void setSupplier(String eventIds) {
		this.supplier = eventIds;
	}

	/**
	 * @return the supplier
	 */
	public String getSupplier() {
		return supplier;
	}

	public String getCreatedDateStr() {
		return createdDateStr;
	}

	public void setCreatedDateStr(String createdDateStr) {
		this.createdDateStr = createdDateStr;
	}

	public String getModifiedDateStr() {
		return modifiedDateStr;
	}

	public void setModifiedDateStr(String modifiedDateStr) {
		this.modifiedDateStr = modifiedDateStr;
	}

	public String getCreatorEmail() {
		return creatorEmail;
	}

	public void setCreatorEmail(String creatorEmail) {
		this.creatorEmail = creatorEmail;
	}

	public String getContractCreator() {
		return contractCreator;
	}

	public void setContractCreator(String contractCreator) {
		this.contractCreator = contractCreator;
	}
	
	public String getDecimal() {
		return decimal;
	}

	public void setDecimal(String decimal) {
		this.decimal = decimal;
	}

	@Override
	public String toString() {
		return "ProductContractPojo [id=" + id + ", contractReferenceNumber=" + contractReferenceNumber + ", groupCode=" + groupCode + ", contractStartDate=" + contractStartDate + ", contractEndDate=" + contractEndDate + ", contractValue=" + contractValue + ", status=" + status + ", createdBy=" + createdBy + ", contractCreator=" + contractCreator + ", operation=" + operation + ", vendorCode=" + vendorCode + ", modifiedBy=" + modifiedBy + ", createdDate=" + createdDate + ", modifiedDate=" + modifiedDate + ", businessUnit=" + businessUnit + ", itemList=" + itemList + ", eventIds=" + eventIds + ", referencenumber=" + referencenumber + ", groupcode=" + groupcode + ", supplier=" + supplier + ", createdDateStr=" + createdDateStr + ", modifiedDateStr=" + modifiedDateStr + ", creatorEmail=" + creatorEmail + "]";
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
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */

}
