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
import javax.validation.constraints.Size;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.validator.constraints.NotEmpty;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;
import com.privasia.procurehere.core.utils.Global;

/**
 * @author Javed Ahmed
 */
@Entity
@Table(name = "PROC_RFS_CATEGORY")
public class RfsCategory implements Serializable {
	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);
	private static final long serialVersionUID = -51337264073448794L;

	public interface RfsCategoryInt {
	}

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@NotEmpty(message = "{rfs.rfsCode.empty}", groups = RfsCategoryInt.class)
	@Size(min = 1, max = 15, message = "{rfs.rfsCategoryCode.length}", groups = RfsCategoryInt.class)
	@Column(name = "RFS_CODE", length = 15, nullable = false)
	private String rfsCode;

	@NotEmpty(message = "{rfs.rfsCode.empty}", groups = RfsCategoryInt.class)
	@Size(min = 1, max = 128, message = "{rfs.rfsCategoryName.length}", groups = RfsCategoryInt.class)
	@Column(name = "RFS_NAME", length = 128, nullable = false)
	private String rfsName;

	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS")
	private Status status;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "CREATED_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_RFS_CAT_CREATED_BY"))
	private User createdBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Column(name = "CREATED_DATE", nullable = true)
	private Date createdDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Column(name = "MODIFIED_DATE", nullable = true)
	private Date modifiedDate;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "MODIFIED_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_RFS_CAT_MODIFIED_BY"))
	private User modifiedBy;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "TENANT_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_RFS_CAT_BUYER_ID"))
	private Buyer buyer;

	public RfsCategory createShallowCopy() {
		RfsCategory ic = new RfsCategory();
		ic.setRfsName(getRfsName());
		ic.setRfsCode(getRfsCode());
		ic.setId(getId());
		if (getCreatedBy() != null) {
			User user = getCreatedBy();
			if (user.getBuyer() != null) {
				Buyer b = new Buyer();
				b.setId(user.getBuyer().getId());
				user.setBuyer(b);
			}
			ic.setCreatedBy(user);
		}

		if (getModifiedBy() != null) {
			User user = getModifiedBy();
			if (user.getBuyer() != null) {
				Buyer b = new Buyer();
				b.setId(user.getBuyer().getId());
				user.setBuyer(b);
			}
			ic.setModifiedBy(user);
		} else {
			ic.setModifiedBy(null);
		}
		ic.setCreatedDate(getCreatedDate());
		ic.setModifiedDate(getModifiedDate());

		return ic;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the rfsCode
	 */
	public String getRfsCode() {
		return rfsCode;
	}

	/**
	 * @param rfsCode the rfsCode to set
	 */
	public void setRfsCode(String rfsCode) {
		this.rfsCode = rfsCode;
	}

	/**
	 * @return the rfsName
	 */
	public String getRfsName() {
		return rfsName;
	}

	/**
	 * @param rfsName the rfsName to set
	 */
	public void setRfsName(String rfsName) {
		this.rfsName = rfsName;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((rfsCode == null) ? 0 : rfsCode.hashCode());
		result = prime * result + ((rfsName == null) ? 0 : rfsName.hashCode());
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
		RfsCategory other = (RfsCategory) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (rfsCode == null) {
			if (other.rfsCode != null)
				return false;
		} else if (!rfsCode.equals(other.rfsCode))
			return false;
		if (rfsName == null) {
			if (other.rfsName != null)
				return false;
		} else if (!rfsName.equals(other.rfsName))
			return false;
		if (status != other.status)
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return rfsCode + "-" + rfsName;
	}

	public String toLogString() {
		return "ProductCategory [id=" + id + ", productCode=" + rfsCode + ", productName=" + rfsName + ", status=" + status + ", createdDate=" + createdDate + ", modifiedDate=" + modifiedDate + "]";
	}
}
