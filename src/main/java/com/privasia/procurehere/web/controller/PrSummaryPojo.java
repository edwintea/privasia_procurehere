package com.privasia.procurehere.web.controller;

import java.awt.Image;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.privasia.procurehere.core.pojo.EvaluationAprovalUsersPojo;
import com.privasia.procurehere.core.pojo.EvaluationDocumentPojo;

/**
 * @author Giridhar
 */
public class PrSummaryPojo implements Serializable {

	private static final long serialVersionUID = 8109068729270717749L;

	private String prName;
	private String remarks;
	private String paymentTerm;
	private String termsAndConditions;
	private String requester;
	private String poNumber;
	private String prRefnumber;

	// Delivery Address
	private String correspondAddress;
	private String deliveryAddress;
	private String deliveryDate;
	private String deliveryReceiver;

	private String prNo;
	private String createdDate;
	private String owner;
	private String baseCurrency;
	private String decimal;
	private String costCenter;
	private String totalAmount;
	private String receiver;
	private String taxnumber;

	private String supplierName;
	private String supplierTaxNumber;
	private String supplierContact;
	private String prDescription;

	// PR Items
	private List<PrItemsSummaryPojo> prItems;

	// Buyer Address
	private String buyerAddress;
	private String comanyName;
	private Image logo;
	private String displayName;

	// Supplier Address
	private String supplierAddress;

	// Documents
	private List<EvaluationDocumentPojo> documents;

	// Approval
	private List<EvaluationAprovalUsersPojo> approvals;

	// Approval Comments
	private List<EvaluationAprovalUsersPojo> approvalComments;

	private Boolean display = Boolean.TRUE;

	private String businesUnit;
	private BigDecimal availableBudget;
	private Boolean enableApprovalReminder = Boolean.FALSE;

	private Integer reminderAfterHour;

	private Integer reminderCount;
	
	private Boolean notifyEventOwner = Boolean.FALSE;
	
	private Integer paymentTermDays;
	
	private String poRevisionDate;

	private boolean availableBudgetVisible = false;

	private String referenceNumber;

	/**
	 * @return the paymentTermDays
	 */
	public Integer getPaymentTermDays() {
		return paymentTermDays;
	}

	/**
	 * @param paymentTermDays the paymentTermDays to set
	 */
	public void setPaymentTermDays(Integer paymentTermDays) {
		this.paymentTermDays = paymentTermDays;
	}

	/**
	 * @return the prName
	 */
	public String getPrName() {
		return prName;
	}

	/**
	 * @param prName the prName to set
	 */
	public void setPrName(String prName) {
		this.prName = prName;
	}


	/**
	 * @return the referenceNumber
	 */
	public String getReferenceNumber() {
		return referenceNumber;
	}

	/**
	 * @param referenceNumber the prName to set
	 */
	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	/**
	 * @return the remarks
	 */
	public String getRemarks() {
		return remarks;
	}

	/**
	 * @param remarks the remarks to set
	 */
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	/**
	 * @return the paymentTerm
	 */
	public String getPaymentTerm() {
		return paymentTerm;
	}

	/**
	 * @param paymentTerm the paymentTerm to set
	 */
	public void setPaymentTerm(String paymentTerm) {
		this.paymentTerm = paymentTerm;
	}

	/**
	 * @return the termsAndConditions
	 */
	public String getTermsAndConditions() {
		return termsAndConditions;
	}

	/**
	 * @param termsAndConditions the termsAndConditions to set
	 */
	public void setTermsAndConditions(String termsAndConditions) {
		this.termsAndConditions = termsAndConditions;
	}

	/**
	 * @return the requester
	 */
	public String getRequester() {
		return requester;
	}

