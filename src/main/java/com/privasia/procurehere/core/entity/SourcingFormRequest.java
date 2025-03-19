/**
 * 
 */
package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Size;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.jfree.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.dao.SourcingFormRequestBqDao;
import com.privasia.procurehere.core.dao.SourcingFormRequestBqItemDao;
import com.privasia.procurehere.core.dao.UomDao;
import com.privasia.procurehere.core.enums.AuctionType;
import com.privasia.procurehere.core.enums.EventStatus;
import com.privasia.procurehere.core.enums.SourcingFormStatus;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author pooja
 */
@Entity
@Table(name = "PROC_SOURCING_FORM_REQ")
public class SourcingFormRequest implements Serializable {
	private static final Logger LOG = LogManager.getLogger(SourcingFormRequest.class);
	private static final long serialVersionUID = -7878112758943195463L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@Column(name = "FORM_ID", length = 64)
	private String formId;

	@Column(name = "FORM_TYPE", length = 64)
	private String formType;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "FORM_OWNER", nullable = false)
	private User formOwner;

	@Column(name = "REFERANCE_NUMBER", length = 64)
	@Size(min = 1, max = 64)
	private String referanceNumber;

	@Column(name = "SOURCING_FORM_NAME", length = 250)
	@Size(min = 1, max = 64)
	private String sourcingFormName;

	@Size(max = 1000)
	@Column(name = "DESCRIPTION", length = 1050)
	private String description;

	@Column(name = "TENANT_ID", nullable = false, length = 64)
	private String tenantId;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.EAGER, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "CREATED_BY", nullable = true)
	private User createdBy;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "ACTION_BY", nullable = true)
	private User actionBy;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MODIFIED_BY", nullable = true)
	private User modifiedBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_DATE", nullable = false)
	private Date createdDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ACTION_DATE", nullable = true)
	private Date actionDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "MODIFIED_DATE")
	private Date modifiedDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS")
	private SourcingFormStatus status;

	@ManyToOne(fetch = FetchType.EAGER, optional = true, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "BUSINESS_UNIT_ID", nullable = true)
	private BusinessUnit businessUnit;

	@Column(name = "URGENT_FORM")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean urgentForm;

	@Column(name = "IS_FORM_DETAIL_COMPLETED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean formDetailCompleted = Boolean.FALSE;

	@Column(name = "IS_CQ_COMPLETED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean cqCompleted = Boolean.FALSE;

	@Column(name = "IS_BQ_COMPLETED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
    private Boolean bqCompleted = Boolean.FALSE;


	@Column(name = "IS_SOR_COMPLETED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean sorCompleted = Boolean.FALSE;

	@Column(name = "IS_SUMMARY_COMPLETED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean summaryCompleted = Boolean.FALSE;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SOURCING_TEMPLATE_ID", nullable = true)
	private SourcingFormTemplate sourcingForm;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sourcingFormRequest", orphanRemoval = true, cascade = CascadeType.ALL)
	@OrderBy("level")
	private List<SourcingFormApprovalRequest> sourcingFormApprovalRequests;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sourcingFormRequest")
	private List<SourcingFormRequestBq> sourcingRequestBqs;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sourcingFormRequest")
	private List<SourcingFormRequestSor> sourcingRequestSors;

	@Column(name = "BUYER_SET_DECIMAL", length = 8)
	private String decimal;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "request", cascade = { CascadeType.ALL })
	@OrderBy("createdDate")
	private List<RequestComment> requestComments;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "COST_CENTER", nullable = true, foreignKey = @ForeignKey(name = "FK_RFS_COSTCENTER_ID"))
	private CostCenter costCenter;

	@Column(name = "BUDGET_AMOUNT", precision = 20, scale = 4)
	private BigDecimal budgetAmount;

	@Column(name = "HISTORICAL_AMOUNT", precision = 20, scale = 4)
	private BigDecimal historicaAmount;

	@Column(name = "IS_DOCUMENT_COMPLETED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean documentCompleted = Boolean.FALSE;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sourcingFormRequest")
	private List<RfsDocument> rfsDocuments;

	@Column(name = "APPROVALS_COUNT", nullable = false)
	private Integer approvalsCount;

	@Column(name = "ALLOW_TO_ADD_ADDI_APPROVAL", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean addAdditionalApprovals = Boolean.FALSE;

	@Column(name = "MINIMUM_SUPPLIER_RATING", precision = 10, scale = 4)
	private BigDecimal minimumSupplierRating;

	@Column(name = "MAXIMUM_SUPPLIER_RATING", precision = 10, scale = 4)
	private BigDecimal maximumSupplierRating;

	@Column(name = "ERP_DOC_NO", length = 64, nullable = true)
	private String erpDocNo;

	@Column(name = "ERP_STATUS", length = 64, nullable = true)
	private String erpStatus;

	@Column(name = "ERP_MESSAGE", length = 3000, nullable = true)
	private String erpMessage;

	@Column(name = "IS_ERP_TRANSFER")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean erpTransferred = Boolean.FALSE;

	@Column(name = "GROUP_CODE_OLD", length = 20, nullable = true)
	private String groupCodeOld;

	// this is added for the save remark into audit trail
	//@Transient
	//private String concludeRemarks;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sourcingFormRequest")
	private List<ApprovalDocument> approvalDocuments;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sourcingFormRequest", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<SourcingFormTeamMember> sourcingFormTeamMember;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "BASE_CURRENCY", nullable = true, foreignKey = @ForeignKey(name = "FK_RFS_BASE_CURRENCY_ID"))
	private Currency currency;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "SUBMITTED_DATE", nullable = true)
	private Date submittedDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "APPROVED_DATE", nullable = true)
	private Date approvedDate;

	@Column(name = "APPROVAL_DAYS_HOURS", length = 3)
	private BigDecimal approvalDaysHours;

	@Column(name = "APPROVAL_TOTAL_LEVELS", length = 3)
	private Integer approvalTotalLevels;

	@Column(name = "APPROVAL_TOTAL_USERS", length = 3)
	private Integer approvalTotalUsers;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "FIRST_APPROVED_DATE", nullable = true)
	private Date firstApprovedDate;

	@Transient
	private String createdByName;

	@Column(name = "IS_ENABLE_APPROVAL_REMINDER", nullable = false)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean enableApprovalReminder = Boolean.TRUE;

	@Column(name = "REMINDER_HOURS", length = 2, nullable = true)
	private Integer reminderAfterHour = 24;

	@Column(name = "REMINDER_COUNT", length = 2, nullable = true)
	private Integer reminderCount = 3;

	@Column(name = "IS_NOTIFY_EVENT_OWNER", nullable = false)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean notifyEventOwner = Boolean.TRUE;

	@Column(name = "ESTIMATED_BUDGET", precision = 20, scale = 4)
	private BigDecimal estimatedBudget;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "PROCUREMENT_METHOD", nullable = true, foreignKey = @ForeignKey(name = "FK_RFS_PROC_METHOD_ID"))
	private ProcurementMethod procurementMethod;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "PROCUREMENT_CATEGORIES", nullable = true, foreignKey = @ForeignKey(name = "FK_RFS_PROC_CATEGORIES_ID"))
	private ProcurementCategories procurementCategories;
	
	@Column(name = "CONCLUDE_REMARKS", length = 500)
	@Size(min = 0, max = 500)
	private String concludeRemarks;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CONCLUDE_DATE")
	private Date concludeDate;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CONCLUDE_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_RFS_CONCLUDE_BY_ID"))
	private User concludeBy;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "GROUP_CODE", nullable = true, foreignKey = @ForeignKey(name = "FK_RFS_GROUP_CODE_ID"))
	private GroupCode groupCode;

	public SourcingFormRequest() {
		this.urgentForm = Boolean.FALSE;
		this.formDetailCompleted = Boolean.FALSE;
		this.bqCompleted = Boolean.FALSE;
		this.cqCompleted = Boolean.FALSE;
		this.summaryCompleted = Boolean.FALSE;
		this.enableApprovalReminder = Boolean.TRUE;
		this.reminderAfterHour = 24;
		this.reminderCount = 3;
		this.notifyEventOwner = Boolean.TRUE;
	}

	public SourcingFormRequest(String id, String sourcingFormName, String description, Date createdDate, User createdBy) {
		super();
		this.id = id;
		this.sourcingFormName = sourcingFormName;
		this.description = description;
		this.createdBy = createdBy;
		this.createdDate = createdDate;
	}

	/**
	 * this is used in dash-board query
	 * 
	 * @param id
	 * @param sourcingFormName
	 * @param description
	 * @param createdDate
	 * @param createdBy
	 * @param formOwner
	 * @param unit
	 */
	public SourcingFormRequest(String id, String sourcingFormName, String description, Date createdDate, User createdBy, User formOwner, BusinessUnit businessUnit) {
		super();
		this.id = id;
		this.businessUnit = businessUnit;
		this.formOwner = formOwner;
		this.sourcingFormName = sourcingFormName;
		this.description = description;
		this.createdBy = createdBy;
		this.createdDate = createdDate;
	}

	public SourcingFormRequest(String id, String sourcingFormName, String referanceNumber, String description, Date createdDate, User createdBy) {
		super();
		this.id = id;
		this.sourcingFormName = sourcingFormName;
		this.referanceNumber = referanceNumber;
		this.description = description;
		this.createdBy = createdBy;
		this.createdDate = createdDate;
	}

	public SourcingFormRequest(String id, String sourcingFormName, String referanceNumber, String formId, String description, Date createdDate, User createdBy, User formOwner, BusinessUnit businessUnit) {
		super();
		this.id = id;
		this.businessUnit = businessUnit;
		this.formOwner = formOwner;
		this.sourcingFormName = sourcingFormName;
		this.referanceNumber = referanceNumber;
		this.formId = formId;
		this.description = description;
		this.createdBy = createdBy;
		this.createdDate = createdDate;
	}

	public SourcingFormRequest(String id, String sourcingFormName, String referanceNumber, String formId, String description, Date createdDate, User createdBy, User formOwner, BusinessUnit businessUnit, SourcingFormStatus status) {
		super();
		this.id = id;
		this.businessUnit = businessUnit;
		this.formOwner = formOwner;
		this.sourcingFormName = sourcingFormName;
		this.referanceNumber = referanceNumber;
		this.formId = formId;
		this.description = description;
		this.createdBy = createdBy;
		this.createdDate = createdDate;
		this.status = status;
	}

	// this is used for get data on sourcing report list
	public SourcingFormRequest(String id, String sourcingFormName, String referanceNumber, String formId, String description, Date createdDate, User createdBy, User formOwner, BusinessUnit businessUnit, CostCenter center, SourcingFormStatus status, Currency currency) {
		super();
		this.id = id;
		this.businessUnit = businessUnit;
		this.formOwner = formOwner;
		this.sourcingFormName = sourcingFormName;
		this.referanceNumber = referanceNumber;
		this.formId = formId;
		this.description = description;
		this.createdBy = createdBy;
		this.createdDate = createdDate;
		this.costCenter = center;
		this.currency = currency;
		this.status = status;
	}

	public SourcingFormRequest(List<SourcingFormApprovalRequest> sourcingFormApprovalRequests, SourcingFormRequest request) {
		if (request != null && request.getStatus() == SourcingFormStatus.APPROVED) {
			if (CollectionUtil.isNotEmpty(sourcingFormApprovalRequests)) {
				this.sourcingFormApprovalRequests = new ArrayList<SourcingFormApprovalRequest>();
				for (SourcingFormApprovalRequest sourcingFormApprovalRequest : sourcingFormApprovalRequests) {
					if (!sourcingFormApprovalRequest.isDone()) {
						this.sourcingFormApprovalRequests.add(sourcingFormApprovalRequest);
					}
				}
			}
		}
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
	 * @return the modifiedBy
	 */
	public User getModifiedBy() {
		return modifiedBy;
	}

	/**
	 * @param modifiedBy the modifiedBy to set
	 */
	public void setModifiedBy(User modifiedBy) {
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
	 * @return the sourcingForm
	 */
	public SourcingFormTemplate getSourcingForm() {
		return sourcingForm;
	}

	/**
	 * @param sourcingForm the sourcingForm to set
	 */
	public void setSourcingForm(SourcingFormTemplate sourcingForm) {
		this.sourcingForm = sourcingForm;
	}

	/**
	 * @return the sourcingFormApprovalRequests
	 */
	public List<SourcingFormApprovalRequest> getSourcingFormApprovalRequests() {
		return sourcingFormApprovalRequests;
	}

	public void updateSourcingFormApprovalRequests(List<SourcingFormApprovalRequest> sourcingFormApprovalRequests) {

		if (this.sourcingFormApprovalRequests == null) {
			this.sourcingFormApprovalRequests = new ArrayList<SourcingFormApprovalRequest>();
		} else {// Do update only it the passed list is a fresh list and not the same instance list.
			if (sourcingFormApprovalRequests != null) {
				for (SourcingFormApprovalRequest oldApproval : this.sourcingFormApprovalRequests) {
					for (SourcingFormApprovalRequest newApproval : sourcingFormApprovalRequests) {
						if (newApproval.getId() == null)
							continue;
						if (newApproval.getId().equals(oldApproval.getId())) {
							newApproval.setActive(oldApproval.isActive());
							newApproval.setDone(oldApproval.isDone());
							newApproval.setId(null);
							newApproval.setBatchNo(oldApproval.getBatchNo());
							// Preserve individual approval user old state
							for (SourcingFormApprovalUserRequest oldApprovalUser : oldApproval.getApprovalUsersRequest()) {
								for (SourcingFormApprovalUserRequest newApprovalUser : newApproval.getApprovalUsersRequest()) {
									if (newApprovalUser.getUser() == null || newApprovalUser.getUser().getId() == null) {
										continue;
									}
									if (newApprovalUser.getUser().getId().equals(oldApprovalUser.getUser().getId())) {
										newApprovalUser.setActionDate(oldApprovalUser.getActionDate());
										newApprovalUser.setApprovalStatus(oldApprovalUser.getApprovalStatus());
										newApprovalUser.setRemarks(oldApprovalUser.getRemarks());
									}
								}
							}
						}
					}
				}
			}
			this.sourcingFormApprovalRequests.clear();
		}
		if (sourcingFormApprovalRequests != null) {
			this.sourcingFormApprovalRequests.addAll(sourcingFormApprovalRequests);
		}

	}

	/**
	 * @param sourcingFormApprovalRequests the sourcingFormApprovalRequests to set
	 */
	public void setSourcingFormApprovalRequests(List<SourcingFormApprovalRequest> approvals) {

		if (this.sourcingFormApprovalRequests == null) {
			this.sourcingFormApprovalRequests = new ArrayList<SourcingFormApprovalRequest>();
		} else {// Do update only it the passed list is a fresh list and not the same instance list.
			if (approvals != null) {
				for (SourcingFormApprovalRequest oldApproval : this.sourcingFormApprovalRequests) {
					for (SourcingFormApprovalRequest newApproval : approvals) {
						if (newApproval.getId() == null) {
							continue;
						}
						if (newApproval.getId().equals(oldApproval.getId())) {
							newApproval.setActive(oldApproval.isActive());
							newApproval.setApprovalType(newApproval.getApprovalType() != null ? newApproval.getApprovalType() : oldApproval.getApprovalType());
							newApproval.setBatchNo(oldApproval.getBatchNo());
							newApproval.setLevel(newApproval.getLevel() == null ? oldApproval.getLevel() : newApproval.getLevel());
							newApproval.setDone(oldApproval.isDone());
							newApproval.setSourcingFormRequest(this);
							newApproval.setId(null);

							// Preserve individual approval user old state
							for (SourcingFormApprovalUserRequest oldApprovalUser : oldApproval.getApprovalUsersRequest()) {
								for (SourcingFormApprovalUserRequest newApprovalUser : newApproval.getApprovalUsersRequest()) {
									if (newApprovalUser.getUser() == null || (newApprovalUser.getUser() != null && StringUtils.checkString(newApprovalUser.getUser().getId()).length() == 0)) {
										continue;
									}
									if (newApprovalUser.getUser().getId().equals(oldApprovalUser.getUser().getId())) {
										newApprovalUser.setActionDate(oldApprovalUser.getActionDate());
										newApprovalUser.setApprovalStatus(oldApprovalUser.getApprovalStatus());
										newApprovalUser.setRemarks(oldApprovalUser.getRemarks());
										newApprovalUser.setApprovalRequest(newApproval);
									}
								}
							}
						}
					}
				}
			} else {
			}
			this.sourcingFormApprovalRequests.clear();
		}
		if (approvals != null) {
			this.sourcingFormApprovalRequests.addAll(approvals);
		}
	}

	/**
	 * @return the sourcingRequestBqs
	 */
	public List<SourcingFormRequestBq> getSourcingRequestBqs() {
		return sourcingRequestBqs;
	}

	/**
	 * @param sourcingRequestBqs the sourcingRequestBqs to set
	 */
	public void setSourcingRequestBqs(List<SourcingFormRequestBq> sourcingRequestBqs) {
		this.sourcingRequestBqs = sourcingRequestBqs;
	}

	public String getDecimal() {
		return decimal;
	}

	public void setDecimal(String decimal) {
		this.decimal = decimal;
	}

	/**
	 * @return the rfsDocuments
	 */
	public List<RfsDocument> getRfsDocuments() {
		return rfsDocuments;
	}

	/**
	 * @param rfsDocuments the rfsDocuments to set
	 */
	public void setRfsDocuments(List<RfsDocument> rfsDocuments) {
		this.rfsDocuments = rfsDocuments;
	}

	/**
	 * @return the approvalsCount
	 */
	public Integer getApprovalsCount() {
		return approvalsCount;
	}

	/**
	 * @param approvalsCount the approvalsCount to set
	 */
	public void setApprovalsCount(Integer approvalsCount) {
		this.approvalsCount = approvalsCount;
	}

	/**
	 * @return the addAdditionalApprovals
	 */
	public Boolean getAddAdditionalApprovals() {
		return addAdditionalApprovals;
	}

	/**
	 * @param addAdditionalApprovals the addAdditionalApprovals to set
	 */
	public void setAddAdditionalApprovals(Boolean addAdditionalApprovals) {
		this.addAdditionalApprovals = addAdditionalApprovals;
	}

	public void copyFromPrevious(SourcingFormRequest oldRequest, SourcingFormRequest newRequest, User loggedInUser, SourcingFormRequestBqDao bqDao, SourcingFormRequestBqItemDao bqItemdao, UomDao uomDao) {
		if (StringUtils.checkString(oldRequest.getSourcingFormName()).length() > 0)
			newRequest.setSourcingFormName(oldRequest.getSourcingFormName());
		newRequest.setDescription(oldRequest.getDescription());
		newRequest.setDecimal(oldRequest.getDecimal());
		newRequest.setCreatedDate(new Date());
		newRequest.setBqCompleted(false);
		newRequest.setCqCompleted(false);
		// newRequest.setCreatedBy(loggedInUser);
		newRequest.setFormDetailCompleted(false);
		newRequest.setFormOwner(loggedInUser);
		newRequest.setTenantId(loggedInUser.getTenantId());
		newRequest.setBusinessUnit(oldRequest.getBusinessUnit());
		newRequest.setSummaryCompleted(false);
		// newRequest.setStatus(oldRequest.getStatus());
		newRequest.setReferanceNumber(oldRequest.getReferanceNumber());
		newRequest.setUrgentForm(oldRequest.getUrgentForm());
		newRequest.setSourcingForm(oldRequest.getSourcingForm());
		// newRequest.setFormId(oldRequest.getFormId());
		newRequest.setFormType(oldRequest.getFormType());

		newRequest.setBudgetAmount(oldRequest.getBudgetAmount());
		newRequest.setHistoricaAmount(oldRequest.getHistoricaAmount());
		newRequest.setCostCenter(oldRequest.getCostCenter());
		newRequest.setBusinessUnit(oldRequest.getBusinessUnit());
		newRequest.setMinimumSupplierRating(oldRequest.getMinimumSupplierRating() != null ? oldRequest.getMinimumSupplierRating() : null);
		newRequest.setMaximumSupplierRating(oldRequest.getMaximumSupplierRating() != null ? oldRequest.getMaximumSupplierRating() : null);
		newRequest.setGroupCode(oldRequest.getGroupCode() != null ? oldRequest.getGroupCode() : null);

		// copy BQ and BqItem
		SourcingFormRequestBq bq = new SourcingFormRequestBq();
		List<SourcingFormRequestBq> bqList = bq.copyBq(oldRequest, bqDao, bqItemdao, uomDao);
		for (SourcingFormRequestBq bqs : bqList) {
			bqs.setSourcingFormRequest(newRequest);
		}
		newRequest.setSourcingRequestBqs(bqList);

		// copy Cq And CqItem and cqOptios
		SourcingFormTemplate template = oldRequest.getSourcingForm();
		List<SourcingTemplateCq> cqList = template.copyCq(template);
		for (SourcingTemplateCq cqs : cqList) {
			cqs.setSourcingForm(template);
		}
		newRequest.setSourcingForm(template);
		Log.info("+++++++++++++++++++=");
		// copy SourcingApprovalRequest
		List<SourcingFormApprovalRequest> approvalList = new ArrayList<SourcingFormApprovalRequest>();
		int level = 1;
		for (SourcingFormApprovalRequest approval : oldRequest.getSourcingFormApprovalRequests()) {
			Log.info("copying Approval UserList");
			approval.setSourcingFormRequest(newRequest);
			approval.setLevel(level++);
			if (approval != null && CollectionUtil.isNotEmpty(approval.getApprovalUsersRequest())) {
				Log.info("copying Approval UserList");
				for (SourcingFormApprovalUserRequest approvalUser : approval.getApprovalUsersRequest()) {
					approvalUser.setApprovalRequest(approval);
				}
			}
		}
		newRequest.setSourcingFormApprovalRequests(approvalList);

	}

	/**
	 * @return the actionBy
	 */
	public User getActionBy() {
		return actionBy;
	}

	/**
	 * @param actionBy the actionBy to set
	 */
	public void setActionBy(User actionBy) {
		this.actionBy = actionBy;
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
	 * @return the budgetAmount
	 */
	public BigDecimal getBudgetAmount() {
		return budgetAmount;
	}

	/**
	 * @param budgetAmount the budgetAmount to set
	 */
	public void setBudgetAmount(BigDecimal budgetAmount) {
		this.budgetAmount = budgetAmount;
	}

	/**
	 * @return the historicaAmount
	 */
	public BigDecimal getHistoricaAmount() {
		return historicaAmount;
	}

	/**
	 * @param historicaAmount the historicaAmount to set
	 */
	public void setHistoricaAmount(BigDecimal historicaAmount) {
		this.historicaAmount = historicaAmount;
	}

	/**
	 * @return the costCenter
	 */
	public CostCenter getCostCenter() {
		return costCenter;
	}

	/**
	 * @param costCenter the costCenter to set
	 */
	public void setCostCenter(CostCenter costCenter) {
		this.costCenter = costCenter;
	}

	/**
	 * @return the documentCompleted
	 */
	public Boolean getDocumentCompleted() {
		return documentCompleted;
	}

	/**
	 * @param documentCompleted the documentCompleted to set
	 */
	public void setDocumentCompleted(Boolean documentCompleted) {
		this.documentCompleted = documentCompleted;
	}

	/**
	 * @return the approvalDocuments
	 */
	public List<ApprovalDocument> getApprovalDocuments() {
		return approvalDocuments;
	}

	/**
	 * @param approvalDocuments the approvalDocuments to set
	 */
	public void setApprovalDocuments(List<ApprovalDocument> approvalDocuments) {
		this.approvalDocuments = approvalDocuments;
	}

	/**
	 * @return the minimumSupplierRating
	 */
	public BigDecimal getMinimumSupplierRating() {
		return minimumSupplierRating;
	}

	/**
	 * @param minimumSupplierRating the minimumSupplierRating to set
	 */
	public void setMinimumSupplierRating(BigDecimal minimumSupplierRating) {
		this.minimumSupplierRating = minimumSupplierRating;
	}

	/**
	 * @return the maximumSupplierRating
	 */
	public BigDecimal getMaximumSupplierRating() {
		return maximumSupplierRating;
	}

	/**
	 * @param maximumSupplierRating the maximumSupplierRating to set
	 */
	public void setMaximumSupplierRating(BigDecimal maximumSupplierRating) {
		this.maximumSupplierRating = maximumSupplierRating;
	}

	public List<SourcingFormTeamMember> getSourcingFormTeamMember() {
		return sourcingFormTeamMember;
	}

	public void setSourcingFormTeamMember(List<SourcingFormTeamMember> sourcingFormTeamMember) {
		this.sourcingFormTeamMember = sourcingFormTeamMember;
	}

	/**
	 * @return the erpDocNo
	 */
	public String getErpDocNo() {
		return erpDocNo;
	}

	/**
	 * @param erpDocNo the erpDocNo to set
	 */
	public void setErpDocNo(String erpDocNo) {
		this.erpDocNo = erpDocNo;
	}

	/**
	 * @return the erpStatus
	 */
	public String getErpStatus() {
		return erpStatus;
	}

	/**
	 * @param erpStatus the erpStatus to set
	 */
	public void setErpStatus(String erpStatus) {
		this.erpStatus = erpStatus;
	}

	/**
	 * @return the erpMessage
	 */
	public String getErpMessage() {
		return erpMessage;
	}

	/**
	 * @param erpMessage the erpMessage to set
	 */
	public void setErpMessage(String erpMessage) {
		this.erpMessage = erpMessage;
	}

	/**
	 * @return the erpTransferred
	 */
	public Boolean getErpTransferred() {
		return erpTransferred;
	}

	/**
	 * @param erpTransferred the erpTransferred to set
	 */
	public void setErpTransferred(Boolean erpTransferred) {
		this.erpTransferred = erpTransferred;
	}

	// public String getGroupCode() {
	// return groupCode;
	// }
	//
	// public void setGroupCode(String groupCode) {
	// this.groupCode = groupCode;
	// }

	public RftEvent createNextRftEvent(SourcingFormRequest request, User loggedInUser) {

		RftEvent newEvent = new RftEvent();
		newEvent.setStatus(EventStatus.DRAFT);
		newEvent.setCreatedBy(loggedInUser);
		newEvent.setCreatedDate(new Date());
		newEvent.setEventOwner(loggedInUser);
		newEvent.setPreviousRequestId(request.getId());

		newEvent.setCostCenter(request.getCostCenter());
		newEvent.setDecimal(request.getDecimal());
		newEvent.setBusinessUnit(request.getBusinessUnit());
		newEvent.setBudgetAmount(request.getBudgetAmount());
		newEvent.setHistoricaAmount(request.getHistoricaAmount());
		newEvent.setMinimumSupplierRating(request.getMinimumSupplierRating() != null ? request.getMinimumSupplierRating() : null);
		newEvent.setMaximumSupplierRating(request.getMaximumSupplierRating() != null ? request.getMaximumSupplierRating() : null);
		newEvent.setEstimatedBudget(request.getEstimatedBudget());
		newEvent.setProcurementMethod(request.getProcurementMethod());
		newEvent.setProcurementCategories(request.getProcurementCategories());
		newEvent.setGroupCode(request.getGroupCode());
		return newEvent;

	}

	public RfqEvent createNextRfqEvent(SourcingFormRequest request, User loggedInUser) {
		RfqEvent newEvent = new RfqEvent();
		newEvent.setStatus(EventStatus.DRAFT);
		newEvent.setCreatedBy(loggedInUser);
		newEvent.setCreatedDate(new Date());
		newEvent.setEventOwner(loggedInUser);
		newEvent.setPreviousRequestId(request.getId());

		newEvent.setCostCenter(request.getCostCenter());
		newEvent.setDecimal(request.getDecimal());
		newEvent.setBusinessUnit(request.getBusinessUnit());
		newEvent.setBudgetAmount(request.getBudgetAmount());
		newEvent.setHistoricaAmount(request.getHistoricaAmount());
		newEvent.setMinimumSupplierRating(request.getMinimumSupplierRating() != null ? request.getMinimumSupplierRating() : null);
		newEvent.setMaximumSupplierRating(request.getMaximumSupplierRating() != null ? request.getMaximumSupplierRating() : null);
		newEvent.setEstimatedBudget(request.getEstimatedBudget());
		newEvent.setProcurementMethod(request.getProcurementMethod());
		newEvent.setProcurementCategories(request.getProcurementCategories());
		newEvent.setGroupCode(request.getGroupCode());
		return newEvent;
	}

	public RfpEvent createNextRfpEvent(SourcingFormRequest request, User loggedInUser) {
		RfpEvent newEvent = new RfpEvent();
		newEvent.setStatus(EventStatus.DRAFT);
		newEvent.setCreatedBy(loggedInUser);
		newEvent.setCreatedDate(new Date());
		newEvent.setEventOwner(loggedInUser);
		newEvent.setPreviousRequestId(request.getId());

		newEvent.setCostCenter(request.getCostCenter());
		newEvent.setDecimal(request.getDecimal());
		newEvent.setBusinessUnit(request.getBusinessUnit());
		newEvent.setBudgetAmount(request.getBudgetAmount());
		newEvent.setHistoricaAmount(request.getHistoricaAmount());
		newEvent.setMinimumSupplierRating(request.getMinimumSupplierRating() != null ? request.getMinimumSupplierRating() : null);
		newEvent.setMaximumSupplierRating(request.getMaximumSupplierRating() != null ? request.getMaximumSupplierRating() : null);
		newEvent.setEstimatedBudget(request.getEstimatedBudget());
		newEvent.setProcurementMethod(request.getProcurementMethod());
		newEvent.setProcurementCategories(request.getProcurementCategories());
		newEvent.setGroupCode(request.getGroupCode());
		return newEvent;
	}

	public RfiEvent createNextRfiEvent(SourcingFormRequest request, User loggedInUser) {
		RfiEvent newEvent = new RfiEvent();
		newEvent.setStatus(EventStatus.DRAFT);
		newEvent.setCreatedBy(loggedInUser);
		newEvent.setCreatedDate(new Date());
		newEvent.setEventOwner(loggedInUser);
		newEvent.setPreviousRequestId(request.getId());

		newEvent.setCostCenter(request.getCostCenter());
		newEvent.setDecimal(request.getDecimal());
		newEvent.setBusinessUnit(request.getBusinessUnit());
		newEvent.setBudgetAmount(request.getBudgetAmount());
		newEvent.setHistoricaAmount(request.getHistoricaAmount());
		newEvent.setMinimumSupplierRating(request.getMinimumSupplierRating() != null ? request.getMinimumSupplierRating() : null);
		newEvent.setMaximumSupplierRating(request.getMaximumSupplierRating() != null ? request.getMaximumSupplierRating() : null);
		newEvent.setEstimatedBudget(request.getEstimatedBudget());
		newEvent.setProcurementMethod(request.getProcurementMethod());
		newEvent.setProcurementCategories(request.getProcurementCategories());
		newEvent.setGroupCode(request.getGroupCode());
		return newEvent;
	}

	public RfaEvent createNextRfaEvent(SourcingFormRequest request, AuctionType auctionType, String bqId, User loggedInUser) {
		RfaEvent newEvent = new RfaEvent();
		newEvent.setStatus(EventStatus.DRAFT);
		newEvent.setCreatedBy(loggedInUser);
		newEvent.setCreatedDate(new Date());
		newEvent.setEventOwner(loggedInUser);
		newEvent.setAuctionType(auctionType);
		newEvent.setPreviousRequestId(request.getId());

		newEvent.setCostCenter(request.getCostCenter());
		newEvent.setDecimal(request.getDecimal());
		newEvent.setBusinessUnit(request.getBusinessUnit());
		newEvent.setBudgetAmount(request.getBudgetAmount());
		newEvent.setHistoricaAmount(request.getHistoricaAmount());
		newEvent.setBaseCurrency(request.getCurrency());
		newEvent.setEstimatedBudget(request.getEstimatedBudget());
		newEvent.setProcurementMethod(request.getProcurementMethod());
		newEvent.setProcurementCategories(request.getProcurementCategories());
		newEvent.setGroupCode(request.getGroupCode());

		/**
		 * commented for event publish type PH-352
		 */
		// newEvent.setEventVisibility(EventVisibilityType.PRIVATE);
		newEvent.setMinimumSupplierRating(request.getMinimumSupplierRating() != null ? request.getMinimumSupplierRating() : null);
		newEvent.setMaximumSupplierRating(request.getMaximumSupplierRating() != null ? request.getMaximumSupplierRating() : null);
		return newEvent;
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
	public BigDecimal getApprovalDaysHours() {
		return approvalDaysHours;
	}

	/**
	 * @param approvalDaysHours the approvalDaysHours to set
	 */
	public void setApprovalDaysHours(BigDecimal approvalDaysHours) {
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

	/**
	 * @return the firstApprovedDate
	 */
	public Date getFirstApprovedDate() {
		return firstApprovedDate;
	}

	/**
	 * @param firstApprovedDate the firstApprovedDate to set
	 */
	public void setFirstApprovedDate(Date firstApprovedDate) {
		this.firstApprovedDate = firstApprovedDate;
	}

	public String getCreatedByName() {
		return createdByName;
	}

	public void setCreatedByName(String createdByName) {
		this.createdByName = createdByName;
	}

	/**
	 * @return the requestComments
	 */
	public List<RequestComment> getRequestComments() {
		return requestComments;
	}

	/**
	 * @param requestComments the requestComments to set
	 */
	public void setRequestComments(List<RequestComment> requestComments) {
		this.requestComments = requestComments;
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

	public ProcurementMethod getProcurementMethod() {
		return procurementMethod;
	}

	public void setProcurementMethod(ProcurementMethod procurementMethod) {
		this.procurementMethod = procurementMethod;
	}

	public ProcurementCategories getProcurementCategories() {
		return procurementCategories;
	}

	public void setProcurementCategories(ProcurementCategories procurementCategories) {
		this.procurementCategories = procurementCategories;
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
	 * @return the concludeBy
	 */
	public User getConcludeBy() {
		return concludeBy;
	}

	/**
	 * @param concludeBy the concludeBy to set
	 */
	public void setConcludeBy(User concludeBy) {
		this.concludeBy = concludeBy;
	}

	/**
	 * @return the groupCode
	 */
	public GroupCode getGroupCode() {
		return groupCode;
	}

	/**
	 * @param groupCode the groupCode to set
	 */
	public void setGroupCode(GroupCode groupCode) {
		this.groupCode = groupCode;
	}

	/**
	 * @return the groupCodeOld
	 */
	public String getGroupCodeOld() {
		return groupCodeOld;
	}

	/**
	 * @param groupCodeOld the groupCodeOld to set
	 */
	public void setGroupCodeOld(String groupCodeOld) {
		this.groupCodeOld = groupCodeOld;
	}


	public Boolean getSorCompleted() {
		return sorCompleted;
	}

	public void setSorCompleted(Boolean sorCompleted) {
		this.sorCompleted = sorCompleted;
	}

	public List<SourcingFormRequestSor> getSourcingRequestSors() {
		return sourcingRequestSors;
	}

	public void setSourcingRequestSors(List<SourcingFormRequestSor> sourcingRequestSors) {
		this.sourcingRequestSors = sourcingRequestSors;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((formId == null) ? 0 : formId.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((tenantId == null) ? 0 : tenantId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SourcingFormRequest other = (SourcingFormRequest) obj;
		if (formId == null) {
			if (other.formId != null)
				return false;
		} else if (!formId.equals(other.formId))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (tenantId == null) {
			if (other.tenantId != null)
				return false;
		} else if (!tenantId.equals(other.tenantId))
			return false;
		return true;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

}