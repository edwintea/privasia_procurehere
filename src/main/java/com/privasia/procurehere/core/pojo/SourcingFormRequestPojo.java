/**
 * 
 */
package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.SourcingFormStatus;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author yogesh
 */
public class SourcingFormRequestPojo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9042262365373511270L;

	private String id;

	private String formId;

	private String formType;

	private String formOwner;

	private String referanceNumber;

	private String sourcingFormName;

	private String description;

	private String tenantId;

	private String createdBy;

	private String actionBy;

	private String modifiedBy;
	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date createdDate;

	private Date actionDate;

	private Date modifiedDate;

	private SourcingFormStatus status;

	private String businessUnit;

	private String costCenter;

	private String sourcingFormStatus;

	private Boolean urgentForm;

	private Boolean formDetailCompleted = Boolean.FALSE;

	private Boolean cqCompleted = Boolean.FALSE;

	private Boolean bqCompleted = Boolean.FALSE;

	private Boolean summaryCompleted = Boolean.FALSE;

	private String concludeRemarks;

	private String eventIds;

	private String stat;

	private String templateName;

	private BigDecimal availableBudget;

	private BigDecimal estimatedBudget;

	private String groupCode;

	private String nameofrequest;

	private String requestid;

	private String requestowner;

	private String referencenumber;

	private String businessunit;

	private String costcenter;

	private String baseCurrency;

	private Date submittedDate;

	private Date approvedDate;

	private Integer approvalDaysHours;

	private Integer approvalTotalLevels;

	private Integer approvalTotalUsers;

	private String sourcingrequestid;

	private String createdDateStr;

	private String approvedDateStr;

	private String submittedDatestr;

	private BigDecimal historicAmount;

	private String procurementMethod;

	private String procurementCategories;
	
	private String groupCodeOld;
	
	private String groupCodeDescription;

	public SourcingFormRequestPojo() {

	}

	// Ph-961
	public SourcingFormRequestPojo(String id, String formId, String sourcingFormName, String referanceNumber, String createdBy, Date createdDate, String formOwner, String businessUnit, String costCenter, String status, String templateName, String description, BigDecimal availableBudget, BigDecimal estimatedBudget, String groupCode) {
		this.id = id;
		this.formId = formId;
		this.sourcingFormName = sourcingFormName;
		this.referanceNumber = referanceNumber;
		this.createdBy = createdBy;
		this.createdDate = createdDate;
		this.formOwner = formOwner;
		this.businessUnit = businessUnit;
		this.costCenter = costCenter;
		this.stat = status;
		this.templateName = templateName;
		this.description = description;
		this.availableBudget = availableBudget;
		this.estimatedBudget = estimatedBudget;
		this.groupCode = groupCode;
	}

	// Supplier on board CR
	public SourcingFormRequestPojo(String id, String formId, String sourcingFormName, String referanceNumber, String createdBy, Date createdDate, String formOwner, String businessUnit, String costCenter, String currency, String status, String templateName, String description, BigDecimal availableBudget, BigDecimal estimatedBudget, String groupCode) {
		this.id = id;
		this.formId = formId;
		this.sourcingFormName = sourcingFormName;
		this.referanceNumber = referanceNumber;
		this.createdBy = createdBy;
		this.createdDate = createdDate;
		this.formOwner = formOwner;
		this.businessUnit = businessUnit;
		this.costCenter = costCenter;
		this.stat = status;
		this.templateName = templateName;
		this.description = description;
		this.availableBudget = availableBudget;
		this.estimatedBudget = estimatedBudget;
		this.groupCode = groupCode;
		this.baseCurrency = currency;
	}

	// Export report
	public SourcingFormRequestPojo(String id, String formId, String sourcingFormName, String referanceNumber, String createdBy, Date createdDate, String formOwner, String businessUnit, String costCenter, String currency, String status, String groupCode, String templateName, String description, BigDecimal availableBudget, BigDecimal estimatedBudget, Date submittedDate, Date approvedDate, BigDecimal approvalDaysHours, BigDecimal approvalTotalLevels, BigDecimal approvalTotalUsers) {
		this.id = id;
		this.formId = formId;
		this.sourcingFormName = sourcingFormName;
		this.referanceNumber = referanceNumber;
		this.createdBy = createdBy;
		this.createdDate = createdDate;
		this.formOwner = formOwner;
		this.businessUnit = businessUnit;
		this.costCenter = costCenter;
		this.baseCurrency = currency;
		this.stat = status;
		this.groupCode = groupCode;
		this.templateName = templateName;
		this.description = description;
		this.availableBudget = availableBudget;
		this.estimatedBudget = estimatedBudget;
		this.submittedDate = submittedDate;
		this.approvedDate = approvedDate;
		if (approvalDaysHours != null) {
			this.approvalDaysHours = approvalDaysHours.intValue();
		}
		if (approvalTotalLevels != null) {
			this.approvalTotalLevels = approvalTotalLevels.intValue();
		}
		if (approvalTotalUsers != null) {
			this.approvalTotalUsers = approvalTotalUsers.intValue();
		}
	}

	// Server side Listing data
	public SourcingFormRequestPojo(String id, String sourcingFormName, String referanceNumber, String formId, String description, Date createdDate, String createdBy, String formOwner, String businessUnit, String costCenter, SourcingFormStatus status, String currency, String templateName, BigDecimal budgetAmount, BigDecimal historicAmount, Date submittedDate, Date approvedDate, BigDecimal approvalDaysHours, Integer approvalTotalLevels, Integer approvalTotalUsers, String groupCode) {
		this.id = id;
		this.sourcingFormName = sourcingFormName;
		this.referanceNumber = referanceNumber;
		this.formId = formId;
		this.description = description;
		this.createdDate = createdDate;
		this.createdBy = createdBy;
		this.formOwner = formOwner;
		this.businessunit = businessUnit;
		this.costcenter = costCenter;
		this.status = status;
		this.baseCurrency = currency;
		this.templateName = templateName;
		this.availableBudget = budgetAmount;
		this.estimatedBudget = historicAmount;
		this.submittedDate = submittedDate;
		this.approvedDate = approvedDate;
		if (approvalTotalLevels != null) {
			this.approvalTotalLevels = approvalTotalLevels;
		}
		if (approvalTotalUsers != null) {
			this.approvalTotalUsers = approvalTotalUsers;
		}
		BigDecimal approvalDays = BigDecimal.ZERO;
		if (approvalDaysHours != null) {
			approvalDays = new BigDecimal((double) approvalDaysHours.intValue() / 24);
			approvalDays = approvalDays.setScale(2, RoundingMode.HALF_EVEN);
			this.approvalDaysHours = approvalDays.intValue();
		}
		this.groupCode = groupCode;
	}

	public SourcingFormRequestPojo(String id, Date createdDate, String sourcingFormName, String referanceNumber, String formId, String description, String createdBy, SourcingFormStatus status, String formOwner, String businessUnit, String costCenter, String currency, String templateName, BigDecimal budgetAmount, BigDecimal historicAmount, Date submittedDate, Date approvedDate, BigDecimal approvalDaysHours, Integer approvalTotalLevels, Integer approvalTotalUsers, String groupCode) {
		this.id = id;
		this.sourcingFormName = sourcingFormName;
		this.referanceNumber = referanceNumber;
		this.formId = formId;
		this.description = description;
		this.createdDate = createdDate;
		this.createdBy = createdBy;
		this.formOwner = formOwner;
		this.businessunit = businessUnit;
		this.costCenter = costCenter;
		this.status = status;
		this.baseCurrency = currency;
		this.templateName = templateName;
		this.availableBudget = budgetAmount;
		this.estimatedBudget = historicAmount;
		this.submittedDate = submittedDate;
		this.approvedDate = approvedDate;
		if (approvalTotalLevels != null) {
			this.approvalTotalLevels = approvalTotalLevels;
		}
		if (approvalTotalUsers != null) {
			this.approvalTotalUsers = approvalTotalUsers;
		}
		BigDecimal approvalDays = BigDecimal.ZERO;
		if (approvalDaysHours != null) {
			approvalDays = new BigDecimal((double) approvalDaysHours.intValue() / 24);
			approvalDays = approvalDays.setScale(2, RoundingMode.HALF_EVEN);
			this.approvalDaysHours = approvalDays.intValue();
		}
		this.groupCode = groupCode;
	}

	// PH-2167
	public SourcingFormRequestPojo(String id, Date createdDate, String sourcingFormName, String referanceNumber, String formId, String description, String createdBy, SourcingFormStatus status, String formOwner, String procurementMethod, String procurementCategory, String businessUnit, String costCenter, String currency, String templateName, BigDecimal budgetAmount, BigDecimal estimatedBudget, BigDecimal historicAmount, Date submittedDate, Date approvedDate, BigDecimal approvalDaysHours, Integer approvalTotalLevels, Integer approvalTotalUsers, String groupCode, String groupCodeOld) {
		this.id = id;
		this.sourcingFormName = sourcingFormName;
		this.referanceNumber = referanceNumber;
		this.formId = formId;
		this.description = description;
		this.createdDate = createdDate;
		this.createdBy = createdBy;
		this.formOwner = formOwner;
		this.procurementMethod = procurementMethod;
		this.procurementCategories = procurementCategory;
		this.businessunit = businessUnit;
		this.costCenter = costCenter;
		this.status = status;
		this.baseCurrency = currency;
		this.templateName = templateName;
		this.availableBudget = budgetAmount;
		this.estimatedBudget = estimatedBudget;
		this.historicAmount = historicAmount;
		this.submittedDate = submittedDate;
		this.approvedDate = approvedDate;
		if (approvalTotalLevels != null) {
			this.approvalTotalLevels = approvalTotalLevels;
		}
		if (approvalTotalUsers != null) {
			this.approvalTotalUsers = approvalTotalUsers;
		}
		BigDecimal approvalDays = BigDecimal.ZERO;
		if (approvalDaysHours != null) {
			approvalDays = new BigDecimal((double) approvalDaysHours.intValue() / 24);
			approvalDays = approvalDays.setScale(2, RoundingMode.HALF_EVEN);
			this.approvalDaysHours = approvalDays.intValue();
		}
		this.groupCode = StringUtils.checkString(groupCode).length() > 0 ? groupCode : (StringUtils.checkString(groupCodeOld).length() > 0 ? groupCodeOld : "");
	}

	/**
	 * @return the sourcingrequestid
	 */
	public String getSourcingrequestid() {
		return sourcingrequestid;
	}

	/**
	 * @param sourcingrequestid the sourcingrequestid to set
	 */
	public void setSourcingrequestid(String sourcingrequestid) {
		this.sourcingrequestid = sourcingrequestid;
	}

	/**
	 * @return the availableBudget
	 */
	public BigDecimal getAvailableBudget() {
		return availableBudget;
	}

	/**
	 * @param availableBudget the availableBudget to set
	 */
	public void setAvailableBudget(BigDecimal availableBudget) {
		this.availableBudget = availableBudget;
	}

	/**
	 * @return the estimatedBudget
	 */
	public BigDecimal getEstimatedBudget() {
		return estimatedBudget;
	}

	/**
	 * @param estimatedBudget the estimatedBudget to set
	 */
	public void setEstimatedBudget(BigDecimal estimatedBudget) {
		this.estimatedBudget = estimatedBudget;
	}

	/**
	 * @return the templateName
	 */
	public String getTemplateName() {
		return templateName;
	}

	/**
	 * @param templateName the templateName to set
	 */
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
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
	 * @return the stat
	 */
	public String getStat() {
		return stat;
	}

	/**
	 * @param stat the stat to set
	 */
	public void setStat(String stat) {
		this.stat = stat;
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
	 * @return the formId
	 */
	public String getFormId() {
		return formId;
	}

	/**
	 * @param formId the formId to set
	 */
	public void setFormId(String formId) {
		this.formId = formId;
	}

	/**
	 * @return the formType
	 */
	public String getFormType() {
		return formType;
	}

	/**
	 * @param formType the formType to set
	 */
	public void setFormType(String formType) {
		this.formType = formType;
	}

	/**
	 * @return the formOwner
	 */
	public String getFormOwner() {
		return formOwner;
	}

	/**
	 * @param formOwner the formOwner to set
	 */
	public void setFormOwner(String formOwner) {
		this.formOwner = formOwner;
	}

	/**
	 * @return the referanceNumber
	 */
	public String getReferanceNumber() {
		return referanceNumber;
	}

	/**
	 * @param referanceNumber the referanceNumber to set
	 */
	public void setReferanceNumber(String referanceNumber) {
		this.referanceNumber = referanceNumber;
	}

	/**
	 * @return the sourcingFormName
	 */
	public String getSourcingFormName() {
		return sourcingFormName;
	}

	/**
	 * @param sourcingFormName the sourcingFormName to set
	 */
	public void setSourcingFormName(String sourcingFormName) {
		this.sourcingFormName = sourcingFormName;
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
	 * @return the tenantId
	 */
	public String getTenantId() {
		return tenantId;
	}

	/**
	 * @param tenantId the tenantId to set
	 */
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
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
	 * @return the actionBy
	 */
	public String getActionBy() {
		return actionBy;
	}

	/**
	 * @param actionBy the actionBy to set
	 */
	public void setActionBy(String actionBy) {
		this.actionBy = actionBy;
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
	 * @return the actionDate
	 */
	public Date getActionDate() {
		return actionDate;
	}

	/**
	 * @param actionDate the actionDate to set
	 */
	public void setActionDate(Date actionDate) {
		this.actionDate = actionDate;
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
	 * @return the status
	 */
	public SourcingFormStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(SourcingFormStatus status) {
		this.status = status;
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
	 * @return the urgentForm
	 */
	public Boolean getUrgentForm() {
		return urgentForm;
	}

	/**
	 * @param urgentForm the urgentForm to set
	 */
	public void setUrgentForm(Boolean urgentForm) {
		this.urgentForm = urgentForm;
	}

	/**
	 * @return the formDetailCompleted
	 */
	public Boolean getFormDetailCompleted() {
		return formDetailCompleted;
	}

	/**
	 * @param formDetailCompleted the formDetailCompleted to set
	 */
	public void setFormDetailCompleted(Boolean formDetailCompleted) {
		this.formDetailCompleted = formDetailCompleted;
	}

	/**
	 * @return the cqCompleted
	 */
	public Boolean getCqCompleted() {
		return cqCompleted;
	}

	/**
	 * @param cqCompleted the cqCompleted to set
	 */
	public void setCqCompleted(Boolean cqCompleted) {
		this.cqCompleted = cqCompleted;
	}

	/**
	 * @return the bqCompleted
	 */
	public Boolean getBqCompleted() {
		return bqCompleted;
	}

	/**
	 * @param bqCompleted the bqCompleted to set
	 */
	public void setBqCompleted(Boolean bqCompleted) {
		this.bqCompleted = bqCompleted;
	}

	/**
	 * @return the summaryCompleted
	 */
	public Boolean getSummaryCompleted() {
		return summaryCompleted;
	}

	/**
	 * @param summaryCompleted the summaryCompleted to set
	 */
	public void setSummaryCompleted(Boolean summaryCompleted) {
		this.summaryCompleted = summaryCompleted;
	}

	/**
	 * @return the concludeRemarks
	 */
	public String getConcludeRemarks() {
		return concludeRemarks;
	}

	/**
	 * @param concludeRemarks the concludeRemarks to set
	 */
	public void setConcludeRemarks(String concludeRemarks) {
		this.concludeRemarks = concludeRemarks;
	}

	/**
	 * @return the sourcingFormStatus
	 */
	public String getSourcingFormStatus() {
		return sourcingFormStatus;
	}

	/**
	 * @param sourcingFormStatus the sourcingFormStatus to set
	 */
	public void setSourcingFormStatus(String sourcingFormStatus) {
		this.sourcingFormStatus = sourcingFormStatus;
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
	 * @return the nameofrequest
	 */
	public String getNameofrequest() {
		return nameofrequest;
	}

	/**
	 * @param nameofrequest the nameofrequest to set
	 */
	public void setNameofrequest(String nameofrequest) {
		this.nameofrequest = nameofrequest;
	}

	/**
	 * @return the requestid
	 */
	public String getRequestid() {
		return requestid;
	}

	/**
	 * @param requestid the requestid to set
	 */
	public void setRequestid(String requestid) {
		this.requestid = requestid;
	}

	/**
	 * @return the requestowner
	 */
	public String getRequestowner() {
		return requestowner;
	}

	/**
	 * @param requestowner the requestowner to set
	 */
	public void setRequestowner(String requestowner) {
		this.requestowner = requestowner;
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
	 * @return the businessunit
	 */
	public String getBusinessunit() {
		return businessunit;
	}

	/**
	 * @param businessunit the businessunit to set
	 */
	public void setBusinessunit(String businessunit) {
		this.businessunit = businessunit;
	}

	/**
	 * @return the costcenter
	 */
	public String getCostcenter() {
		return costcenter;
	}

	/**
	 * @param costcenter the costcenter to set
	 */
	public void setCostcenter(String costcenter) {
		this.costcenter = costcenter;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SourcingFormRequestPojo [status=" + status + ", costCenter=" + costCenter + ", stat=" + stat + ", getAvailableBudget()=" + getAvailableBudget() + ", getEstimatedBudget()=" + getEstimatedBudget() + ", getTemplateName()=" + getTemplateName() + ", getGroupCode()=" + getGroupCode() + ", getStat()=" + getStat() + ", getEventIds()=" + getEventIds() + ", getId()=" + getId() + ", getFormId()=" + getFormId() + ", getFormType()=" + getFormType() + ", getFormOwner()=" + getFormOwner() + ", getReferanceNumber()=" + getReferanceNumber() + ", getSourcingFormName()=" + getSourcingFormName() + ", getDescription()=" + getDescription() + ", getTenantId()=" + getTenantId() + ", getCreatedBy()=" + getCreatedBy() + ", getActionBy()=" + getActionBy() + ", getModifiedBy()=" + getModifiedBy() + ", getCreatedDate()=" + getCreatedDate() + ", getActionDate()=" + getActionDate() + ", getModifiedDate()=" + getModifiedDate() + ", getStatus()=" + getStatus() + ", getBusinessUnit()=" + getBusinessUnit() + ", getUrgentForm()=" + getUrgentForm() + ", getFormDetailCompleted()=" + getFormDetailCompleted() + ", getCqCompleted()=" + getCqCompleted() + ", getBqCompleted()=" + getBqCompleted() + ", getSummaryCompleted()=" + getSummaryCompleted() + ", getConcludeRemarks()=" + getConcludeRemarks() + ", getSourcingFormStatus()=" + getSourcingFormStatus() + ", getCostCenter()=" + getCostCenter() + ", getNameofrequest()=" + getNameofrequest() + ", getRequestid()=" + getRequestid() + ", getRequestowner()=" + getRequestowner() + ", getReferencenumber()=" + getReferencenumber() + ", getBusinessunit()=" + getBusinessunit() + ", getCostcenter()=" + getCostcenter() + ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString() + "]";
	}

	public String getBaseCurrency() {
		return baseCurrency;
	}

	public void setBaseCurrency(String baseCurrency) {
		this.baseCurrency = baseCurrency;
	}

	/**
	 * @return the submittedDate
	 */
	public Date getSubmittedDate() {
		return submittedDate;
	}

	/**
	 * @param submittedDate the submittedDate to set
	 */
	public void setSubmittedDate(Date submittedDate) {
		this.submittedDate = submittedDate;
	}

	/**
	 * @return the approvedDate
	 */
	public Date getApprovedDate() {
		return approvedDate;
	}

	/**
	 * @param approvedDate the approvedDate to set
	 */
	public void setApprovedDate(Date approvedDate) {
		this.approvedDate = approvedDate;
	}

	/**
	 * @return the approvalDaysHours
	 */
	public Integer getApprovalDaysHours() {
		return approvalDaysHours;
	}

	/**
	 * @param approvalDaysHours the approvalDaysHours to set
	 */
	public void setApprovalDaysHours(Integer approvalDaysHours) {
		this.approvalDaysHours = approvalDaysHours;
	}

	/**
	 * @return the approvalTotalLevels
	 */
	public Integer getApprovalTotalLevels() {
		return approvalTotalLevels;
	}

	/**
	 * @param approvalTotalLevels the approvalTotalLevels to set
	 */
	public void setApprovalTotalLevels(Integer approvalTotalLevels) {
		this.approvalTotalLevels = approvalTotalLevels;
	}

	/**
	 * @return the approvalTotalUsers
	 */
	public Integer getApprovalTotalUsers() {
		return approvalTotalUsers;
	}

	/**
	 * @param approvalTotalUsers the approvalTotalUsers to set
	 */
	public void setApprovalTotalUsers(Integer approvalTotalUsers) {
		this.approvalTotalUsers = approvalTotalUsers;
	}

	public String getCreatedDateStr() {
		return createdDateStr;
	}

	public void setCreatedDateStr(String createdDateStr) {
		this.createdDateStr = createdDateStr;
	}

	public String getApprovedDateStr() {
		return approvedDateStr;
	}

	public void setApprovedDateStr(String approvedDateStr) {
		this.approvedDateStr = approvedDateStr;
	}

	public String getSubmittedDatestr() {
		return submittedDatestr;
	}

	public void setSubmittedDatestr(String submittedDatestr) {
		this.submittedDatestr = submittedDatestr;
	}

	public BigDecimal getHistoricAmount() {
		return historicAmount;
	}

	public void setHistoricAmount(BigDecimal historicAmount) {
		this.historicAmount = historicAmount;
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

	public String getGroupCodeDescription() {
		return groupCodeDescription;
	}

	public void setGroupCodeDescription(String groupCodeDescription) {
		this.groupCodeDescription = groupCodeDescription;
	}
	
	

}