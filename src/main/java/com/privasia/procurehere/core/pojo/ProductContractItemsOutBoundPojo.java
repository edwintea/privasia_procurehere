package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProductContractItemsOutBoundPojo implements Serializable {

	private static final long serialVersionUID = -6318020094939401097L;

	@JsonProperty("Item_Number")
	private String itemNumber;

	@JsonProperty("Product_Item")
	private String productItem;

	@JsonProperty("Product_Code")
	private String productCode;

	@JsonProperty("Unit_Code")
	private String unitCode;

	@JsonProperty("Product_Category")
	private String productGroup;

	@JsonProperty("Quantity")
	private BigDecimal quantity;

	@JsonProperty("UOM")
	private String uom;

	@JsonProperty("Unit_price")
	private BigDecimal unitPrice;

	@JsonProperty("Price_Per_Unit")
	private BigDecimal pricePerUnit;

	@JsonProperty("Account_Assignment")
	private String accountAssignment;



	public String getItemNumber() {
		return itemNumber;
	}

	public void setItemNumber(String itemNumber) {
		this.itemNumber = itemNumber;
	}

	/**
	 * @return the productItem
	 */
	public String getProductItem() {
		return productItem;
	}

	/**
	 * @param productItem the productItem to set
	 */
	public void setProductItem(String productItem) {
		this.productItem = productItem;
	}

	/**
	 * @return the productCode
	 */
	public String getProductCode() {
		return productCode;
	}

	/**
	 * @param productCode the productCode to set
	 */
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	/**
	 * @return the unitCode
	 */
	public String getUnitCode() {
		return unitCode;
	}

	/**
	 * @param unitCode the unitCode to set
	 */
	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
	}

	/**
	 * @return the productGroup
	 */
	public String getProductGroup() {
		return productGroup;
	}

	/**
	 * @param productGroup the productGroup to set
	 */
	public void setProductGroup(String productGroup) {
		this.productGroup = productGroup;
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
	 * @return the accountAssignment
	 */
	public String getAccountAssignment() {
		return accountAssignment;
	}

	/**
	 * @param accountAssignment the accountAssignment to set
	 */
	public void setAccountAssignment(String accountAssignment) {
		this.accountAssignment = accountAssignment;
	}

}
