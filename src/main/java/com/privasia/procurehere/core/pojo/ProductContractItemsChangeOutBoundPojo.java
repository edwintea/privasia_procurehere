package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class ProductContractItemsChangeOutBoundPojo implements Serializable {

	private static final long serialVersionUID = -6318020094939401097L;

	@JsonProperty("Item_Number")
	private String itemNumber;

	@JsonProperty("Product_Item")
	private String productItem;

	@JsonProperty("Product_Code")
	private String productCode;

	@JsonProperty("Product_Category")
	private String productGroup;

	@JsonProperty("Unit_Code")
	private String unitCode;

	@JsonProperty("Quantity")
	private BigDecimal quantity;

	@JsonProperty("UOM")
	private String uom;

	@JsonProperty("Unit_Price")
	private BigDecimal unitPrice;

	@JsonProperty("Price_Per_Unit")
	private BigDecimal pricePerUnit;

	@JsonProperty("Account_Assignment")
	private String accountAssignment;

	@JsonProperty("Delete_Indicator")
	private String deleteIndicator;

	public String getItemNumber() {
		return itemNumber;
	}

	public void setItemNumber(String itemNumber) {
		this.itemNumber = itemNumber;
	}

	public String getProductItem() {
		return productItem;
	}

	public void setProductItem(String productItem) {
		this.productItem = productItem;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getProductGroup() {
		return productGroup;
	}

	public void setProductGroup(String productGroup) {
		this.productGroup = productGroup;
	}

	public String getUnitCode() {
		return unitCode;
	}

	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
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

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	public BigDecimal getPricePerUnit() {
		return pricePerUnit;
	}

	public void setPricePerUnit(BigDecimal pricePerUnit) {
		this.pricePerUnit = pricePerUnit;
	}

	public String getAccountAssignment() {
		return accountAssignment;
	}

	public void setAccountAssignment(String accountAssignment) {
		this.accountAssignment = accountAssignment;
	}

	public String getDeleteIndicator() {
		return deleteIndicator;
	}

	public void setDeleteIndicator(String deleteIndicator) {
		this.deleteIndicator = deleteIndicator;
	}

}
