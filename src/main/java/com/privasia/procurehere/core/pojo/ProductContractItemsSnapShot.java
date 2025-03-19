package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.math.BigDecimal;

public class ProductContractItemsSnapShot implements Serializable {

	private static final long serialVersionUID = 4333576410974266673L;

	private String id;

	private String ItemNumber;

	private String productItem;

	private String productCode;

	private String unitCode;

	private BigDecimal quantity;

	private String uom;

	private BigDecimal unitPrice;

	private BigDecimal pricePerUnit;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getItemNumber() {
		return ItemNumber;
	}

	public void setItemNumber(String itemNumber) {
		ItemNumber = itemNumber;
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

}
