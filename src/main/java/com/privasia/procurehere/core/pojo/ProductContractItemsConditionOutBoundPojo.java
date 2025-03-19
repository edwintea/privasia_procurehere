package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProductContractItemsConditionOutBoundPojo implements Serializable {

	private static final long serialVersionUID = -7010974092342023943L;

	@JsonProperty("Item_Number")
	private String itemNumber;

	@JsonProperty("Condition_Type")
	private String conditionType;

	@JsonProperty("Unit_price")
	private BigDecimal unitPrice;

	@JsonProperty("Currency")
	private String currency;

	@JsonProperty("Price_Per_Unit")
	private BigDecimal pricePerUnit;

	@JsonProperty("UOM")
	private String uom;

	public String getItemNumber() {
		return itemNumber;
	}

	public void setItemNumber(String itemNumber) {
		this.itemNumber = itemNumber;
	}

	public String getConditionType() {
		return conditionType;
	}

	public void setConditionType(String conditionType) {
		this.conditionType = conditionType;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public BigDecimal getPricePerUnit() {
		return pricePerUnit;
	}

	public void setPricePerUnit(BigDecimal pricePerUnit) {
		this.pricePerUnit = pricePerUnit;
	}

	public String getUom() {
		return uom;
	}

	public void setUom(String uom) {
		this.uom = uom;
	}

}
