package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.entity.SupplierPerformanceEvaluatorUser;
import com.privasia.procurehere.core.entity.SupplierPerformanceForm;
import com.privasia.procurehere.core.enums.SupplierPerformanceFormStatus;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;

public class SupplierPerformanceEvaluationPojo implements Serializable {

	private static final long serialVersionUID = -2598705125320835038L;

	private String id;

	private String formId;

	private String referenceNumber;

	private String evaluator;

	private String formOwner;

	private String supplierName;

	private SupplierPerformanceFormStatus formStatus;

	private String unitName;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date evaluationStartDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date evaluationEndDate;

	private String formName;

	private String referenceName;

	private String procurementCategory;

	private Long totalEvaluator;

	private Integer totalEvaluationComplete;

	private String recurrence;

	private Boolean isRecurrenceEvaluation;

	private Integer recurrenceEvaluation;

	private Integer noOfRecurrence;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date concludeDate;

	private BigDecimal overallScore;

	private Integer scoreRating;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date createdDate;

	private String evaluationRecurrence;

	private Integer evaluationDueBy;

	private List<EvaluationAuditPojo> auditDetails;

	private List<PerformanceEvaluationCriteriaPojo> criteriaList;

	private List<SupplierPerformanceEvaluatorUser> columns;

	private String evaluationStartDateStr;

	private String evaluationEndDateStr;

	private String reminderDate;

	private String supplierCode;
	private String supplierTag;

	private List<EvaluationApprovalsPojo> approvals;

	private List<EvaluationCommentsPojo> comments;

	private List<PerformanceEvaluationCriteriaPojo> consolCriteriaList;

	private String overallScoreStr;

	private String rating;

	private String ratingDescription;

	private String noOfRecurrenceStr;

	private String buyerName;

	private String unitCode;

	private List<SupplierPerformanceEvaluationPojo> overallScoreByBusinessUnit;

	private List<SupplierPerformanceEvaluationPojo> overallScoreBySpForm;

	private String criteriaName;

	private BigDecimal totalOverallScore;

	private String unitId;

	private String eventId;

	private String eventType;

	private BigDecimal weightage;

	public SupplierPerformanceEvaluationPojo() {
	}

	public SupplierPerformanceEvaluationPojo(String id, String formId, String referenceNumber, String supplierName, Date evaluationStartDate, Date evaluationEndDate, String formOwner, String unitName) {
		this.id = id;
		this.formId = formId;
		this.referenceNumber = referenceNumber;
		this.supplierName = supplierName;
		this.evaluationStartDate = evaluationStartDate;
		this.evaluationEndDate = evaluationEndDate;
		this.formOwner = formOwner;
		this.unitName = unitName;
	}

	public SupplierPerformanceEvaluationPojo(String id, String formId, String referenceNumber, String supplierName, Date evaluationStartDate, Date evaluationEndDate, String evaluator, String formOwner, String unitName) {
		this.id = id;
		this.formId = formId;
		this.referenceNumber = referenceNumber;
		this.supplierName = supplierName;
		this.evaluationStartDate = evaluationStartDate;
		this.evaluationEndDate = evaluationEndDate;
		this.formOwner = formOwner;
		this.unitName = unitName;
		this.evaluator = evaluator;
	}

	public SupplierPerformanceEvaluationPojo(String id, String formId, String formName, String referenceNumber, String referenceName, String formOwner, String procurementCategory, String unitName, String supplierName, Long totalEvaluator, Integer totalEvaluationComplete, Date evaluationStartDate, Date evaluationEndDate, Integer recurrenceEvaluation, Boolean isRecurrenceEvaluation, String formStatus, Date concludeDate, Integer scoreRating, BigDecimal overallScore, Date createdDate) {
		this.id = id;
		this.formId = formId;
		this.formName = formName;
		this.referenceNumber = referenceNumber;
		this.referenceName = referenceName;
		this.formOwner = formOwner;
		this.procurementCategory = procurementCategory;
		this.unitName = unitName;
		this.supplierName = supplierName;
		this.totalEvaluator = totalEvaluator;
		this.totalEvaluationComplete = totalEvaluationComplete;
		this.evaluationStartDate = evaluationStartDate;
		this.evaluationEndDate = evaluationEndDate;
		this.recurrenceEvaluation = recurrenceEvaluation;
		this.isRecurrenceEvaluation = isRecurrenceEvaluation;
		this.formStatus = SupplierPerformanceFormStatus.fromString(formStatus);
		this.concludeDate = concludeDate;
		this.scoreRating = scoreRating;
		this.overallScore = overallScore;
		this.createdDate = createdDate;
	}

	public SupplierPerformanceEvaluationPojo(String id, String evaluator) {
		this.id = id;
		this.evaluator = evaluator;
	}

