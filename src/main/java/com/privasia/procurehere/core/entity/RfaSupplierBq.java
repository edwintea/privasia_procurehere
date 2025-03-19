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
import javax.persistence.Transient;
import javax.validation.constraints.DecimalMax;
import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.privasia.procurehere.core.enums.SupplierBqStatus;
import com.privasia.procurehere.core.utils.CollectionUtil;
import com.privasia.procurehere.core.utils.Global;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Vipul
 * @author Ravi
 */
@Entity
@Table(name = "PROC_RFA_SUPPLIER_BQ")
@JsonIgnoreProperties(ignoreUnknown = true)
public class RfaSupplierBq extends Bq implements Serializable {
	protected static final Logger LOG = LogManager.getLogger(Global.AUCTION_LOG);
	private static final long serialVersionUID = 8017404448515065282L;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_SUP_RFA_EVENT_BQ"))
	private RfaEvent event;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = CascadeType.REFRESH)
	@JoinColumn(name = "BQ_ID", foreignKey = @ForeignKey(name = "FK_RFA_EVENT_SUP_BQ_ID"))
	private RfaEventBq bq;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "supplierBq")
	@OrderBy("level, order")
	private List<RfaSupplierBqItem> supplierBqItems;

	@Column(name = "TAX_DESCRIPTION", length = 250, nullable = true)
	private String taxDescription;

	@DecimalMax("9999999999999999.999999")
	@Column(name = "ADDITIONAL_TAX", precision = 22, scale = 6, nullable = true)
	private BigDecimal additionalTax;

	@DecimalMax("9999999999999999.999999")
	@Column(name = "GRAND_TOTAL", precision = 22, scale = 6, nullable = true)
	private BigDecimal grandTotal;

	@DecimalMax("9999999999999999.999999")
	@Column(name = "REVISED_GRAND_TOTAL", precision = 22, scale = 6, nullable = true)
	private BigDecimal revisedGrandTotal;

	@DecimalMax("9999999999999999.999999")
	@Column(name = "TOTAL_AFTER_TAX", precision = 22, scale = 6, nullable = true)
	private BigDecimal totalAfterTax;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "SUPPLIER_ID", foreignKey = @ForeignKey(name = "FK_RFA_BQ_SUP_ID"))
	private Supplier supplier;

	@DecimalMax("9999999999999999.999999")
	@Column(name = "INITIAL_PRICE", precision = 22, scale = 6, nullable = true)
	private BigDecimal initialPrice;

	@DecimalMax("99999999.99")
	@Column(name = "DIFFERENCE_PERCENTAGE", precision = 10, scale = 2, nullable = true)
	private BigDecimal differncePerToInitial;

	@Column(name = "BUYER_SUBMITED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean buyerSubmited = Boolean.FALSE;

	@Column(name = "SUPPLIER_REMARK", length = 3000, nullable = true)
	private String remark;

	@Enumerated(EnumType.STRING)
	@Column(name = "SUPPLIER_BQ_STATUS", nullable = true)
	private SupplierBqStatus supplierBqStatus = SupplierBqStatus.PENDING;

	@Transient
	private List<RfaSupplierBqItem> auditSupplierBqItems;
	@Transient
	private BigDecimal bidPrice;

	@Transient
	private String suppCompanyName;

	/**
	 * @return the suppCompanyName
	 */
	public String getSuppCompanyName() {
		return suppCompanyName;
	}

	/**
	 * @param suppCompanyName the suppCompanyName to set
	 */
	public void setSuppCompanyName(String suppCompanyName) {
		this.suppCompanyName = suppCompanyName;
	}

	public RfaSupplierBq() {
		this.buyerSubmited = Boolean.FALSE;
	}

	public RfaSupplierBq(SupplierBqStatus status, String bqId) {
		this.supplierBqStatus = status;
		this.bq = new RfaEventBq();
		this.bq.setId(bqId);
	}

	public RfaSupplierBq(String name, BigDecimal grandTotal, Boolean buyerSubmited) {
		super.setName(name);
		this.grandTotal = grandTotal;
		this.buyerSubmited = buyerSubmited;
	}

	public RfaSupplierBq copyBq(Supplier supplier, RfaEventBq newBq, RfaEvent event) {

		RfaSupplierBq bq = new RfaSupplierBq();
		bq.setAdditionalTax(this.additionalTax);
		bq.setBuyerSubmited(true);
		bq.setCreatedDate(new Date());
		bq.setDescription(this.getDescription());
		bq.setDifferncePerToInitial(this.getDifferncePerToInitial());
		bq.setTotalAfterTax(this.getTotalAfterTax());
		bq.setTaxDescription(this.getTaxDescription());
		bq.setRemark(this.getRemark());
		bq.setName(this.getName());
		bq.setBqOrder(this.getBqOrder());
		bq.setInitialPrice(this.getInitialPrice());
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

		bq.setAuditSupplierBqItems(this.getAuditSupplierBqItems());
		// RfaEventBq rfaBq = this.getBq().copyForRfa();
		bq.setBq(newBq);
		List<RfaSupplierBqItem> bqItemsCopy = new ArrayList<RfaSupplierBqItem>();
		for (RfaSupplierBqItem rfaSupplierBqItem : this.getSupplierBqItems()) {
			RfaSupplierBqItem item = rfaSupplierBqItem.copyForRfa();
			item.setSupplierBq(bq);
			LOG.info(bq.getId());
			// Attach the BQ item by matching it with the cloned (with original id) in the list
			RfaBqItem newBqItem = null;
			for (RfaBqItem rbi : newBq.getBqItems()) {
				if (rbi.getId().equals(rfaSupplierBqItem.getBqItem().getId())) {
					newBqItem = rbi;
					break;
				}
			}

			item.setBqItem(newBqItem); // Cloned BqItem
			item.setEvent(event); // Cloned Event
			item.setSupplier(supplier);
			bqItemsCopy.add(item);
		}
		bq.setSupplierBqItems(bqItemsCopy);

		bq.setEvent(event); // Cloned Event
		bq.setBq(newBq); // Cloned BQ
		bq.setSupplier(supplier);
		return bq;

	}

	public RfaSupplierBq(RfaEventBq bq) {
		this.setGrandTotal(grandTotal);
		this.setAdditionalTax(additionalTax);
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
		this.setTaxDescription(taxDescription);
		this.setTotalAfterTax(totalAfterTax);
		this.setDescription(bq.getDescription());
		this.setBq(bq);
	}

	/**
	 * @param totalAfterTax
	 * @param initialPrice
	 * @param differncePerToInitial
	 */
	public RfaSupplierBq(BigDecimal totalAfterTax, BigDecimal initialPrice, BigDecimal differncePerToInitial) {
		super();
		this.totalAfterTax = totalAfterTax;
		this.initialPrice = initialPrice;
		this.differncePerToInitial = differncePerToInitial;
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
	 * @return the event
	 */
	public RfaEvent getEvent() {
		return event;
	}

	/**
	 * @param event the event to set
	 */
	public void setEvent(RfaEvent event) {
		this.event = event;
	}

	/**
	 * @return the bq
	 */
	public RfaEventBq getBq() {
		return bq;
	}

	/**
	 * @param bq the bq to set
	 */
	public void setBq(RfaEventBq bq) {
		this.bq = bq;
	}

	/**
	 * @return the supplierBqItems
	 */
	public List<RfaSupplierBqItem> getSupplierBqItems() {
		return supplierBqItems;
	}

	/**
	 * @param supplierBqItems the supplierBqItems to set
	 */
	public void setSupplierBqItems(List<RfaSupplierBqItem> supplierBqItems) {
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

	/**
	 * @return the initialPrice
	 */
	public BigDecimal getInitialPrice() {
		return initialPrice;
	}

	/**
	 * @param initialPrice the initialPrice to set
	 */
	public void setInitialPrice(BigDecimal initialPrice) {
		this.initialPrice = initialPrice;
	}

	/**
	 * @return the differncePerToInitial
	 */
	public BigDecimal getDifferncePerToInitial() {
		return differncePerToInitial;
	}

	/**
	 * @param differncePerToInitial the differncePerToInitial to set
	 */
	public void setDifferncePerToInitial(BigDecimal differncePerToInitial) {
		this.differncePerToInitial = differncePerToInitial;
	}

	/**
	 * @return the auditSupplierBqItems
	 */
	public List<RfaSupplierBqItem> getAuditSupplierBqItems() {
		try {
			if (CollectionUtil.isNotEmpty(supplierBqItems)) {
				auditSupplierBqItems = new ArrayList<RfaSupplierBqItem>();
				for (RfaSupplierBqItem item : supplierBqItems) {
					RfaSupplierBqItem copy = item.createShallowCopy();
					copy.setSupplierBq(null);
					auditSupplierBqItems.add(copy);
				}
			}
		} catch (Exception e) {
		}
		return auditSupplierBqItems;
	}

	/**
	 * @param auditSupplierBqItems the auditSupplierBqItems to set
	 */
	public void setAuditSupplierBqItems(List<RfaSupplierBqItem> auditSupplierBqItems) {
		this.auditSupplierBqItems = auditSupplierBqItems;
	}

	/**
	 * @return the buyerSubmited
	 */
	public Boolean getBuyerSubmited() {
		return buyerSubmited;
	}

	/**
	 * @param buyerSubmited the buyerSubmited to set
	 */
	public void setBuyerSubmited(Boolean buyerSubmited) {
		this.buyerSubmited = buyerSubmited;
	}

	/**
	 * @return the revisedGrandTotal
	 */
	public BigDecimal getRevisedGrandTotal() {
		return revisedGrandTotal;
	}

	/**
	 * @param revisedGrandTotal the revisedGrandTotal to set
	 */
	public void setRevisedGrandTotal(BigDecimal revisedGrandTotal) {
		this.revisedGrandTotal = revisedGrandTotal;
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

	public RfaSupplierBq createForRfa(RfaEvent newEvent, RfaEventBq newBq) {
		RfaSupplierBq bq = new RfaSupplierBq();
		bq.setAdditionalTax(this.additionalTax);
		bq.setBuyerSubmited(true);
		bq.setCreatedDate(new Date());
		bq.setDescription(this.getDescription());
		bq.setDifferncePerToInitial(this.getDifferncePerToInitial());
		bq.setTotalAfterTax(this.getTotalAfterTax());
		bq.setTaxDescription(this.getTaxDescription());
		bq.setRemark(this.getRemark());
		bq.setName(this.getName());
		bq.setBqOrder(this.getBqOrder());
		bq.setInitialPrice(this.getInitialPrice());
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

		bq.setAuditSupplierBqItems(this.getAuditSupplierBqItems());
		// RfaEventBq rfaBq = this.getBq().copyForRfa();
		bq.setBq(newBq);
		List<RfaSupplierBqItem> bqItemsCopy = new ArrayList<RfaSupplierBqItem>();
		for (RfaSupplierBqItem rfaSupplierBqItem : this.getSupplierBqItems()) {
			RfaSupplierBqItem item = rfaSupplierBqItem.copyForRfa();
			item.setSupplierBq(bq);
			LOG.info(bq.getId());
			// Attach the BQ item by matching it with the cloned (with original id) in the list
			RfaBqItem newBqItem = null;
			for (RfaBqItem rbi : newBq.getBqItems()) {
				if (rbi.getId().equals(rfaSupplierBqItem.getBqItem().getId())) {
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

	public RfpSupplierBq createForRfp(RfpEvent newEvent, RfpEventBq newBq, RfaEventBq rfaEventBq) {
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

		bq.setBq(newBq);

		List<RfpSupplierBqItem> bqItemsCopy = new ArrayList<RfpSupplierBqItem>();
		for (RfaSupplierBqItem rfaSupplierBqItem : this.getSupplierBqItems()) {
			RfpSupplierBqItem item = rfaSupplierBqItem.copyForRfp();
			LOG.info(bq.getId());
			item.setSupplierBq(bq);
			item.setBq(newBq); // Cloned Bq

			// Attach the BQ item by matching it with the cloned (with original id) in the list
			RfpBqItem newBqItem = null;
			for (RfpBqItem rbi : newBq.getBqItems()) {
				if (rbi.getId().equals(rfaSupplierBqItem.getBqItem().getId())) {
					newBqItem = rbi;
					break;
				}
			}

			item.setBqItem(newBqItem); // Cloned BqItem
			item.setEvent(newEvent); // Cloned Event
			item.setSupplier(this.supplier);
			LOG.info(item.toString());
			bqItemsCopy.add(item);
		}
		bq.setSupplierBqItems(bqItemsCopy);

		bq.setSupplierBqItems(bqItemsCopy);

		bq.setEvent(newEvent); // Cloned Event
		bq.setBq(newBq); // Cloned BQ
		bq.setSupplier(this.supplier);
		return bq;
	}

	public RfqSupplierBq createForRfq(RfqEvent newEvent, RfqEventBq newBq, RfaEventBq rfaEventBq) {
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

		bq.setBq(newBq);

		List<RfqSupplierBqItem> bqItemsCopy = new ArrayList<RfqSupplierBqItem>();
		for (RfaSupplierBqItem rfaSupplierBqItem : this.getSupplierBqItems()) {
			RfqSupplierBqItem item = rfaSupplierBqItem.copyForRfq();
			LOG.info(bq.getId());
			item.setSupplierBq(bq);
			item.setBq(newBq); // Cloned Bq
			// Attach the BQ item by matching it with the cloned (with original id) in the list
			RfqBqItem newBqItem = null;
			for (RfqBqItem rbi : newBq.getBqItems()) {
				if (rbi.getId().equals(rfaSupplierBqItem.getBqItem().getId())) {
					newBqItem = rbi;
					break;
				}
			}

			item.setBqItem(newBqItem); // Cloned BqItem
			item.setEvent(newEvent); // Cloned Event
			item.setSupplier(this.supplier);
			LOG.info(item.toString());
			bqItemsCopy.add(item);
		}
		bq.setSupplierBqItems(bqItemsCopy);
		bq.setSupplierBqItems(bqItemsCopy);

		bq.setEvent(newEvent); // Cloned Event
		bq.setBq(newBq); // Cloned BQ
		bq.setSupplier(this.supplier);
		return bq;

	}

	public RftSupplierBq createForRft(RftEvent newEvent, RftEventBq newBq, RfaEventBq bq2) {
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

		// RftEventBq rfpBq = this.getBq().copyForRft();
		bq.setBq(newBq);

		List<RftSupplierBqItem> bqItemsCopy = new ArrayList<RftSupplierBqItem>();
		for (RfaSupplierBqItem rfaSupplierBqItem : this.getSupplierBqItems()) {
			RftSupplierBqItem item = rfaSupplierBqItem.copyForRft();
			LOG.info(bq.getId());
			item.setSupplierBq(bq);
			// item.setBq(rfpBq); // Cloned Bq
			// Attach the BQ item by matching it with the cloned (with original id) in the list
			RftBqItem newBqItem = null;
			for (RftBqItem rbi : newBq.getBqItems()) {
				if (rbi.getId().equals(rfaSupplierBqItem.getBqItem().getId())) {
					newBqItem = rbi;
					break;
				}
			}

			item.setBqItem(newBqItem); // Cloned BqItem
			item.setEvent(newEvent); // Cloned Event
			item.setSupplier(this.supplier);
			LOG.info(item.toString());
			bqItemsCopy.add(item);
		}
		bq.setSupplierBqItems(bqItemsCopy);
		bq.setSupplierBqItems(bqItemsCopy);

		bq.setRfxEvent(newEvent); // Cloned Event
		bq.setBq(newBq); // Cloned BQ
		bq.setSupplier(this.supplier);
		return bq;
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
		RfaSupplierBq other = (RfaSupplierBq) obj;
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

	public RfaSupplierBq(BigDecimal bidPrice, String suppCompanyName) {
		this.bidPrice = bidPrice;
		this.suppCompanyName = suppCompanyName;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RfpSupplierBq [taxDescription=" + taxDescription + ", additionalTax=" + additionalTax + ", grandTotal=" + grandTotal + ", totalAfterTax=" + totalAfterTax + " " + toLogString() + "]";
	}

}
