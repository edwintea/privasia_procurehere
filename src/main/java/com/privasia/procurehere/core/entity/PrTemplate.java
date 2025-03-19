package com.privasia.procurehere.core.entity;

import java.io.Serializable;
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
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.pojo.PrTemplateFieldPojo;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;
import org.hibernate.annotations.Type;

/**
 * @author parveen
 */
@Entity
@Table(name = "PROC_PR_TEMPLATE", indexes = { @Index(columnList = "TENANT_ID", name = "INDEX_PR_TMPLATE_TENANT_ID") })
public class PrTemplate implements Serializable {

	private static final long serialVersionUID = 4407334326910235437L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@NotNull(message = "{template.name.empty}")
	@Size(min = 1, max = 128, message = "{template.name.length}")
	@Column(name = "TEMPLATE_NAME", length = 128)
	private String templateName;

	@Size(min = 0, max = 300, message = "{template.description.length}")
	@Column(name = "TEMPLATE_DESCRIPTION", length = 300)
	private String templateDescription;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "TENANT_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_PR_TMPLATE_BUYER_ID"))
	private Buyer buyer;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "template", cascade = { CascadeType.ALL }, orphanRemoval = true)
	private List<PrTemplateField> fields;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "CREATED_BY", nullable = false, foreignKey = @ForeignKey(name = "FK_PR_TMPLATE_CREATED_BY"))
	private User createdBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_DATE", nullable = false)
	private Date createdDate;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "MODIFIED_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_PR_TMPLATE_MODIFIED_BY"))
	private User modifiedBy;

	@JsonIgnore
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "MODIFIED_DATE")
	private Date modifiedDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "TEMPLATE_STATUS")
	private Status status;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "prTemplate", orphanRemoval = true, cascade = CascadeType.ALL)
	@OrderBy("level")
	private List<PrTemplateApproval> approvals;

	@JsonIgnore
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "prTemplate", orphanRemoval = true, cascade = CascadeType.ALL)
	@OrderBy("level")
	private List<PoTemplateApproval> poApprovals;

	@JsonIgnore
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "prTemplate", orphanRemoval = true, cascade = CascadeType.ALL)
	@OrderBy("level")
	private List<GrTemplateApproval> grApprovals;

	@JsonIgnore
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "prTemplate", orphanRemoval = true, cascade = CascadeType.ALL)
	@OrderBy("level")
	private List<InvoiceTemplateApproval> invoiceApprovals;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "prTemplate", orphanRemoval = true, cascade = CascadeType.ALL)
	private List<TemplatePrTeamMembers> teamMembers;

	@NotNull
	@Column(name = "IS_APPROVAL_VISIBLE", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean approvalVisible = Boolean.TRUE;

	@NotNull
	@Column(name = "IS_APPROVAL_READ_ONLY", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean approvalReadOnly = Boolean.FALSE;

	@NotNull
	@Column(name = "IS_APPROVAL_OPTIONAL", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean approvalOptional = Boolean.FALSE;

	@Min(0)
	@Max(100)
	@Column(name = "MINIMUM_APPROVAL_COUNT", nullable = true)
	private Integer minimumApprovalCount;

	@NotNull
	@Column(name = "IS_APPROVAL_PO_VISIBLE", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean approvalPoVisible = Boolean.TRUE;

	@NotNull
	@Column(name = "IS_APPROVAL_PO_READ_ONLY", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean approvalPoReadOnly = Boolean.FALSE;

	@NotNull
	@Column(name = "IS_APPROVAL_PO_OPTIONAL", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean approvalPoOptional = Boolean.FALSE;

	@Min(0)
	@Max(100)
	@Column(name = "MINIMUM_PO_APPROVAL_COUNT", nullable = true)
	private Integer minimumPoApprovalCount;

	@NotNull
	@Column(name = "IS_APPROVAL_GR_VISIBLE", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean approvalGrVisible = Boolean.TRUE;

	@NotNull
	@Column(name = "IS_APPROVAL_GR_READ_ONLY", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean approvalGrReadOnly = Boolean.FALSE;

	@NotNull
	@Column(name = "IS_APPROVAL_GR_OPTIONAL", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean approvalGrOptional = Boolean.FALSE;

	@Min(0)
	@Max(100)
	@Column(name = "MINIMUM_GR_APPROVAL_COUNT", nullable = true)
	private Integer minimumGrApprovalCount;

	@NotNull
	@Column(name = "IS_APPROVAL_INVOICE_VISIBLE", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean approvalInvoiceVisible = Boolean.TRUE;

	@NotNull
	@Column(name = "IS_APPROVAL_INVOICE_READ_ONLY", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean approvalInvoiceReadOnly = Boolean.FALSE;

	@NotNull
	@Column(name = "IS_APPROVAL_INVOICE_OPTIONAL", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
	private Boolean approvalInvoiceOptional = Boolean.FALSE;

	@Min(0)
	@Max(100)
	@Column(name = "MINIMUM_INVOICE_APPROVAL_COUNT", nullable = true)
	private Integer minimumInvoiceApprovalCount;

	@Column(name = "CONTRACT_ITEMS_ONLY", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean contractItemsOnly = Boolean.FALSE;

	@Column(name = "LOCK_BUDGET", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean lockBudget = Boolean.FALSE;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "PAYMENT_TERMES", nullable = true, foreignKey = @ForeignKey(name = "FK_PR_TMPLATE_PAY_TERMS"))
	private PaymentTermes paymentTermes;

	@Transient
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean lockBudgetNo = Boolean.FALSE;

	@Transient
	PrTemplateFieldPojo templateFieldBinding;

	@Transient
	boolean checkControl = true;

	public PrTemplate() {
		this.approvalOptional = Boolean.TRUE;
		this.approvalReadOnly = Boolean.FALSE;
		this.approvalVisible = Boolean.TRUE;

		this.approvalPoOptional = Boolean.TRUE;
		this.approvalPoReadOnly = Boolean.FALSE;
		this.approvalPoVisible = Boolean.TRUE;

		this.approvalGrOptional = Boolean.TRUE;
		this.approvalGrReadOnly = Boolean.FALSE;
		this.approvalGrVisible = Boolean.TRUE;

		this.approvalInvoiceOptional = Boolean.TRUE;
		this.approvalInvoiceReadOnly = Boolean.FALSE;
		this.approvalInvoiceVisible = Boolean.TRUE;

	}

	public PrTemplate createShallowCopy() {
		PrTemplate ret = new PrTemplate();
		ret.setCreatedBy(createdBy);
		ret.setCreatedDate(createdDate);
		ret.setId(id);
		ret.setModifiedBy(modifiedBy);
		ret.setModifiedDate(modifiedDate);
		ret.setStatus(status);
		ret.setTemplateDescription(templateDescription);
		ret.setTemplateName(templateName);
		ret.setContractItemsOnly(contractItemsOnly);
		return ret;
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
	 * @return the templateDescription
	 */
	public String getTemplateDescription() {
		return templateDescription;
	}

	/**
	 * @param templateDescription the templateDescription to set
	 */
	public void setTemplateDescription(String templateDescription) {
		this.templateDescription = templateDescription;
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
	 * @return the fields
	 */
	public List<PrTemplateField> getFields() {
		return fields;
	}

	/**
	 * @param fields the fields to set
	 */
	public void setFields(List<PrTemplateField> fields) {
		this.fields = fields;
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
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(Status status) {
		this.status = status;
	}

	/**
	 * @return the templateFieldBinding
	 */
	public PrTemplateFieldPojo getTemplateFieldBinding() {
		return templateFieldBinding;
	}

	/**
	 * @param templateFieldBinding the templateFieldBinding to set
	 */
	public void setTemplateFieldBinding(PrTemplateFieldPojo templateFieldBinding) {
		this.templateFieldBinding = templateFieldBinding;
	}

	/**
	 * @return the checkControl
	 */
	public boolean isCheckControl() {
		return checkControl;
	}

	/**
	 * @param checkControl the checkControl to set
	 */
	public void setCheckControl(boolean checkControl) {
		this.checkControl = checkControl;
	}

	/**
	 * @return the approvalVisible
	 */
	public Boolean getApprovalVisible() {
		return approvalVisible;
	}

	/**
	 * @param approvalVisible the approvalVisible to set
	 */
	public void setApprovalVisible(Boolean approvalVisible) {
		this.approvalVisible = approvalVisible;
	}

	/**
	 * @return the approvalReadOnly
	 */
	public Boolean getApprovalReadOnly() {
		return approvalReadOnly;
	}

	/**
	 * @param approvalReadOnly the approvalReadOnly to set
	 */
	public void setApprovalReadOnly(Boolean approvalReadOnly) {
		this.approvalReadOnly = approvalReadOnly;
	}

	/**
	 * @return the approvalOptional
	 */
	public Boolean getApprovalOptional() {
		return approvalOptional;
	}

	/**
	 * @param approvalOptional the approvalOptional to set
	 */
	public void setApprovalOptional(Boolean approvalOptional) {
		this.approvalOptional = approvalOptional;
	}

	/**
	 * @return the approvalVisible
	 */
	public Boolean getApprovalPoVisible() {
		return approvalPoVisible;
	}

	/**
	 * @param approvalPoVisible the approvalPoVisible to set
	 */
	public void setApprovalPoVisible(Boolean approvalPoVisible) {
		this.approvalPoVisible = approvalPoVisible;
	}

	/**
	 * @return the approvalReadOnly
	 */
	public Boolean getApprovalPoReadOnly() {
		return approvalPoReadOnly;
	}

	/**
	 * @param approvalPoReadOnly the approvalReadOnly to set
	 */
	public void setApprovalPoReadOnly(Boolean approvalPoReadOnly) {
		this.approvalPoReadOnly = approvalPoReadOnly;
	}

	/**
	 * @return the approvalOptional
	 */
	public Boolean getApprovalPoOptional() {
		return approvalPoOptional;
	}

	/**
	 * @param approvalPoOptional the approvalOptional to set
	 */
	public void setApprovalPoOptional(Boolean approvalPoOptional) {
		this.approvalPoOptional = approvalPoOptional;
	}

	/**
	 * @return the approvalVisible
	 */
	public Boolean getApprovalGrVisible() {
		return approvalGrVisible;
	}

	/**
	 * @param approvalGrVisible the approvalGrVisible to set
	 */
	public void setApprovalGrVisible(Boolean approvalGrVisible) {
		this.approvalGrVisible = approvalGrVisible;
	}

	/**
	 * @return the approvalReadOnly
	 */
	public Boolean getApprovalGrReadOnly() {
		return approvalGrReadOnly;
	}

	/**
	 * @param approvalGrReadOnly the approvalReadOnly to set
	 */
	public void setApprovalGrReadOnly(Boolean approvalGrReadOnly) {
		this.approvalGrReadOnly = approvalGrReadOnly;
	}

	/**
	 * @return the approvalOptional
	 */
	public Boolean getApprovalGrOptional() {
		return approvalGrOptional;
	}

	/**
	 * @param approvalGrOptional the approvalOptional to set
	 */
	public void setApprovalGrOptional(Boolean approvalGrOptional) {
		this.approvalGrOptional = approvalGrOptional;
	}

	/**
	 * @return the approvalVisible
	 */
	public Boolean getApprovalInvoiceVisible() {
		return approvalInvoiceVisible;
	}

	/**
	 * @param approvalInvoiceVisible the approvalPoVisible to set
	 */
	public void setApprovalInvoiceVisible(Boolean approvalInvoiceVisible) {
		this.approvalInvoiceVisible = approvalInvoiceVisible;
	}

	/**
	 * @return the approvalReadOnly
	 */
	public Boolean getApprovalInvoiceReadOnly() {
		return approvalInvoiceReadOnly;
	}

	/**
	 * @param approvalInvoiceReadOnly the approvalReadOnly to set
	 */
	public void setApprovalInvoiceReadOnly(Boolean approvalInvoiceReadOnly) {
		this.approvalInvoiceReadOnly = approvalInvoiceReadOnly;
	}

	/**
	 * @return the approvalOptional
	 */
	public Boolean getApprovalInvoiceOptional() {
		return approvalInvoiceOptional;
	}

	/**
	 * @param approvalInvoiceOptional the approvalOptional to set
	 */
	public void setApprovalInvoiceOptional(Boolean approvalInvoiceOptional) {
		this.approvalInvoiceOptional = approvalInvoiceOptional;
	}


	public Integer getMinimumApprovalCount() {
		return minimumApprovalCount;
	}

	public void setMinimumApprovalCount(Integer minimumApprovalCount) {
		this.minimumApprovalCount = minimumApprovalCount;
	}

	public Integer getMinimumPoApprovalCount() {
		return minimumPoApprovalCount;
	}

	public void setMinimumPoApprovalCount(Integer minimumPoApprovalCount) {
		this.minimumPoApprovalCount = minimumPoApprovalCount;
	}

	public Integer getMinimumGrApprovalCount() {
		return minimumGrApprovalCount;
	}

	public void setMinimumGrApprovalCount(Integer minimumGrApprovalCount) {
		this.minimumGrApprovalCount = minimumGrApprovalCount;
	}

	public Integer getMinimumInvoiceApprovalCount() {
		return minimumInvoiceApprovalCount;
	}

	public void setMinimumInvoiceApprovalCount(Integer minimumInvoiceApprovalCount) {
		this.minimumInvoiceApprovalCount = minimumInvoiceApprovalCount;
	}




	public Boolean getContractItemsOnly() {
		return contractItemsOnly;
	}

	public void setContractItemsOnly(Boolean contractItemsOnly) {
		this.contractItemsOnly = contractItemsOnly;
	}

	/**
	 * @return the approvals
	 */
	public List<PrTemplateApproval> getApprovals() {
		return approvals;
	}

	/**
	 * @param approvals the approvals to set
	 */
	public void setApprovals(List<PrTemplateApproval> approvals) {
		if (this.approvals == null) {
			this.approvals = new ArrayList<PrTemplateApproval>();
		} else {
			this.approvals.clear();
		}
		if (approvals != null) {
			this.approvals.addAll(approvals);
		}
	}

	/**
	 * @return the poApprovals
	 */

	public List<PoTemplateApproval> getPoApprovals() {
		return poApprovals;
	}

	/**
	 * @param poApprovals the poApprovals to set
	 */
	public void setPoApprovals(List<PoTemplateApproval> poApprovals) {
		if (this.poApprovals == null) {
			this.poApprovals = new ArrayList<PoTemplateApproval>();
		} else {
			this.poApprovals.clear();
		}
		if (poApprovals != null) {
			this.poApprovals.addAll(poApprovals);
		}
	}

	/**
	 * @return the grApprovals
	 */
	public List<GrTemplateApproval> getGrApprovals() {
		return grApprovals;
	}

	/**
	 * @param grApprovals the grApprovals to set
	 */
	public void setGrApprovals(List<GrTemplateApproval> grApprovals) {
		if (this.grApprovals == null) {
			this.grApprovals = new ArrayList<GrTemplateApproval>();
		} else {
			this.grApprovals.clear();
		}
		if (grApprovals != null) {
			this.grApprovals.addAll(grApprovals);
		}
	}

	/**
	 * @return the grApprovals
	 */
	public List<InvoiceTemplateApproval> getInvoiceApprovals() {
		return invoiceApprovals;
	}

	/**
	 * @param invoiceApprovals the grApprovals to set
	 */
	public void setInvoiceApprovals(List<InvoiceTemplateApproval> invoiceApprovals) {
		if (this.invoiceApprovals == null) {
			this.invoiceApprovals = new ArrayList<InvoiceTemplateApproval>();
		} else {
			this.invoiceApprovals.clear();
		}
		if (invoiceApprovals != null) {
			this.invoiceApprovals.addAll(invoiceApprovals);
		}
	}

	public List<TemplatePrTeamMembers> getTeamMembers() {
		return teamMembers;
	}

	public void setTeamMembers(List<TemplatePrTeamMembers> teamMembers) {
		// this.teamMembers = teamMembers;
		if (this.teamMembers == null) {
			this.teamMembers = new ArrayList<TemplatePrTeamMembers>();
		} else {
			this.teamMembers.clear();
		}
		if (teamMembers != null) {
			this.teamMembers.addAll(teamMembers);
		}
	}

	public Boolean getLockBudget() {
		return lockBudget;
	}

	public void setLockBudget(Boolean lockBudget) {
		this.lockBudget = lockBudget;
	}

	public Boolean getLockBudgetNo() {
		return lockBudgetNo;
	}

	public void setLockBudgetNo(Boolean lockBudgetNo) {
		this.lockBudgetNo = lockBudgetNo;
	}
	
	/**
	 * @return the paymentTermes
	 */
	public PaymentTermes getPaymentTermes() {
		return paymentTermes;
	}

	/**
	 * @param paymentTermes the paymentTermes to set
	 */
	public void setPaymentTermes(PaymentTermes paymentTermes) {
		this.paymentTermes = paymentTermes;
	}
	

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((templateDescription == null) ? 0 : templateDescription.hashCode());
		result = prime * result + ((templateName == null) ? 0 : templateName.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PrTemplate other = (PrTemplate) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (templateDescription == null) {
			if (other.templateDescription != null)
				return false;
		} else if (!templateDescription.equals(other.templateDescription))
			return false;
		if (templateName == null) {
			if (other.templateName != null)
				return false;
		} else if (!templateName.equals(other.templateName))
			return false;
		return true;
	}

	public String toLogString() {
		return "PrTemplate [id=" + id + ", templateName=" + templateName + ", templateDescription=" + templateDescription + ", createdDate=" + createdDate + ", modifiedDate=" + modifiedDate + ", status=" + status + "]";
	}

}
