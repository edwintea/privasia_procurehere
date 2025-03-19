package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.math.BigDecimal;

import com.privasia.procurehere.core.entity.PrItem;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author parveen
 */
public class PrItemErpPojo implements Serializable {

	private static final long serialVersionUID = 9202614736960706231L;

	private String id;

	private String productId;

	private BigDecimal quantity;

	private String itemName;

	private String itemDesc;

	private Integer level = 0;

	private Integer order = 0;

	private BigDecimal unitPrice;

	private String purchaseGroupCode;

	private String historicPricingRefNo;

	public PrItemErpPojo() {
	}

	public PrItemErpPojo(PrItem prItem) {
		this.id = prItem.getId();
		this.productId = prItem.getProduct() != null ? (StringUtils.checkString(prItem.getProduct().getInterfaceCode()).length() > 0 ? prItem.getProduct().getInterfaceCode() : prItem.getProduct().getProductCode() ) : "";
		this.quantity = prItem.getQuantity();
		this.itemName = prItem.getItemName();
		this.itemDesc = prItem.getItemDescription();
		this.level = prItem.getLevel();
		this.order = prItem.getOrder();
		this.unitPrice = prItem.getUnitPrice();
		this.purchaseGroupCode = prItem.getProduct() != null ? prItem.getProduct().getPurchaseGroupCode() : "";
		this.historicPricingRefNo = prItem.getProduct() != null ? prItem.getProduct().getHistoricPricingRefNo() : "";
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the productId
	 */
	public String getProductId() {
		return productId;
	}

	/**
	 * @param productId the productId to set
	 */
	public void setProductId(String productId) {
		this.productId = productId;
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
	 * @return the itemDesc
	 */
	public String getItemDesc() {
		return itemDesc;
	}

	/**
	 * @param itemDesc the itemDesc to set
	 */
	public void setItemDesc(String itemDesc) {
		this.itemDesc = itemDesc;
	}

	/**
	 * @return the level
	 */
	public Integer getLevel() {
		return level;
	}

	/**
	 * @param level the level to set
	 */
	public void setLevel(Integer level) {
		this.level = level;
	}

	/**
	 * @return the order
	 */
	public Integer getOrder() {
		return order;
	}

	/**
	 * @param order the order to set
	 */
	public void setOrder(Integer order) {
		this.order = order;
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
	 * @return the purchaseGroupCode
	 */
	public String getPurchaseGroupCode() {
		return purchaseGroupCode;
	}

	/**
	 * @param purchaseGroupCode the purchaseGroupCode to set
	 */
	public void setPurchaseGroupCode(String purchaseGroupCode) {
		this.purchaseGroupCode = purchaseGroupCode;
	}

	/**
	 * @return the historicPricingRefNo
	 */
	public String getHistoricPricingRefNo() {
		return historicPricingRefNo;
	}

	/**
	 * @param historicPricingRefNo the historicPricingRefNo to set
	 */
	public void setHistoricPricingRefNo(String historicPricingRefNo) {
		this.historicPricingRefNo = historicPricingRefNo;
	}

	public String toLogString() {
		return "PrItemErpPojo [id=" + id + ", productId=" + productId + ", quantity=" + quantity + ", itemName=" + itemName + ", itemDesc= " + itemDesc + ", level=" + level + ", order=" + order + ", unitPrice=" + unitPrice + ", purchaseGroupCode=" + purchaseGroupCode + ", historicPricingRefNo=" + historicPricingRefNo + "]";
	}

}
