package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Nitin Otageri
 */
public class AwardDetailsErp2Pojo implements Serializable {

	private static final long serialVersionUID = -3140870884572390197L;

	@JsonProperty("PRItemPreqItem")
	private String itemNo;

	@JsonProperty("PRItemMaterial")
	private String itemCode;

	@JsonProperty("PRItemQuantity")
	private BigDecimal quantity;

	@JsonProperty("PRItemPreqPrice")
	private BigDecimal unitPrice;

	@JsonProperty("PRItemPriceUnit")
	private Integer itemUnit = 1;

	@JsonProperty("PRItemDesVendor")
	private String vendorCode;

	/**
	 * @return the itemNo
	 */
	public String getItemNo() {
		return itemNo;
	}

	/**
	 * @param itemNo the itemNo to set
	 */
	public void setItemNo(String itemNo) {
		this.itemNo = itemNo;
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
	 * @return the vendorCode
	 */
	public String getVendorCode() {
		return vendorCode;
	}

	/**
	 * @param vendorCode the vendorCode to set
	 */
	public void setVendorCode(String vendorCode) {
		this.vendorCode = vendorCode;
	}

	/**
	 * @return the itemCode
	 */
	public String getItemCode() {
		return itemCode;
	}

	/**
	 * @param itemCode the itemCode to set
	 */
	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	/**
	 * @return the itemUnit
	 */
	public Integer getItemUnit() {
		return itemUnit;
	}

	/**
	 * @param itemUnit the itemUnit to set
	 */
	public void setItemUnit(Integer itemUnit) {
		this.itemUnit = itemUnit;
	}

}