	/**
	 * @param requester the requester to set
	 */
	public void setRequester(String requester) {
		this.requester = requester;
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
	 * @return the prRefnumber
	 */
	public String getPrRefnumber() {
		return prRefnumber;
	}

	/**
	 * @param prRefnumber the prRefnumber to set
	 */
	public void setPrRefnumber(String prRefnumber) {
		this.prRefnumber = prRefnumber;
	}

	/**
	 * @return the deliveryAddress
	 */
	public String getDeliveryAddress() {
		return deliveryAddress;
	}

	/**
	 * @param deliveryAddress the deliveryAddress to set
	 */
	public void setDeliveryAddress(String deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}

	/**
	 * @return the deliveryDate
	 */
	public String getDeliveryDate() {
		return deliveryDate;
	}

	/**
	 * @param deliveryDate the deliveryDate to set
	 */
	public void setDeliveryDate(String deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	/**
	 * @return the deliveryReceiver
	 */
	public String getDeliveryReceiver() {
		return deliveryReceiver;
	}

	/**
	 * @param deliveryReceiver the deliveryReceiver to set
	 */
	public void setDeliveryReceiver(String deliveryReceiver) {
		this.deliveryReceiver = deliveryReceiver;
	}

	/**
	 * @return the taxnumber
	 */
	public String getTaxnumber() {
		return taxnumber;
	}

	/**
	 * @param taxnumber the taxnumber to set
	 */
	public void setTaxnumber(String taxnumber) {
		this.taxnumber = taxnumber;
	}

	/**
	 * @return the supplierName
	 */
	public String getSupplierName() {
		return supplierName;
	}

	/**
	 * @param supplierName the supplierName to set
	 */
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	/**
	 * @return the prNo
	 */
	public String getPrNo() {
		return prNo;
	}

	/**
	 * @param prNo the prNo to set
	 */
	public void setPrNo(String prNo) {
		this.prNo = prNo;
	}

	/**
	 * @return the createdDate
	 */
	public String getCreatedDate() {
		return createdDate;
	}

	/**
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * @return the owner
	 */
	public String getOwner() {
		return owner;
	}

	/**
	 * @param owner the owner to set
	 */
	public void setOwner(String owner) {
		this.owner = owner;
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

	/**
	 * @return the costCenter
	 */
	public String getCostCenter() {
		return costCenter;
	}

	/**
	 * @param costCenter the costCenter to set
	 */
	public void setCostCenter(String costCenter) {
		this.costCenter = costCenter;
	}

	/**
	 * @return the totalAmount
	 */
	public String getTotalAmount() {
		return totalAmount;
	}

	/**
	 * @param totalAmount the totalAmount to set
	 */
	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}

	/**
	 * @return the receiver
	 */
	public String getReceiver() {
		return receiver;
	}

	/**
	 * @param receiver the receiver to set
	 */
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	/**
	 * @return the correspondAddress
	 */
	public String getCorrespondAddress() {
		return correspondAddress;
	}

	/**
	 * @param correspondAddress the correspondAddress to set
	 */
	public void setCorrespondAddress(String correspondAddress) {
		this.correspondAddress = correspondAddress;
	}

	/**
	 * @return the prItems
	 */
	public List<PrItemsSummaryPojo> getPrItems() {
		return prItems;
	}

	/**
	 * @param prItems the prItems to set
	 */
	public void setPrItems(List<PrItemsSummaryPojo> prItems) {
		this.prItems = prItems;
	}

	/**
	 * @return the buyerAddress
	 */
	public String getBuyerAddress() {
		return buyerAddress;
	}

	/**
	 * @param buyerAddress the buyerAddress to set
	 */
	public void setBuyerAddress(String buyerAddress) {
		this.buyerAddress = buyerAddress;
	}

	/**
	 * @return the comanyName
	 */
	public String getComanyName() {
		return comanyName;
	}

	/**
	 * @param comanyName the comanyName to set
	 */
	public void setComanyName(String comanyName) {
		this.comanyName = comanyName;
	}

	/**
	 * @return the supplierAddress
	 */
	public String getSupplierAddress() {
		return supplierAddress;
	}

	/**
	 * @param supplierAddress the supplierAddress to set
	 */
	public void setSupplierAddress(String supplierAddress) {
		this.supplierAddress = supplierAddress;
	}

	/**
	 * @return the supplierContact
	 */
	public String getSupplierContact() {
		return supplierContact;
	}

	/**
	 * @param supplierContact the supplierContact to set
	 */
	public void setSupplierContact(String supplierContact) {
		this.supplierContact = supplierContact;
	}

	/**
	 * @return the documents
	 */
	public List<EvaluationDocumentPojo> getDocuments() {
		return documents;
	}

	/**
	 * @param documents the documents to set
	 */
	public void setDocuments(List<EvaluationDocumentPojo> documents) {
		this.documents = documents;
	}

	/**
	 * @return the approvals
	 */
	public List<EvaluationAprovalUsersPojo> getApprovals() {
		return approvals;
	}

	/**
	 * @param approvals the approvals to set
	 */
	public void setApprovals(List<EvaluationAprovalUsersPojo> approvals) {
		this.approvals = approvals;
	}

	/**
	 * @return the approvalComments
	 */
	public List<EvaluationAprovalUsersPojo> getApprovalComments() {
		return approvalComments;
	}

	/**
	 * @param approvalComments the approvalComments to set
	 */
	public void setApprovalComments(List<EvaluationAprovalUsersPojo> approvalComments) {
		this.approvalComments = approvalComments;
	}

	/**
	 * @return the prDescription
	 */
	public String getPrDescription() {
		return prDescription;
	}

	/**
	 * @param prDescription the prDescription to set
	 */
	public void setPrDescription(String prDescription) {
		this.prDescription = prDescription;
	}

	/**
	 * @return the logo
	 */
	public Image getLogo() {
		return logo;
	}

	/**
	 * @param logo the logo to set
	 */
	public void setLogo(Image logo) {
		this.logo = logo;
	}

	/**
	 * @return the display
	 */
	public Boolean getDisplay() {
		return display;
	}

	/**
	 * @param display the display to set
	 */
	public void setDisplay(Boolean display) {
		this.display = display;
	}

	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @param displayName the displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * @return the businesUnit
	 */
	public String getBusinesUnit() {
		return businesUnit;
	}

	/**
	 * @param businesUnit the businesUnit to set
	 */
	public void setBusinesUnit(String businesUnit) {
		this.businesUnit = businesUnit;
	}

	/**
	 * @return the supplierTaxNumber
	 */
	public String getSupplierTaxNumber() {
		return supplierTaxNumber;
	}

	/**
	 * @param supplierTaxNumber the supplierTaxNumber to set
	 */
	public void setSupplierTaxNumber(String supplierTaxNumber) {
		this.supplierTaxNumber = supplierTaxNumber;
	}

	/**
	 * @return the enableApprovalReminder
	 */
	public Boolean getEnableApprovalReminder() {
		return enableApprovalReminder;
	}

	/**
	 * @param enableApprovalReminder the enableApprovalReminder to set
	 */
	public void setEnableApprovalReminder(Boolean enableApprovalReminder) {
		this.enableApprovalReminder = enableApprovalReminder;
	}

	/**
	 * @return the reminderAfterHour
	 */
	public Integer getReminderAfterHour() {
		return reminderAfterHour;
	}

	/**
	 * @param reminderAfterHour the reminderAfterHour to set
	 */
	public void setReminderAfterHour(Integer reminderAfterHour) {
		this.reminderAfterHour = reminderAfterHour;
	}

	/**
	 * @return the reminderCount
	 */
	public Integer getReminderCount() {
		return reminderCount;
	}

	/**
	 * @param reminderCount the reminderCount to set
	 */
	public void setReminderCount(Integer reminderCount) {
		this.reminderCount = reminderCount;
	}

	/**
	 * @return the notifyEventOwner
	 */
	public Boolean getNotifyEventOwner() {
		return notifyEventOwner;
	}

	/**
	 * @param notifyEventOwner the notifyEventOwner to set
	 */
	public void setNotifyEventOwner(Boolean notifyEventOwner) {
		this.notifyEventOwner = notifyEventOwner;
	}

	public String getPoRevisionDate() {
		return poRevisionDate;
	}

	public void setPoRevisionDate(String poRevisionDate) {
		this.poRevisionDate = poRevisionDate;
	}

	public BigDecimal getAvailableBudget() {
		return availableBudget;
	}

	public void setAvailableBudget(BigDecimal availableBudget) {
		this.availableBudget = availableBudget;
	}

	public boolean getAvailableBudgetVisible() {
		return availableBudgetVisible;
	}

	public void setAvailableBudgetVisible(boolean availableBudgetVisible) {
		this.availableBudgetVisible = availableBudgetVisible;
	}
}
