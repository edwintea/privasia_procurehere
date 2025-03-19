package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author sudesha
 */
public class RequestBqItemPojo implements Serializable {

	private static final long serialVersionUID = 3597002524187211427L;

	private String level;
	private String description;
	private String itemName;
	private String uom;
	private BigDecimal quantity;
	private BigDecimal unitPrice;
	private BigDecimal amount;
	private BigDecimal taxAmt;
	private BigDecimal totalAmt;
	private String totalAmtS;
	private String priceType;
	private String decimal;
	private String imgPath;
	private BigDecimal totalAfterTax;
	private BigDecimal unitBudgetPrice;

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
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

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getTaxAmt() {
		return taxAmt;
	}

	public void setTaxAmt(BigDecimal taxAmt) {
		this.taxAmt = taxAmt;
	}

	public BigDecimal getTotalAmt() {
		return totalAmt;
	}

	public void setTotalAmt(BigDecimal totalAmt) {
		this.totalAmt = totalAmt;
	}

	public String getTotalAmtS() {
		return totalAmtS;
	}

	public void setTotalAmtS(String totalAmtS) {
		this.totalAmtS = totalAmtS;
	}

	public String getPriceType() {
		return priceType;
	}

	public void setPriceType(String priceType) {
		this.priceType = priceType;
	}

	public String getDecimal() {
		return decimal;
	}

	public void setDecimal(String decimal) {
		this.decimal = decimal;
	}

	public String getImgPath() {
		return imgPath;
	}

	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}

	public BigDecimal getTotalAfterTax() {
		return totalAfterTax;
	}

	public void setTotalAfterTax(BigDecimal totalAfterTax) {
		this.totalAfterTax = totalAfterTax;
	}

	public BigDecimal getUnitBudgetPrice() {
		return unitBudgetPrice;
	}

	public void setUnitBudgetPrice(BigDecimal unitBudgetPrice) {
		this.unitBudgetPrice = unitBudgetPrice;
	}

}
