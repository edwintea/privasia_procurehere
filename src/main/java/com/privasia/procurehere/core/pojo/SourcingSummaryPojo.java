package com.privasia.procurehere.core.pojo;

import com.sun.org.apache.xpath.internal.operations.Bool;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author sudesha
 */
public class SourcingSummaryPojo implements Serializable {

	private static final long serialVersionUID = 8109068729270717749L;

	private String referencenumber;

	private String sourcingId;
	private String description;
	private String sourcingFormName;
	private String requester;
	private String createdDate;
	private String owner;
	private String baseCurrency;
	private String decimal;
	private String costCenter;
	private String totalAmount;
	private String receiver;
	private String businesUnit;

	private BigDecimal budgetAmount;
	private BigDecimal historicaAmount;
	private BigDecimal minimumSupplierRating;
	private BigDecimal maximumSupplierRating;

	private String groupCode;

	private String procurementMethod;

	private String procurementCategories;
	// Documents
	private List<EvaluationDocumentPojo> documents;

	// Approval Documents
	private List<EvaluationDocumentPojo> approvalDocuments;

	// Audit
	private List<RequestAuditPojo> requestAuditPojo;

	// Team member
	private List<EvaluationTeamsPojo> sourcingTeam;

	// Approval
	private List<EvaluationAprovalUsersPojo> approvals;

	// Approval Comments
	private List<EvaluationCommentsPojo> approvalComments;
	// Bq
	private List<EvaluationBqPojo> bqs;

	private List<EvaluationSorPojo> sors;

	private List<EvaluationCqPojo> cqs;

	private Boolean enableApprovalReminder = Boolean.FALSE;

	private Integer reminderAfterHour;

	private Integer reminderCount;

	private Boolean notifyEventOwner = Boolean.FALSE;

	private BigDecimal estimatedBudget;

	public String getReferencenumber() {
		return referencenumber;
	}

	public void setReferencenumber(String referencenumber) {
		this.referencenumber = referencenumber;
	}

	public String getSourcingId() {
		return sourcingId;
	}

	public void setSourcingId(String sourcingId) {
		this.sourcingId = sourcingId;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSourcingFormName() {
		return sourcingFormName;
	}

	public void setSourcingFormName(String sourcingFormName) {
		this.sourcingFormName = sourcingFormName;
	}

	public String getRequester() {
		return requester;
	}

	public void setRequester(String requester) {
		this.requester = requester;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getBaseCurrency() {
		return baseCurrency;
	}

	public void setBaseCurrency(String baseCurrency) {
		this.baseCurrency = baseCurrency;
	}

	public String getDecimal() {
		return decimal;
	}

	public void setDecimal(String decimal) {
		this.decimal = decimal;
	}

	public String getCostCenter() {
		return costCenter;
	}

	public void setCostCenter(String costCenter) {
		this.costCenter = costCenter;
	}

	public String getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getBusinesUnit() {
		return businesUnit;
	}

	public void setBusinesUnit(String businesUnit) {
		this.businesUnit = businesUnit;
	}

	public List<EvaluationDocumentPojo> getDocuments() {
		return documents;
	}

	public void setDocuments(List<EvaluationDocumentPojo> documents) {
		this.documents = documents;
	}

	public List<EvaluationDocumentPojo> getApprovalDocuments() {
		return approvalDocuments;
	}

	public void setApprovalDocuments(List<EvaluationDocumentPojo> approvalDocuments) {
		this.approvalDocuments = approvalDocuments;
	}

	public List<RequestAuditPojo> getRequestAuditPojo() {
		return requestAuditPojo;
	}

	public void setRequestAuditPojo(List<RequestAuditPojo> requestAuditPojo) {
		this.requestAuditPojo = requestAuditPojo;
	}

	public List<EvaluationTeamsPojo> getSourcingTeam() {
		return sourcingTeam;
	}

	public void setSourcingTeam(List<EvaluationTeamsPojo> sourcingTeam) {
		this.sourcingTeam = sourcingTeam;
	}

	public List<EvaluationAprovalUsersPojo> getApprovals() {
		return approvals;
	}

	public void setApprovals(List<EvaluationAprovalUsersPojo> approvals) {
		this.approvals = approvals;
	}

	public List<EvaluationCommentsPojo> getApprovalComments() {
		return approvalComments;
	}

	public void setApprovalComments(List<EvaluationCommentsPojo> approvalComments) {
		this.approvalComments = approvalComments;
	}

	public BigDecimal getBudgetAmount() {
		return budgetAmount;
	}

	public void setBudgetAmount(BigDecimal budgetAmount) {
		this.budgetAmount = budgetAmount;
	}

	public BigDecimal getHistoricaAmount() {
		return historicaAmount;
	}

	public void setHistoricaAmount(BigDecimal historicaAmount) {
		this.historicaAmount = historicaAmount;
	}

	public List<EvaluationBqPojo> getBqs() {
		return bqs;
	}

	public void setBqs(List<EvaluationBqPojo> bqs) {
		this.bqs = bqs;
	}

	public List<EvaluationCqPojo> getCqs() {
		return cqs;
	}

	public void setCqs(List<EvaluationCqPojo> cqs) {
		this.cqs = cqs;
	}

	public String getGroupCode() {
		return groupCode;
	}

	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
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

	public BigDecimal getEstimatedBudget() {
		return estimatedBudget;
	}

	public void setEstimatedBudget(BigDecimal estimatedBudget) {
		this.estimatedBudget = estimatedBudget;
	}

	public String getProcurementMethod() {
		return procurementMethod;
	}

	public void setProcurementMethod(String procurementMethod) {
		this.procurementMethod = procurementMethod;
	}

	public String getProcurementCategories() {
		return procurementCategories;
	}

	public void setProcurementCategories(String procurementCategories) {
		this.procurementCategories = procurementCategories;
	}

	public BigDecimal getMinimumSupplierRating() {
		return minimumSupplierRating;
	}

	public void setMinimumSupplierRating(BigDecimal minimumSupplierRating) {
		this.minimumSupplierRating = minimumSupplierRating;
	}

	public BigDecimal getMaximumSupplierRating() {
		return maximumSupplierRating;
	}

	public void setMaximumSupplierRating(BigDecimal maximumSupplierRating) {
		this.maximumSupplierRating = maximumSupplierRating;}


	public List<EvaluationSorPojo> getSors() {
		return sors;
	}

	public void setSors(List<EvaluationSorPojo> sors) {
		this.sors = sors;
	}
}
