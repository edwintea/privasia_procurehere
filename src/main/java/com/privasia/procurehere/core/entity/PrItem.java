package com.privasia.procurehere.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.privasia.procurehere.core.pojo.DraftEventPojo;
import com.privasia.procurehere.core.utils.Global;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.Digits;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author parveen
 */
@Entity
@Table(name = "PROC_PR_ITEM")
@JsonIgnoreProperties(ignoreUnknown = true)
@SqlResultSetMappings({ @SqlResultSetMapping(name = "eventExcelReportData", classes = { @ConstructorResult(targetClass = DraftEventPojo.class, 
columns = { @ColumnResult(name = "id"),
		@ColumnResult(name = "grandTotal"), @ColumnResult(name = "prPushDate"),
		@ColumnResult(name = "teamMember"), @ColumnResult(name = "addressTitle"), @ColumnResult(name = "line1"), 
		@ColumnResult(name = "memberType"), @ColumnResult(name = "sysEventId"), @ColumnResult(name = "eventName"), 
		@ColumnResult(name = "eventDecimal"), @ColumnResult(name = "viewUnmaskSupplerName", type = Boolean.class), @ColumnResult(name = "referenceNumber"), 
		@ColumnResult(name = "eventDescription"), @ColumnResult(name = "ownerName"), @ColumnResult(name = "publishDate"), 
		@ColumnResult(name = "eventStart"), @ColumnResult(name = "eventEnd"), @ColumnResult(name = "deliveryDate"), 
		@ColumnResult(name = "visibility"), @ColumnResult(name = "validityDays"), @ColumnResult(name = "eventType"),
		@ColumnResult(name = "currencyName"), @ColumnResult(name = "unitName"), @ColumnResult(name = "costCenter"), 
		@ColumnResult(name = "budgetAmount"), @ColumnResult(name = "estimatedBudget"),
		@ColumnResult(name = "historicAmount"), @ColumnResult(name = "participationFees"), 
		@ColumnResult(name = "deposit"), @ColumnResult(name = "awardDate"), 
		@ColumnResult(name = "eventPushDate"), @ColumnResult(name = "concludeDate"),
		@ColumnResult(name = "templateName"), @ColumnResult(name = "procurementMethod"), 
		@ColumnResult(name = "procurementCategories"), @ColumnResult(name = "status"),
		@ColumnResult(name = "leadingSupplier"), @ColumnResult(name = "leadingAmount"), 
		@ColumnResult(name = "invitedSupplierCount"), @ColumnResult(name = "submittedSupplierCount"), 
		@ColumnResult(name = "acceptedSupplierCount"), @ColumnResult(name = "eventCategories"),
		@ColumnResult(name = "preViewSupplierCount"), @ColumnResult(name = "rejectedSupplierCount"), 
		@ColumnResult(name = "disqualifedSuppliers"), @ColumnResult(name = "unMaskedUser"), 
		@ColumnResult(name = "groupCode"), @ColumnResult(name = "groupCode"), 
		
		
		@ColumnResult(name = "auctionType") }) }), @SqlResultSetMapping(name = "eventCsvReportData", classes = { @ConstructorResult(targetClass = DraftEventPojo.class,
		columns = { @ColumnResult(name = "grandTotal"), @ColumnResult(name = "id"), @ColumnResult(name = "prPushDate"), 
				@ColumnResult(name = "teamMember"), @ColumnResult(name = "addressTitle"), @ColumnResult(name = "line1"), 
				@ColumnResult(name = "memberType"), @ColumnResult(name = "sysEventId"), @ColumnResult(name = "eventName"),
				
				@ColumnResult(name = "eventDecimal"), @ColumnResult(name = "viewUnmaskSupplerName", type = Boolean.class),
				@ColumnResult(name = "referenceNumber"), @ColumnResult(name = "eventDescription"), @ColumnResult(name = "ownerName"),
				
				@ColumnResult(name = "publishDate"), @ColumnResult(name = "eventStart"), @ColumnResult(name = "eventEnd"),
				@ColumnResult(name = "deliveryDate"), @ColumnResult(name = "visibility"), @ColumnResult(name = "validityDays"),
				
				@ColumnResult(name = "eventType"), @ColumnResult(name = "currencyName"), @ColumnResult(name = "unitName"),
				@ColumnResult(name = "costCenter"), @ColumnResult(name = "budgetAmount"), @ColumnResult(name = "estimatedBudget"), 
				@ColumnResult(name = "historicAmount"), @ColumnResult(name = "participationFees"), @ColumnResult(name = "deposit"),
				@ColumnResult(name = "awardDate"), @ColumnResult(name = "eventPushDate"), @ColumnResult(name = "concludeDate"),
				@ColumnResult(name = "templateName"), @ColumnResult(name = "procurementMethod"), @ColumnResult(name = "procurementCategories"), 
				
				@ColumnResult(name = "status"), @ColumnResult(name = "leadingSupplier"), @ColumnResult(name = "leadingAmount"), 
				@ColumnResult(name = "invitedSupplierCount"), @ColumnResult(name = "submittedSupplierCount"),
				@ColumnResult(name = "acceptedSupplierCount"), @ColumnResult(name = "eventCategories"), @ColumnResult(name = "preViewSupplierCount"), 
				@ColumnResult(name = "rejectedSupplierCount"), @ColumnResult(name = "disqualifedSuppliers"), @ColumnResult(name = "unMaskedUser"),
				@ColumnResult(name = "auctionType"), @ColumnResult(name="groupCode") }) }) })