	public SupplierPerformanceEvaluationPojo(SupplierPerformanceForm spf) {
		this.id = spf.getId();
		this.formId = spf.getFormId();
		this.formName = spf.getFormName();
		this.referenceNumber = spf.getReferenceNumber();
		this.referenceName = spf.getReferenceName();
		this.evaluationStartDate = spf.getEvaluationStartDate();
		this.evaluationEndDate = spf.getEvaluationEndDate();
		this.recurrence = spf.getRecurrenceEvaluation() != null ? "YES" : "";
		this.recurrenceEvaluation = spf.getRecurrenceEvaluation();
		this.formStatus = spf.getFormStatus();
		this.concludeDate = spf.getConcludeDate();
		this.overallScore = spf.getOverallScore();
		this.createdDate = spf.getCreatedDate();
		this.noOfRecurrence = spf.getNoOfRecurrence();
	}

	public SupplierPerformanceEvaluationPojo(String id, BigDecimal overallScore, String rating, String ratingDescription) {
		this.id = id;
		this.overallScore = overallScore;
		this.rating = rating;
		this.ratingDescription = ratingDescription;
	}

	public SupplierPerformanceEvaluationPojo(String id, String formId, BigDecimal overallScore, String procurementCategories, String displayName, Integer rating, String ratingDescription) {
		this.id = id;
		this.formId = formId;
		this.overallScore = overallScore.setScale(0, RoundingMode.HALF_UP);
		this.procurementCategory = procurementCategories;
		this.unitName = displayName;
		this.rating = rating != null ? rating.toString() : "";
		this.ratingDescription = ratingDescription;
	}

	public SupplierPerformanceEvaluationPojo(String id, String formId, String eventId, BigDecimal overallScore, String procurementCategories, String displayName, Integer rating, String ratingDescription) {
		this.id = id;
		this.formId = formId;
		this.eventId = eventId;
		this.overallScore = overallScore.setScale(0, RoundingMode.HALF_UP);
		this.procurementCategory = procurementCategories;
		this.unitName = displayName;
		this.rating = rating != null ? rating.toString() : "";
		this.ratingDescription = ratingDescription;
	}

	// spfc.id, spf.formId, spf.totalScore)
	public SupplierPerformanceEvaluationPojo(String formCriteriaId, String criteriaName, BigDecimal overallScore) {
		this.id = formCriteriaId;
		this.criteriaName = criteriaName;
		this.overallScore = overallScore;
	}

	public SupplierPerformanceEvaluationPojo(String formCriteriaId, String criteriaName, BigDecimal overallScore, BigDecimal weightage) {
		this.id = formCriteriaId;
		this.criteriaName = criteriaName;
		this.overallScore = overallScore;
		this.weightage = weightage;
	}

