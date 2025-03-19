/**
 * 
 */
package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.privasia.procurehere.core.enums.TaxType;
import com.privasia.procurehere.core.utils.CollectionUtil;

/**
 * @author Ravi
 */
@Entity
@Table(name = "PROC_RFQ_EVENT_BQ_SUP_ITEM")
public class RfqSupplierBqItem extends BqItem implements Serializable {

	private static final long serialVersionUID = -5313960510390409681L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_RFQ_EVENT_BQ_SUP_ITEM"))
	private RfqEvent event;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "SUPPLIER_ID", foreignKey = @ForeignKey(name = "FK_RFQ_EVENT_BQ_SUP_ID"))
	private Supplier supplier;

	@ManyToOne(optional = true, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "PARENT_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_RFQ_EVNT_SUP_BQ_PARENT"))
	private RfqSupplierBqItem parent;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
	@OrderBy("level, order")
	private List<RfqSupplierBqItem> children;

	@ManyToOne(optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "BQ_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_RFQ_SUP_BQI_EVT_BQ"))
	private RfqEventBq bq;

	@ManyToOne(optional = false, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "SUPP_BQ_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_RFQ_EV_SUP_BQ"))
	private RfqSupplierBq supplierBq;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "supplierBqItem", cascade = CascadeType.ALL)
	private List<RfqSupplierComment> comments;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "BQ_ITEM_ID", foreignKey = @ForeignKey(name = "FK_SUP_RFQ_EVENT_BQ_ITEM_ID"))
	private RfqBqItem bqItem;

	public RfqSupplierBqItem() {
	}

	public RfqSupplierBqItem(RfqBqItem item, Supplier supplier, RfqSupplierBq supplierBq) {
		// Children, Parent, id - Dont do
		this.setBqItem(item);
		this.setSupplierBq(supplierBq);
		this.setBq(item.getBq());
		this.event = item.getRfxEvent();
		this.setSupplier(supplier);
		this.setField1(item.getField1());
		this.setField2(item.getField2());
		this.setField3(item.getField3());
		this.setField4(item.getField4());

		this.setField5(item.getField5());
		this.setField6(item.getField6());
		this.setField7(item.getField7());
		this.setField8(item.getField8());
		this.setField9(item.getField9());
		this.setField10(item.getField10());

		this.setItemDescription(item.getItemDescription());
		this.setItemName(item.getItemName());
		this.setLevel(item.getLevel());
		this.setOrder(item.getOrder());
		this.setPriceType(item.getPriceType());
		this.setUnitPrice(item.getUnitPrice());
		this.setQuantity(item.getQuantity());
		this.setUom(item.getUom());
		BigDecimal qty = item.getQuantity();
		if (qty == null) {
			qty = BigDecimal.ZERO;
		}

		BigDecimal totalAmount = item.getUnitPrice() != null ? item.getUnitPrice().multiply(item.getQuantity()) : BigDecimal.ZERO;
		this.setTotalAmount(totalAmount);
		if (CollectionUtil.isNotEmpty(item.getChildren())) {
			this.children = new ArrayList<RfqSupplierBqItem>();
			for (RfqBqItem child : item.getChildren()) {
				RfqSupplierBqItem childBqItem = new RfqSupplierBqItem(child, supplier, supplierBq);
				childBqItem.setParent(this);
				this.children.add(childBqItem);
			}
		}
	}

	public RfqSupplierBqItem createSearchShallowCopy() {
		RfqSupplierBqItem ic = new RfqSupplierBqItem();
		ic.setItemDescription(getItemDescription());
		if (getUom() != null) {
			ic.setUom(getUom().createShallowCopy());
		}
		ic.setQuantity(getQuantity());
		ic.setItemName(getItemName());
		ic.setUnitPrice(getUnitPrice());
		ic.setUnitPriceType(getUnitPriceType());
		ic.setLevel(getLevel());
		ic.setOrder(getOrder());
		ic.setPriceType(getPriceType());
		ic.setTotalAmount(getTotalAmount());
		ic.setTax(getTax());
		ic.setTaxType(getTaxType());
		ic.setTotalAmountWithTax(getTotalAmountWithTax());
		ic.setId(getId());
		ic.setField1(getField1());
		ic.setField2(getField2());
		ic.setField3(getField3());
		ic.setField4(getField4());

		ic.setField5(getField5());
		ic.setField6(getField6());
		ic.setField7(getField7());
		ic.setField8(getField8());
		ic.setField9(getField9());
		ic.setField10(getField10());

		ic.setBqItem(getBqItem());
		ic.setSupplierBq(getSupplierBq());
		if (getParent() != null) {
			RfqSupplierBqItem parent = new RfqSupplierBqItem();
			parent.setId(getParent().getId());
			ic.setParent(parent);
		}

		return ic;
	}

	/**
	 * @return
	 */
	public List<TaxType> getTaxTypeList() {
		return Arrays.asList(TaxType.values());
	}

	/**
	 * @return the event
	 */
	public RfqEvent getEvent() {
		return event;
	}

	/**
	 * @param event the event to set
	 */
	public void setEvent(RfqEvent event) {
		this.event = event;
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
	public RfqSupplierBqItem getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(RfqSupplierBqItem parent) {
		this.parent = parent;
	}

	/**
	 * @return the children
	 */
	public List<RfqSupplierBqItem> getChildren() {
		return children;
	}

	/**
	 * @param children the children to set
	 */
	public void setChildren(List<RfqSupplierBqItem> children) {
		this.children = children;
	}

	/**
	 * @return the bq
	 */
	public RfqEventBq getBq() {
		return bq;
	}

	/**
	 * @param bq the bq to set
	 */
	public void setBq(RfqEventBq bq) {
		this.bq = bq;
	}

	/**
	 * @return the supplierBq
	 */
	public RfqSupplierBq getSupplierBq() {
		return supplierBq;
	}

	/**
	 * @param supplierBq the supplierBq to set
	 */
	public void setSupplierBq(RfqSupplierBq supplierBq) {
		this.supplierBq = supplierBq;
	}

	/**
	 * @return the comments
	 */
	public List<RfqSupplierComment> getComments() {
		return comments;
	}

	/**
	 * @param comments the comments to set
	 */
	public void setComments(List<RfqSupplierComment> comments) {
		this.comments = comments;
	}

	/**
	 * @return the bqItem
	 */
	public RfqBqItem getBqItem() {
		return bqItem;
	}

	/**
	 * @param bqItem the bqItem to set
	 */
	public void setBqItem(RfqBqItem bqItem) {
		this.bqItem = bqItem;
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
		return "RfqSupplierBqItem [ " + super.toLogString() + "]";
	}

	public RfqSupplierBqItem createShallowCopy() {
		RfqSupplierBqItem ic = new RfqSupplierBqItem();
		ic.setItemDescription(getItemDescription());
		if (getUom() != null) {
			ic.setUom(getUom().createShallowCopy());
		}
		ic.setQuantity(getQuantity());
		ic.setItemName(getItemName());
		ic.setUnitPriceType(getUnitPriceType());
		ic.setUnitPrice(getUnitPrice());
		ic.setLevel(getLevel());
		ic.setOrder(getOrder());
		ic.setPriceType(getPriceType());
		ic.setTotalAmount(getTotalAmount());
		ic.setTax(getTax());
		ic.setTaxType(getTaxType());
		ic.setTotalAmountWithTax(getTotalAmountWithTax());
		ic.setId(getId());
		ic.setField1(getField1());
		ic.setField2(getField2());
		ic.setField3(getField3());
		ic.setField4(getField4());

		ic.setField5(getField5());
		ic.setField6(getField6());
		ic.setField7(getField7());
		ic.setField8(getField8());
		ic.setField9(getField9());
		ic.setField10(getField10());

		ic.setBqItem(getBqItem());
		ic.setBq(getBq());
		ic.setSupplierBq(getSupplierBq());
		ic.setEvent(getEvent());

		return ic;
	}

	public RfaSupplierBqItem copyForRfa() {
		RfaSupplierBqItem supBqItem = new RfaSupplierBqItem();
		supBqItem.setItemDescription(getItemDescription());
		if (getUom() != null) {
			supBqItem.setUom(getUom());
		}
		supBqItem.setQuantity(getQuantity());
		supBqItem.setItemName(getItemName());
		supBqItem.setUnitPrice(getUnitPrice());// getUnitPrice());
		supBqItem.setLevel(getLevel());
		supBqItem.setOrder(getOrder());
		supBqItem.setPriceType(getPriceType());
		supBqItem.setTotalAmount(getTotalAmount());
		supBqItem.setTax(getTax());
		supBqItem.setTaxType(getTaxType());
		supBqItem.setTotalAmountWithTax(getTotalAmountWithTax());
		supBqItem.setField1(getField1());
		supBqItem.setField2(getField2());
		supBqItem.setField3(getField3());
		supBqItem.setField4(getField4());
		supBqItem.setField5(getField5());
		supBqItem.setField6(getField6());
		supBqItem.setField7(getField7());
		supBqItem.setField8(getField8());
		supBqItem.setField9(getField9());
		supBqItem.setField10(getField10());

		return supBqItem;
	}

	public RfpSupplierBqItem copyForRfp() {
		RfpSupplierBqItem supBqItem = new RfpSupplierBqItem();
		supBqItem.setItemDescription(getItemDescription());
		if (getUom() != null) {
			supBqItem.setUom(getUom());
		}
		supBqItem.setQuantity(getQuantity());
		supBqItem.setItemName(getItemName());
		supBqItem.setUnitPrice(getUnitPrice());// getUnitPrice());
		supBqItem.setLevel(getLevel());
		supBqItem.setOrder(getOrder());
		supBqItem.setPriceType(getPriceType());
		supBqItem.setTotalAmount(getTotalAmount());
		supBqItem.setTax(getTax());
		supBqItem.setTaxType(getTaxType());
		supBqItem.setTotalAmountWithTax(getTotalAmountWithTax());
		supBqItem.setField1(getField1());
		supBqItem.setField2(getField2());
		supBqItem.setField3(getField3());
		supBqItem.setField4(getField4());
		supBqItem.setField5(getField5());
		supBqItem.setField6(getField6());
		supBqItem.setField7(getField7());
		supBqItem.setField8(getField8());
		supBqItem.setField9(getField9());
		supBqItem.setField10(getField10());

		return supBqItem;
	}

	public RfqSupplierBqItem copyForRfq() {
		RfqSupplierBqItem supBqItem = new RfqSupplierBqItem();
		supBqItem.setItemDescription(getItemDescription());
		if (getUom() != null) {
			supBqItem.setUom(getUom());
		}
		supBqItem.setQuantity(getQuantity());
		supBqItem.setItemName(getItemName());
		supBqItem.setUnitPrice(getUnitPrice());// getUnitPrice());
		supBqItem.setLevel(getLevel());
		supBqItem.setOrder(getOrder());
		supBqItem.setPriceType(getPriceType());
		supBqItem.setTotalAmount(getTotalAmount());
		supBqItem.setTax(getTax());
		supBqItem.setTaxType(getTaxType());
		supBqItem.setTotalAmountWithTax(getTotalAmountWithTax());
		supBqItem.setField1(getField1());
		supBqItem.setField2(getField2());
		supBqItem.setField3(getField3());
		supBqItem.setField4(getField4());
		supBqItem.setField5(getField5());
		supBqItem.setField6(getField6());
		supBqItem.setField7(getField7());
		supBqItem.setField8(getField8());
		supBqItem.setField9(getField9());
		supBqItem.setField10(getField10());

		return supBqItem;
	}

	public RftSupplierBqItem copyForRft() {
		RftSupplierBqItem supBqItem = new RftSupplierBqItem();
		supBqItem.setItemDescription(getItemDescription());
		if (getUom() != null) {
			supBqItem.setUom(getUom());
		}
		supBqItem.setQuantity(getQuantity());
		supBqItem.setItemName(getItemName());
		supBqItem.setUnitPrice(getUnitPrice());// getUnitPrice());
		supBqItem.setLevel(getLevel());
		supBqItem.setOrder(getOrder());
		supBqItem.setPriceType(getPriceType());
		supBqItem.setTotalAmount(getTotalAmount());
		supBqItem.setTax(getTax());
		supBqItem.setTaxType(getTaxType());
		supBqItem.setTotalAmountWithTax(getTotalAmountWithTax());
		supBqItem.setField1(getField1());
		supBqItem.setField2(getField2());
		supBqItem.setField3(getField3());
		supBqItem.setField4(getField4());
		supBqItem.setField5(getField5());
		supBqItem.setField6(getField6());
		supBqItem.setField7(getField7());
		supBqItem.setField8(getField8());
		supBqItem.setField9(getField9());
		supBqItem.setField10(getField10());

		return supBqItem;
	}
}
