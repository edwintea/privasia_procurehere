package com.privasia.procurehere.core.pojo;

import java.util.List;

public class ContractSummaryPojo {

	private String contractId;

	private String contractName;

	private String eventId;

	private String contractCreator;

	private String sapContractNumber;

	private String renewalContract;

	private String referenceNumber;

	private String previousContractNumber;

	private String businessUnit;

	private String groupCode;

	private String supplier;

	private String procurementCategory;

	private String agreementType;

	private String contractStartDate;

	private String contractEndDate;

	private String baseCurrency;

	private String decimal;

	private String contractValue;

	private String loaDate;

	private String loaDocument;

	private String agreementDate;

	private String agreementDocument;

	private Integer reminderAfterHour;

	private Integer reminderCount;

	// Documents
	private List<ContractDocumentPojo> documents;

	private List<ContractLoaAndAgreementPojo> loaAndAssingDocument;

	private List<ContractAuditPojo> contractAudit;

	private List<ProductContractReminderPojo> contractExpiryReminder;

	private List<ProductContractNotifyUserPojo> reminderNotifyUser;

	private List<ProductContractItemsPojo> items;

	private List<ContractApprovalUserPojo> approvalList;

	private List<ContractCommentPojo> approvalComments;

	private List<ContractTeamUserPojo> teamMembers;

	private String documentDate;

	private String remark;

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
	 * @return the contractCreator
	 */
	public String getContractCreator() {
		return contractCreator;
	}

	/**
	 * @param contractCreator the contractCreator to set
	 */
	public void setContractCreator(String contractCreator) {
		this.contractCreator = contractCreator;
	}

	/**
	 * @return the sapContractNumber
	 */
	public String getSapContractNumber() {
		return sapContractNumber;
	}

	/**
	 * @param sapContractNumber the sapContractNumber to set
	 */
	public void setSapContractNumber(String sapContractNumber) {
		this.sapContractNumber = sapContractNumber;
	}

	/**
	 * @return the renewalContract
	 */
	public String getRenewalContract() {
		return renewalContract;
	}

