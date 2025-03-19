package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.entity.Pr.PrCreate;
import com.privasia.procurehere.core.enums.ContractStatus;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;

/**
 * @author pavan
 */
@Entity
@Table(name = "PROC_PRODUCT_CONTRACT")
public class ProductContract implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5050983764259064312L;

	public interface ProductContractInt {
	}

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@Column(name = "CONTRACT_ID", length = 64)
	private String contractId;

	@NotEmpty(message = "{product.contract.referenceNumber.empty}", groups = ProductContractInt.class)
	@Size(min = 1, max = 64, message = "{product.contract.referencenumber.length}", groups = ProductContractInt.class)
	@Column(name = "CONTRACT_REFERENCE_NUMBER", length = 32, nullable = false)
	private String contractReferenceNumber;

	@NotEmpty(message = "{product.groupCode.empty}", groups = ProductContractInt.class)
	@Size(min = 1, max = 9, message = "{product.groupCode.length}", groups = ProductContractInt.class)
	@Column(name = "GROUP_CODE", length = 64, nullable = false)
	private String groupCodeStr;

	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "GROUP_CODE_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_PC_GRP_CODE_ID"))
	private GroupCode groupCode;

	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "BUSINESS_UNIT_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_PC_BUSS_UNIT_ID"))
	private BusinessUnit businessUnit;

	@Temporal(TemporalType.DATE)
	@Column(name = "CONTRACT_START_DATE", nullable = false)
	private Date contractStartDate;

	@Temporal(TemporalType.DATE)
	@Column(name = "CONTRACT_END_DATE", nullable = false)
	private Date contractEndDate;

	@Digits(integer = 22, fraction = 6, message = "{product.contractValue.length}", groups = ProductContractInt.class)
	@Column(name = "CONTRACT_VALUE", precision = 22, scale = 6, nullable = true)
	private BigDecimal contractValue = BigDecimal.ZERO;

	@Enumerated(EnumType.STRING)
	@Column(name = "PRODUCT_ACTIVE")
	private ContractStatus status;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "FAV_SUPPLIER_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_PROD_CON_FAV_SUP_ID"))
	private FavouriteSupplier supplier;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "TENANT_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_PROD_CONTRACT_BUY_ID"))
	private Buyer buyer;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "CREATED_BY", nullable = false, foreignKey = @ForeignKey(name = "FK_PROD_CONTRACT_CREATED_BY"))
	private User createdBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Column(name = "CREATION_DATE", nullable = false)
	private Date createdDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Column(name = "MODIFIED_TIME", nullable = true)
	private Date modifiedDate;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "MODIFIED_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_PROD_CONTRACT_MOD_BY"))
	private User modifiedBy;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "productContract", cascade = { CascadeType.MERGE })
	private List<ProductContractItems> productContractItem;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "productContract", orphanRemoval = true, cascade = CascadeType.ALL)
	@OrderBy("reminderDate")
	private List<ProductContractReminder> contractReminders;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "productContract", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<ProductContractNotifyUsers> notifyUsers;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@NotNull(message = "{pr.currency.required}", groups = { PrCreate.class })
	@JoinColumn(name = "BASE_CURRENCY_ID", foreignKey = @ForeignKey(name = "FK_PROD_CONTRACT_CURRENCY"))
	private Currency currency;

	@NotNull(message = "{pr.decimal.required}")
	@Column(name = "CONTRACT_DECIMAL", length = 64)
	private String decimal;

	@Column(name = "EVENT_ID", length = 64)
	private String eventId;

	@Column(name = "CONTRACT_NAME", length = 250)
	private String contractName;

	@Column(name = "SAP_CONTRACT_NO", length = 12)
	private String sapContractNumber;

	@Column(name = "PREVIOUS_CONTRACT_NO", length = 32)
	private String previousContractNo;

	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "PROCUREMENT_CATEGORIES", nullable = true, foreignKey = @ForeignKey(name = "FK_PROD_PROC_CAT_ID"))
	private ProcurementCategories procurementCategory;

	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "AGREEMENT_TYPE", nullable = true, foreignKey = @ForeignKey(name = "FK_PROC_ARG_TYPE_ID"))
	private AgreementType agreementType;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "productContract", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<ContractDocument> contractDocument;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "CONTRACT_CREATOR", nullable = false, foreignKey = @ForeignKey(name = "FK_PROD_CONTRACT_CONTRACT_CREATOR"))
	private User contractCreator;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "productContract", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<ContractTeamMember> teamMembers;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "productContract", orphanRemoval = true, cascade = CascadeType.ALL)
	@OrderBy("level")
	private List<ContractApproval> approvals;

	@JsonIgnore
	@OneToOne(fetch = FetchType.LAZY, mappedBy = "productContract", orphanRemoval = true, cascade = CascadeType.ALL)
	private ContractLoaAndAgreement contractLoaAndAgreement;

	@Column(name = "RENEWAL_CONTRACT")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean renewalContract;

	@Column(name = "EXP_REM_BFR_30_DAY")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean remBefore30Day = Boolean.FALSE;

	@Column(name = "EXP_REM_BFR_90_DAY")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean remBefore90Day = Boolean.FALSE;

	@Column(name = "EXP_REM_BFR_180_DAY")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean remBefore180Day = Boolean.FALSE;

	@Column(name = "IS_CONTRACT_DETAIL_COMPLETED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean contractDetailCompleted = Boolean.FALSE;

	@Column(name = "IS_CONTRACT_ITEMS_COMPLETED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean contractItemCompleted = Boolean.FALSE;

	@Column(name = "IS_CONTRACT_SUMMARY_COMPLETED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean contractSummaryCompleted = Boolean.FALSE;

	@Column(name = "CONTRACT_REMINDER_DATES", length = 2000)
	private String contractReminderDates;

	@Column(name = "IS_ENABLE_APPROVAL_REMINDER", nullable = false)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean enableApprovalReminder = Boolean.FALSE;

	@Column(name = "REMINDER_HOURS", length = 2, nullable = true)
	private Integer reminderAfterHour;

	@Column(name = "REMINDER_COUNT", length = 2, nullable = true)
	private Integer reminderCount;

	@Column(name = "IS_NOTIFY_EVENT_OWNER", nullable = false)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean notifyEventOwner = Boolean.FALSE;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "productContract", cascade = { CascadeType.ALL })
	@OrderBy("createdDate")
	private List<ContractComment> contractComments;

	@Column(name = "IS_TERMINATION_REQUESTED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean terminationRequested = Boolean.FALSE;

	@Column(name = "TERMINATION_REMARKS", length = 250)
	private String terminationRemarks;

	@Column(name = "CANCEL_REASON", length = 1050)
	private String cancelReason;

	@Column(name = "IS_ENABLE_APPROVAL", nullable = false)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean enableApproval = Boolean.TRUE;

	@Column(name = "IS_ERP_TRANSFERRED", nullable = false)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean erpTransferred = Boolean.FALSE;

	@Enumerated(EnumType.STRING)
	@Column(name = "OLD_STATUS")
	private ContractStatus oldStatus;

	@Temporal(TemporalType.DATE)
	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Column(name = "DOCUMENT_DATE", nullable = true)
	private Date documentDate;

	@Temporal(TemporalType.DATE)
	@Column(name = "OLD_CONTRACT_START_DATE", nullable = false)
	private Date oldContractStartDate;

	@Temporal(TemporalType.DATE)
	@Column(name = "OLD_CONTRACT_END_DATE", nullable = false)
	private Date oldContractEndDate;

	@Column(name = "OLD_CONTRACT_VALUE", precision = 22, scale = 6, nullable = true)
	private BigDecimal oldContractValue = BigDecimal.ZERO;

	@Column(name = "IS_ID_BASED_ON_BU")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean idBasedOnBusinessUnit = Boolean.FALSE;

	@Column(name = "REMARK", length = 1050)
	private String remark;

	@Column(name = "SUPPLIER_NAME", length = 300)
	private String supplierName;

	@Column(name = "IS_EDITABLE")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean isEditable = true;

	public ProductContract() {
	}

	public ProductContract(String id, String contractId, User contractCreator, Date contractStartDate, Date contractEndDate) {
		this.id = id;
		this.contractId = contractId;
		this.contractCreator = contractCreator;
		if (this.contractCreator != null) {
			this.contractCreator.getName();
		}
		this.contractEndDate = contractEndDate;
		this.contractStartDate = contractStartDate;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public String getSupplierName() {
		try {
			if (this.supplier != null && this.supplier.getSupplier() != null) {
				return this.supplier.getSupplier().getCompanyName();
			}else{
				return this.supplierName;
			}
		} catch (Exception e) {
		}
		return null;
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
	 * @return the productContractItem
	 */
	public List<ProductContractItems> getProductContractItem() {
		return productContractItem;
	}

	/**
	 * @param productContractItem the productContractItem to set
	 */
	public void setProductContractItem(List<ProductContractItems> productContractItem) {
		this.productContractItem = productContractItem;
	}

	/**
	 * @return the supplier
	 */
	public FavouriteSupplier getSupplier() {
		return supplier;
	}

	/**
	 * @param supplier the supplier to set
	 */
	public void setSupplier(FavouriteSupplier supplier) {
		this.supplier = supplier;
	}

	/**
	 * @return the contractReminders
	 */
	public List<ProductContractReminder> getContractReminders() {
		return contractReminders;
	}

	/**
	 * @param contractReminders the contractReminders to set
	 */
	public void setContractReminders(List<ProductContractReminder> contractReminders) {
		this.contractReminders = contractReminders;
	}

	/**
	 * @return the notifyUsers
	 */
	public List<ProductContractNotifyUsers> getNotifyUsers() {
		return notifyUsers;
	}

	/**
	 * @param notifyUsers the notifyUsers to set
	 */
	public void setNotifyUsers(List<ProductContractNotifyUsers> notifyUsers) {
		this.notifyUsers = notifyUsers;
		if (this.notifyUsers != null) {
			if (this.notifyUsers != notifyUsers) {
				this.notifyUsers.clear();
				if (CollectionUtil.isNotEmpty(notifyUsers)) {
					this.notifyUsers.addAll(notifyUsers);
				}
			}
		} else {
			this.notifyUsers = notifyUsers;
		}
	}

	/**
	 * @return the remBefore30Day
	 */
	public Boolean getRemBefore30Day() {
		return remBefore30Day;
	}

	/**
	 * @param remBefore30Day the remBefore30Day to set
	 */
	public void setRemBefore30Day(Boolean remBefore30Day) {
		this.remBefore30Day = remBefore30Day;
	}

	/**
	 * @return the remBefore90Day
	 */
	public Boolean getRemBefore90Day() {
		return remBefore90Day;
	}

	/**
	 * @param remBefore90Day the remBefore90Day to set
	 */
	public void setRemBefore90Day(Boolean remBefore90Day) {
		this.remBefore90Day = remBefore90Day;
	}

	public Boolean getRemBefore180Day() {
		return remBefore180Day;
	}

	public void setRemBefore180Day(Boolean remBefore180Day) {
		this.remBefore180Day = remBefore180Day;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public List<ContractDocument> getContractDocument() {
		return contractDocument;
	}

	public void setContractDocument(List<ContractDocument> contractDocument) {
		this.contractDocument = contractDocument;
	}

	public String getDecimal() {
		return decimal;
	}

	public void setDecimal(String decimal) {
		this.decimal = decimal;
	}

	/**
	 * @return the contractCreator
	 */
	public User getContractCreator() {
		return contractCreator;
	}

	/**
	 * @param contractCreator the contractCreator to set
	 */
	public void setContractCreator(User contractCreator) {
		this.contractCreator = contractCreator;
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
	 * @return the agreementType
	 */
	public AgreementType getAgreementType() {
		return agreementType;
	}

	/**
	 * @param agreementType the agreementType to set
	 */
	public void setAgreementType(AgreementType agreementType) {
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
	 * @return the contractLoaAndAgreement
	 */
	public ContractLoaAndAgreement getContractLoaAndAgreement() {
		return contractLoaAndAgreement;
	}

	/**
	 * @param contractLoaAndAgreement the contractLoaAndAgreement to set
	 */
	public void setContractLoaAndAgreement(ContractLoaAndAgreement contractLoaAndAgreement) {
		this.contractLoaAndAgreement = contractLoaAndAgreement;
	}

	/**
	 * @return the teamMembers
	 */
	public List<ContractTeamMember> getTeamMembers() {
		return teamMembers;
	}

	/**
	 * @param teamMembers the teamMembers to set
	 */
	public void setTeamMembers(List<ContractTeamMember> teamMembers) {
		this.teamMembers = teamMembers;
	}

	/**
	 * @return the approvals
	 */
	public List<ContractApproval> getApprovals() {
		return approvals;
	}

	/**
	 * @param approvals the approvals to set
	 */
	public void setApprovals(List<ContractApproval> approvals) {
		if (this.approvals == null) {
			this.approvals = new ArrayList<ContractApproval>();
		} else {// Do update only it the passed list is a fresh list and not the same instance list.
			if (approvals != null) {
				for (ContractApproval oldApproval : this.approvals) {
					for (ContractApproval newApproval : approvals) {
						if (newApproval.getId() == null)
							continue;
						if (newApproval.getId().equals(oldApproval.getId())) {
							newApproval.setActive(oldApproval.isActive());
							newApproval.setDone(oldApproval.isDone());
							newApproval.setId(null);
							// Preserve individual approval user old state
							for (ContractApprovalUser oldApprovalUser : oldApproval.getApprovalUsers()) {
								for (ContractApprovalUser newApprovalUser : newApproval.getApprovalUsers()) {
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
			this.approvals.clear();
		}
		if (approvals != null) {
			this.approvals.addAll(approvals);
		}
	}

	/**
	 * @return the contractDetailCompleted
	 */
	public Boolean getContractDetailCompleted() {
		return contractDetailCompleted;
	}

	/**
	 * @return the contractItemCompleted
	 */
	public Boolean getContractItemCompleted() {
		return contractItemCompleted;
	}

	/**
	 * @return the contractSummaryCompleted
	 */
	public Boolean getContractSummaryCompleted() {
		return contractSummaryCompleted;
	}

	/**
	 * @param contractDetailCompleted the contractDetailCompleted to set
	 */
	public void setContractDetailCompleted(Boolean contractDetailCompleted) {
		this.contractDetailCompleted = contractDetailCompleted;
	}

	/**
	 * @param contractItemCompleted the contractItemCompleted to set
	 */
	public void setContractItemCompleted(Boolean contractItemCompleted) {
		this.contractItemCompleted = contractItemCompleted;
	}

	/**
	 * @param contractSummaryCompleted the contractSummaryCompleted to set
	 */
	public void setContractSummaryCompleted(Boolean contractSummaryCompleted) {
		this.contractSummaryCompleted = contractSummaryCompleted;
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

	/**
	 * @return the contractComments
	 */
	public List<ContractComment> getContractComments() {
		return contractComments;
	}

	/**
	 * @param contractComments the contractComments to set
	 */
	public void setContractComments(List<ContractComment> contractComments) {
		this.contractComments = contractComments;
	}

	/**
	 * @return the terminationRequested
	 */
	public Boolean getTerminationRequested() {
		return terminationRequested;
	}

	/**
	 * @param terminationRequested the terminationRequested to set
	 */
	public void setTerminationRequested(Boolean terminationRequested) {
		this.terminationRequested = terminationRequested;
	}

	/**
	 * @return the terminationRemarks
	 */
	public String getTerminationRemarks() {
		return terminationRemarks;
	}

	/**
	 * @param terminationRemarks the terminationRemarks to set
	 */
	public void setTerminationRemarks(String terminationRemarks) {
		this.terminationRemarks = terminationRemarks;
	}

	/**
	 * @return the cancelReason
	 */
	public String getCancelReason() {
		return cancelReason;
	}

	/**
	 * @param cancelReason the cancelReason to set
	 */
	public void setCancelReason(String cancelReason) {
		this.cancelReason = cancelReason;
	}

	/**
	 * @return the enableApproval
	 */
	public Boolean getEnableApproval() {
		return enableApproval;
	}

	/**
	 * @param enableApproval the enableApproval to set
	 */
	public void setEnableApproval(Boolean enableApproval) {
		this.enableApproval = enableApproval;
	}

	public Boolean getErpTransferred() {
		return erpTransferred;
	}

	public void setErpTransferred(Boolean erpTransferred) {
		this.erpTransferred = erpTransferred;
	}

	public ContractStatus getOldStatus() {
		return oldStatus;
	}

	public void setOldStatus(ContractStatus oldStatus) {
		this.oldStatus = oldStatus;
	}

	/**
	 * @return the documentDate
	 */
	public Date getDocumentDate() {
		return documentDate;
	}

	/**
	 * @param documentDate the documentDate to set
	 */
	public void setDocumentDate(Date documentDate) {
		this.documentDate = documentDate;
	}

	public Date getOldContractStartDate() {
		return oldContractStartDate;
	}

	public void setOldContractStartDate(Date oldContractStartDate) {
		this.oldContractStartDate = oldContractStartDate;
	}

	public Date getOldContractEndDate() {
		return oldContractEndDate;
	}

	public void setOldContractEndDate(Date oldContractEndDate) {
		this.oldContractEndDate = oldContractEndDate;
	}

	public BigDecimal getOldContractValue() {
		return oldContractValue;
	}

	public void setOldContractValue(BigDecimal oldContractValue) {
		this.oldContractValue = oldContractValue;
	}

	/**
	 * @return the idBasedOnBusinessUnit
	 */
	public Boolean getIdBasedOnBusinessUnit() {
		return idBasedOnBusinessUnit;
	}

	/**
	 * @param idBasedOnBusinessUnit the idBasedOnBusinessUnit to set
	 */
	public void setIdBasedOnBusinessUnit(Boolean idBasedOnBusinessUnit) {
		this.idBasedOnBusinessUnit = idBasedOnBusinessUnit;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProductContract other = (ProductContract) obj;
		return Objects.equals(id, other.id);
	}

	/**
	 * @return the remark
	 */
	public String getRemark() {
		return remark;
	}

	/**
	 * @param remark the remark to set
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Boolean getIsEditable() {
		return isEditable;
	}

	public void setIsEditable(Boolean isEditable) {
		this.isEditable = isEditable;
	}
}
