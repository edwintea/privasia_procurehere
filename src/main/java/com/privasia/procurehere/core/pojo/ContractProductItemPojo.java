package com.privasia.procurehere.core.pojo;

import java.math.BigDecimal;

import com.privasia.procurehere.core.enums.ProductItemType;

public class ContractProductItemPojo {

	private BigDecimal tax;

	private String brand;

	private String uom;

	private String productCategory;
	private String productCategoryName;
	private String productCategoryCode;

	private ProductItemType productItemType;

	private BigDecimal unitPrice;

	private String itemCode;

	public ContractProductItemPojo(String brand, BigDecimal tax, String uom, String productCategory, String productCategoryCode, String productCategoryName, BigDecimal unitPrice, ProductItemType productItemType, String itemCode) {
		this.tax = tax;
		this.brand = brand;
		this.uom = uom;
		this.productCategory = productCategory;
		this.productCategoryCode = productCategoryCode;
		this.productCategoryName = productCategoryName;
		this.unitPrice = unitPrice;
		this.productItemType = productItemType;
		this.itemCode = itemCode;
	}

	public ContractProductItemPojo() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the tax
	 */
	public BigDecimal getTax() {
		return tax;
	}

	/**
	 * @param tax the tax to set
	 */
	public void setTax(BigDecimal tax) {
		this.tax = tax;
	}

	/**
	 * @return the brand
	 */
	public String getBrand() {
		return brand;
	}

	/**
	 * @param brand the brand to set
	 */
	public void setBrand(String brand) {
		this.brand = brand;
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
	 * @return the productCategory
	 */
	public String getProductCategory() {
		return productCategory;
	}

	/**
	 * @param productCategory the productCategory to set
	 */
	public void setProductCategory(String productCategory) {
		this.productCategory = productCategory;
	}

	/**
	 * @return the productItemType
	 */
	public ProductItemType getProductItemType() {
		return productItemType;
	}

	/**
	 * @param productItemType the productItemType to set
	 */
	public void setProductItemType(ProductItemType productItemType) {
		this.productItemType = productItemType;
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
	 * @return the productCategoryName
	 */
	public String getProductCategoryName() {
		return productCategoryName;
	}

	/**
	 * @param productCategoryName the productCategoryName to set
	 */
	public void setProductCategoryName(String productCategoryName) {
		this.productCategoryName = productCategoryName;
	}

	/**
	 * @return the productCategoryCode
	 */
	public String getProductCategoryCode() {
		return productCategoryCode;
	}

	/**
	 * @param productCategoryCode the productCategoryCode to set
	 */
	public void setProductCategoryCode(String productCategoryCode) {
		this.productCategoryCode = productCategoryCode;
	}

}