public class PrItem implements Serializable {
	private static final Logger LOG = LogManager.getLogger(Global.ADMIN_LOG);

	public interface PurchaseItem {
	}

	private static final long serialVersionUID = -3598599178346654844L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@Column(name = "ITEM_NAME", length = 250)
	private String itemName;

	@Column(name = "ITEM_LEVEL", length = 2, nullable = false)
	private Integer level = 0;

	@Column(name = "SUB_ORDER", length = 2, nullable = false)
	private Integer order = 0;

	@Column(name = "ITEM_QUANTITY", nullable = true, precision = 22, scale = 6)
	private BigDecimal quantity;

	@Column(name = "UNIT_PRICE", nullable = true, precision = 22, scale = 6)
	private BigDecimal unitPrice;

	@Column(name = "PRICE_PER_UNIT", nullable = true, precision = 22, scale = 6)
	private BigDecimal pricePerUnit;

	@Column(name = "ITEM_DESCRIPTION", length = 2100)
	private String itemDescription;

	// @JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "PR_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_PR_ITEM_PR_ID"))
	private Pr pr;

	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "PARENT_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_PR_ITEM_PARENT_ID"))
	private PrItem parent;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
	@OrderBy("order")
	private List<PrItem> children;

	@Column(name = "ITEM_TAX", nullable = true, length = 20)
	private String itemTax;

	@Digits(integer = 16, fraction = 8, message = "{totalamount.length.error}", groups = { PurchaseItem.class })
	@Column(name = "TOTAL_AMOUNT", nullable = true, precision = 22, scale = 6)
	private BigDecimal totalAmount;

	@Column(name = "TAX_AMOUNT", nullable = true, precision = 22, scale = 6)
	private BigDecimal taxAmount;

	@Column(name = "TOTAL_AMT_WITH_TAX", nullable = true, precision = 22, scale = 6)
	private BigDecimal totalAmountWithTax;

	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "TENANT_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_PR_ITEM_TENANT_ID"))
	private Buyer buyer;

	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "UOM_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_PR_UOM_ID"))
	private Uom unit;

	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "PRODUCT_CATEGORY_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_PR_ITEM_CAT_ID"))
	private ProductCategory productCategory;

	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "PRODUCT_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_PR_ITEM_PROD_ID"))
	private ProductItem product;

	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "CONTRACT_PRODUCT_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_PR_CONTRACT_PROD_ID"))
	private ProductContractItems productContractItem;

	@Column(name = "FIELD1", nullable = true, length = 100)
	private String field1;

	@Column(name = "FIELD2", nullable = true, length = 100)
	private String field2;

	@Column(name = "FIELD3", nullable = true, length = 100)
	private String field3;

	@Column(name = "FIELD4", nullable = true, length = 100)
	private String field4;

	@Column(name = "FIELD5", nullable = true, length = 100)
	private String field5;

	@Column(name = "FIELD6", nullable = true, length = 100)
	private String field6;

	@Column(name = "FIELD7", nullable = true, length = 100)
	private String field7;

	@Column(name = "FIELD8", nullable = true, length = 100)
	private String field8;

	@Column(name = "FIELD9", nullable = true, length = 100)
	private String field9;

	@Column(name = "FIELD10", nullable = true, length = 100)
	private String field10;

	@Transient
	private String uom;

	// to check item is free text or selected
	@Column(name = "FREE_ITEM_TEXT_ENTERED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean freeTextItemEntered;

	// for validation on Pr item screen
	@Transient
	private String itemId;

	// it is for save pr item with supplier
	@Transient
	private String supplierId;

	// used for ERP Integration only
	@Transient
	private String costCenter;

	// used for ERP Integration only
	@Transient
	private String purchaseGroup;

	@Transient
	private String dateTimeRange;

	@Transient
	// used only for SAP Integration
	private String itemContractReferenceNumber;

	@Transient
	// used only for SAP Integration
	private String storageLocation;

	@Transient
	// used only for SAP Integration
	private String contractReferenceNumber;

	public PrItem createShallowCopy() {
		PrItem ic = new PrItem();
		// ic.setParent(getParent());
		ic.setPr(getPr());
		ic.setItemDescription(getItemDescription());
		// ic.setUom(getUom());
		ic.setQuantity(getQuantity());
		ic.setItemName(getItemName());
		ic.setUnitPrice(getUnitPrice());
		ic.setPricePerUnit(getPricePerUnit());
		ic.setLevel(getLevel());
		ic.setOrder(getOrder());
		ic.setId(getId());
		ic.setItemTax(getItemTax());
		ic.setTotalAmount(getTotalAmount());
		ic.setTaxAmount(getTaxAmount());
		ic.setTotalAmountWithTax(getTotalAmountWithTax());
		ic.setProduct(getProduct() != null ? getProduct().createShallowCopy() : null);
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

		ic.setFreeTextItemEntered(getFreeTextItemEntered());

		/*
		 * if (ic.getFreeTextItemEntered() != null && ic.getFreeTextItemEntered()) { if
		 * (StringUtils.checkString(getItemDescription()).length() > 0) ic.setItemName(getItemDescription()); }
		 */

		try {
			if (this.getUnit() != null) {
				ic.setUom(this.getUnit().getUom());
				ic.setUnit(this.getUnit().createShallowCopy());
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}

		try {
			if (this.getProductCategory() != null) {

				ic.setProductCategory(this.getProductCategory());

			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}

		return ic;
	}

	public PrItem createMobileShallowCopy() {
		PrItem ic = new PrItem();
		ic.setItemDescription(getItemDescription());
		ic.setQuantity(getQuantity());
		ic.setItemName(getItemName());
		ic.setUnitPrice(getUnitPrice());
		ic.setPricePerUnit(getPricePerUnit());
		ic.setLevel(getLevel());
		ic.setOrder(getOrder());
		ic.setId(getId());
		try {
			if (this.getUnit() != null) {
				ic.setUom(this.getUnit().getUom());
				ic.setUom(this.getUnit().getUom());
			}
		} catch (Exception e) {
		}
		try {
			if (this.getProductCategory() != null) {
				ic.setProductCategory(this.getProductCategory().createShallowCopy());
			}
		} catch (Exception e) {
		}
		// ic.setUom(getProduct() != null ? (getProduct().getUom() != null ? getProduct().getUom().getUom() : null) :
		// null);
		ic.setTotalAmountWithTax(getTotalAmountWithTax());
		return ic;
	}

	public PrItem copyFrom() {
		PrItem newPrItem = new PrItem();
		// newPrItem.setPr(getPr());
		newPrItem.setItemDescription(getItemDescription());
		newPrItem.setQuantity(getQuantity());
		newPrItem.setItemName(getItemName());
		newPrItem.setUnitPrice(getUnitPrice());
		newPrItem.setPricePerUnit(getPricePerUnit());
		newPrItem.setLevel(getLevel());
		newPrItem.setOrder(getOrder());
		// newPrItem.setId(getId());
		newPrItem.setItemTax(getItemTax());
		newPrItem.setTotalAmount(getTotalAmount());
		newPrItem.setTaxAmount(getTaxAmount());
		newPrItem.setTotalAmountWithTax(getTotalAmountWithTax());
		newPrItem.setProduct(getProduct() != null ? getProduct().createShallowCopy() : null);
		newPrItem.setField1(getField1());
		newPrItem.setField2(getField2());
		newPrItem.setField3(getField3());
		newPrItem.setField4(getField4());
		newPrItem.setField5(getField5());
		newPrItem.setField6(getField6());
		newPrItem.setField7(getField7());
		newPrItem.setField8(getField8());
		newPrItem.setField9(getField9());
		newPrItem.setField10(getField10());

		newPrItem.setBuyer(getBuyer());
		newPrItem.setFreeTextItemEntered(getFreeTextItemEntered());
		try {
			if (this.getUnit() != null) {
				newPrItem.setUom(this.getUnit().getUom());
				newPrItem.setUnit(this.getUnit().createShallow());
			}
		} catch (Exception e) {
			LOG.info(e.getMessage(), e);
		}

		if (getProduct() != null) {
			LOG.info("not empty product");
			if (getProduct().getProductCategory() != null)
				newPrItem.setProductCategory(getProduct().getProductCategory());
		} else {
			LOG.info("empty product");
		}

		try {
			if (this.getProductCategory() != null) {
				newPrItem.setProductCategory(this.getProductCategory());
			}
		} catch (Exception e) {
			LOG.info(e.getMessage(), e);
		}
		return newPrItem;
	}

	public ProductCategory getProductCategory() {
		return productCategory;
	}

	public void setProductCategory(ProductCategory productCategory) {
		this.productCategory = productCategory;
	}

	/**
	 * @return the pr
	 */
	public Pr getPr() {
		return pr;
	}

	/**
	 * @param pr the pr to set
	 */
	public void setPr(Pr pr) {
		this.pr = pr;
	}

	/**
	 * @return the parent
	 */
	public PrItem getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(PrItem parent) {
		this.parent = parent;
	}

	/**
	 * @return the children
	 */
	public List<PrItem> getChildren() {
		return children;
	}

	/**
	 * @param children the children to set
	 */
	public void setChildren(List<PrItem> children) {
		this.children = children;
	}

	/**
	 * @return the itemTax
	 */
	public String getItemTax() {
		return itemTax;
	}

	/**
	 * @param itemTax the itemTax to set
	 */
	public void setItemTax(String itemTax) {
		this.itemTax = itemTax;
	}

	/**
	 * @return the totalAmount
	 */
	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	/**
	 * @param totalAmount the totalAmount to set
	 */
	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	/**
	 * @return the taxAmount
	 */
	public BigDecimal getTaxAmount() {
		return taxAmount;
	}

	/**
	 * @param taxAmount the taxAmount to set
	 */
	public void setTaxAmount(BigDecimal taxAmount) {
		this.taxAmount = taxAmount;
	}

	/**
	 * @return the totalAmountWithTax
	 */
	public BigDecimal getTotalAmountWithTax() {
		return totalAmountWithTax;
	}

	/**
	 * @param totalAmountWithTax the totalAmountWithTax to set
	 */
	public void setTotalAmountWithTax(BigDecimal totalAmountWithTax) {
		this.totalAmountWithTax = totalAmountWithTax;
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
	 * @return the quantity
	 */
	public BigDecimal getQuantity() {
		return quantity;
	}

	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	/**
	 * @return the unitPrice
	 */
	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	/**
	 * @param unitPrice the unitPrice to set
	 */
	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	/**
	 * @return the pricePerUnit
	 */
	public BigDecimal getPricePerUnit() {
		return pricePerUnit;
	}

	/**
	 * @param pricePerUnit the pricePerUnit to set
	 */
	public void setPricePerUnit(BigDecimal pricePerUnit) {
		this.pricePerUnit = pricePerUnit;
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
	 * @return the product
	 */
	public ProductItem getProduct() {
		return product;
	}

	/**
	 * @param product the product to set
	 */
	public void setProduct(ProductItem product) {
		this.product = product;
	}

	/**
	 * @return the field1
	 */
	public String getField1() {
		return field1;
	}

	/**
	 * @param field1 the field1 to set
	 */
	public void setField1(String field1) {
		this.field1 = field1;
	}

	/**
	 * @return the field2
	 */
	public String getField2() {
		return field2;
	}

	/**
	 * @param field2 the field2 to set
	 */
	public void setField2(String field2) {
		this.field2 = field2;
	}

	/**
	 * @return the field3
	 */
	public String getField3() {
		return field3;
	}

	/**
	 * @param field3 the field3 to set
	 */
	public void setField3(String field3) {
		this.field3 = field3;
	}

	/**
	 * @return the field4
	 */
	public String getField4() {
		return field4;
	}

	/**
	 * @param field4 the field4 to set
	 */
	public void setField4(String field4) {
		this.field4 = field4;
	}

	/**
	 * @return the uom
	 */
	public String getUom() {
		return uom;
	}

	/**
	 * @param uom the uom to set
	 */
	public void setUom(String uom) {
		this.uom = uom;
	}

	/**
	 * @return the freeTextItemEntered
	 */
	public Boolean getFreeTextItemEntered() {
		return freeTextItemEntered;
	}

	/**
	 * @param freeTextItemEntered the freeTextItemEntered to set
	 */
	public void setFreeTextItemEntered(Boolean freeTextItemEntered) {
		this.freeTextItemEntered = freeTextItemEntered;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	/**
	 * @return the unit
	 */
	public Uom getUnit() {
		return unit;
	}

	/**
	 * @param unit the unit to set
	 */
	public void setUnit(Uom unit) {
		this.unit = unit;
	}

	/**
	 * @return the supplierId
	 */
	public String getSupplierId() {
		return supplierId;
	}

	/**
	 * @param supplierId the supplierId to set
	 */
	public void setSupplierId(String supplierId) {
		this.supplierId = supplierId;
	}

	public String getField5() {
		return field5;
	}

	public void setField5(String field5) {
		this.field5 = field5;
	}

	public String getField6() {
		return field6;
	}

	public void setField6(String field6) {
		this.field6 = field6;
	}

	public String getField7() {
		return field7;
	}

	public void setField7(String field7) {
		this.field7 = field7;
	}

	public String getField8() {
		return field8;
	}

	public void setField8(String field8) {
		this.field8 = field8;
	}

	public String getField9() {
		return field9;
	}

	public void setField9(String field9) {
		this.field9 = field9;
	}

	public String getField10() {
		return field10;
	}

	public void setField10(String field10) {
		this.field10 = field10;
	}

	/**
	 * @return the costCenter
	 */
	public String getCostCenter() {
		return costCenter;
	}

	/**
	 * @param costCenter the costCenter to set
	 */
	public void setCostCenter(String costCenter) {
		this.costCenter = costCenter;
	}

	/**
	 * @return the purchaseGroup
	 */
	public String getPurchaseGroup() {
		return purchaseGroup;
	}

	/**
	 * @param purchaseGroup the purchaseGroup to set
	 */
	public void setPurchaseGroup(String purchaseGroup) {
		this.purchaseGroup = purchaseGroup;
	}

	public ProductContractItems getProductContractItem() {
		return productContractItem;
	}

	public void setProductContractItem(ProductContractItems productContractItem) {
		this.productContractItem = productContractItem;
	}

	/**
	 * @return the dateTimeRange
	 */
	public String getDateTimeRange() {
		return dateTimeRange;
	}

	/**
	 * @param dateTimeRange the dateTimeRange to set
	 */
	public void setDateTimeRange(String dateTimeRange) {
		this.dateTimeRange = dateTimeRange;
	}

	/**
	 * @return the itemContractReferenceNumber
	 */
	public String getItemContractReferenceNumber() {
		return itemContractReferenceNumber;
	}

	/**
	 * @param itemContractReferenceNumber the itemContractReferenceNumber to set
	 */
	public void setItemContractReferenceNumber(String itemContractReferenceNumber) {
		this.itemContractReferenceNumber = itemContractReferenceNumber;
	}

	/**
	 * @return the storageLocation
	 */
	public String getStorageLocation() {
		return storageLocation;
	}

	/**
	 * @param storageLocation the storageLocation to set
	 */
	public void setStorageLocation(String storageLocation) {
		this.storageLocation = storageLocation;
	}

	/**
	 * @return the contractReferenceNumber
	 */
	public String getContractReferenceNumber() {
		return contractReferenceNumber;
	}

	/**
	 * @param contractReferenceNumber the contractReferenceNumber to set
	 */
	public void setContractReferenceNumber(String contractReferenceNumber) {
		this.contractReferenceNumber = contractReferenceNumber;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((field1 == null) ? 0 : field1.hashCode());
		result = prime * result + ((field2 == null) ? 0 : field2.hashCode());
		result = prime * result + ((field3 == null) ? 0 : field3.hashCode());
		result = prime * result + ((field4 == null) ? 0 : field4.hashCode());
		result = prime * result + ((field5 == null) ? 0 : field5.hashCode());
		result = prime * result + ((field6 == null) ? 0 : field6.hashCode());
		result = prime * result + ((field7 == null) ? 0 : field7.hashCode());
		result = prime * result + ((field8 == null) ? 0 : field8.hashCode());
		result = prime * result + ((field9 == null) ? 0 : field9.hashCode());
		result = prime * result + ((field10 == null) ? 0 : field10.hashCode());

		result = prime * result + ((itemDescription == null) ? 0 : itemDescription.hashCode());
		result = prime * result + ((itemName == null) ? 0 : itemName.hashCode());
		result = prime * result + ((level == null) ? 0 : level.hashCode());
		result = prime * result + ((order == null) ? 0 : order.hashCode());
		result = prime * result + ((quantity == null) ? 0 : quantity.hashCode());
		result = prime * result + ((unitPrice == null) ? 0 : unitPrice.hashCode());
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
		PrItem other = (PrItem) obj;

		if (field1 == null) {
			if (other.field1 != null)
				return false;
		} else if (!field1.equals(other.field1))
			return false;

		if (field2 == null) {
			if (other.field2 != null)
				return false;
		} else if (!field2.equals(other.field2))
			return false;

		if (field3 == null) {
			if (other.field3 != null)
				return false;
		} else if (!field3.equals(other.field3))
			return false;

		if (field4 == null) {
			if (other.field4 != null)
				return false;
		} else if (!field4.equals(other.field4))
			return false;

		if (field5 == null) {
			if (other.field5 != null)
				return false;
		} else if (!field5.equals(other.field5))
			return false;

		if (field6 == null) {
			if (other.field6 != null)
				return false;
		} else if (!field6.equals(other.field6))
			return false;

		if (field7 == null) {
			if (other.field7 != null)
				return false;
		} else if (!field7.equals(other.field7))
			return false;

		if (field8 == null) {
			if (other.field8 != null)
				return false;
		} else if (!field8.equals(other.field8))
			return false;

		if (field9 == null) {
			if (other.field9 != null)
				return false;
		} else if (!field9.equals(other.field9))
			return false;

		if (field10 == null) {
			if (other.field10 != null)
				return false;
		} else if (!field10.equals(other.field10))
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
		if (order == null) {
			if (other.order != null)
				return false;
		} else if (!order.equals(other.order))
			return false;
		if (quantity == null) {
			if (other.quantity != null)
				return false;
		} else if (!quantity.equals(other.quantity))
			return false;

		if (unitPrice == null) {
			if (other.unitPrice != null)
				return false;
		} else if (!unitPrice.equals(other.unitPrice))
			return false;

		// Added pricePerUnit comparison
		if (pricePerUnit == null) {
			if (other.pricePerUnit != null)
				return false;
		} else if (!pricePerUnit.equals(other.pricePerUnit))
			return false;

		return true;
    }

	public String toLogString() {
		return "PrItem [itemName=" + itemName + ", level=" + level + ", order=" + order + ", quantity=" + quantity + ", unitPrice=" + unitPrice + ", pricePerUnit=" + pricePerUnit + ", itemDescription=" + itemDescription + ", itemTax=" + itemTax + ", totalAmount=" + totalAmount + ", taxAmount=" + taxAmount + ", totalAmountWithTax=" + totalAmountWithTax + ", field1=" + field1 + ", field2=" + field2 + ", field3=" + field3 + ", field4=" + field4 + "]";
	}

}
