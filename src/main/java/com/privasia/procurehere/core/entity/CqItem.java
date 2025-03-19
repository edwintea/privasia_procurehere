/**
 * 
 */
package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.privasia.procurehere.core.enums.CqType;
import org.hibernate.annotations.Type;

/**
 * @author RT-Kapil
 * @author Ravi
 */
@MappedSuperclass
@JsonIgnoreProperties(ignoreUnknown = true)
public class CqItem implements Serializable {

	private static final long serialVersionUID = 8446708672025636557L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@Column(name = "ITEM_NAME", nullable = false, length = 250)
	private String itemName;

	@Column(name = "ITEM_DESCRIPTION", length = 1050)
	private String itemDescription;

	@Column(name = "ITEM_LEVEL", length = 2, nullable = false)
	private Integer level = 0;

	@Column(name = "SUB_ORDER", length = 2, nullable = false)
	private Integer order = 0;

	@Column(name = "CQ_TYPE", nullable = true)
	@Enumerated(EnumType.STRING)
	private CqType cqType;

	@Column(name = "IS_ATTACHMENT", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean attachment = Boolean.FALSE;

	@Column(name = "IS_OPTIONAL", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean optional = Boolean.FALSE;

	@Column(name = "IS_SUPPLIER_ATTACH_REQUIRED", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean isSupplierAttachRequired = Boolean.FALSE;

	@Transient
	private List<String> options;

	public CqItem() {
		this.attachment = Boolean.FALSE;
		this.isSupplierAttachRequired = Boolean.FALSE;
		this.optional = Boolean.FALSE;
	}

	public String getCqTypeName() {
		return this.cqType != null ? this.cqType.getValue() : "";
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
	 * @return the itemName
	 */
	public String getItemName() {
		return itemName;
	}

	/**
	 * @param itemName the itemName to set
	 */
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	/**
	 * @return the itemDescription
	 */
	public String getItemDescription() {
		return itemDescription;
	}

	/**
	 * @param itemDescription the itemDescription to set
	 */
	public void setItemDescription(String itemDescription) {
		this.itemDescription = itemDescription;
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
	 * @return the cqType
	 */
	public CqType getCqType() {
		return cqType;
	}

	/**
	 * @param cqType the cqType to set
	 */
	public void setCqType(CqType cqType) {
		this.cqType = cqType;
	}

	/**
	 * @return the attachment
	 */
	public Boolean getAttachment() {
		return attachment;
	}

	/**
	 * @param attachment the attachment to set
	 */
	public void setAttachment(Boolean attachment) {
		this.attachment = attachment;
	}

	/**
	 * @return the optional
	 */
	public Boolean getOptional() {
		return optional;
	}

	/**
	 * @param optional the optional to set
	 */
	public void setOptional(Boolean optional) {
		this.optional = optional;
	}

	/**
	 * @return the options
	 */
	public List<String> getOptions() {
		if (options == null) {
			options = new ArrayList<String>();
		}
		return options;
	}

	public Boolean getIsSupplierAttachRequired() {
		return isSupplierAttachRequired;
	}

	public void setIsSupplierAttachRequired(Boolean isSupplierAttachRequired) {
		this.isSupplierAttachRequired = isSupplierAttachRequired;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attachment == null) ? 0 : attachment.hashCode());
		result = prime * result + ((cqType == null) ? 0 : cqType.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((itemDescription == null) ? 0 : itemDescription.hashCode());
		result = prime * result + ((itemName == null) ? 0 : itemName.hashCode());
		result = prime * result + ((level == null) ? 0 : level.hashCode());
		result = prime * result + ((optional == null) ? 0 : optional.hashCode());
		result = prime * result + ((order == null) ? 0 : order.hashCode());
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
		CqItem other = (CqItem) obj;
		if (attachment == null) {
			if (other.attachment != null)
				return false;
		} else if (!attachment.equals(other.attachment))
			return false;
		if (cqType != other.cqType)
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (itemDescription == null) {
			if (other.itemDescription != null)
				return false;
		} else if (!itemDescription.equals(other.itemDescription))
			return false;
		if (itemName == null) {
			if (other.itemName != null)
				return false;
		} else if (!itemName.equals(other.itemName))
			return false;
		if (level == null) {
			if (other.level != null)
				return false;
		} else if (!level.equals(other.level))
			return false;
		if (optional == null) {
			if (other.optional != null)
				return false;
		} else if (!optional.equals(other.optional))
			return false;
		if (order == null) {
			if (other.order != null)
				return false;
		} else if (!order.equals(other.order))
			return false;
		return true;
	}

	public String toLogString() {
		return "CqItem [id=" + id + ", itemName=" + itemName + ", itemDescription=" + itemDescription + ", level=" + level + ", order=" + order + "]";
	}

}
