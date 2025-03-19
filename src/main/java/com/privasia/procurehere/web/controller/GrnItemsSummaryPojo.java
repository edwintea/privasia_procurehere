package com.privasia.procurehere.web.controller;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author pooja
 */
public class GrnItemsSummaryPojo implements Serializable {
	private static final long serialVersionUID = -4125724625603968054L;

	private String slno;
	private String itemName;
	private BigDecimal quantity;
	private String uom;
	private String unitPrice;
	private String itemDescription;
	private String totalAmount;
	private String taxAmount;
	private String totalAmountWithTax;
	private String currency;
	private String additionalTax;
	private String grandTotal;
	private String taxDescription;
	private String sumAmount;

	private BigDecimal sumTotalAmt;
	private BigDecimal sumTaxAmount;
	private String decimal;

	/**
	 * @return the slno
	 */
	public String getSlno() {
		return slno;
	}

	/**
	 * @param slno the slno to set
	 */
	public void setSlno(String slno) {
		this.slno = slno;
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
	public String getUnitPrice() {
		return unitPrice;
	}

	/**
	 * @param unitPrice the unitPrice to set
	 */
	public void setUnitPrice(String unitPrice) {
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
	 * @return the totalAmount
	 */
	public String getTotalAmount() {
		return totalAmount;
	}

	/**
	 * @param totalAmount the totalAmount to set
	 */
	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}

	/**
	 * @return the taxAmount
	 */
	public String getTaxAmount() {
		return taxAmount;
	}

	/**
	 * @param taxAmount the taxAmount to set
	 */
	public void setTaxAmount(String taxAmount) {
		this.taxAmount = taxAmount;
	}

	/**
	 * @return the totalAmountWithTax
	 */
	public String getTotalAmountWithTax() {
		return totalAmountWithTax;
	}

	/**
	 * @param totalAmountWithTax the totalAmountWithTax to set
	 */
	public void setTotalAmountWithTax(String totalAmountWithTax) {
		this.totalAmountWithTax = totalAmountWithTax;
	}

	/**
	 * @return the currency
	 */
	public String getCurrency() {
		return currency;
	}

	/**
	 * @param currency the currency to set
	 */
	public void setCurrency(String currency) {
		this.currency = currency;
	}

	/**
	 * @return the additionalTax
	 */
	public String getAdditionalTax() {
		return additionalTax;
	}

	/**
	 * @param additionalTax the additionalTax to set
	 */
	public void setAdditionalTax(String additionalTax) {
		this.additionalTax = additionalTax;
	}

	/**
	 * @return the grandTotal
	 */
	public String getGrandTotal() {
		return grandTotal;
	}

	/**
	 * @param grandTotal the grandTotal to set
	 */
	public void setGrandTotal(String grandTotal) {
		this.grandTotal = grandTotal;
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
	 * @return the sumAmount
	 */
	public String getSumAmount() {
		return sumAmount;
	}

	/**
	 * @param sumAmount the sumAmount to set
	 */
	public void setSumAmount(String sumAmount) {
		this.sumAmount = sumAmount;
	}

	/**
	 * @return the sumTotalAmt
	 */
	public BigDecimal getSumTotalAmt() {
		return sumTotalAmt;
	}

	/**
	 * @param sumTotalAmt the sumTotalAmt to set
	 */
	public void setSumTotalAmt(BigDecimal sumTotalAmt) {
		this.sumTotalAmt = sumTotalAmt;
	}

	/**
	 * @return the sumTaxAmount
	 */
	public BigDecimal getSumTaxAmount() {
		return sumTaxAmount;
	}

	/**
	 * @param sumTaxAmount the sumTaxAmount to set
	 */
	public void setSumTaxAmount(BigDecimal sumTaxAmount) {
		this.sumTaxAmount = sumTaxAmount;
	}

	/**
	 * @return the decimal
	 */
	public String getDecimal() {
		return decimal;
	}

	/**
	 * @param decimal the decimal to set
	 */
	public void setDecimal(String decimal) {
		this.decimal = decimal;
	}
}
