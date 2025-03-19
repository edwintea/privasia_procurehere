package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
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
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.privasia.procurehere.core.enums.SupplierPerformanceAuditActionType;
import org.hibernate.annotations.Type;

/**
 * @author Jasyhree
 */
@Entity
@Table(name = "PROC_SUP_PERFORMANCE_AUDIT")

public class SupplierPerformanceAudit implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7940813546752659261L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "ACTION_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_SP_AUDIT_ACT_BY_ID"))
	private User actionBy;

	@Column(name = "ACTION_DATE", nullable = false)
	private Date actionDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "ACTION_TYPE", nullable = false)
	private SupplierPerformanceAuditActionType action;

	@Column(name = "DESCRIPTION", length = 3000)
	private String description;

	@Lob
	@Type(type = "org.hibernate.type.BinaryType")
	@Column(name = "SUMMARY_SNAPSHOT")
	private byte[] summarySnapshot;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "FORM_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_SP_AUDIT_FORM_ID"))
	private SupplierPerformanceForm form;

	@Column(name = "HAS_SNAPSHOT")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean hasSnapshot = Boolean.FALSE;

	@Column(name = "SHOW_TO_OWNER")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean owner = Boolean.FALSE;

	@Column(name = "SHOW_TO_EVALUATOR")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean evaluator = Boolean.FALSE;

	@Column(name = "SHOW_TO_APPROVER")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean approver = Boolean.FALSE;

	@Column(name = "SHOW_SNAPSHOT_TO_OWNER")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean ownerSnapshot = Boolean.FALSE;

	@Column(name = "SHOW_SNAPSHOT_TO_EVALUATOR")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean evaluatorSnapshot = Boolean.FALSE;

	@Column(name = "SHOW_SNAPSHOT_TO_APPROVER")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean approverSnapshot = Boolean.FALSE;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "FORM_EVAL_USER_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_SP_AUDIT_FORM_EVAL_USR_ID"))
	private SupplierPerformanceEvaluatorUser evaluatorUser;

	public SupplierPerformanceAudit(String id, String actionBy, Date actionDate, SupplierPerformanceAuditActionType action, String description, Boolean hasSnapshot) {
		this.id = id;
		User user = new User();
		user.setName(actionBy);
		this.actionBy = user;
		this.actionDate = actionDate;
		this.action = action;
		this.description = description;
		this.hasSnapshot = hasSnapshot;
	}

	public SupplierPerformanceAudit() {
		this.hasSnapshot = Boolean.FALSE;
	}

	/**
	 * @param form
	 * @param evaluatorUser
	 * @param actionBy
	 * @param actionDate
	 * @param action
	 * @param description
	 * @param summarySnapshot
	 * @param owner
	 * @param evaluator
	 * @param approver
	 * @param ownerSnapshot
	 * @param evaluatorSnapshot
	 * @param approverSnapshot
	 */
	public SupplierPerformanceAudit(SupplierPerformanceForm form, SupplierPerformanceEvaluatorUser evaluatorUser, User actionBy, Date actionDate, SupplierPerformanceAuditActionType action, String description, byte[] summarySnapshot, Boolean owner, Boolean evaluator, Boolean approver, Boolean ownerSnapshot, Boolean evaluatorSnapshot, Boolean approverSnapshot) {
		this.form = form;
		this.evaluatorUser = evaluatorUser;
		this.actionBy = actionBy;
		this.actionDate = actionDate;
		this.action = action;
		this.description = description;
		this.summarySnapshot = summarySnapshot;
		this.hasSnapshot = Boolean.TRUE;
		this.owner = owner;
		this.evaluator = evaluator;
		this.approver = approver;
		this.ownerSnapshot = ownerSnapshot;
		this.evaluatorSnapshot = evaluatorSnapshot;
		this.approverSnapshot = approverSnapshot;

	}

	/**
	 * @param form
	 * @param evaluatorUser
	 * @param actionBy
	 * @param actionDate
	 * @param action
	 * @param description
	 * @param owner
	 * @param evaluator
	 * @param approver
	 */
	public SupplierPerformanceAudit(SupplierPerformanceForm form, SupplierPerformanceEvaluatorUser evaluatorUser, User actionBy, Date actionDate, SupplierPerformanceAuditActionType action, String description, Boolean owner, Boolean evaluator, Boolean approver) {
		this.form = form;
		this.evaluatorUser = evaluatorUser;
		this.actionBy = actionBy;
		this.actionDate = actionDate;
		this.action = action;
		this.description = description;
		this.owner = owner;
		this.evaluator = evaluator;
		this.approver = approver;
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
	 * @return the action
	 */
	public SupplierPerformanceAuditActionType getAction() {
		return action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(SupplierPerformanceAuditActionType action) {
		this.action = action;
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
	 * @return the summarySnapshot
	 */
	public byte[] getSummarySnapshot() {
		return summarySnapshot;
	}

	/**
	 * @param summarySnapshot the summarySnapshot to set
	 */
	public void setSummarySnapshot(byte[] summarySnapshot) {
		this.summarySnapshot = summarySnapshot;
	}

	/**
	 * @return the form
	 */
	public SupplierPerformanceForm getForm() {
		return form;
	}

	/**
	 * @param form the form to set
	 */
	public void setForm(SupplierPerformanceForm form) {
		this.form = form;
	}

	/**
	 * @return the hasSnapshot
	 */
	public Boolean getHasSnapshot() {
		return hasSnapshot;
	}

	/**
	 * @param hasSnapshot the hasSnapshot to set
	 */
	public void setHasSnapshot(Boolean hasSnapshot) {
		this.hasSnapshot = hasSnapshot;
	}

	/**
	 * @return the owner
	 */
	public Boolean getOwner() {
		return owner;
	}

	/**
	 * @param owner the owner to set
	 */
	public void setOwner(Boolean owner) {
		this.owner = owner;
	}

	/**
	 * @return the evaluator
	 */
	public Boolean getEvaluator() {
		return evaluator;
	}

	/**
	 * @param evaluator the evaluator to set
	 */
	public void setEvaluator(Boolean evaluator) {
		this.evaluator = evaluator;
	}

	/**
	 * @return the approver
	 */
	public Boolean getApprover() {
		return approver;
	}

	/**
	 * @param approver the approver to set
	 */
	public void setApprover(Boolean approver) {
		this.approver = approver;
	}

	/**
	 * @return the ownerSnapshot
	 */
	public Boolean getOwnerSnapshot() {
		return ownerSnapshot;
	}

	/**
	 * @param ownerSnapshot the ownerSnapshot to set
	 */
	public void setOwnerSnapshot(Boolean ownerSnapshot) {
		this.ownerSnapshot = ownerSnapshot;
	}

	/**
	 * @return the evaluatorSnapshot
	 */
	public Boolean getEvaluatorSnapshot() {
		return evaluatorSnapshot;
	}

	/**
	 * @param evaluatorSnapshot the evaluatorSnapshot to set
	 */
	public void setEvaluatorSnapshot(Boolean evaluatorSnapshot) {
		this.evaluatorSnapshot = evaluatorSnapshot;
	}

	/**
	 * @return the approverSnapshot
	 */
	public Boolean getApproverSnapshot() {
		return approverSnapshot;
	}

	/**
	 * @param approverSnapshot the approverSnapshot to set
	 */
	public void setApproverSnapshot(Boolean approverSnapshot) {
		this.approverSnapshot = approverSnapshot;
	}

	/**
	 * @return the evaluatorUser
	 */
	public SupplierPerformanceEvaluatorUser getEvaluatorUser() {
		return evaluatorUser;
	}

	/**
	 * @param evaluatorUser the evaluatorUser to set
	 */
	public void setEvaluatorUser(SupplierPerformanceEvaluatorUser evaluatorUser) {
		this.evaluatorUser = evaluatorUser;
	}

	@Override
	public int hashCode() {
		return Objects.hash(action, actionBy, actionDate, description, form, id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SupplierPerformanceAudit other = (SupplierPerformanceAudit) obj;
		return action == other.action && Objects.equals(actionBy, other.actionBy) && Objects.equals(actionDate, other.actionDate) && Objects.equals(description, other.description) && Objects.equals(form, other.form) && Objects.equals(id, other.id);
	}

	@Override
	public String toString() {
		return "SupplierPerformanceAudit [id=" + id + ", actionBy=" + actionBy + ", actionDate=" + actionDate + ", action=" + action + ", description=" + description + ", summarySnapshot=" + Arrays.toString(summarySnapshot) + ", form=" + form + "]";
	}

}
