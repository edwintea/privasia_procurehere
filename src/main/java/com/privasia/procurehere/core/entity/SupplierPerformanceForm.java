/**
 * 
 */
package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.RfxTypes;
import com.privasia.procurehere.core.enums.SupplierPerformanceFormStatus;
import com.privasia.procurehere.core.enums.converter.RfxTypesCoverter;
import com.privasia.procurehere.core.pojo.SupplierPerformanceEvaluationPojo;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;
import org.hibernate.annotations.Type;

/**
 * @author Jayshree
 */
@Entity
@Table(name = "PROC_SUPPLIER_PERFORMANCE_FORM")
@SqlResultSetMappings({ @SqlResultSetMapping(name = "pendingSPAppResult", classes = { @ConstructorResult(targetClass = SupplierPerformanceEvaluationPojo.class, columns = { //
		@ColumnResult(name = "id"), //
		@ColumnResult(name = "formId"), //
		@ColumnResult(name = "referenceNumber"), //
		@ColumnResult(name = "supplierName"), //
		@ColumnResult(name = "evaluationStartDate"), //
		@ColumnResult(name = "evaluationEndDate"), //
		@ColumnResult(name = "evaluator"), //
		@ColumnResult(name = "formOwner"), //
		@ColumnResult(name = "unitName") }) }//
), //
		@SqlResultSetMapping(name = "pendingSPResult", classes = { @ConstructorResult(targetClass = SupplierPerformanceEvaluationPojo.class, columns = { //
				@ColumnResult(name = "id"), //
				@ColumnResult(name = "formId"), //
				@ColumnResult(name = "referenceNumber"), //
				@ColumnResult(name = "supplierName"), //
				@ColumnResult(name = "evaluationStartDate"), //
				@ColumnResult(name = "evaluationEndDate"), //
				@ColumnResult(name = "formOwner"), //
				@ColumnResult(name = "unitName") }) }//
		), //
		@SqlResultSetMapping(name = "supplierPerformanceList", classes = { @ConstructorResult(targetClass = SupplierPerformanceEvaluationPojo.class, columns = { //
				@ColumnResult(name = "id"), //
				@ColumnResult(name = "formId"), //
				@ColumnResult(name = "formName"), //
				@ColumnResult(name = "referenceNumber"), //
				@ColumnResult(name = "referenceName"), //
				@ColumnResult(name = "formOwner", type = String.class), //
				@ColumnResult(name = "procurementCategory", type = String.class), //
				@ColumnResult(name = "unitName", type = String.class), //
				@ColumnResult(name = "supplierName", type = String.class), //
				@ColumnResult(name = "totalEvaluator", type = Long.class), //
				@ColumnResult(name = "totalEvaluationComplete", type = Integer.class), //
				@ColumnResult(name = "evaluationStartDate", type = Date.class), //
				@ColumnResult(name = "evaluationEndDate", type = Date.class), //
				@ColumnResult(name = "recurrenceEvaluation", type = Integer.class), //
				@ColumnResult(name = "isRecurrenceEvaluation", type = Boolean.class), //
				@ColumnResult(name = "formStatus", type = String.class), //
				@ColumnResult(name = "concludeDate", type = Date.class), //
				@ColumnResult(name = "scoreRating", type = Integer.class), //
				@ColumnResult(name = "overallScore", type = BigDecimal.class), //
				@ColumnResult(name = "createdDate", type = Date.class)//
		}) }//
		)

})
public class SupplierPerformanceForm implements Serializable {

	private static final long serialVersionUID = 6812122490070409541L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@Column(name = "FORM_ID", length = 64, nullable = false)
	private String formId;

	@Column(name = "FORM_NAME", length = 70, nullable = false)
	@Size(min = 1, max = 70, message = "{spTemplate.formName.length}")
	private String formName;

	@Column(name = "REFERENCE_NUMBER", length = 64, nullable = true)
	@Size(min = 0, max = 64, message = "{spTemplate.referenceNumber.length}")
	private String referenceNumber;

