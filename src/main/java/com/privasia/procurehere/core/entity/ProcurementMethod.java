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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;

/**
 * @author sana
 */
@Entity
@Table(name = "PROC_PROCUREMENT_METHOD")
public class ProcurementMethod implements Serializable {

	private static final long serialVersionUID = 3286398447195427122L;

	public interface ProcurementMethodInt{
	}

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@Pattern(regexp = "^[-_/\\w ]*$", message = "{procurement.code.format} ", groups = ProcurementMethod.ProcurementMethodInt.class)
	@Size(min = 0, max = 16, message = "{procurement.code.length}", groups = ProcurementMethod.ProcurementMethodInt.class)
	@Column(name = "PROCUREMENT_METHOD_CODE", length = 16)
	private String procurementMethodCode;

	@Pattern(regexp = "^[-_/\\w ]*$", message = "{procurement.method.format} ", groups = ProcurementMethod.ProcurementMethodInt.class)
	@NotEmpty(message = "{procurement.method.empty} ", groups = ProcurementMethod.ProcurementMethodInt.class)
	@Size(min = 1, max = 64, message = "{procurement.method.length}", groups =  ProcurementMethod.ProcurementMethodInt.class)
	@Column(name = "PROCUREMENT_METHOD", length = 64, nullable = false)
	private String procurementMethod;

	@Size(min = 0, max = 150, message = "{buyer.proc.method.desc.length}", groups =  ProcurementMethod.ProcurementMethodInt.class)
	@Column(name = "DESCRIPTION", length = 150)
	private String description;

	@Enumerated(EnumType.STRING)

	@Column(name = "STATUS")
	private Status status;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "BUYER_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_PM_BUYER_ID"))
	private Buyer buyer;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "CREATED_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_PM_CREATED_BY"))
	private User createdBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_DATE")
	private Date createdDate;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "MODIFIED_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_PM_MODIFIED_BY"))
	private User modifiedBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "MODIFIED_DATE")
	private Date modifiedDate;

	public ProcurementMethod() {

	}

	public ProcurementMethod(String id, String procurementMethodCode, String procurementMethod, String description) {
		this.id = id;
		this.procurementMethodCode = procurementMethodCode;
		this.procurementMethod = procurementMethod;
		this.description = description;
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
	 * @return the procurementCode
	 */
	public String getProcurementMethodCode(){ return procurementMethodCode; }

	/**
	 * @param procurementMethodCode the procurementMethodCode to set
	 */
	public void setProcurementMethodCode(String procurementMethodCode) {
		this.procurementMethodCode = procurementMethodCode;
	}

	/**
	 * @return the procurementMethod
	 */
	public String getProcurementMethod() {
		return procurementMethod;
	}

	/**
	 * @param procurementMethod the procurementMethod to set
	 */
	public void setProcurementMethod(String procurementMethod) {
		this.procurementMethod = procurementMethod;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result  = prime * result + ((procurementMethodCode == null) ? 0 : procurementMethodCode.hashCode());
		result = prime * result + ((procurementMethod == null) ? 0 : procurementMethod.hashCode());
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
		ProcurementMethod other = (ProcurementMethod) obj;
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
		if (procurementMethod == null) {
			if (other.procurementMethod != null)
				return false;
		} else if (!procurementMethod.equals(other.procurementMethod))
			return false;
		if (status != other.status)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ProcurementMethod [id=" + id + ", procurementMethod=" + procurementMethod + ", description=" + description + "]";
	}

}
