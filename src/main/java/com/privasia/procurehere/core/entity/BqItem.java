/**
 * 
 */
package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;

import com.privasia.procurehere.core.enums.PricingTypes;
import com.privasia.procurehere.core.enums.ProductItemType;
import com.privasia.procurehere.core.enums.TaxType;
import com.privasia.procurehere.core.enums.UnitPricingTypes;

/**
 * @author Ravi
 */
@MappedSuperclass
public class BqItem implements Serializable {

	private static final long serialVersionUID = 8446708672025636557L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@Column(name = "ITEM_NAME", nullable = false, length = 250)
	private String itemName;

	@Column(name = "ITEM_LEVEL", length = 2, nullable = false)
	private Integer level = 0;

	@Column(name = "SUB_ORDER", length = 2, nullable = false)
	private Integer order = 0;

	@ManyToOne(optional = true, cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinColumn(name = "UOM_ID", nullable = true)
	private Uom uom;

	@Column(name = "UNIT_QUANTITY", precision = 18, scale = 6, nullable = true)
	private BigDecimal quantity;

	@Column(name = "UNIT_PRICE", precision = 18, scale = 6)
	private BigDecimal unitPrice;

	@Enumerated(EnumType.STRING)
	@Column(name = "UNIT_PRICE_TYPE", nullable = true)
	private UnitPricingTypes unitPriceType;

	@Size(max = 2000)
	@Column(name = "ITEM_DESCRIPTION", nullable = true, length = 2000)
	private String itemDescription;

	@Enumerated(EnumType.STRING)
	@Column(name = "PRICE_TYPE", nullable = true)
	private PricingTypes priceType;

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

	@DecimalMax("999999999999.999999")
	@Column(name = "TOTAL_AMOUNT", precision = 18, scale = 6, nullable = true)
	private BigDecimal totalAmount;

	@DecimalMax("999999999999.999999")
	@Column(name = "TAX", precision = 18, scale = 6, nullable = true)
	private BigDecimal tax;

	@Enumerated(EnumType.STRING)
	@Column(name = "TAX_TYPE")
	private TaxType taxType;

	@DecimalMax("999999999999.999999")
	@Column(name = "TOTAL_AMOUNT_WITH_TAX", precision = 18, scale = 6, nullable = true)
	private BigDecimal totalAmountWithTax;

	@Transient
	private String position;

	@Transient
	private String eventId;

	@Transient
	private String taxDescription;

	@Transient
	private BigDecimal additionalTax;

	@Transient
	private String bqId;

	@Transient
	private List<String> columnTitles;

	@Transient
	private List<ProductCategory> productCategoryList;
	
	@Transient
	private List<CostCenter> costCenterList;
	
	@Transient
	private List<ProductItemType> productItemType;
	
	@Transient
	private List<BusinessUnit> businessUnitList;

	@Transient
	private String remarks;

	/**
	 * @return the remarks
	 */
	public String getRemarks() {
		return remarks;
	}

	/**
	 * @param remarks the remarks to set
	 */
	public void setRemarks(String remarks) {
		this.remarks = remarks;
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
	 * @return the uom
	 */
	public Uom getUom() {
		return uom;
	}

	/**
	 * @param uom the uom to set
	 */
	public void setUom(Uom uom) {
		this.uom = uom;
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
	 * @return the priceType
	 */
	public PricingTypes getPriceType() {
		return priceType;
	}

	/**
	 * @param priceType the priceType to set
	 */
	public void setPriceType(PricingTypes priceType) {
		this.priceType = priceType;
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
	 * @return the field5
	 */
	public String getField5() {
		return field5;
	}

	/**
	 * @param field5 the field5 to set
	 */
	public void setField5(String field5) {
		this.field5 = field5;
	}

	/**
	 * @return the field6
	 */
	public String getField6() {
		return field6;
	}

	/**
	 * @param field6 the field6 to set
	 */
	public void setField6(String field6) {
		this.field6 = field6;
	}

	/**
	 * @return the field7
	 */
	public String getField7() {
		return field7;
	}

	/**
	 * @param field7 the field7 to set
	 */
	public void setField7(String field7) {
		this.field7 = field7;
	}

	/**
	 * @return the field8
	 */
	public String getField8() {
		return field8;
	}

	/**
	 * @param field8 the field8 to set
	 */
	public void setField8(String field8) {
		this.field8 = field8;
	}

	/**
	 * @return the field9
	 */
	public String getField9() {
		return field9;
	}

	/**
	 * @param field9 the field9 to set
	 */
	public void setField9(String field9) {
		this.field9 = field9;
	}

	/**
	 * @return the field10
	 */
	public String getField10() {
		return field10;
	}

	/**
	 * @param field10 the field10 to set
	 */
	public void setField10(String field10) {
		this.field10 = field10;
	}

	/**
	 * @return the tax
	 */
	public BigDecimal getTax() {
		return tax;

	}

	/**
	 * @param tax the tax to set
	 */
	public void setTax(BigDecimal tax) {
		this.tax = tax;
	}

	/**
	 * @return the taxType
	 */
	public TaxType getTaxType() {
		return taxType;
	}

	/**
	 * @param taxType the taxType to set
	 */
	public void setTaxType(TaxType taxType) {
		this.taxType = taxType;
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
	 * @return the position
	 */
	public String getPosition() {
		return position;
	}

	/**
	 * @param position the position to set
	 */
	public void setPosition(String position) {
		this.position = position;
	}

	/**
	 * @return the eventId
	 */
	public String getEventId() {
		return eventId;
	}

	/**
	 * @param eventId the eventId to set
	 */
	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	/**
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
	 * @return the bqId
	 */
	public String getBqId() {
		return bqId;
	}

	/**
	 * @param bqId the bqId to set
	 */
	public void setBqId(String bqId) {
		this.bqId = bqId;
	}

	/**
	 * @return the columnTitles
	 */
	public List<String> getColumnTitles() {
		return columnTitles;
	}

	/**
	 * @param columnTitles the columnTitles to set
	 */
	public void setColumnTitles(List<String> columnTitles) {
		this.columnTitles = columnTitles;
	}

	/**
	 * @return the productCategoryList
	 */
	public List<ProductCategory> getProductCategoryList() {
		return productCategoryList;
	}

	/**
	 * @param productCategoryList the productCategoryList to set
	 */
	public void setProductCategoryList(List<ProductCategory> productCategoryList) {
		this.productCategoryList = productCategoryList;
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
	 * @return the unitPriceType
	 */
	public UnitPricingTypes getUnitPriceType() {
		return unitPriceType;
	}

	/**
	 * @param unitPriceType the unitPriceType to set
	 */
	public void setUnitPriceType(UnitPricingTypes unitPriceType) {
		this.unitPriceType = unitPriceType;
	}
	
	/**
	 * @return the costCenterList
	 */
	public List<CostCenter> getCostCenterList() {
		return costCenterList;
	}

	/**
	 * @param costCenterList the costCenterList to set
	 */
	public void setCostCenterList(List<CostCenter> costCenterList) {
		this.costCenterList = costCenterList;
	}

	/**
	 * @return the productItemType
	 */
	public List<ProductItemType> getProductItemType() {
		return productItemType;
	}

	/**
	 * @param productItemType the productItemType to set
	 */
	public void setProductItemType(List<ProductItemType> productItemType) {
		this.productItemType = productItemType;
	}

	/**
	 * @return the businessUnitList
	 */
	public List<BusinessUnit> getBusinessUnitList() {
		return businessUnitList;
	}

	/**
	 * @param businessUnitList the businessUnitList to set
	 */
	public void setBusinessUnitList(List<BusinessUnit> businessUnitList) {
		this.businessUnitList = businessUnitList;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "BqItem [id=" + id + ", itemName=" + itemName + ", level=" + level + ", order=" + order + ",  quantity=" + quantity + ", unitPrice=" + unitPrice + ", itemDescription=" + itemDescription + ", priceType=" + priceType + ", field1=" + field1 + ", field2=" + field2 + ", field3=" + field3 + ", field4=" + field4 + ", totalAmount=" + totalAmount + ", tax=" + tax + ", taxType=" + taxType + ", totalAmountWithTax=" + totalAmountWithTax + ", position=" + position + ", eventId=" + eventId + "]";
	}

	public String toLogString() {
		return "BqItem [id=" + id + ", itemName=" + itemName + ", level=" + level + ", order=" + order + ",  quantity=" + quantity + ", unitPrice=" + unitPrice + ", itemDescription=" + itemDescription + ", priceType=" + priceType + ", field1=" + field1 + ", field2=" + field2 + ", field3=" + field3 + ", field4=" + field4 + "]";
	}

}
