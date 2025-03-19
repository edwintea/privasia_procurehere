/**
 * 
 */
package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

/**
 * @author ravi
 */
public class DeliveryOrderItemPojo implements Serializable {

	private static final long serialVersionUID = 3493046147163023154L;

	private String itemId;

	private String itemName;

	private String quantity;

	private String unitPrice;

	private String itemTax;

	/**
	 * @return the itemId
	 */
	public String getItemId() {
		return itemId;
	}

	/**
	 * @param itemId the itemId to set
	 */
	public void setItemId(String itemId) {
		this.itemId = itemId;
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
	public String getQuantity() {
		return quantity;
	}

	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(String quantity) {
		this.quantity = quantity;
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

	public String toLogString() {
		return "DeliveryOrderItemPojo [itemId=" + itemId + ", itemName=" + itemName + ", quantity=" + quantity + ", unitPrice=" + unitPrice + ", itemTax=" + itemTax + "]";
	}

}
