/**
 * 
 */
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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.privasia.procurehere.core.enums.SupplierFormApprovalStatus;
import com.privasia.procurehere.core.enums.SupplierFormSubmitionStatus;
import com.privasia.procurehere.core.utils.StringUtils;
import org.hibernate.annotations.Type;

/**
 * @author Ravi
 */
@Entity
@Table(name = "PROC_SUPPLIER_FORM_SUBM")
public class SupplierFormSubmition implements Serializable {

	private static final long serialVersionUID = -3140278439307258798L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@Column(name = "NAME", length = 128)
	private String name;

	@Column(name = "DESCRIPTION", length = 550)
	private String description;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "REQUESTED_DATE")
	private Date requestedDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "SUBMITED_DATE")
	private Date submitedDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "REQUESTED_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_SFB_USER_REQ_BY"))
	private User requestedBy;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_SUBMITED_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_SFB_USER_SUB_BY"))
	private User submittedBy;

	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS")
	private SupplierFormSubmitionStatus status;

	@Enumerated(EnumType.STRING)
	@Column(name = "APPROVAL_STATUS")
	private SupplierFormApprovalStatus approvalStatus;

	@ManyToOne(optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "SUPPLIER_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_SFB_SUPPLIER_ID"))
	private Supplier supplier;

	@ManyToOne(optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "FAV_SUPPLIER_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_SFB_FAV_SUPPLIER_ID"))
	private FavouriteSupplier favouriteSupplier;

	@ManyToOne(optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "BUYER_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_SFB_BUYER_ID"))
	private Buyer buyer;

	@Column(name = "IS_ONBOARDING_FORM")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean isOnboardingForm = Boolean.FALSE;

	@ManyToOne(optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "FORM_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_SFB_FOMR_ID"))
	private SupplierForm supplierForm;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "supplierFormSubmition", orphanRemoval = true, cascade = CascadeType.ALL)
	List<SupplierFormSubmissionItem> formSubmitionItems;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "supplierFormSubmition", orphanRemoval = true, cascade = CascadeType.ALL)
	@OrderBy("level")
	private List<SupplierFormSubmitionApproval> approvals;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "supplierFormSubmition", cascade = { CascadeType.ALL })
	@OrderBy("createdDate")
	private List<SupplierFormSubmitionComment> formComments;

	public SupplierFormSubmition() {
		this.isOnboardingForm = Boolean.FALSE;
	}

	public SupplierFormSubmition(String id) {
		this.id = id;
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
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
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
	 * @return the requestedDate
	 */
	public Date getRequestedDate() {
		return requestedDate;
	}

	/**
	 * @param requestedDate the requestedDate to set
	 */
	public void setRequestedDate(Date requestedDate) {
		this.requestedDate = requestedDate;
	}

	/**
	 * @return the submitedDate
	 */
	public Date getSubmitedDate() {
		return submitedDate;
	}

	/**
	 * @param submitedDate the submitedDate to set
	 */
	public void setSubmitedDate(Date submitedDate) {
		this.submitedDate = submitedDate;
	}

	/**
	 * @return the requestedBy
	 */
	public User getRequestedBy() {
		return requestedBy;
	}

	/**
	 * @param requestedBy the requestedBy to set
	 */
	public void setRequestedBy(User requestedBy) {
		this.requestedBy = requestedBy;
	}

	/**
	 * @return the submittedBy
	 */
	public User getSubmittedBy() {
		return submittedBy;
	}

	/**
	 * @param submittedBy the submittedBy to set
	 */
	public void setSubmittedBy(User submittedBy) {
		this.submittedBy = submittedBy;
	}

	/**
	 * @return the status
	 */
	public SupplierFormSubmitionStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(SupplierFormSubmitionStatus status) {
		this.status = status;
	}

	/**
	 * @return the approvalStatus
	 */
	public SupplierFormApprovalStatus getApprovalStatus() {
		return approvalStatus;
	}

	/**
	 * @param approvalStatus the approvalStatus to set
	 */
	public void setApprovalStatus(SupplierFormApprovalStatus approvalStatus) {
		this.approvalStatus = approvalStatus;
	}

	/**
	 * @return the supplier
	 */
	public Supplier getSupplier() {
		return supplier;
	}

	/**
	 * @param supplier the supplier to set
	 */
	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	/**
	 * @return the favouriteSupplier
	 */
	public FavouriteSupplier getFavouriteSupplier() {
		return favouriteSupplier;
	}

	/**
	 * @param favouriteSupplier the favouriteSupplier to set
	 */
	public void setFavouriteSupplier(FavouriteSupplier favouriteSupplier) {
		this.favouriteSupplier = favouriteSupplier;
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
	 * @return the isOnboardingForm
	 */
	public Boolean getIsOnboardingForm() {
		return isOnboardingForm;
	}

	/**
	 * @param isOnboardingForm the isOnboardingForm to set
	 */
	public void setIsOnboardingForm(Boolean isOnboardingForm) {
		this.isOnboardingForm = isOnboardingForm;
	}

	/**
	 * @return the supplierForm
	 */
	public SupplierForm getSupplierForm() {
		return supplierForm;
	}

	/**
	 * @param supplierForm the supplierForm to set
	 */
	public void setSupplierForm(SupplierForm supplierForm) {
		this.supplierForm = supplierForm;
	}

	/**
	 * @return the formSubmitionItems
	 */
	public List<SupplierFormSubmissionItem> getFormSubmitionItems() {
		return formSubmitionItems;
	}

	/**
	 * @param formSubmitionItems the formSubmitionItems to set
	 */
	public void setFormSubmitionItems(List<SupplierFormSubmissionItem> formSubmitionItems) {
		this.formSubmitionItems = formSubmitionItems;
	}

	/**
	 * @return the approvals
	 */
	public List<SupplierFormSubmitionApproval> getApprovals() {
		return approvals;
	}

	/**
	 * @param approvals the approvals to set
	 */
	public void setApprovals(List<SupplierFormSubmitionApproval> approvals) {
		if (this.approvals == null) {
			this.approvals = new ArrayList<SupplierFormSubmitionApproval>();
		} else {// Do update only it the passed list is a fresh list and not the same instance list.
			if (approvals != null) {
				for (SupplierFormSubmitionApproval oldApproval : this.approvals) {
					for (SupplierFormSubmitionApproval newApproval : approvals) {
						if (newApproval.getId() == null) {
							continue;
						}
						if (newApproval.getId().equals(oldApproval.getId())) {
							newApproval.setActive(oldApproval.isActive());

							newApproval.setApprovalType(newApproval.getApprovalType() != null ? newApproval.getApprovalType() : oldApproval.getApprovalType());
							newApproval.setBatchNo(oldApproval.getBatchNo());

							newApproval.setLevel(newApproval.getLevel() == null ? oldApproval.getLevel() : newApproval.getLevel());
							newApproval.setDone(oldApproval.isDone());
							newApproval.setSupplierFormSubmition(this);
							newApproval.setId(null);

							// Preserve individual approval user old state
							for (SupplierFormSubmitionApprovalUser oldApprovalUser : oldApproval.getApprovalUsers()) {
								for (SupplierFormSubmitionApprovalUser newApprovalUser : newApproval.getApprovalUsers()) {
									if (newApprovalUser.getUser() == null || (newApprovalUser.getUser() != null && StringUtils.checkString(newApprovalUser.getUser().getId()).length() == 0)) {
										continue;
									}
									if (newApprovalUser.getUser().getId().equals(oldApprovalUser.getUser().getId())) {
										newApprovalUser.setActionDate(oldApprovalUser.getActionDate());
										newApprovalUser.setApprovalStatus(oldApprovalUser.getApprovalStatus());
										newApprovalUser.setRemarks(oldApprovalUser.getRemarks());
										newApprovalUser.setApproval(newApproval);
									}
								}
							}
						}
					}
				}
			} else {
			}
			this.approvals.clear();
		}
		if (approvals != null) {
			this.approvals.addAll(approvals);
		}

	}

	public void updateSupplierFormApprovals(List<SupplierFormSubmitionApproval> approvals) {

		if (this.approvals == null) {
			this.approvals = new ArrayList<SupplierFormSubmitionApproval>();
		} else {// Do update only it the passed list is a fresh list and not the same instance list.
			if (approvals != null) {
				for (SupplierFormSubmitionApproval oldApproval : this.approvals) {
					for (SupplierFormSubmitionApproval newApproval : approvals) {
						if (newApproval.getId() == null)
							continue;
						if (newApproval.getId().equals(oldApproval.getId())) {
							newApproval.setActive(oldApproval.isActive());
							newApproval.setDone(oldApproval.isDone());
							newApproval.setId(null);
							newApproval.setBatchNo(oldApproval.getBatchNo());
							// Preserve individual approval user old state
							for (SupplierFormSubmitionApprovalUser oldApprovalUser : oldApproval.getApprovalUsers()) {
								for (SupplierFormSubmitionApprovalUser newApprovalUser : newApproval.getApprovalUsers()) {
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
	 * @return the formComments
	 */
	public List<SupplierFormSubmitionComment> getFormComments() {
		return formComments;
	}

	/**
	 * @param formComments the formComments to set
	 */
	public void setFormComments(List<SupplierFormSubmitionComment> formComments) {
		this.formComments = formComments;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
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
		SupplierFormSubmition other = (SupplierFormSubmition) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (status != other.status)
			return false;
		return true;
	}

	public String toLogString() {
		return "SupplierFormSubmition [name=" + name + ", description=" + description + ", status=" + status + ", approvalStatus=" + approvalStatus + ", isOnboardingForm=" + isOnboardingForm + "]";
	}

	
}