	/**
	 * @param renewalContract the renewalContract to set
	 */
	public void setRenewalContract(String renewalContract) {
		this.renewalContract = renewalContract;
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
	 * @return the previousContractNumber
	 */
	public String getPreviousContractNumber() {
		return previousContractNumber;
	}

	/**
	 * @param previousContractNumber the previousContractNumber to set
	 */
	public void setPreviousContractNumber(String previousContractNumber) {
		this.previousContractNumber = previousContractNumber;
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
	 * @return the contractStartDate
	 */
	public String getContractStartDate() {
		return contractStartDate;
	}

	/**
	 * @param contractStartDate the contractStartDate to set
	 */
	public void setContractStartDate(String contractStartDate) {
		this.contractStartDate = contractStartDate;
	}

	/**
	 * @return the contractEndDate
	 */
	public String getContractEndDate() {
		return contractEndDate;
	}

	/**
	 * @param contractEndDate the contractEndDate to set
	 */
	public void setContractEndDate(String contractEndDate) {
		this.contractEndDate = contractEndDate;
	}

	/**
	 * @return the baseCurrency
	 */
	public String getBaseCurrency() {
		return baseCurrency;
	}

	/**
	 * @param baseCurrency the baseCurrency to set
	 */
	public void setBaseCurrency(String baseCurrency) {
		this.baseCurrency = baseCurrency;
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

	public String getContractValue() {
		return contractValue;
	}

	public void setContractValue(String contractValue) {
		this.contractValue = contractValue;
	}

	/**
	 * @return the documents
	 */
	public List<ContractDocumentPojo> getDocuments() {
		return documents;
	}

	/**
	 * @param documents the documents to set
	 */
	public void setDocuments(List<ContractDocumentPojo> documents) {
		this.documents = documents;
	}

	/**
	 * @return the loaDate
	 */
	public String getLoaDate() {
		return loaDate;
	}

	/**
	 * @param loaDate the loaDate to set
	 */
	public void setLoaDate(String loaDate) {
		this.loaDate = loaDate;
	}

	/**
	 * @return the loaDocument
	 */
	public String getLoaDocument() {
		return loaDocument;
	}

	/**
	 * @param loaDocument the loaDocument to set
	 */
	public void setLoaDocument(String loaDocument) {
		this.loaDocument = loaDocument;
	}

	/**
	 * @return the agreementDate
	 */
	public String getAgreementDate() {
		return agreementDate;
	}

	/**
	 * @param agreementDate the agreementDate to set
	 */
	public void setAgreementDate(String agreementDate) {
		this.agreementDate = agreementDate;
	}

	/**
	 * @return the agreementDocument
	 */
	public String getAgreementDocument() {
		return agreementDocument;
	}

	/**
	 * @param agreementDocument the agreementDocument to set
	 */
	public void setAgreementDocument(String agreementDocument) {
		this.agreementDocument = agreementDocument;
	}

	/**
	 * @return the loaAndAssingDocument
	 */
	public List<ContractLoaAndAgreementPojo> getLoaAndAssingDocument() {
		return loaAndAssingDocument;
	}

	/**
	 * @param loaAndAssingDocument the loaAndAssingDocument to set
	 */
	public void setLoaAndAssingDocument(List<ContractLoaAndAgreementPojo> loaAndAssingDocument) {
		this.loaAndAssingDocument = loaAndAssingDocument;
	}

	/**
	 * @return the contractAudit
	 */
	public List<ContractAuditPojo> getContractAudit() {
		return contractAudit;
	}

	/**
	 * @param contractAudit the contractAudit to set
	 */
	public void setContractAudit(List<ContractAuditPojo> contractAudit) {
		this.contractAudit = contractAudit;
	}

	/**
	 * @return the contractExpiryReminder
	 */
	public List<ProductContractReminderPojo> getContractExpiryReminder() {
		return contractExpiryReminder;
	}

	/**
	 * @param contractExpiryReminder the contractExpiryReminder to set
	 */
	public void setContractExpiryReminder(List<ProductContractReminderPojo> contractExpiryReminder) {
		this.contractExpiryReminder = contractExpiryReminder;
	}

	/**
	 * @return the reminderNotifyUser
	 */
	public List<ProductContractNotifyUserPojo> getReminderNotifyUser() {
		return reminderNotifyUser;
	}

	/**
	 * @param reminderNotifyUser the reminderNotifyUser to set
	 */
	public void setReminderNotifyUser(List<ProductContractNotifyUserPojo> reminderNotifyUser) {
		this.reminderNotifyUser = reminderNotifyUser;
	}

	/**
	 * @return the items
	 */
	public List<ProductContractItemsPojo> getItems() {
		return items;
	}

	/**
	 * @param items the items to set
	 */
	public void setItems(List<ProductContractItemsPojo> items) {
		this.items = items;
	}

	/**
	 * @return the approvalList
	 */
	public List<ContractApprovalUserPojo> getApprovalList() {
		return approvalList;
	}

	/**
	 * @param approvalList the approvalList to set
	 */
	public void setApprovalList(List<ContractApprovalUserPojo> approvalList) {
		this.approvalList = approvalList;
	}

	/**
	 * @return the approvalComments
	 */
	public List<ContractCommentPojo> getApprovalComments() {
		return approvalComments;
	}

	/**
	 * @param approvalComments the approvalComments to set
	 */
	public void setApprovalComments(List<ContractCommentPojo> approvalComments) {
		this.approvalComments = approvalComments;
	}

	/**
	 * @return the reminderAfterHour
	 */
	public Integer getReminderAfterHour() {
		return reminderAfterHour;
	}

	/**
	 * @return the reminderCount
	 */
	public Integer getReminderCount() {
		return reminderCount;
	}

	/**
	 * @param reminderAfterHour the reminderAfterHour to set
	 */
	public void setReminderAfterHour(Integer reminderAfterHour) {
		this.reminderAfterHour = reminderAfterHour;
	}

	/**
	 * @param reminderCount the reminderCount to set
	 */
	public void setReminderCount(Integer reminderCount) {
		this.reminderCount = reminderCount;
	}

	public List<ContractTeamUserPojo> getTeamMembers() {
		return teamMembers;
	}

	public void setTeamMembers(List<ContractTeamUserPojo> teamMembers) {
		this.teamMembers = teamMembers;
	}

	public String getDocumentDate() {
		return documentDate;
	}

	public void setDocumentDate(String documentDate) {
		this.documentDate = documentDate;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
}
