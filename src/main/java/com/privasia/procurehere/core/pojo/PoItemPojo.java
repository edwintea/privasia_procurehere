package com.privasia.procurehere.core.pojo;

import java.math.BigDecimal;
import java.util.Date;

import com.privasia.procurehere.core.enums.PoStatus;

public class PoItemPojo {

	private int srNo;

	private String id;

	private String poNumber;

	private String name;

	private PoStatus status;

	private String companyName;

	private String unitName;

	private String createdBy;

	private Date createdDate;

	private String orderedBy;

	private Date orderedDate;

	private Date poRevisedDate;

	private String productCode;

	private String productCategory;

	private String itemName;

	private String itemDescription;

	private String uom;

	public BigDecimal getLockedQuantity() {
		return lockedQuantity;
	}

	public void setLockedQuantity(BigDecimal lockedQuantity) {
		this.lockedQuantity = lockedQuantity;
	}

	private BigDecimal quantity;

	private BigDecimal lockedQuantity;

	private BigDecimal balanceQuantity;

	private String currency;

	private BigDecimal unitPrice;

	public BigDecimal getBalanceQuantity() {
		return balanceQuantity;
	}

	public void setBalanceQuantity(BigDecimal balanceQuantity) {
		this.balanceQuantity = balanceQuantity;
	}

	public BigDecimal getPricePerUnit() {
		return pricePerUnit;
	}

