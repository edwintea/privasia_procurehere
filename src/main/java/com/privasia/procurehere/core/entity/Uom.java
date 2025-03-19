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
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;

@Entity
@Table(name = "PROC_BUYER_UOM", indexes = { @Index(columnList = "BUYER_ID", name = "INDEX_UOM_BUYER_ID"), @Index(columnList = "OWNER_ID", name = "INDEX_UOM_OWNER_ID") })
public class Uom implements Serializable {

	private static final long serialVersionUID = 2600995389983721819L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", nullable = false, length = 64)
	private String id;

	@NotEmpty(message = "{uom.empty}")
	@Size(min = 1, max = 64, message = "{uom.length}")
	@Column(name = "UOM", nullable = false, length = 64)
	private String uom;

	@NotEmpty(message = "{uom.description.empty}")
	@Size(min = 1, max = 400, message = "{uom.description.length}")
	@Column(name = "UOM_DESCRIPTION", length = 400)
	private String uomDescription;

	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS")
	private Status status;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "CREATED_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_UOM_CREATED_BY"))
	private User createdBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_DATE")
	private Date createdDate;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "MODIFIED_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_UOM_MODIFIED_BY"))
	private User modifiedBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "MODIFIED_DATE")
	private Date modifiedDate;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "BUYER_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_UOM_BUYER_ID"))
	private Buyer buyer;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "OWNER_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_UOM_OWNER_ID"))
	private Owner owner;

	public Uom() {
	}

	public Uom(String id, String uom) {
		this.id = id;
		this.uom = uom;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUom() {
		return uom;
	}

	public void setUom(String uom) {
		this.uom = uom;
	}

	public String getUomDescription() {
		return uomDescription;
	}

	public void setUomDescription(String uomDescription) {
		this.uomDescription = uomDescription;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public User getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(User modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
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
	 * @return the owner
	 */
	public Owner getOwner() {
		return owner;
	}

	/**
	 * @param owner the owner to set
	 */
	public void setOwner(Owner owner) {
		this.owner = owner;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uom == null) ? 0 : uom.hashCode());
		result = prime * result + ((uomDescription == null) ? 0 : uomDescription.hashCode());
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
		Uom other = (Uom) obj;
		if (uom == null) {
			if (other.uom != null)
				return false;
		} else if (!uom.equals(other.uom))
			return false;
		if (uomDescription == null) {
			if (other.uomDescription != null)
				return false;
		} else if (!uomDescription.equals(other.uomDescription))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Uom [id=" + id + ", uom=" + uom + ", uomDescription=" + uomDescription + "]";
	}

	public Uom createShallowCopy() {
		Uom ic = new Uom();
		// ic.setId(getId());
		ic.setUom(getUom());
		ic.setUomDescription(getUomDescription());
		ic.setStatus(getStatus());
		return ic;
	}

	public Uom createShallow() {
		Uom ic = new Uom();
		ic.setId(getId());
		ic.setUom(getUom());
		ic.setUomDescription(getUomDescription());
		ic.setStatus(getStatus());
		return ic;
	}

	public Uom createMobileShallowCopy() {
		Uom ic = new Uom();
		ic.setUom(getUom());
		return ic;
	}
}
