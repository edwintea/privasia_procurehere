package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.Date;

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
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.privasia.procurehere.core.enums.RequestAuditType;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author sarang
 */
@Entity
@Table(name = "PROC_REQ_AUDIT")
public class RequestAudit implements Serializable {

	private static final long serialVersionUID = 4456686217427413920L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "ACTION_BY", nullable = true)
	private User actionBy;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "TENANT_ID", nullable = true)
	private Buyer buyer;

	@Column(name = "ACTION_DATE", nullable = false)
	private Date actionDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "ACTION_TYPE", nullable = false)
	private RequestAuditType action;

	@Column(name = "DESCRIPTION", length = 3000)
	private String description;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "REQ_ID", foreignKey = @ForeignKey(name = "FK_REQ_AUDIT"))
	private SourcingFormRequest req;

	public RequestAudit() {
	}

	public RequestAudit(String id, User actionBy, Date actionDate, RequestAuditType action, String description, SourcingFormRequest req) {
		super();
		this.id = id;
		this.actionBy = actionBy;
		this.actionDate = actionDate;
		this.action = action;
		this.description = description;
		this.req = req;
	}

	/**
	 * @param id
	 * @param actionBy
	 * @param buyer
	 * @param actionDate
	 * @param action
	 * @param description
	 */
	public RequestAudit(String id, String actionBy, Date actionDate, RequestAuditType action, String description) {
		this.id = id;
		User user = new User();
		user.setName(actionBy);
		this.actionBy = user;
		this.actionDate = actionDate;
		this.action = action;
		this.description = description;

	}

	/**
	 * @param actionBy
	 * @param actionDate
	 * @param action
	 * @param description
	 * @param req
	 */
	public RequestAudit(User actionBy, Date actionDate, RequestAuditType action, String description, SourcingFormRequest req) {
		super();
		this.actionBy = actionBy;
		this.actionDate = actionDate;
		this.action = action;
		this.description = description;
		this.req = req;
	}

	public RequestAudit(User actionBy, Date actionDate, RequestAuditType action, String description, SourcingFormRequest req, String tenantId) {
		super();
		this.actionBy = actionBy;
		this.actionDate = actionDate;
		this.action = action;
		this.description = description;
		this.req = req;
		if (StringUtils.checkString(tenantId).length() > 0) {
			Buyer obj = new Buyer();
			obj.setId(tenantId);
			this.buyer = obj;
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
	public RequestAuditType getAction() {
		return action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(RequestAuditType action) {
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
	 * @return the req
	 */
	public SourcingFormRequest getReq() {
		return req;
	}

	/**
	 * @param req the req to set
	 */
	public void setReq(SourcingFormRequest req) {
		this.req = req;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((action == null) ? 0 : action.hashCode());
		result = prime * result + ((actionDate == null) ? 0 : actionDate.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		RequestAudit other = (RequestAudit) obj;
		if (action != other.action)
			return false;
		if (actionDate == null) {
			if (other.actionDate != null)
				return false;
		} else if (!actionDate.equals(other.actionDate))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public String toLogString() {
		return "PrAudit [id=" + id + ", actionDate=" + actionDate + ", action=" + action + ", description=" + description + "]";
	}
}