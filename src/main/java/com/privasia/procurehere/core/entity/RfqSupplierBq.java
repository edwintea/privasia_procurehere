/**
 * 
 */
package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.DecimalMax;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.privasia.procurehere.core.enums.SupplierBqStatus;

/**
 * @author Vipul
 * @author Ravi
 */
@Entity
@Table(name = "PROC_RFQ_SUPPLIER_BQ")
public class RfqSupplierBq extends Bq implements Serializable {

	private static final long serialVersionUID = 5867513085042725778L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_SUP_RFQ_EVENT_BQ"))
	private RfqEvent event;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "BQ_ID", foreignKey = @ForeignKey(name = "FK_SUP_RFQ_EVENT_BQ_ID"))
	private RfqEventBq bq;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "supplierBq")
	@OrderBy("level, order")
	private List<RfqSupplierBqItem> supplierBqItems;

	@Column(name = "TAX_DESCRIPTION", length = 250, nullable = true)
	private String taxDescription;

	@DecimalMax("9999999999999999.999999")
	@Column(name = "ADDITIONAL_TAX", precision = 22, scale = 6, nullable = true)
	private BigDecimal additionalTax;

	@DecimalMax("9999999999999999.999999")
	@Column(name = "GRAND_TOTAL", precision = 22, scale = 6, nullable = true)
	private BigDecimal grandTotal;

	@DecimalMax("9999999999999999.999999")
	@Column(name = "TOTAL_AFTER_TAX", precision = 22, scale = 6, nullable = true)
	private BigDecimal totalAfterTax;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "SUPPLIER_ID", foreignKey = @ForeignKey(name = "FK_RFQ_BQ_SUP_ID"))
	private Supplier supplier;

	@Column(name = "SUPPLIER_REMARK", length = 3000, nullable = true)
	private String remark;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "SUPPLIER_BQ_STATUS", nullable = true)
	private SupplierBqStatus supplierBqStatus = SupplierBqStatus.PENDING;

	public RfqSupplierBq() {
	}

	public RfqSupplierBq(String name, BigDecimal grandTotal) {
		super.setName(name);
		this.grandTotal = grandTotal;
	}
	
	public RfqSupplierBq(String name, BigDecimal grandTotal, SupplierBqStatus supplierBqStatus) {
		super.setName(name);
		this.grandTotal = grandTotal;
		this.supplierBqStatus = supplierBqStatus;
	}
	
	public RfqSupplierBq(SupplierBqStatus status, String bqId) {
		this.supplierBqStatus = status;
		this.bq = new RfqEventBq();
		this.bq.setId(bqId);
	}

	public RfqSupplierBq(RfqEventBq bq) {
		this.setGrandTotal(grandTotal);
		// this.setAdditionalTax(getAdditionalTax());
		this.setField1FilledBy(bq.getField1FilledBy());
		this.setField1Label(bq.getField1Label());
		this.setField1Required(bq.getField1Required());
		this.setField1ToShowSupplier(bq.getField1ToShowSupplier());
		this.setField2FilledBy(bq.getField2FilledBy());
		this.setField2Label(bq.getField2Label());
		this.setField2Required(bq.getField2Required());
		this.setField2ToShowSupplier(bq.getField2ToShowSupplier());
		this.setField2Visible(bq.getField2Visible());
		this.setField3FilledBy(bq.getField3FilledBy());
		this.setField3Label(bq.getField3Label());
		this.setField3Required(bq.getField3Required());
		this.setField3ToShowSupplier(bq.getField3ToShowSupplier());
		this.setField4FilledBy(bq.getField4FilledBy());
		this.setField4Label(bq.getField4Label());
		this.setField4Required(bq.getField4Required());
		this.setField4ToShowSupplier(bq.getField4ToShowSupplier());

		this.setField5FilledBy(bq.getField5FilledBy());
		this.setField5Label(bq.getField5Label());
		this.setField5Required(bq.getField5Required());
		this.setField5ToShowSupplier(bq.getField5ToShowSupplier());
		this.setField5Visible(bq.getField5Visible());
		this.setField6FilledBy(bq.getField6FilledBy());
		this.setField6Label(bq.getField6Label());
		this.setField6Required(bq.getField6Required());
		this.setField6ToShowSupplier(bq.getField6ToShowSupplier());
		this.setField6Visible(bq.getField6Visible());
		this.setField7FilledBy(bq.getField7FilledBy());
		this.setField7Label(bq.getField7Label());
		this.setField7Required(bq.getField7Required());
		this.setField7ToShowSupplier(bq.getField7ToShowSupplier());
		this.setField7Visible(bq.getField7Visible());
		this.setField8FilledBy(bq.getField8FilledBy());
		this.setField8Label(bq.getField8Label());
		this.setField8Required(bq.getField8Required());
		this.setField8ToShowSupplier(bq.getField8ToShowSupplier());
		this.setField8Visible(bq.getField8Visible());
		this.setField9FilledBy(bq.getField9FilledBy());
		this.setField9Label(bq.getField9Label());
		this.setField9Required(bq.getField9Required());
		this.setField9ToShowSupplier(bq.getField9ToShowSupplier());
		this.setField9Visible(bq.getField9Visible());
		this.setField10FilledBy(bq.getField10FilledBy());
		this.setField10Label(bq.getField10Label());
		this.setField10Required(bq.getField10Required());
		this.setField10ToShowSupplier(bq.getField10ToShowSupplier());
		this.setField10Visible(bq.getField10Visible());

		this.setName(bq.getName());
		this.setBqOrder(bq.getBqOrder());
		this.setModifiedDate(bq.getModifiedDate());
		this.setCreatedDate(bq.getCreatedDate());
		// this.setTaxDescription(getTaxDescription());
		// this.setTotalAfterTax(getTotalAfterTax());
		this.setAdditionalTax(additionalTax);
		this.setTaxDescription(taxDescription);
		this.setTotalAfterTax(totalAfterTax);
		this.setDescription(bq.getDescription());
		this.setBq(bq);
	}

	/**
	 * @return the supplierBqStatus
	 */
	public SupplierBqStatus getSupplierBqStatus() {
		return supplierBqStatus;
	}

	/**
	 * @param supplierBqStatus the supplierBqStatus to set
	 */
	public void setSupplierBqStatus(SupplierBqStatus supplierBqStatus) {
		this.supplierBqStatus = supplierBqStatus;
	}

	/**
	 * @return the remark
	 */
	public String getRemark() {
		return remark;
	}

	/**
	 * @param remark the remark to set
	 */
	public void setRemark(String remark) {
		this.remark = remark;
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
	 * @return the supplierBqItems
	 */
	public List<RfqSupplierBqItem> getSupplierBqItems() {
		return supplierBqItems;
	}

	/**
	 * @param supplierBqItems the supplierBqItems to set
	 */
	public void setSupplierBqItems(List<RfqSupplierBqItem> supplierBqItems) {
		this.supplierBqItems = supplierBqItems;
	}

	/**
	 * publishedDate
	 * 
	 * @return the taxDescription
	 */
	public String getTaxDescription() {
		return taxDescription;
	}

	/**
	 * @param taxDescription the taxDescription to set
	 */
	public void setTaxDescription(String taxDescription) {
		this.taxDescription = taxDescription;
	}

	/**
	 * @return the additionalTax
	 */
	public BigDecimal getAdditionalTax() {
		return additionalTax;
	}

	/**
	 * @param additionalTax the additionalTax to set
	 */
	public void setAdditionalTax(BigDecimal additionalTax) {
		this.additionalTax = additionalTax;
	}

	/**
	 * @return the grandTotal
	 */
	public BigDecimal getGrandTotal() {
		return grandTotal;
	}

	/**
	 * @param grandTotal the grandTotal to set
	 */
	public void setGrandTotal(BigDecimal grandTotal) {
		this.grandTotal = grandTotal;
	}

	/**
	 * @return the totalAfterTax
	 */
	public BigDecimal getTotalAfterTax() {
		return totalAfterTax;
	}

	/**
	 * @param totalAfterTax the totalAfterTax to set
	 */
	public void setTotalAfterTax(BigDecimal totalAfterTax) {
		this.totalAfterTax = totalAfterTax;
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

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((additionalTax == null) ? 0 : additionalTax.hashCode());
		result = prime * result + ((grandTotal == null) ? 0 : grandTotal.hashCode());
		result = prime * result + ((event == null) ? 0 : event.hashCode());
		result = prime * result + ((supplierBqItems == null) ? 0 : supplierBqItems.hashCode());
		result = prime * result + ((taxDescription == null) ? 0 : taxDescription.hashCode());
		result = prime * result + ((totalAfterTax == null) ? 0 : totalAfterTax.hashCode());
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
		RfqSupplierBq other = (RfqSupplierBq) obj;
		if (additionalTax == null) {
			if (other.additionalTax != null)
				return false;
		} else if (!additionalTax.equals(other.additionalTax))
			return false;
		if (grandTotal == null) {
			if (other.grandTotal != null)
				return false;
		} else if (!grandTotal.equals(other.grandTotal))
			return false;
		if (event == null) {
			if (other.event != null)
				return false;
		} else if (!event.equals(other.event))
			return false;
		if (supplierBqItems == null) {
			if (other.supplierBqItems != null)
				return false;
		} else if (!supplierBqItems.equals(other.supplierBqItems))
			return false;
		if (taxDescription == null) {
			if (other.taxDescription != null)
				return false;
		} else if (!taxDescription.equals(other.taxDescription))
			return false;
		if (totalAfterTax == null) {
			if (other.totalAfterTax != null)
				return false;
		} else if (!totalAfterTax.equals(other.totalAfterTax))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RfpSupplierBq [taxDescription=" + taxDescription + ", additionalTax=" + additionalTax + ", grandTotal=" + grandTotal + ", totalAfterTax=" + totalAfterTax + " " + toLogString() + "]";
	}

	public RfaSupplierBq createForRfa(RfaEvent newEvent, RfaEventBq newBq) {
		RfaSupplierBq bq = new RfaSupplierBq();
		bq.setAdditionalTax(this.additionalTax);
		bq.setCreatedDate(new Date());
		bq.setBuyerSubmited(true);
		bq.setDescription(this.getDescription());
		bq.setTotalAfterTax(this.getTotalAfterTax());
		bq.setTaxDescription(this.getTaxDescription());
		bq.setRemark(this.getRemark());
		bq.setName(this.getName());
		bq.setBqOrder(this.getBqOrder());
		bq.setHeaderCount(this.getHeaderCount());
		bq.setGrandTotal(this.getGrandTotal());
		bq.setField10FilledBy(this.getField9FilledBy());
		bq.setField10Visible(this.getField8Visible());
		bq.setField10Label(this.getField8Label());
		bq.setField10Required(this.getField8Required());
		bq.setField10ToShowSupplier(this.getField8ToShowSupplier());
		bq.setField10FilledBy(this.getField8FilledBy());

		bq.setField9FilledBy(this.getField9FilledBy());
		bq.setField9Visible(this.getField8Visible());
		bq.setField9Label(this.getField8Label());
		bq.setField9Required(this.getField8Required());
		bq.setField9ToShowSupplier(this.getField8ToShowSupplier());
		bq.setField9FilledBy(this.getField8FilledBy());

		bq.setField8FilledBy(this.getField9FilledBy());
		bq.setField8Visible(this.getField8Visible());
		bq.setField8Label(this.getField8Label());
		bq.setField8Required(this.getField8Required());
		bq.setField8ToShowSupplier(this.getField8ToShowSupplier());
		bq.setField8FilledBy(this.getField8FilledBy());

		bq.setField7FilledBy(this.getField9FilledBy());
		bq.setField7Visible(this.getField8Visible());
		bq.setField7Label(this.getField8Label());
		bq.setField7Required(this.getField8Required());
		bq.setField7ToShowSupplier(this.getField8ToShowSupplier());
		bq.setField7FilledBy(this.getField8FilledBy());

		bq.setField6FilledBy(this.getField9FilledBy());
		bq.setField6Visible(this.getField8Visible());
		bq.setField6Label(this.getField8Label());
		bq.setField6Required(this.getField8Required());
		bq.setField6ToShowSupplier(this.getField8ToShowSupplier());
		bq.setField6FilledBy(this.getField8FilledBy());

		bq.setField5FilledBy(this.getField9FilledBy());
		bq.setField5Visible(this.getField8Visible());
		bq.setField5Label(this.getField8Label());
		bq.setField5Required(this.getField8Required());
		bq.setField5ToShowSupplier(this.getField8ToShowSupplier());
		bq.setField5FilledBy(this.getField8FilledBy());

		bq.setField4FilledBy(this.getField9FilledBy());
		bq.setField4Visible(this.getField8Visible());
		bq.setField4Label(this.getField8Label());
		bq.setField4Required(this.getField8Required());
		bq.setField4ToShowSupplier(this.getField8ToShowSupplier());
		bq.setField4FilledBy(this.getField8FilledBy());

		bq.setField3FilledBy(this.getField9FilledBy());
		bq.setField3Visible(this.getField8Visible());
		bq.setField3Label(this.getField8Label());
		bq.setField3Required(this.getField8Required());
		bq.setField3ToShowSupplier(this.getField8ToShowSupplier());
		bq.setField3FilledBy(this.getField8FilledBy());

		bq.setField2FilledBy(this.getField9FilledBy());
		bq.setField2Visible(this.getField8Visible());
		bq.setField2Label(this.getField8Label());
		bq.setField2Required(this.getField8Required());
		bq.setField2ToShowSupplier(this.getField8ToShowSupplier());
		bq.setField2FilledBy(this.getField8FilledBy());

		bq.setField1FilledBy(this.getField9FilledBy());
		bq.setField1Label(this.getField8Label());
		bq.setField1Required(this.getField8Required());
		bq.setField1ToShowSupplier(this.getField8ToShowSupplier());
		bq.setField1FilledBy(this.getField8FilledBy());
		// RfaEventBq rfaBq = this.getBq().copyForRfa();
		bq.setBq(newBq);
		List<RfaSupplierBqItem> bqItemsCopy = new ArrayList<RfaSupplierBqItem>();
		for (RfqSupplierBqItem rfpSupplierBqItem : this.getSupplierBqItems()) {
			RfaSupplierBqItem item = rfpSupplierBqItem.copyForRfa();
			item.setSupplierBq(bq);
			// Attach the BQ item by matching it with the cloned (with original id) in the list
			RfaBqItem newBqItem = null;
			for (RfaBqItem rbi : newBq.getBqItems()) {
				if (rbi.getId().equals(rfpSupplierBqItem.getBqItem().getId())) {
					newBqItem = rbi;
					break;
				}
			}

			item.setBqItem(newBqItem); // Cloned BqItem
			item.setEvent(newEvent); // Cloned Event
			item.setSupplier(this.supplier);
			bqItemsCopy.add(item);
		}
		bq.setSupplierBqItems(bqItemsCopy);

		bq.setEvent(newEvent); // Cloned Event
		bq.setBq(newBq); // Cloned BQ
		bq.setSupplier(this.supplier);
		return bq;

	}

	public RfpSupplierBq createForRfp(RfpEvent newEvent, RfpEventBq newBq) {
		RfpSupplierBq bq = new RfpSupplierBq();
		bq.setAdditionalTax(this.additionalTax);
		bq.setCreatedDate(new Date());
		bq.setDescription(this.getDescription());
		bq.setTotalAfterTax(this.getTotalAfterTax());
		bq.setTaxDescription(this.getTaxDescription());
		bq.setRemark(this.getRemark());
		bq.setName(this.getName());
		bq.setBqOrder(this.getBqOrder());
		bq.setHeaderCount(this.getHeaderCount());
		bq.setGrandTotal(this.getGrandTotal());
		bq.setField10FilledBy(this.getField9FilledBy());
		bq.setField10Visible(this.getField8Visible());
		bq.setField10Label(this.getField8Label());
		bq.setField10Required(this.getField8Required());
		bq.setField10ToShowSupplier(this.getField8ToShowSupplier());
		bq.setField10FilledBy(this.getField8FilledBy());

		bq.setField9FilledBy(this.getField9FilledBy());
		bq.setField9Visible(this.getField8Visible());
		bq.setField9Label(this.getField8Label());
		bq.setField9Required(this.getField8Required());
		bq.setField9ToShowSupplier(this.getField8ToShowSupplier());
		bq.setField9FilledBy(this.getField8FilledBy());

		bq.setField8FilledBy(this.getField9FilledBy());
		bq.setField8Visible(this.getField8Visible());
		bq.setField8Label(this.getField8Label());
		bq.setField8Required(this.getField8Required());
		bq.setField8ToShowSupplier(this.getField8ToShowSupplier());
		bq.setField8FilledBy(this.getField8FilledBy());

		bq.setField7FilledBy(this.getField9FilledBy());
		bq.setField7Visible(this.getField8Visible());
		bq.setField7Label(this.getField8Label());
		bq.setField7Required(this.getField8Required());
		bq.setField7ToShowSupplier(this.getField8ToShowSupplier());
		bq.setField7FilledBy(this.getField8FilledBy());

		bq.setField6FilledBy(this.getField9FilledBy());
		bq.setField6Visible(this.getField8Visible());
		bq.setField6Label(this.getField8Label());
		bq.setField6Required(this.getField8Required());
		bq.setField6ToShowSupplier(this.getField8ToShowSupplier());
		bq.setField6FilledBy(this.getField8FilledBy());

		bq.setField5FilledBy(this.getField9FilledBy());
		bq.setField5Visible(this.getField8Visible());
		bq.setField5Label(this.getField8Label());
		bq.setField5Required(this.getField8Required());
		bq.setField5ToShowSupplier(this.getField8ToShowSupplier());
		bq.setField5FilledBy(this.getField8FilledBy());

		bq.setField4FilledBy(this.getField9FilledBy());
		bq.setField4Visible(this.getField8Visible());
		bq.setField4Label(this.getField8Label());
		bq.setField4Required(this.getField8Required());
		bq.setField4ToShowSupplier(this.getField8ToShowSupplier());
		bq.setField4FilledBy(this.getField8FilledBy());

		bq.setField3FilledBy(this.getField9FilledBy());
		bq.setField3Visible(this.getField8Visible());
		bq.setField3Label(this.getField8Label());
		bq.setField3Required(this.getField8Required());
		bq.setField3ToShowSupplier(this.getField8ToShowSupplier());
		bq.setField3FilledBy(this.getField8FilledBy());

		bq.setField2FilledBy(this.getField9FilledBy());
		bq.setField2Visible(this.getField8Visible());
		bq.setField2Label(this.getField8Label());
		bq.setField2Required(this.getField8Required());
		bq.setField2ToShowSupplier(this.getField8ToShowSupplier());
		bq.setField2FilledBy(this.getField8FilledBy());

		bq.setField1FilledBy(this.getField9FilledBy());
		bq.setField1Label(this.getField8Label());
		bq.setField1Required(this.getField8Required());
		bq.setField1ToShowSupplier(this.getField8ToShowSupplier());
		bq.setField1FilledBy(this.getField8FilledBy());
		// RfpEventBq rfaBq = this.getBq().copyForRfp();
		bq.setBq(newBq);
		List<RfpSupplierBqItem> bqItemsCopy = new ArrayList<RfpSupplierBqItem>();
		for (RfqSupplierBqItem rfpSupplierBqItem : this.getSupplierBqItems()) {
			RfpSupplierBqItem item = rfpSupplierBqItem.copyForRfp();
			item.setSupplierBq(bq);
			// Attach the BQ item by matching it with the cloned (with original id) in the list
			RfpBqItem newBqItem = null;
			item.setBq(newBq);
			for (RfpBqItem rbi : newBq.getBqItems()) {
				if (rbi.getId().equals(rfpSupplierBqItem.getBqItem().getId())) {
					newBqItem = rbi;
					break;
				}
			}

			item.setBqItem(newBqItem); // Cloned BqItem
			item.setEvent(newEvent); // Cloned Event
			item.setSupplier(this.supplier);
			bqItemsCopy.add(item);
		}
		bq.setSupplierBqItems(bqItemsCopy);

		bq.setEvent(newEvent); // Cloned Event
		bq.setBq(newBq); // Cloned BQ
		bq.setSupplier(this.supplier);
		return bq;
	}

	public RfqSupplierBq createForRfq(RfqEvent newEvent, RfqEventBq newBq) {
		RfqSupplierBq bq = new RfqSupplierBq();
		bq.setAdditionalTax(this.additionalTax);
		bq.setCreatedDate(new Date());
		bq.setDescription(this.getDescription());
		bq.setTotalAfterTax(this.getTotalAfterTax());
		bq.setTaxDescription(this.getTaxDescription());
		bq.setRemark(this.getRemark());
		bq.setName(this.getName());
		bq.setBqOrder(this.getBqOrder());
		bq.setHeaderCount(this.getHeaderCount());
		bq.setGrandTotal(this.getGrandTotal());
		bq.setField10FilledBy(this.getField9FilledBy());
		bq.setField10Visible(this.getField8Visible());
		bq.setField10Label(this.getField8Label());
		bq.setField10Required(this.getField8Required());
		bq.setField10ToShowSupplier(this.getField8ToShowSupplier());
		bq.setField10FilledBy(this.getField8FilledBy());

		bq.setField9FilledBy(this.getField9FilledBy());
		bq.setField9Visible(this.getField8Visible());
		bq.setField9Label(this.getField8Label());
		bq.setField9Required(this.getField8Required());
		bq.setField9ToShowSupplier(this.getField8ToShowSupplier());
		bq.setField9FilledBy(this.getField8FilledBy());

		bq.setField8FilledBy(this.getField9FilledBy());
		bq.setField8Visible(this.getField8Visible());
		bq.setField8Label(this.getField8Label());
		bq.setField8Required(this.getField8Required());
		bq.setField8ToShowSupplier(this.getField8ToShowSupplier());
		bq.setField8FilledBy(this.getField8FilledBy());

		bq.setField7FilledBy(this.getField9FilledBy());
		bq.setField7Visible(this.getField8Visible());
		bq.setField7Label(this.getField8Label());
		bq.setField7Required(this.getField8Required());
		bq.setField7ToShowSupplier(this.getField8ToShowSupplier());
		bq.setField7FilledBy(this.getField8FilledBy());

		bq.setField6FilledBy(this.getField9FilledBy());
		bq.setField6Visible(this.getField8Visible());
		bq.setField6Label(this.getField8Label());
		bq.setField6Required(this.getField8Required());
		bq.setField6ToShowSupplier(this.getField8ToShowSupplier());
		bq.setField6FilledBy(this.getField8FilledBy());

		bq.setField5FilledBy(this.getField9FilledBy());
		bq.setField5Visible(this.getField8Visible());
		bq.setField5Label(this.getField8Label());
		bq.setField5Required(this.getField8Required());
		bq.setField5ToShowSupplier(this.getField8ToShowSupplier());
		bq.setField5FilledBy(this.getField8FilledBy());

		bq.setField4FilledBy(this.getField9FilledBy());
		bq.setField4Visible(this.getField8Visible());
		bq.setField4Label(this.getField8Label());
		bq.setField4Required(this.getField8Required());
		bq.setField4ToShowSupplier(this.getField8ToShowSupplier());
		bq.setField4FilledBy(this.getField8FilledBy());

		bq.setField3FilledBy(this.getField9FilledBy());
		bq.setField3Visible(this.getField8Visible());
		bq.setField3Label(this.getField8Label());
		bq.setField3Required(this.getField8Required());
		bq.setField3ToShowSupplier(this.getField8ToShowSupplier());
		bq.setField3FilledBy(this.getField8FilledBy());

		bq.setField2FilledBy(this.getField9FilledBy());
		bq.setField2Visible(this.getField8Visible());
		bq.setField2Label(this.getField8Label());
		bq.setField2Required(this.getField8Required());
		bq.setField2ToShowSupplier(this.getField8ToShowSupplier());
		bq.setField2FilledBy(this.getField8FilledBy());

		bq.setField1FilledBy(this.getField9FilledBy());
		bq.setField1Label(this.getField8Label());
		bq.setField1Required(this.getField8Required());
		bq.setField1ToShowSupplier(this.getField8ToShowSupplier());
		bq.setField1FilledBy(this.getField8FilledBy());
		// RfqEventBq rfaBq = this.getBq().copyForRfq();
		bq.setBq(newBq);
		List<RfqSupplierBqItem> bqItemsCopy = new ArrayList<RfqSupplierBqItem>();
		for (RfqSupplierBqItem rfpSupplierBqItem : this.getSupplierBqItems()) {
			RfqSupplierBqItem item = rfpSupplierBqItem.copyForRfq();
			item.setSupplierBq(bq);
			item.setBq(newBq);
			// Attach the BQ item by matching it with the cloned (with original id) in the list
			RfqBqItem newBqItem = null;
			for (RfqBqItem rbi : newBq.getBqItems()) {
				if (rbi.getId().equals(rfpSupplierBqItem.getBqItem().getId())) {
					newBqItem = rbi;
					break;
				}
			}

			item.setBqItem(newBqItem); // Cloned BqItem
			item.setEvent(newEvent); // Cloned Event
			item.setSupplier(this.supplier);
			bqItemsCopy.add(item);
		}
		bq.setSupplierBqItems(bqItemsCopy);

		bq.setEvent(newEvent); // Cloned Event
		bq.setBq(newBq); // Cloned BQ
		bq.setSupplier(this.supplier);
		return bq;
	}

	public RftSupplierBq createForRft(RftEvent newEvent, RftEventBq newBq) {
		RftSupplierBq bq = new RftSupplierBq();
		bq.setAdditionalTax(this.additionalTax);
		bq.setCreatedDate(new Date());
		bq.setDescription(this.getDescription());
		bq.setTotalAfterTax(this.getTotalAfterTax());
		bq.setTaxDescription(this.getTaxDescription());
		bq.setRemark(this.getRemark());
		bq.setName(this.getName());
		bq.setBqOrder(this.getBqOrder());
		bq.setHeaderCount(this.getHeaderCount());
		bq.setGrandTotal(this.getGrandTotal());
		bq.setField10FilledBy(this.getField9FilledBy());
		bq.setField10Visible(this.getField8Visible());
		bq.setField10Label(this.getField8Label());
		bq.setField10Required(this.getField8Required());
		bq.setField10ToShowSupplier(this.getField8ToShowSupplier());
		bq.setField10FilledBy(this.getField8FilledBy());

		bq.setField9FilledBy(this.getField9FilledBy());
		bq.setField9Visible(this.getField8Visible());
		bq.setField9Label(this.getField8Label());
		bq.setField9Required(this.getField8Required());
		bq.setField9ToShowSupplier(this.getField8ToShowSupplier());
		bq.setField9FilledBy(this.getField8FilledBy());

		bq.setField8FilledBy(this.getField9FilledBy());
		bq.setField8Visible(this.getField8Visible());
		bq.setField8Label(this.getField8Label());
		bq.setField8Required(this.getField8Required());
		bq.setField8ToShowSupplier(this.getField8ToShowSupplier());
		bq.setField8FilledBy(this.getField8FilledBy());

		bq.setField7FilledBy(this.getField9FilledBy());
		bq.setField7Visible(this.getField8Visible());
		bq.setField7Label(this.getField8Label());
		bq.setField7Required(this.getField8Required());
		bq.setField7ToShowSupplier(this.getField8ToShowSupplier());
		bq.setField7FilledBy(this.getField8FilledBy());

		bq.setField6FilledBy(this.getField9FilledBy());
		bq.setField6Visible(this.getField8Visible());
		bq.setField6Label(this.getField8Label());
		bq.setField6Required(this.getField8Required());
		bq.setField6ToShowSupplier(this.getField8ToShowSupplier());
		bq.setField6FilledBy(this.getField8FilledBy());

		bq.setField5FilledBy(this.getField9FilledBy());
		bq.setField5Visible(this.getField8Visible());
		bq.setField5Label(this.getField8Label());
		bq.setField5Required(this.getField8Required());
		bq.setField5ToShowSupplier(this.getField8ToShowSupplier());
		bq.setField5FilledBy(this.getField8FilledBy());

		bq.setField4FilledBy(this.getField9FilledBy());
		bq.setField4Visible(this.getField8Visible());
		bq.setField4Label(this.getField8Label());
		bq.setField4Required(this.getField8Required());
		bq.setField4ToShowSupplier(this.getField8ToShowSupplier());
		bq.setField4FilledBy(this.getField8FilledBy());

		bq.setField3FilledBy(this.getField9FilledBy());
		bq.setField3Visible(this.getField8Visible());
		bq.setField3Label(this.getField8Label());
		bq.setField3Required(this.getField8Required());
		bq.setField3ToShowSupplier(this.getField8ToShowSupplier());
		bq.setField3FilledBy(this.getField8FilledBy());

		bq.setField2FilledBy(this.getField9FilledBy());
		bq.setField2Visible(this.getField8Visible());
		bq.setField2Label(this.getField8Label());
		bq.setField2Required(this.getField8Required());
		bq.setField2ToShowSupplier(this.getField8ToShowSupplier());
		bq.setField2FilledBy(this.getField8FilledBy());

		bq.setField1FilledBy(this.getField9FilledBy());
		bq.setField1Label(this.getField8Label());
		bq.setField1Required(this.getField8Required());
		bq.setField1ToShowSupplier(this.getField8ToShowSupplier());
		bq.setField1FilledBy(this.getField8FilledBy());
		// RftEventBq rfaBq = this.getBq().copyForRft();
		bq.setBq(newBq);
		List<RftSupplierBqItem> bqItemsCopy = new ArrayList<RftSupplierBqItem>();
		for (RfqSupplierBqItem rfpSupplierBqItem : this.getSupplierBqItems()) {
			RftSupplierBqItem item = rfpSupplierBqItem.copyForRft();
			item.setSupplierBq(bq);
			// Attach the BQ item by matching it with the cloned (with original id) in the list
			RftBqItem newBqItem = null;
			for (RftBqItem rbi : newBq.getBqItems()) {
				if (rbi.getId().equals(rfpSupplierBqItem.getBqItem().getId())) {
					newBqItem = rbi;
					break;
				}
			}

			item.setBqItem(newBqItem); // Cloned BqItem
			item.setEvent(newEvent); // Cloned Event
			item.setSupplier(this.supplier);
			bqItemsCopy.add(item);
		}
		bq.setSupplierBqItems(bqItemsCopy);

		bq.setRfxEvent(newEvent); // Cloned Event
		bq.setBq(newBq); // Cloned BQ
		bq.setSupplier(this.supplier);
		return bq;
	}

}