	public SupplierPerformanceEvaluationPojo(String id, String formId, String formName) {
		this.id = id;
		this.formId = formId;
		this.formName = formName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFormId() {
		return formId;
	}

	public void setFormId(String formId) {
		this.formId = formId;
	}

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public Date getEvaluationStartDate() {
		return evaluationStartDate;
	}

	public void setEvaluationStartDate(Date evaluationStartDate) {
		this.evaluationStartDate = evaluationStartDate;
	}

	public Date getEvaluationEndDate() {
		return evaluationEndDate;
	}

	public void setEvaluationEndDate(Date evaluationEndDate) {
		this.evaluationEndDate = evaluationEndDate;
	}

	public String getEvaluator() {
		return evaluator;
	}

	public void setEvaluator(String evaluator) {
		this.evaluator = evaluator;
	}

	public String getFormOwner() {
		return formOwner;
	}

	public void setFormOwner(String formOwner) {
		this.formOwner = formOwner;
	}

	public String getFormName() {
		return formName;
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}

	public String getReferenceName() {
		return referenceName;
	}

	public void setReferenceName(String referenceName) {
		this.referenceName = referenceName;
	}

	public String getProcurementCategory() {
		return procurementCategory;
	}

	public void setProcurementCategory(String procurementCategory) {
		this.procurementCategory = procurementCategory;
	}

	public Long getTotalEvaluator() {
		return totalEvaluator;
	}

	public void setTotalEvaluator(Long totalEvaluator) {
		this.totalEvaluator = totalEvaluator;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Integer getTotalEvaluationComplete() {
		return totalEvaluationComplete;
	}

	public void setTotalEvaluationComplete(Integer totalEvaluationComplete) {
		this.totalEvaluationComplete = totalEvaluationComplete;
	}

	public Integer getRecurrenceEvaluation() {
		return recurrenceEvaluation;
	}

	public void setRecurrenceEvaluation(Integer recurrenceEvaluation) {
		this.recurrenceEvaluation = recurrenceEvaluation;
	}

	public Date getConcludeDate() {
		return concludeDate;
	}

	public void setConcludeDate(Date concludeDate) {
		this.concludeDate = concludeDate;
	}

	public BigDecimal getOverallScore() {
		return overallScore;
	}

	public void setOverallScore(BigDecimal overallScore) {
		this.overallScore = overallScore;
	}

	public Integer getScoreRating() {
		return scoreRating;
	}

	public void setScoreRating(Integer scoreRating) {
		this.scoreRating = scoreRating;
	}

	/**
	 * @return the evaluationRecurrence
	 */
	public String getEvaluationRecurrence() {
		return evaluationRecurrence;
	}

	/**
	 * @param evaluationRecurrence the evaluationRecurrence to set
	 */
	public void setEvaluationRecurrence(String evaluationRecurrence) {
		this.evaluationRecurrence = evaluationRecurrence;
	}

	/**
	 * @return the evaluationDueBy
	 */
	public Integer getEvaluationDueBy() {
		return evaluationDueBy;
	}

	/**
	 * @param evaluationDueBy the evaluationDueBy to set
	 */
	public void setEvaluationDueBy(Integer evaluationDueBy) {
		this.evaluationDueBy = evaluationDueBy;
	}

	/**
	 * @return the auditDetails
	 */
	public List<EvaluationAuditPojo> getAuditDetails() {
		return auditDetails;
	}

	/**
	 * @param auditDetails the auditDetails to set
	 */
	public void setAuditDetails(List<EvaluationAuditPojo> auditDetails) {
		this.auditDetails = auditDetails;
	}

	/**
	 * @return the criteriaList
	 */
	public List<PerformanceEvaluationCriteriaPojo> getCriteriaList() {
		return criteriaList;
	}

	/**
	 * @param criteriaList the criteriaList to set
	 */
	public void setCriteriaList(List<PerformanceEvaluationCriteriaPojo> criteriaList) {
		this.criteriaList = criteriaList;
	}

	/**
	 * @return the evaluationStartDateStr
	 */
	public String getEvaluationStartDateStr() {
		return evaluationStartDateStr;
	}

	/**
	 * @param evaluationStartDateStr the evaluationStartDateStr to set
	 */
	public void setEvaluationStartDateStr(String evaluationStartDateStr) {
		this.evaluationStartDateStr = evaluationStartDateStr;
	}

	/**
	 * @return the evaluationEndDateStr
	 */
	public String getEvaluationEndDateStr() {
		return evaluationEndDateStr;
	}

	/**
	 * @param evaluationEndDateStr the evaluationEndDateStr to set
	 */
	public void setEvaluationEndDateStr(String evaluationEndDateStr) {
		this.evaluationEndDateStr = evaluationEndDateStr;
	}

	/**
	 * @return the reminderDate
	 */
	public String getReminderDate() {
		return reminderDate;
	}

	/**
	 * @param reminderDate the reminderDate to set
	 */
	public void setReminderDate(String reminderDate) {
		this.reminderDate = reminderDate;
	}

	public SupplierPerformanceFormStatus getFormStatus() {
		return formStatus;
	}

	public void setFormStatus(SupplierPerformanceFormStatus formStatus) {
		this.formStatus = formStatus;
	}

	public String getSupplierCode() {
		return supplierCode;
	}

	public void setSupplierCode(String supplierCode) {
		this.supplierCode = supplierCode;
	}

	public String getSupplierTag() {
		return supplierTag;
	}

	public void setSupplierTag(String supplierTag) {
		this.supplierTag = supplierTag;
	}

	/**
	 * @return the columns
	 */
	public List<SupplierPerformanceEvaluatorUser> getColumns() {
		return columns;
	}

	/**
	 * @param columns the columns to set
	 */
	public void setColumns(List<SupplierPerformanceEvaluatorUser> columns) {
		this.columns = columns;
	}

	/**
	 * @return the approvals
	 */
	public List<EvaluationApprovalsPojo> getApprovals() {
		return approvals;
	}

	/**
	 * @param approvals the approvals to set
	 */
	public void setApprovals(List<EvaluationApprovalsPojo> approvals) {
		this.approvals = approvals;
	}

	/**
	 * @return the comments
	 */
	public List<EvaluationCommentsPojo> getComments() {
		return comments;
	}

	/**
	 * @param comments the comments to set
	 */
	public void setComments(List<EvaluationCommentsPojo> comments) {
		this.comments = comments;
	}

	/**
	 * @return the consolCriteriaList
	 */
	public List<PerformanceEvaluationCriteriaPojo> getConsolCriteriaList() {
		return consolCriteriaList;
	}

	/**
	 * @param consolCriteriaList the consolCriteriaList to set
	 */
	public void setConsolCriteriaList(List<PerformanceEvaluationCriteriaPojo> consolCriteriaList) {
		this.consolCriteriaList = consolCriteriaList;
	}

	/**
	 * @return the overallScoreStr
	 */
	public String getOverallScoreStr() {
		return overallScoreStr;
	}

	/**
	 * @param overallScoreStr the overallScoreStr to set
	 */
	public void setOverallScoreStr(String overallScoreStr) {
		this.overallScoreStr = overallScoreStr;
	}

	/**
	 * @return the rating
	 */
	public String getRating() {
		return rating;
	}

	/**
	 * @param rating the rating to set
	 */
	public void setRating(String rating) {
		this.rating = rating;
	}

	/**
	 * @return the ratingDescription
	 */
	public String getRatingDescription() {
		return ratingDescription;
	}

	/**
	 * @param ratingDescription the ratingDescription to set
	 */
	public void setRatingDescription(String ratingDescription) {
		this.ratingDescription = ratingDescription;
	}

	public String getRecurrence() {
		return recurrence;
	}

	public void setRecurrence(String recurrence) {
		this.recurrence = recurrence;
	}

	/**
	 * @return the isRecurrenceEvaluation
	 */
	public Boolean getIsRecurrenceEvaluation() {
		return isRecurrenceEvaluation;
	}

	/**
	 * @param isRecurrenceEvaluation the isRecurrenceEvaluation to set
	 */
	public void setIsRecurrenceEvaluation(Boolean isRecurrenceEvaluation) {
		this.isRecurrenceEvaluation = isRecurrenceEvaluation;
	}

	public Integer getNoOfRecurrence() {
		return noOfRecurrence;
	}

	public void setNoOfRecurrence(Integer noOfRecurrence) {
		this.noOfRecurrence = noOfRecurrence;
	}

	/**
	 * @return the noOfRecurrenceStr
	 */
	public String getNoOfRecurrenceStr() {
		return noOfRecurrenceStr;
	}

	/**
	 * @param noOfRecurrenceStr the noOfRecurrenceStr to set
	 */
	public void setNoOfRecurrenceStr(String noOfRecurrenceStr) {
		this.noOfRecurrenceStr = noOfRecurrenceStr;
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
	 * @return the buyerName
	 */
	public String getBuyerName() {
		return buyerName;
	}

	/**
	 * @param buyerName the buyerName to set
	 */
	public void setBuyerName(String buyerName) {
		this.buyerName = buyerName;
	}

	/**
	 * @return the overallScoreByBusinessUnit
	 */
	public List<SupplierPerformanceEvaluationPojo> getOverallScoreByBusinessUnit() {
		return overallScoreByBusinessUnit;
	}

	/**
	 * @param overallScoreByBusinessUnit the overallScoreByBusinessUnit to set
	 */
	public void setOverallScoreByBusinessUnit(List<SupplierPerformanceEvaluationPojo> overallScoreByBusinessUnit) {
		this.overallScoreByBusinessUnit = overallScoreByBusinessUnit;
	}

	/**
	 * @return the overallScoreBySpForm
	 */
	public List<SupplierPerformanceEvaluationPojo> getOverallScoreBySpForm() {
		return overallScoreBySpForm;
	}

	/**
	 * @param overallScoreBySpForm the overallScoreBySpForm to set
	 */
	public void setOverallScoreBySpForm(List<SupplierPerformanceEvaluationPojo> overallScoreBySpForm) {
		this.overallScoreBySpForm = overallScoreBySpForm;
	}

	/**
	 * @return the criteriaName
	 */
	public String getCriteriaName() {
		return criteriaName;
	}

	/**
	 * @param criteriaName the criteriaName to set
	 */
	public void setCriteriaName(String criteriaName) {
		this.criteriaName = criteriaName;
	}

	/**
	 * @return the totalOverallScore
	 */
	public BigDecimal getTotalOverallScore() {
		return totalOverallScore;
	}

	/**
	 * @param totalOverallScore the totalOverallScore to set
	 */
	public void setTotalOverallScore(BigDecimal totalOverallScore) {
		this.totalOverallScore = totalOverallScore;
	}

	/**
	 * @return the unitId
	 */
	public String getUnitId() {
		return unitId;
	}

	/**
	 * @param unitId the unitId to set
	 */
	public void setUnitId(String unitId) {
		this.unitId = unitId;
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
	 * @return the eventType
	 */
	public String getEventType() {
		return eventType;
	}

	/**
	 * @param eventType the eventType to set
	 */
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	/**
	 * @return the weightage
	 */
	public BigDecimal getWeightage() {
		return weightage;
	}

	/**
	 * @param weightage the weightage to set
	 */
	public void setWeightage(BigDecimal weightage) {
		this.weightage = weightage;
	}

}