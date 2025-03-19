package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

/**
 * @author sarang
 */
public class EventApiPojo implements Serializable{
	
	private static final long serialVersionUID = -1051588469466796178L;
	private String eventId;
	private String eventOwner;
	private String referanceNumber;
	private String eventName;
	private String eventVisibility;
	private String eventStartDate;
	private String eventEndDate;
	private String eventPublishDate;
	private String submissionValidityDays;
	private String participationFees;
	private String participationFeeCurrency;
	private String tenantId;
	private String createdBy;
	private String modifiedBy;
	private String createdDate;
	private String modifiedDate;
	private String status;
	private String baseCurrency;
	private String decimal;
	private String costCenter;
	private String budgetAmount;
	private String historicaAmount;
	private String paymentTerm;
	private String templateName;
	private boolean documentReq;
	private boolean meetingReq;
	private boolean questionnairesReq;
	private boolean billOfQuantityReq;
	private boolean eventDetailCompleted;
	private boolean documentCompleted;
	private boolean supplierCompleted;
	private boolean meetingCompleted;
	private boolean cqCompleted;
	private boolean bqCompleted;
	private boolean envelopCompleted;
	private boolean summaryCompleted;

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public String getEventOwner() {
		return eventOwner;
	}

	public void setEventOwner(String eventOwner) {
		this.eventOwner = eventOwner;
	}

	public String getReferanceNumber() {
		return referanceNumber;
	}

	public void setReferanceNumber(String referanceNumber) {
		this.referanceNumber = referanceNumber;
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public String getEventVisibility() {
		return eventVisibility;
	}

	public void setEventVisibility(String eventVisibility) {
		this.eventVisibility = eventVisibility;
	}

	public String getEventStartDate() {
		return eventStartDate;
	}

	public void setEventStartDate(String eventStartDate) {
		this.eventStartDate = eventStartDate;
	}

	public String getEventEndDate() {
		return eventEndDate;
	}

	public void setEventEndDate(String eventEndDate) {
		this.eventEndDate = eventEndDate;
	}

	public String getEventPublishDate() {
		return eventPublishDate;
	}

	public void setEventPublishDate(String eventPublishDate) {
		this.eventPublishDate = eventPublishDate;
	}

	public String getSubmissionValidityDays() {
		return submissionValidityDays;
	}

	public void setSubmissionValidityDays(String submissionValidityDays) {
		this.submissionValidityDays = submissionValidityDays;
	}

	public String getParticipationFees() {
		return participationFees;
	}

	public void setParticipationFees(String participationFees) {
		this.participationFees = participationFees;
	}

	public String getParticipationFeeCurrency() {
		return participationFeeCurrency;
	}

	public void setParticipationFeeCurrency(String participationFeeCurrency) {
		this.participationFeeCurrency = participationFeeCurrency;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public String getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(String modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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

	public String getBudgetAmount() {
		return budgetAmount;
	}

	public void setBudgetAmount(String budgetAmount) {
		this.budgetAmount = budgetAmount;
	}

	public String getHistoricaAmount() {
		return historicaAmount;
	}

	public void setHistoricaAmount(String historicaAmount) {
		this.historicaAmount = historicaAmount;
	}

	public String getPaymentTerm() {
		return paymentTerm;
	}

	public void setPaymentTerm(String paymentTerm) {
		this.paymentTerm = paymentTerm;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public boolean isDocumentReq() {
		return documentReq;
	}

	public void setDocumentReq(boolean documentReq) {
		this.documentReq = documentReq;
	}

	public boolean isMeetingReq() {
		return meetingReq;
	}

	public void setMeetingReq(boolean meetingReq) {
		this.meetingReq = meetingReq;
	}

	public boolean isQuestionnairesReq() {
		return questionnairesReq;
	}

	public void setQuestionnairesReq(boolean questionnairesReq) {
		this.questionnairesReq = questionnairesReq;
	}

	public boolean isBillOfQuantityReq() {
		return billOfQuantityReq;
	}

	public void setBillOfQuantityReq(boolean billOfQuantityReq) {
		this.billOfQuantityReq = billOfQuantityReq;
	}

	public boolean isEventDetailCompleted() {
		return eventDetailCompleted;
	}

	public void setEventDetailCompleted(boolean eventDetailCompleted) {
		this.eventDetailCompleted = eventDetailCompleted;
	}

	public boolean isDocumentCompleted() {
		return documentCompleted;
	}

	public void setDocumentCompleted(boolean documentCompleted) {
		this.documentCompleted = documentCompleted;
	}

	public boolean isSupplierCompleted() {
		return supplierCompleted;
	}

	public void setSupplierCompleted(boolean supplierCompleted) {
		this.supplierCompleted = supplierCompleted;
	}

	public boolean isMeetingCompleted() {
		return meetingCompleted;
	}

	public void setMeetingCompleted(boolean meetingCompleted) {
		this.meetingCompleted = meetingCompleted;
	}

	public boolean isCqCompleted() {
		return cqCompleted;
	}

	public void setCqCompleted(boolean cqCompleted) {
		this.cqCompleted = cqCompleted;
	}

	public boolean isBqCompleted() {
		return bqCompleted;
	}

	public void setBqCompleted(boolean bqCompleted) {
		this.bqCompleted = bqCompleted;
	}

	public boolean isEnvelopCompleted() {
		return envelopCompleted;
	}

	public void setEnvelopCompleted(boolean envelopCompleted) {
		this.envelopCompleted = envelopCompleted;
	}

	public boolean isSummaryCompleted() {
		return summaryCompleted;
	}

	public void setSummaryCompleted(boolean summaryCompleted) {
		this.summaryCompleted = summaryCompleted;
	}

}
