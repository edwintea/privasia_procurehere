/**
 * 
 */
package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.privasia.procurehere.core.utils.CollectionUtil;

/**
 * @author Ravi
 */
@Entity
@Table(name = "PROC_RFI_EVENT_BQ_SUP_ITEM")
public class RfiSupplierBqItem extends BqItem implements Serializable {

	private static final long serialVersionUID = -4744735142279683103L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_RFI_EVENT_BQ_SUP_ITEM"))
	private RfpEvent rfxEvent;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "SUPPLIER_ID", foreignKey = @ForeignKey(name = "FK_RFI_EVENT_BQ_SUP_ID"))
	private Supplier supplier;

	@ManyToOne(optional = true, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "PARENT_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_RFI_EVNT_SUP_BQ_PARENT"))
	private RfiSupplierBqItem parent;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
	@OrderBy("level, order")
	private List<RfiSupplierBqItem> children;

	@ManyToOne(optional = false, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "BQ_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_RFI_SUP_BQI_EVT_BQ"))
	private RfpEventBq bq;

	public RfiSupplierBqItem() {
	}

	public RfiSupplierBqItem(RfpBqItem item, Supplier supplier) {
		// Children, Parent, id - Dont do
		this.setBq(item.getBq());
		this.rfxEvent = item.getRfxEvent();
		this.setSupplier(supplier);
		this.setTax(item.getTax());
		this.setTotalAmount(item.getTotalAmount());
		this.setField1(item.getField1());
		this.setField2(item.getField2());
		this.setField3(item.getField3());
		this.setField4(item.getField4());
		this.setItemDescription(item.getItemDescription());
		this.setItemName(item.getItemName());
		this.setLevel(item.getLevel());
		this.setOrder(item.getOrder());
		this.setPriceType(item.getPriceType());
		this.setUnitPrice(item.getUnitPrice());
		this.setUnitPriceType(item.getUnitPriceType());
		this.setQuantity(item.getQuantity());
		if (item.getUom() != null) {
			this.setUom(item.getUom());
		}

		if (CollectionUtil.isNotEmpty(item.getChildren())) {
			this.children = new ArrayList<RfiSupplierBqItem>();
			for (RfpBqItem child : item.getChildren()) {
				RfiSupplierBqItem childBqItem = new RfiSupplierBqItem(child, supplier);
				childBqItem.setParent(this);
				this.children.add(childBqItem);
			}
		}
	}

	/**
	 * @return the rfxEvent
	 */
	public RfpEvent getRfxEvent() {
		return rfxEvent;
	}

	/**
	 * @param rfxEvent the rfxEvent to set
	 */
	public void setRfxEvent(RfpEvent rfxEvent) {
		this.rfxEvent = rfxEvent;
	}

	/**
	 * @return the supplier
	 */
	public Supplier getSupplier() {
		return supplier;
	}

	/**
	 * @param supplier the supplier to set
	 */
	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	/**
	 * @return the parent
	 */
	public RfiSupplierBqItem getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(RfiSupplierBqItem parent) {
		this.parent = parent;
	}

	/**
	 * @return the children
	 */
	public List<RfiSupplierBqItem> getChildren() {
		return children;
	}

	/**
	 * @param children the children to set
	 */
	public void setChildren(List<RfiSupplierBqItem> children) {
		this.children = children;
	}

	/**
	 * @return the bq
	 */
	public RfpEventBq getBq() {
		return bq;
	}

	/**
	 * @param bq the bq to set
	 */
	public void setBq(RfpEventBq bq) {
		this.bq = bq;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {

		return super.equals(obj);
	}

	public String toLogString() {
		return "RftBqItem [ " + super.toLogString() + "]";
	}

	public RfiSupplierBqItem createShallowCopy() {
		RfiSupplierBqItem ic = new RfiSupplierBqItem();
		ic.setItemDescription(getItemDescription());
		ic.setUom(getUom());
		ic.setQuantity(getQuantity());
		ic.setItemName(getItemName());
		ic.setUnitPrice(getUnitPrice());
		ic.setUnitPriceType(getUnitPriceType());
		ic.setLevel(getLevel());
		ic.setOrder(getOrder());
		ic.setPriceType(getPriceType());
		ic.setId(getId());
		ic.setField1(getField1());
		ic.setField2(getField2());
		ic.setField3(getField3());
		ic.setField4(getField4());
		ic.setBq(getBq());

		return ic;
	}
}
