/**
 * 
 */
package com.privasia.procurehere.core.entity;

import java.io.Serializable;
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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;

/**
 * @author Arc
 */
@Entity
@Table(name = "PROC_NAICS_CODES", indexes = { @Index(columnList = "CATEGORY_NAME", name = "INDEX_IND_CAT_NAME"), @Index(columnList = "CATEGORY_CODE", name = "INDEX_IND_CAT_CODE") })
public class NaicsCodes implements Serializable {

	private static final long serialVersionUID = -1347339181218602745L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", nullable = false, length = 80)
	private String id;

	@NotNull(message = "{industry.category.code.empty}")
	@Column(name = "CATEGORY_CODE", length = 2, nullable = false)
	private Integer categoryCode;

	@NotEmpty(message = "{industry.category.name.empty}")
	@Size(min = 1, max = 128, message = "{industry.category.name.length}")
	@Column(name = "CATEGORY_NAME", nullable = false, length = 250)
	private String categoryName;

	@JsonIgnore
	@ManyToOne(optional = true, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "PARENT_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_IND_CAT_PARENT"))
	private NaicsCodes parent;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
	@OrderBy("categoryCode")
	private List<NaicsCodes> children;

	@Column(name = "CATEGORY_LEVEL", length = 2, nullable = false)
	private Integer level = 1;

	@Column(name = "SUB_ORDER", length = 2, nullable = false)
	private Integer order = 1;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "CREATED_BY", nullable = false, foreignKey = @ForeignKey(name = "FK_IND_CAT_CREATED_BY"))
	private User createdBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED_DATE", nullable = false)
	private Date createdDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MODIFIED_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_IND_CAT_MODIFIED_BY"))
	private User modifiedBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "MODIFIED_DATE", nullable = true)
	private Date modifiedDate;

	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS")
	private Status status = Status.ACTIVE;

	@Transient
	@Type(type = "org.hibernate.type.NumericBooleanType")
private boolean checked = false;

	public NaicsCodes() {
		this.status = Status.ACTIVE;
	}

	public NaicsCodes(String id, Integer categoryCode, String categoryName) {
		this.id = id;
		this.categoryCode = categoryCode;
		this.categoryName = categoryName;
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
	 * @return the categoryName
	 */
	public String getCategoryName() {
		return categoryName;
	}

	/**
	 * @param categoryName the categoryName to set
	 */
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	/**
	 * @return the parent
	 */
	public NaicsCodes getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(NaicsCodes parent) {
		this.parent = parent;
	}

	/**
	 * @return the children
	 */
	public List<NaicsCodes> getChildren() {
		try {
			if (CollectionUtil.isNotEmpty(children)) {
				for (NaicsCodes ic : children) {
					ic.getCategoryCode();
				}
			}
			return children;
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * @param children the children to set
	 */
	public void setChildren(List<NaicsCodes> children) {
		this.children = children;
	}

	/**
	 * @return the order
	 */
	public Integer getOrder() {
		return order;
	}

	/**
	 * @param order the order to set
	 */
	public void setOrder(Integer order) {
		this.order = order;
	}

	/**
	 * @return the level
	 */
	public Integer getLevel() {
		return level;
	}

	/**
	 * @param level the level to set
	 */
	public void setLevel(Integer level) {
		this.level = level;
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
	 * @return the categoryCode
	 */
	public Integer getCategoryCode() {
		return categoryCode;
	}

	/**
	 * @param categoryCode the categoryCode to set
	 */
	public void setCategoryCode(Integer categoryCode) {
		this.categoryCode = categoryCode;
	}

	/**
	 * @return the checked
	 */
	public boolean isChecked() {
		return checked;
	}

	/**
	 * @param checked the checked to set
	 */
	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((categoryCode == null) ? 0 : categoryCode.hashCode());
		result = prime * result + ((categoryName == null) ? 0 : categoryName.hashCode());
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
		NaicsCodes other = (NaicsCodes) obj;
		if (categoryCode == null) {
			if (other.categoryCode != null)
				return false;
		} else if (!categoryCode.equals(other.categoryCode))
			return false;
		if (categoryName == null) {
			if (other.categoryName != null)
				return false;
		} else if (!categoryName.equals(other.categoryName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return categoryCode + " - " + categoryName;
	}

	public String toLogString() {
		return "NaicsCodes [id=" + id + ", categoryCode=" + categoryCode + ", categoryName=" + categoryName + ", children=" + children + ", level=" + level + ", order=" + order + ", createdDate=" + createdDate + ", modifiedDate=" + modifiedDate + ", status=" + status + "]";
	}

	public NaicsCodes createShallowCopy() {
		NaicsCodes ic = new NaicsCodes();
		ic.setCategoryCode(categoryCode);
		ic.setCategoryName(categoryName);
		ic.setId(id);
		ic.setLevel(level);
		ic.setOrder(order);
		ic.setStatus(status);
		ic.setChildren(null);
		return ic;
	}
}
