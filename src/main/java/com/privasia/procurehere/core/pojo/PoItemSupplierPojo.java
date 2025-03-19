package com.privasia.procurehere.core.pojo;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.PoStatus;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;

public class PoItemSupplierPojo {
	

	private String id;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date createdDate;

	private String createdBy;

	private String currency;

	private String businessUnit;

	private String poNumber;

	private PoStatus status;

	private String description;

	private String buyerCompanyName;

	private int srNo;

	private String poCreatedby;

	private String unitName;

	private String poIds;

	private String createdDateStr;
	
	private Date acceptRejectDate;

	private String acceptRejectdateStr;
	
	private String name;
	
	private String itemName;
	
	private String itemDescription;
	
	private String itemTax;
	
	private BigDecimal taxAmount;
	
	private BigDecimal totalAmountWithTax;
	
	private BigDecimal totalAmount;
	
	private BigDecimal unitPrice;

	private BigDecimal pricePerUnit;
	
	private String companyName;
	
	private BigDecimal quantity;

	private BigDecimal lockedQuantity;

	private BigDecimal balanceQuantity;
	
	private String uom;
	
	private Integer order = 0;

	private Integer level = 0;

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public String getItemIndicator() {
		return itemIndicator;
	}

	public void setItemIndicator(String itemIndicator) {
		this.itemIndicator = itemIndicator;
	}

	private String buyerId;

	private  String itemIndicator;


	public BigDecimal getPricePerUnit() {
		return pricePerUnit;
	}

	public void setPricePerUnit(BigDecimal pricePerUnit) {
		this.pricePerUnit = pricePerUnit;
	}

	public BigDecimal getLockedQuantity() {
		return lockedQuantity;
	}

	public void setLockedQuantity(BigDecimal lockedQuantity) {
		this.lockedQuantity = lockedQuantity;
	}

	public BigDecimal getBalanceQuantity() {
		return balanceQuantity;
	}

	public void setBalanceQuantity(BigDecimal balanceQuantity) {
		this.balanceQuantity = balanceQuantity;
	}

	public PoItemSupplierPojo(String id, String poNumber, String name, PoStatus status, String companyName, String unitName, Date createdDate, Date acceptRejectDate, String itemName, String itemDescription, String uom, BigDecimal quantity,BigDecimal balanceQuantity,BigDecimal lockedQuantity, String currency, BigDecimal unitPrice,BigDecimal pricePerUnit, BigDecimal totalAmount, String itemTax, BigDecimal taxAmount, BigDecimal totalAmountWithTax, Integer order) {
		this.id = id;
		this.poNumber = poNumber;
		this.name = name;
		this.status = status;
		this.unitName = unitName;
		this.createdDate = createdDate;
		this.acceptRejectDate = acceptRejectDate;
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
		this.lockedQuantity = lockedQuantity;
		this.balanceQuantity = balanceQuantity;
		this.pricePerUnit = pricePerUnit;
		this.uom = uom;
	}

	//PH-4205

	public PoItemSupplierPojo(String id, String poNumber, String name, PoStatus status, String companyName, String unitName, Date createdDate, Date acceptRejectDate, String itemName, String itemDescription, String uom, BigDecimal quantity,BigDecimal lockedQuantity,BigDecimal balanceQuantity, String currency, BigDecimal unitPrice,BigDecimal pricePerUnit, BigDecimal totalAmount, String itemTax, BigDecimal taxAmount, BigDecimal totalAmountWithTax, Integer order, Integer level,String itemIndicator, String buyerId) {
		this.id = id;
		this.poNumber = poNumber;
		this.name = name;
		this.status = status;
		this.unitName = unitName;
		this.createdDate = createdDate;
		this.acceptRejectDate = acceptRejectDate;
		this.itemName = itemName;
		this.itemDescription = itemDescription;
		if(order != 0)
			this.currency = currency;
		this.unitPrice = unitPrice;
		this.pricePerUnit = pricePerUnit!= null?pricePerUnit:new BigDecimal(1);
		this.totalAmount = totalAmount;
		this.itemTax = itemTax;
		this.taxAmount = taxAmount;
		this.totalAmountWithTax = totalAmountWithTax;
		this.companyName = companyName;
		this.quantity = quantity;
		this.balanceQuantity = balanceQuantity!= null?balanceQuantity:new BigDecimal(0);
		this.lockedQuantity = lockedQuantity!= null?lockedQuantity:new BigDecimal(0);
		this.uom = uom;
		this.level = level;
		this.buyerId= buyerId;
		this.itemIndicator = itemIndicator;
	}

	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public Date getCreatedDate() {
		return createdDate;
	}


	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}


	public String getCreatedBy() {
		return createdBy;
	}


	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}


	public String getCurrency() {
		return currency;
	}


	public void setCurrency(String currency) {
		this.currency = currency;
	}


	public String getBusinessUnit() {
		return businessUnit;
	}


	public void setBusinessUnit(String businessUnit) {
		this.businessUnit = businessUnit;
	}


	public String getPoNumber() {
		return poNumber;
	}


	public void setPoNumber(String poNumber) {
		this.poNumber = poNumber;
	}


	public PoStatus getStatus() {
		return status;
	}


	public void setStatus(PoStatus status) {
		this.status = status;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public String getBuyerCompanyName() {
		return buyerCompanyName;
	}


	public void setBuyerCompanyName(String buyerCompanyName) {
		this.buyerCompanyName = buyerCompanyName;
	}


	public int getSrNo() {
		return srNo;
	}


	public void setSrNo(int srNo) {
		this.srNo = srNo;
	}


	public String getPoCreatedby() {
		return poCreatedby;
	}


	public void setPoCreatedby(String poCreatedby) {
		this.poCreatedby = poCreatedby;
	}


	public String getUnitName() {
		return unitName;
	}


	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}


	public String getPoIds() {
		return poIds;
	}


	public void setPoIds(String poIds) {
		this.poIds = poIds;
	}


	public String getCreatedDateStr() {
		return createdDateStr;
	}


	public void setCreatedDateStr(String createdDateStr) {
		this.createdDateStr = createdDateStr;
	}


	public Date getAcceptRejectDate() {
		return acceptRejectDate;
	}


	public void setAcceptRejectDate(Date acceptRejectDate) {
		this.acceptRejectDate = acceptRejectDate;
	}


	public String getAcceptRejectdateStr() {
		return acceptRejectdateStr;
	}


	public void setAcceptRejectdateStr(String acceptRejectdateStr) {
		this.acceptRejectdateStr = acceptRejectdateStr;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
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


	public BigDecimal getTotalAmount() {
		return totalAmount;
	}


	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}


	public BigDecimal getUnitPrice() {
		return unitPrice;
	}


	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}


	public String getCompanyName() {
		return companyName;
	}


	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}


	public BigDecimal getQuantity() {
		return quantity;
	}


	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}


	public String getUom() {
		return uom;
	}


	public void setUom(String uom) {
		this.uom = uom;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public String getBuyerId() {
		return buyerId;
	}

	public void setBuyerId(String buyerId) {
		this.buyerId = buyerId;
	}
}
