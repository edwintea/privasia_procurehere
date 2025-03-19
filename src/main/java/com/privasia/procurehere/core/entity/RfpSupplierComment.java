package com.privasia.procurehere.core.entity;

/**
 * @author VIPUL
 */

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "PROC_RFP_SUPPLIER_COMMENTS")
public class RfpSupplierComment extends Comments implements Serializable {

	private static final long serialVersionUID = 4545635016600630844L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", nullable = false, length = 64)
	private String id;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinColumn(name = "BQ_ITEM_ID", foreignKey = @ForeignKey(name = "FK_RFP_SUPPLIER_ITEM_ID") )
	private RfpSupplierBqItem supplierBqItem;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinColumn(name = "SOR_ITEM_ID", foreignKey = @ForeignKey(name = "FK_RFP_SOR_SUPPLIER_ITEM_ID") )
	private RfpSupplierSorItem supplierSorItem;

	@Transient
	private String bqItemId;

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
	 * @return the supplierBqItem
	 */
	public RfpSupplierBqItem getSupplierBqItem() {
		return supplierBqItem;
	}

	/**
	 * @param supplierBqItem the supplierBqItem to set
	 */
	public void setSupplierBqItem(RfpSupplierBqItem supplierBqItem) {
		this.supplierBqItem = supplierBqItem;
	}

	/**
	 * @return the bqItemId
	 */
	public String getBqItemId() {
		return bqItemId;
	}

	/**
	 * @param bqItemId the bqItemId to set
	 */
	public void setBqItemId(String bqItemId) {
		this.bqItemId = bqItemId;
	}


	public RfpSupplierSorItem getSupplierSorItem() {
		return supplierSorItem;
	}

	public void setSupplierSorItem(RfpSupplierSorItem supplierSorItem) {
		this.supplierSorItem = supplierSorItem;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
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
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		RfpSupplierComment other = (RfpSupplierComment) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	public RfpSupplierComment createShallowCopy() {
		RfpSupplierComment ic = new RfpSupplierComment();
		ic.setId(getId());
		ic.setComment(getComment());
		ic.setUserName(getUserName());
		ic.setCreatedDate(getCreatedDate());
		return ic;
	}

}
