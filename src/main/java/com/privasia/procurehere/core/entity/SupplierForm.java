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
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.privasia.procurehere.core.enums.SupplierFormsStatus;
import com.privasia.procurehere.core.utils.CollectionUtil;

/**
 * @author Ravi
 */
@Entity
@Table(name = "PROC_SUPPLIER_FORM", indexes = { @Index(columnList = "TENANT_ID", name = "INDEX_SF_TENANT_ID") })
public class SupplierForm implements Serializable {

	private static final long serialVersionUID = -8404448312458008270L;

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
	@Column(name = "CREATED_DATE")
	private Date createdDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "MODIFIED_DATE")
	private Date modifiedDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CREATED_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_SF_USER_CREATED_BY"))
	private User createdBy;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_LAST_MODIFIED_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_SF_USER_MODIFIED_BY"))
	private User modifiedBy;

	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS")
	private SupplierFormsStatus status;

	@Column(name = "TENANT_ID", nullable = false, length = 64)
	private String tenantId;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "supplierForm", orphanRemoval = true, cascade = CascadeType.ALL)
	@OrderBy("level, order")
	List<SupplierFormItem> formItems;

	@Column(name = "PENDING_COUNT", nullable = true)
	private Long pendingCount;

	@Column(name = "SUBMITTED_COUNT", nullable = true)
	private Long submittedCount;

	@Column(name = "ACCEPTED_COUNT", nullable = true)
	private Long acceptedCount;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "supplierForm", orphanRemoval = true, cascade = CascadeType.ALL)
	@OrderBy("level")
	private List<SupplierFormApproval> approvals;

	@Transient
	private String btnValue;

	public SupplierForm() {
		super();
	}

	public SupplierForm(String id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public SupplierForm copyFormDetails(SupplierForm oldSupplierForm, String tenantId) {
		SupplierForm newSupplierForm = new SupplierForm();
		copyFormItemDetails(oldSupplierForm, newSupplierForm, tenantId);
		copyApprovalDetails(oldSupplierForm, newSupplierForm, tenantId);
		return newSupplierForm;

	}

	private void copyFormItemDetails(SupplierForm oldSupplierForm, SupplierForm newSupplierForm, String tenantId) {
		if (CollectionUtil.isNotEmpty(oldSupplierForm.getFormItems())) {
			newSupplierForm.setFormItems(new ArrayList<SupplierFormItem>());
			for (SupplierFormItem formItem : oldSupplierForm.getFormItems()) {
				SupplierFormItem newFormItem = formItem.copyFrom(newSupplierForm, tenantId);
				newSupplierForm.getFormItems().add(newFormItem);
			}
		}
	}

	private void copyApprovalDetails(SupplierForm oldSupplierForm, SupplierForm newSupplierForm, String tenantId) {

		if (CollectionUtil.isNotEmpty(oldSupplierForm.getApprovals())) {
			newSupplierForm.setApprovals(new ArrayList<SupplierFormApproval>());
			for (SupplierFormApproval app : oldSupplierForm.getApprovals()) {
				newSupplierForm.getApprovals().add(app.copyFrom());
			}
		}

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
	 * @return the status
	 */
	public SupplierFormsStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(SupplierFormsStatus status) {
		this.status = status;
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
	 * @return the formItems
	 */
	public List<SupplierFormItem> getFormItems() {
		return formItems;
	}

	/**
	 * @param formItems the formItems to set
	 */
	public void setFormItems(List<SupplierFormItem> formItems) {
		this.formItems = formItems;
	}

	/**
	 * @return the pendingCount
	 */
	public Long getPendingCount() {
		return pendingCount;
	}

	/**
	 * @param pendingCount the pendingCount to set
	 */
	public void setPendingCount(Long pendingCount) {
		this.pendingCount = pendingCount;
	}

	/**
	 * @return the submittedCount
	 */
	public Long getSubmittedCount() {
		return submittedCount;
	}

	/**
	 * @param submittedCount the submittedCount to set
	 */
	public void setSubmittedCount(Long submittedCount) {
		this.submittedCount = submittedCount;
	}

	/**
	 * @return the acceptedCount
	 */
	public Long getAcceptedCount() {
		return acceptedCount;
	}

	/**
	 * @param acceptedCount the acceptedCount to set
	 */
	public void setAcceptedCount(Long acceptedCount) {
		this.acceptedCount = acceptedCount;
	}

	/**
	 * @return the btnValue
	 */
	public String getBtnValue() {
		return btnValue;
	}

	/**
	 * @param btnValue the btnValue to set
	 */
	public void setBtnValue(String btnValue) {
		this.btnValue = btnValue;
	}

	/**
	 * @return the approvals
	 */
	public List<SupplierFormApproval> getApprovals() {
		return approvals;
	}

	/**
	 * @param approvals the approvals to set
	 */
	public void setApprovals(List<SupplierFormApproval> approvals) {
		if (this.approvals == null) {
			this.approvals = new ArrayList<SupplierFormApproval>();
		} else {// Do update only it the passed list is a fresh list and not the same instance list.
			if (approvals != null) {
				for (SupplierFormApproval oldApproval : this.approvals) {
					for (SupplierFormApproval newApproval : approvals) {
						if (newApproval.getId() == null)
							continue;
						if (newApproval.getId().equals(oldApproval.getId())) {
							newApproval.setId(null);

							// Preserve individual approval user old state
							for (SupplierFormApprovalUser oldApprovalUser : oldApproval.getApprovalUsers()) {
								for (SupplierFormApprovalUser newApprovalUser : newApproval.getApprovalUsers()) {
									if (newApprovalUser.getUser() == null || newApprovalUser.getUser().getId() == null) {
										continue;
									}
									if (newApprovalUser.getUser().getId().equals(oldApprovalUser.getUser().getId())) {
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		SupplierForm other = (SupplierForm) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SupplierForm [id=" + id + ", name=" + name + ", description=" + description + ", createdDate=" + createdDate + ", status=" + status + "]";
	}

}
