package com.privasia.procurehere.core.entity;

import java.io.Serializable;
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
import com.privasia.procurehere.core.enums.AuditActionType;
import org.hibernate.annotations.Type;

/**
 * @author Aishwarya
 */
@Entity
@Table(name = "PROC_CONTRACT_AUDIT")
public class ContractAudit implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4756402761670131280L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@ManyToOne(fetch = FetchType.EAGER, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "ACTION_BY", foreignKey = @ForeignKey(name = "FK_CONTRACT_ACTION_BY"), nullable = true)
	private User actionBy;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "TENANT_ID", foreignKey = @ForeignKey(name = "FK_CONTRACT_BUYER_ID"), nullable = true)
	private Buyer buyer;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "CONTRACT_ID", foreignKey = @ForeignKey(name = "FK_CONT_AUDIT_CONT_ID"))
	private ProductContract productContract;

	@Column(name = "ACTION_DATE", nullable = false)
	private Date actionDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "ACTION_TYPE", nullable = false)
	private AuditActionType action;

	@Column(name = "THE_DESCRIPTION", length = 3000)
	private String description;

	@Lob
	@Type(type = "org.hibernate.type.BinaryType")
	@Column(name = "SUMMARY_SNAPSHOT")
	private byte[] summarySnapshot;

	@Column(name = "HAS_SNAPSHOT", nullable = false)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean hasSnapshot = Boolean.FALSE;

	public ContractAudit() {
	}

	public ContractAudit(Date actionDate, User actionBy, AuditActionType action, String description) {
		this.actionDate = actionDate;
		this.actionBy = actionBy;
		this.action = action;
		this.description = description;
	}

	public ContractAudit(String id, String actionBy, Date actionDate, AuditActionType action, String description, Boolean hasSnapshot) {
		this.id = id;
		User user = new User();
		user.setName(actionBy);
		this.actionBy = user;
		this.actionDate = actionDate;
		this.action = action;
		this.description = description;
		this.hasSnapshot = hasSnapshot;
	}

	public ContractAudit(Buyer buyer, ProductContract persistObj, User loginUser, Date date, AuditActionType type, String message, byte[] summarySnapshot) {
		this.buyer = buyer;
		this.productContract = persistObj;
		this.actionBy = loginUser;
		this.actionDate = date;
		this.action = type;
		this.description = message;
		this.summarySnapshot = summarySnapshot;
		if (summarySnapshot != null) {
			this.hasSnapshot = Boolean.TRUE;
		}
	}

	public ContractAudit(ProductContract contract, User actionBy, Date actionDate, AuditActionType type, String message) {
		this.productContract = contract;
		this.actionBy = actionBy;
		this.actionDate = actionDate;
		this.action = type;
		this.description = message;
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
	public AuditActionType getAction() {
		return action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(AuditActionType action) {
		this.action = action;
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
	 * @return the productContract
	 */
	public ProductContract getProductContract() {
		return productContract;
	}

	/**
	 * @param productContract the productContract to set
	 */
	public void setProductContract(ProductContract productContract) {
		this.productContract = productContract;
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
		ContractAudit other = (ContractAudit) obj;
		return Objects.equals(id, other.id);
	}

	/**
	 * @return
	 */
	public String toLogString() {
		return "EventAudit [id=" + id + ", actionDate=" + actionDate + ",action=" + action + ",description=" + description + " ]";
	}

}