	public void setPricePerUnit(BigDecimal pricePerUnit) {
		this.pricePerUnit = pricePerUnit;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	private BigDecimal pricePerUnit;

	private BigDecimal totalAmount;

	private String itemTax;

	private BigDecimal taxAmount;

	private BigDecimal totalAmountWithTax;

	private String prId;

	private String createdDateStr;

	private String orderedDateStr;

	private String revisedDateStr;
	
	private Date actionDate;
	
	private BigDecimal grandTotal;
	
	private String vendorCode;
	
	private String actionDateStr;

	private Integer order = 0;

	private Integer level = 0;

	public String getItemIndicator() {
		return itemIndicator;
	}

	public void setItemIndicator(String itemIndicator) {
		this.itemIndicator = itemIndicator;
	}

	private String itemIndicator;
	
	public PoItemPojo() {
	}

	public PoItemPojo(String id, String poNumber) {
		this.id = id;
		this.poNumber = poNumber;
	}

	public PoItemPojo(String id, String poNumber, String name) {
		this.id = id;
		this.poNumber = poNumber;
		this.name = name;
	}

	public PoItemPojo(String id, String poNumber, String name, PoStatus status, String vendorCode, String companyName, String unitName,
			Date createdDate, Date actionDate, String createdBy, String orderedBy, Date orderedDate, Date poRevisedDate,
			String productCode, String productName, String itemName, String itemDescription, String uom,
			BigDecimal quantity, String currency, BigDecimal unitPrice, BigDecimal totalAmount, String itemTax,
			BigDecimal taxAmount, BigDecimal totalAmountWithTax, String prNumber, Integer order) {
		this.id = id;
		this.poNumber = poNumber;
		this.name = name;
		this.status = status;
		this.unitName = unitName;
		this.createdBy = createdBy;
		this.orderedBy = orderedBy;
		this.createdDate = createdDate;
		this.actionDate = actionDate;
		this.itemName = itemName;
		this.itemDescription = itemDescription;
		if(order != 0)
			this.currency = currency;
		this.unitPrice = unitPrice;
		this.totalAmount = totalAmount;
		this.itemTax = itemTax;
		this.taxAmount = taxAmount;
		this.totalAmountWithTax = totalAmountWithTax;
		this.companyName = companyName;
		this.quantity = quantity;
		this.productCode = productCode;
		this.uom = uom;
		this.prId = prNumber;
		this.orderedDate = orderedDate;
		this.poRevisedDate = poRevisedDate;
		this.productCategory = productName;
	}

	public PoItemPojo(String id, String poNumber, String name, PoStatus status, String vendorCode, String companyName, String unitName,
					  Date createdDate, Date actionDate, String createdBy, String orderedBy, Date orderedDate, Date poRevisedDate,
					  String productCode, String productName, String itemName, String itemDescription, String uom,
					  BigDecimal quantity,BigDecimal lockedQuantity,BigDecimal balanceQuantity, String currency, BigDecimal unitPrice,BigDecimal pricePerUnit, BigDecimal totalAmount, String itemTax,
					  BigDecimal taxAmount, BigDecimal totalAmountWithTax, String prNumber, Integer order, Integer level,String indicator) {
		this.id = id;
		this.poNumber = poNumber;
		this.name = name;
		this.status = status;
		this.unitName = unitName;
		this.createdBy = createdBy;
		this.orderedBy = orderedBy;
		this.createdDate = createdDate;
		this.actionDate = actionDate;
		this.itemName = itemName;
		this.itemDescription = itemDescription;
		if(order != 0)
			this.currency = currency;
		this.unitPrice = unitPrice;
		this.totalAmount = totalAmount;
		this.itemTax = itemTax;
		this.taxAmount = taxAmount;
		this.totalAmountWithTax = totalAmountWithTax;
		this.companyName = companyName;
		this.quantity = quantity;
		this.productCode = productCode;
		this.uom = uom;
		this.prId = prNumber;
		this.orderedDate = orderedDate;
		this.poRevisedDate = poRevisedDate;
		this.productCategory = productName;
		this.level = level;
		this.itemIndicator=indicator;
	}

	public int getSrNo() {
		return srNo;
	}

	public void setSrNo(int srNo) {
		this.srNo = srNo;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPoNumber() {
		return poNumber;
	}

	public void setPoNumber(String poNumber) {
		this.poNumber = poNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public PoStatus getStatus() {
		return status;
	}

	public void setStatus(PoStatus status) {
		this.status = status;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public String getOrderedBy() {
		return orderedBy;
	}

	public void setOrderedBy(String orderedBy) {
		this.orderedBy = orderedBy;
	}

	public Date getOrderedDate() {
		return orderedDate;
	}

	public void setOrderedDate(Date orderedDate) {
		this.orderedDate = orderedDate;
	}

	public Date getPoRevisedDate() {
		return poRevisedDate;
	}

	public void setPoRevisedDate(Date poRevisedDate) {
		this.poRevisedDate = poRevisedDate;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getProductCategory() {
		return productCategory;
	}

	public void setProductCategory(String productCategory) {
		this.productCategory = productCategory;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getItemDescription() {
		return itemDescription;
	}

	public void setItemDescription(String itemDescription) {
		this.itemDescription = itemDescription;
	}

	public String getUom() {
		return uom;
	}

	public void setUom(String uom) {
		this.uom = uom;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getItemTax() {
		return itemTax;
	}

	public void setItemTax(String itemTax) {
		this.itemTax = itemTax;
	}

	public BigDecimal getTaxAmount() {
		return taxAmount;
	}

	public void setTaxAmount(BigDecimal taxAmount) {
		this.taxAmount = taxAmount;
	}

	public BigDecimal getTotalAmountWithTax() {
		return totalAmountWithTax;
	}

	public void setTotalAmountWithTax(BigDecimal totalAmountWithTax) {
		this.totalAmountWithTax = totalAmountWithTax;
	}

	public String getPrId() {
		return prId;
	}

	public void setPrId(String prId) {
		this.prId = prId;
	}

	public String getCreatedDateStr() {
		return createdDateStr;
	}

	public void setCreatedDateStr(String createdDateStr) {
		this.createdDateStr = createdDateStr;
	}

	public String getOrderedDateStr() {
		return orderedDateStr;
	}

	public void setOrderedDateStr(String orderedDateStr) {
		this.orderedDateStr = orderedDateStr;
	}

	public String getRevisedDateStr() {
		return revisedDateStr;
	}

	public void setRevisedDateStr(String revisedDateStr) {
		this.revisedDateStr = revisedDateStr;
	}

	public Date getActionDate() {
		return actionDate;
	}

	public void setActionDate(Date actionDate) {
		this.actionDate = actionDate;
	}

	public BigDecimal getGrandTotal() {
		return grandTotal;
	}

	public void setGrandTotal(BigDecimal grandTotal) {
		this.grandTotal = grandTotal;
	}

	public String getVendorCode() {
		return vendorCode;
	}

	public void setVendorCode(String vendorCode) {
		this.vendorCode = vendorCode;
	}

	public String getActionDateStr() {
		return actionDateStr;
	}

	public void setActionDateStr(String actionDateStr) {
		this.actionDateStr = actionDateStr;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	
}