	@Column(name = "REFERENCE_NAME", length = 200, nullable = true)
	@Size(min = 0, max = 200, message = "{spTemplate.referenceName.length}")
	private String referenceName;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "TEMPLATE_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_SP_FORM_TEMP_ID"))
	private SupplierPerformanceTemplate template;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "FORM_OWNER_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_SP_FORM_OWNER_ID"))
	private User formOwner;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "CREATED_BY_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_SP_FORM_CREATED_BY_ID"))
	private User createdBy;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "REF_FORM_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_SP_FORM_REF_FORM_ID"))
	SupplierPerformanceForm createdFromRef;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_DATE", nullable = true)
	private Date createdDate;

	@Column(name = "EVENT_ID", length = 65, nullable = true)
	private String eventId;

	@Convert(converter = RfxTypesCoverter.class)
	@Column(name = "EVENT_TYPE", length = 50, nullable = true)
	private RfxTypes eventType;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "AWARDED_SUPPLIER_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_SPF_AWARDED_SUP_ID"))
	private Supplier awardedSupplier;

	@Enumerated(EnumType.STRING)
	@Column(name = "FORM_STATUS")
	private SupplierPerformanceFormStatus formStatus = SupplierPerformanceFormStatus.DRAFT;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "PROCUREMENT_CATEGORY_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_SPF_PROCMT_CAT_ID"))
	private ProcurementCategories procurementCategory;

	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "BUSINESS_UNIT_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_SP_FORM_BU_ID"))
	private BusinessUnit businessUnit;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "form", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<SupplierPerformanceEvaluatorUser> evaluators;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "EVALUATION_START_DATE", nullable = true)
	private Date evaluationStartDate;

	@Transient
	private Date evaluationStartTime;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "EVALUATION_END_DATE", nullable = true)
	private Date evaluationEndDate;

	@Transient
	private Date evaluationEndTime;

	@Column(name = "OVERALL_SCORE", precision = 5, scale = 2, nullable = true)
	private BigDecimal overallScore;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH }, optional = true)
	@JoinColumn(name = "SCORE_RATING_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_SPF_SCORE_RATING_ID"))
	private ScoreRating scoreRating;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CONCLUDE_DATE", nullable = true)
	private Date concludeDate;

	@Column(name = "EVALUATION_DURATION", length = 3, nullable = true)
	private Integer evaluationDuration;

	@Column(name = "RECURRENCE_EVALUATION", length = 3, nullable = true)
	private Integer recurrenceEvaluation;

	@Column(name = "NO_OF_RECURRENCE", length = 3, nullable = true)
	private Integer noOfRecurrence;

	@Column(name = "RECURRENCE_EXECUTED", length = 3, nullable = true)
	private Integer recurrenceExecuted = 0;

	@Column(name = "IS_SUP_PER_DETAIL_COMPLETED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean supDetailCompleted = Boolean.FALSE;

	@Column(name = "IS_SUMMARY_COMPLETED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean summaryCompleted = Boolean.FALSE;

	@Column(name = "IS_RECURRENCE_EVALUATION")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean isRecurrenceEvaluation = Boolean.FALSE;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "form", orphanRemoval = true, cascade = CascadeType.ALL)
	@OrderBy("reminderDate")
	private List<SupplierPerformanceReminder> formReminder;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "form", orphanRemoval = true, cascade = CascadeType.ALL)
	@OrderBy("level, order")
	private List<SupplierPerformanceFormCriteria> criteria;

	@Column(name = "CONCLUDE_REMARKS", length = 250, nullable = true)
	private String remarks;

	@Enumerated(EnumType.STRING)
	@Column(name = "OLD_FORM_STATUS")
	private SupplierPerformanceFormStatus oldFormStatus = SupplierPerformanceFormStatus.DRAFT;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "TENANT_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_SP_FORM_BUYER_ID"))
	private Buyer buyer;

	public SupplierPerformanceForm() {

	}

	public SupplierPerformanceForm(String id, Date evaluationStartDate, String formName, String referenceNumber) {
		this.id = id;
		this.evaluationStartDate = evaluationStartDate;
		this.formName = formName;
		this.referenceNumber = referenceNumber;
	}

	public SupplierPerformanceForm(String id, String formName, Date evaluationEndDate, String referenceNumber) {
		this.id = id;
		this.evaluationEndDate = evaluationEndDate;
		this.formName = formName;
		this.referenceNumber = referenceNumber;
	}

	public SupplierPerformanceForm(String id, String formId, Date evaluationStartDate, String formName, String referenceNumber, Integer recurrenceEvaluation, Integer noOfRecurrence, Integer recurrenceExecuted) {
		this.id = id;
		this.formId = formId;
		this.evaluationStartDate = evaluationStartDate;
		this.formName = formName;
		this.referenceNumber = referenceNumber;
		this.recurrenceEvaluation = recurrenceEvaluation;
		this.noOfRecurrence = noOfRecurrence;
		this.recurrenceExecuted = recurrenceExecuted;
	}

	public SupplierPerformanceForm(String id, String formId, Date evaluationStartDate, String formName, String referenceNumber) {
		this.id = id;
		this.formId = formId;
		this.evaluationStartDate = evaluationStartDate;
		this.formName = formName;
		this.referenceNumber = referenceNumber;
	}

	public SupplierPerformanceForm(String id, String eventId, String referenceNumber) {
		this.id = id;
		this.eventId = eventId;
		this.referenceNumber = referenceNumber;
	}

	public SupplierPerformanceForm(String id, String formId, Date evaluationStartDate, String formName, String referenceNumber, User formOwner) {
		this.id = id;
		this.formId = formId;
		this.evaluationStartDate = evaluationStartDate;
		this.formName = formName;
		this.referenceNumber = referenceNumber;
		this.formOwner = formOwner;
	}

	public SupplierPerformanceForm(String id, String formId, Date evaluationEndDate, String referenceNumber, User formOwner) {
		this.id = id;
		this.evaluationEndDate = evaluationEndDate;
		this.formId = formId;
		this.referenceNumber = referenceNumber;
		this.formOwner = formOwner;
	}

	public SupplierPerformanceForm(String id, String formId, Date evaluationStartDate, String formName, String referenceNumber, User formOwner, SupplierPerformanceFormStatus oldFormStatus, String tenantId) {
		this.id = id;
		this.formId = formId;
		this.evaluationStartDate = evaluationStartDate;
		this.formName = formName;
		this.referenceNumber = referenceNumber;
		this.formOwner = formOwner;
		this.oldFormStatus = oldFormStatus;
		Buyer by = new Buyer(tenantId);
		this.buyer = by;
	}
	
	public SupplierPerformanceForm(String id, String formId, Date evaluationEndDate, String referenceNumber, User formOwner, String tenantId) {
		this.id = id;
		this.evaluationEndDate = evaluationEndDate;
		this.formId = formId;
		this.referenceNumber = referenceNumber;
		this.formOwner = formOwner;
		Buyer by = new Buyer(tenantId);
		this.buyer = by;
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
	 * @return the formName
	 */
	public String getFormName() {
		return formName;
	}

	/**
	 * @param formName the formName to set
	 */
	public void setFormName(String formName) {
		this.formName = formName;
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
	 * @return the referenceName
	 */
	public String getReferenceName() {
		return referenceName;
	}

	/**
	 * @param referenceName the referenceName to set
	 */
	public void setReferenceName(String referenceName) {
		this.referenceName = referenceName;
	}

	/**
	 * @return the template
	 */
	public SupplierPerformanceTemplate getTemplate() {
		return template;
	}

	/**
	 * @param template the template to set
	 */
	public void setTemplate(SupplierPerformanceTemplate template) {
		this.template = template;
	}

	/**
	 * @return the formOwner
	 */
	public User getFormOwner() {
		return formOwner;
	}

	/**
	 * @param formOwner the formOwner to set
	 */
	public void setFormOwner(User formOwner) {
		this.formOwner = formOwner;
	}

	/**
	 * @return the createdBy
	 */
	public User getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
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
	public RfxTypes getEventType() {
		return eventType;
	}

	/**
	 * @param eventType the eventType to set
	 */
	public void setEventType(RfxTypes eventType) {
		this.eventType = eventType;
	}

	/**
	 * @return the awardedSupplier
	 */
	public Supplier getAwardedSupplier() {
		return awardedSupplier;
	}

	/**
	 * @param awardedSupplier the awardedSupplier to set
	 */
	public void setAwardedSupplier(Supplier awardedSupplier) {
		this.awardedSupplier = awardedSupplier;
	}

	/**
	 * @return the formStatus
	 */
	public SupplierPerformanceFormStatus getFormStatus() {
		return formStatus;
	}

	/**
	 * @param formStatus the formStatus to set
	 */
	public void setFormStatus(SupplierPerformanceFormStatus formStatus) {
		this.formStatus = formStatus;
	}

	/**
	 * @return the procurementCategory
	 */
	public ProcurementCategories getProcurementCategory() {
		return procurementCategory;
	}

	/**
	 * @param procurementCategory the procurementCategory to set
	 */
	public void setProcurementCategory(ProcurementCategories procurementCategory) {
		this.procurementCategory = procurementCategory;
	}

	/**
	 * @return the businessUnit
	 */
	public BusinessUnit getBusinessUnit() {
		return businessUnit;
	}

	/**
	 * @param businessUnit the businessUnit to set
	 */
	public void setBusinessUnit(BusinessUnit businessUnit) {
		this.businessUnit = businessUnit;
	}

	/**
	 * @return the evaluators
	 */
	public List<SupplierPerformanceEvaluatorUser> getEvaluators() {
		return evaluators;
	}

	/**
	 * @param evaluators the evaluators to set
	 */
	public void setEvaluators(List<SupplierPerformanceEvaluatorUser> evaluators) {
		this.evaluators = evaluators;
	}

	/**
	 * @return the evaluationStartDate
	 */
	public Date getEvaluationStartDate() {
		return evaluationStartDate;
	}

	/**
	 * @param evaluationStartDate the evaluationStartDate to set
	 */
	public void setEvaluationStartDate(Date evaluationStartDate) {
		this.evaluationStartDate = evaluationStartDate;
	}

	/**
	 * @return the evaluationEndDate
	 */
	public Date getEvaluationEndDate() {
		return evaluationEndDate;
	}

	/**
	 * @param evaluationEndDate the evaluationEndDate to set
	 */
	public void setEvaluationEndDate(Date evaluationEndDate) {
		this.evaluationEndDate = evaluationEndDate;
	}

	/**
	 * @return the overallScore
	 */
	public BigDecimal getOverallScore() {
		return overallScore;
	}

	/**
	 * @param overallScore the overallScore to set
	 */
	public void setOverallScore(BigDecimal overallScore) {
		this.overallScore = overallScore;
	}

	/**
	 * @return the scoreRating
	 */
	public ScoreRating getScoreRating() {
		return scoreRating;
	}

	/**
	 * @param scoreRating the scoreRating to set
	 */
	public void setScoreRating(ScoreRating scoreRating) {
		this.scoreRating = scoreRating;
	}

	/**
	 * @return the concludeDate
	 */
	public Date getConcludeDate() {
		return concludeDate;
	}

	/**
	 * @param concludeDate the concludeDate to set
	 */
	public void setConcludeDate(Date concludeDate) {
		this.concludeDate = concludeDate;
	}

	/**
	 * @return the evaluationDuration
	 */
	public Integer getEvaluationDuration() {
		return evaluationDuration;
	}

	/**
	 * @param evaluationDuration the evaluationDuration to set
	 */
	public void setEvaluationDuration(Integer evaluationDuration) {
		this.evaluationDuration = evaluationDuration;
	}

	/**
	 * @return the recurrenceEvaluation
	 */
	public Integer getRecurrenceEvaluation() {
		return recurrenceEvaluation;
	}

	/**
	 * @param recurrenceEvaluation the recurrenceEvaluation to set
	 */
	public void setRecurrenceEvaluation(Integer recurrenceEvaluation) {
		this.recurrenceEvaluation = recurrenceEvaluation;
	}

	/**
	 * @return the noOfRecurrence
	 */
	public Integer getNoOfRecurrence() {
		return noOfRecurrence;
	}

	/**
	 * @param noOfRecurrence the noOfRecurrence to set
	 */
	public void setNoOfRecurrence(Integer noOfRecurrence) {
		this.noOfRecurrence = noOfRecurrence;
	}

	/**
	 * @return the formReminder
	 */
	public List<SupplierPerformanceReminder> getFormReminder() {
		return formReminder;
	}

	/**
	 * @param formReminder the formReminder to set
	 */
	public void setFormReminder(List<SupplierPerformanceReminder> formReminder) {
		if (CollectionUtil.isNotEmpty(this.formReminder)) {
			this.formReminder.clear();
		}
		if (this.formReminder == null) {
			this.formReminder = new ArrayList<SupplierPerformanceReminder>();
		}
		if (CollectionUtil.isNotEmpty(formReminder)) {
			this.formReminder.addAll(formReminder);
		}
	}

	/**
	 * @return the supDetailCompleted
	 */
	public Boolean getSupDetailCompleted() {
		return supDetailCompleted;
	}

	/**
	 * @param supDetailCompleted the supDetailCompleted to set
	 */
	public void setSupDetailCompleted(Boolean supDetailCompleted) {
		this.supDetailCompleted = supDetailCompleted;
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

	/**
	 * @return the evaluationStartTime
	 */
	public Date getEvaluationStartTime() {
		return evaluationStartTime;
	}

	/**
	 * @param evaluationStartTime the evaluationStartTime to set
	 */
	public void setEvaluationStartTime(Date evaluationStartTime) {
		this.evaluationStartTime = evaluationStartTime;
	}

	/**
	 * @return the evaluationEndTime
	 */
	public Date getEvaluationEndTime() {
		return evaluationEndTime;
	}

	/**
	 * @param evaluationEndTime the evaluationEndTime to set
	 */
	public void setEvaluationEndTime(Date evaluationEndTime) {
		this.evaluationEndTime = evaluationEndTime;
	}

	/**
	 * @return the criteria
	 */
	public List<SupplierPerformanceFormCriteria> getCriteria() {
		return criteria;
	}

	/**
	 * @param criteria the criteria to set
	 */
	public void setCriteria(List<SupplierPerformanceFormCriteria> criteria) {
		this.criteria = criteria;
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
	 * @return the recurrenceExecuted
	 */
	public Integer getRecurrenceExecuted() {
		return recurrenceExecuted;
	}

	/**
	 * @param recurrenceExecuted the recurrenceExecuted to set
	 */
	public void setRecurrenceExecuted(Integer recurrenceExecuted) {
		this.recurrenceExecuted = recurrenceExecuted;
	}

	/**
	 * @return the oldFormStatus
	 */
	public SupplierPerformanceFormStatus getOldFormStatus() {
		return oldFormStatus;
	}

	/**
	 * @param oldFormStatus the oldFormStatus to set
	 */
	public void setOldFormStatus(SupplierPerformanceFormStatus oldFormStatus) {
		this.oldFormStatus = oldFormStatus;
	}

	/**
	 * @return the buyer
	 */
	public Buyer getBuyer() {
		return buyer;
	}

	/**
	 * @param buyer the buyer to set
	 */
	public void setBuyer(Buyer buyer) {
		this.buyer = buyer;
	}

	/**
	 * @return the createdFromRef
	 */
	public SupplierPerformanceForm getCreatedFromRef() {
		return createdFromRef;
	}

	/**
	 * @param createdFromRef the createdFromRef to set
	 */
	public void setCreatedFromRef(SupplierPerformanceForm createdFromRef) {
		this.createdFromRef = createdFromRef;
	}

	public void copyCriteriaDetails(SupplierPerformanceForm form, SupplierPerformanceTemplate spTemplate) {
		if (CollectionUtil.isNotEmpty(spTemplate.getCriteria())) {
			form.setCriteria(new ArrayList<SupplierPerformanceFormCriteria>());
			for (SupplierPerformanceTemplateCriteria oldCriteria : spTemplate.getCriteria()) {
				form.getCriteria().add(oldCriteria.copyFromTemplate());
			}
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(eventId, formId, id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SupplierPerformanceForm other = (SupplierPerformanceForm) obj;
		return Objects.equals(eventId, other.eventId) && Objects.equals(formId, other.formId) && Objects.equals(id, other.id);
	}

}
