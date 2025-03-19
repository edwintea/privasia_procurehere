package com.privasia.procurehere.core.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.privasia.procurehere.core.enums.ProductItemType;

/**
 * @author Nitin Otageri
 */
@ApiModel(description = "Items")
public class PoItemsPojo implements Serializable {

	private static final long serialVersionUID = 325257718954204640L;

	@ApiModelProperty(notes = "Item No", required = true)
	private String itemNo;

	@ApiModelProperty(notes = "Item Code", required = true)
	private String itemCode;

	@ApiModelProperty(notes = "Item Name", required = true)
	private String itemName;

	@ApiModelProperty(notes = "Item Description", required = false)
	private String itemDescription;

	@ApiModelProperty(notes = "Item Category", required = true)
	private String itemCategory;

	@ApiModelProperty(notes = "UOM", required = true)
	private String uom;

	@ApiModelProperty(notes = "Business Unit Code", required = true)
	private String businessUnit;

	@ApiModelProperty(notes = "Cost Center Code", required = false)
	private String costCenter;

	@ApiModelProperty(notes = "Quantity", allowableValues = "range[1, 5]", required = true)
	private BigDecimal quantity;

	@ApiModelProperty(notes = "Locked Quantity", allowableValues = "range[1, 5]", required = true)
	private BigDecimal lockedQuantity;

	@ApiModelProperty(notes = "Balance Quantity", allowableValues = "range[1, 5]", required = true)
	private BigDecimal balanceQuantity;

	@ApiModelProperty(notes = "Unit Price", required = true)
	private BigDecimal unitPrice;

	@ApiModelProperty(notes = "Price Per Unit", required = true)
	private BigDecimal pricePerPrice;

	@ApiModelProperty(notes = "Total Amount", required = true)
	private BigDecimal totalAmount;

	@ApiModelProperty(notes = "Tax Value (in % e.g. 8.5)", required = false)
	private String itemTax;

	@ApiModelProperty(notes = "Tax Amount", required = false)
	private BigDecimal taxAmount;

	@ApiModelProperty(notes = "Total Amount With Tax", required = true)
	private BigDecimal totalAmountWithTax;

	@ApiModelProperty(notes = "Item Type - MATERIAL or SERVICE", required = true)
	private ProductItemType itemType;

	@ApiModelProperty(notes = "Delivery Address", required = false)
	private AddressPojo deliveryAddress;

	@ApiModelProperty(notes = "Delivery Date in YYYYMMDD format", required = false)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyyMMdd")
	private Date deliveryDate;

	@ApiModelProperty(notes = "Delivery Receiver", required = false)
	private String deliveryReceiver;

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
	 * @return the itemCategory
	 */
	public String getItemCategory() {
		return itemCategory;
	}

	/**
	 * @param itemCategory the itemCategory to set
	 */
	public void setItemCategory(String itemCategory) {
		this.itemCategory = itemCategory;
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
	 * @return the deliveryAddress
	 */
	public AddressPojo getDeliveryAddress() {
		return deliveryAddress;
	}

	/**
	 * @param deliveryAddress the deliveryAddress to set
	 */
	public void setDeliveryAddress(AddressPojo deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}

	/**
	 * @return the deliveryDate
	 */
	public Date getDeliveryDate() {
		return deliveryDate;
	}

	/**
	 * @param deliveryDate the deliveryDate to set
	 */
	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	/**
	 * @return the deliveryReceiver
	 */
	public String getDeliveryReceiver() {
		return deliveryReceiver;
	}

	/**
	 * @param deliveryReceiver the deliveryReceiver to set
	 */
	public void setDeliveryReceiver(String deliveryReceiver) {
		this.deliveryReceiver = deliveryReceiver;
	}

	/**
	 * @return the itemType
	 */
	public ProductItemType getItemType() {
		return itemType;
	}

	/**
	 * @param itemType the itemType to set
	 */
	public void setItemType(ProductItemType itemType) {
		this.itemType = itemType;
	}

	/**
	 * @return the businessUnit
	 */
	public String getBusinessUnit() {
		return businessUnit;
	}

	/**
	 * @param businessUnit the businessUnit to set
	 */
	public void setBusinessUnit(String businessUnit) {
		this.businessUnit = businessUnit;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PoItemsPojo [itemNo=" + itemNo + ", itemCode=" + itemCode + ", itemName=" + itemName + ", itemDescription=" + itemDescription + ", itemCategory=" + itemCategory + ", uom=" + uom + ", businessUnit=" + businessUnit + ", costCenter=" + costCenter + ", quantity=" + quantity + ", unitPrice=" + unitPrice + ", totalAmount=" + totalAmount + ", itemTax=" + itemTax + ", taxAmount=" + taxAmount + ", totalAmountWithTax=" + totalAmountWithTax + ", itemType=" + itemType + ", deliveryAddress=" + deliveryAddress + ", deliveryDate=" + deliveryDate + ", deliveryReceiver=" + deliveryReceiver + "]";
	}

}
