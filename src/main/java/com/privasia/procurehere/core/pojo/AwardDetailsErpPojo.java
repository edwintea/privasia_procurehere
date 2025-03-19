package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author parveen
 */
public class AwardDetailsErpPojo implements Serializable {

	private static final long serialVersionUID = 880410400054917180L;

	private String id;

	private Integer level;

	private Integer order;

	private String itemCategory; // new Field in BQ

	private String bqItemCode; // new Field in BQ

	private String itemName;

	private String itemDesc;

	private String mfr_PartNO;

	private String materialGroup; // new Field in BQ

	private BigDecimal quantity;

	private String uom;

	private BigDecimal unitPrice;

	private BigDecimal totalAmount;

	private String vendorCode;

	private String itemNo;

	// this is used for add send section value to SAP
	private String lnno;

	private ErpSupplierPojo supplier;

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
	 * @return the bqItemCode
	 */
	public String getBqItemCode() {
		return bqItemCode;
	}

	/**
	 * @param bqItemCode the bqItemCode to set
	 */
	public void setBqItemCode(String bqItemCode) {
		this.bqItemCode = bqItemCode;
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
	 * @return the materialGroup
	 */
	public String getMaterialGroup() {
		return materialGroup;
	}

	/**
	 * @param materialGroup the materialGroup to set
	 */
	public void setMaterialGroup(String materialGroup) {
		this.materialGroup = materialGroup;
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

	public String getLnno() {
		return lnno;
	}

	public void setLnno(String lnno) {
		this.lnno = lnno;
	}

	public ErpSupplierPojo getSupplier() {
		return supplier;
	}

	public void setSupplier(ErpSupplierPojo supplier) {
		this.supplier = supplier;
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
	 * @return the mfr_PartNO
	 */
	public String getMfr_PartNO() {
		return mfr_PartNO;
	}

	/**
	 * @param mfr_PartNO the mfr_PartNO to set
	 */
	public void setMfr_PartNO(String mfr_PartNO) {
		this.mfr_PartNO = mfr_PartNO;
	}

}
