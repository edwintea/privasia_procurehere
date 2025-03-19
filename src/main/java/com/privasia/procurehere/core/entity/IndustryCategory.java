package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.pojo.IndustryCategoryPojo;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;

/**
 * @author RT-Kapil
 * @author Ravi
 */

@Entity
@Table(name = "PROC_INDUSTRY_CATEGORY")
@SqlResultSetMapping(name = "industryCat", classes = { @ConstructorResult(targetClass = IndustryCategoryPojo.class, columns = { @ColumnResult(name = "ID"), @ColumnResult(name = "CATEGORY_NAME"), @ColumnResult(name = "CATEGORY_CODE") }) })
public class IndustryCategory implements Serializable {

	private static final long serialVersionUID = -3786056514384246073L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	// @NotEmpty(message = "{buyerIndCat.code.empty}")
	@Size(min = 1, max = 64, message = "{buyerIndCat.code.length}")
	@Column(name = "CATEGORY_CODE", length = 64, nullable = false)
	private String code;

	@Size(min = 1, max = 128, message = "{buyerIndCat.name.length}")
	@Column(name = "CATEGORY_NAME", length = 64, nullable = false)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS")
	private Status status;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TANENT_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_BUYER_INDCAT_BUYER"))
	private Buyer buyer;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "CREATED_BY", nullable = false, foreignKey = @ForeignKey(name = "FK_BUYER_INDCAT_CREATED_BY"))
	private User createdBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_DATE", nullable = false)
	private Date createdDate;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "MODIFIED_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_BUYER_INDCAT_MODIFIED_BY"))
	private User modifiedBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "MODIFIED_DATE")
	private Date modifiedDate;

	public IndustryCategory() {
	}

	/**
	 * @param naicsCode
	 */
	public IndustryCategory(NaicsCodes naicsCode) {
		this.code = String.valueOf(naicsCode.getCategoryCode());
		this.name = naicsCode.getCategoryName();
		this.status = naicsCode.getStatus();
	}

	public IndustryCategory(String id, String code, String name) {
		this.code = code;
		this.name = name;
		this.id = id;
	}

	public IndustryCategory createShallowCopy() {
		IndustryCategory ic = new IndustryCategory();
		ic.setId(getId());
		ic.setCode(getCode());
		ic.setName(getName());
		ic.setStatus(getStatus());
		ic.setCreatedDate(getCreatedDate());
		return ic;
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
	 * @return the code
	 */
	public String getCode() {
		return code;
	}

	/**
	 * @param code the code to set
	 */
	public void setCode(String code) {
		this.code = code;
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

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
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
		IndustryCategory other = (IndustryCategory) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
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
		return "IndustryCategory [id=" + id + ", code=" + code + ", name=" + name + ", status=" + status + "]";
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return code + " - " + name;
	}

}
