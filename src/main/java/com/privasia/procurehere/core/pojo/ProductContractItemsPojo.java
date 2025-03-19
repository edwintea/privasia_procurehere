package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.privasia.procurehere.core.enums.ProductItemType;

import io.swagger.annotations.ApiModelProperty;

public class ProductContractItemsPojo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1139685009212583850L;

	@ApiModelProperty(required = false, hidden = true)
	private String id;

	@ApiModelProperty(notes = "Item Contract Number", allowableValues = "range[1, 5]", required = true)
	private String contractItemNumber;

	@ApiModelProperty(notes = "Item Code", allowableValues = "range[1, 15]", required = true)
	private String itemCode;

	@ApiModelProperty(notes = "Item Name", allowableValues = "range[1, 32]", required = false)
	private String itemName;

	@ApiModelProperty(notes = "Unit Price", allowableValues = "range[1, 5]")
	private BigDecimal unitPrice;

	private BigDecimal pricePerUnit;

	@ApiModelProperty(notes = "Item Category", required = true)
	private String itemCategory;

	@ApiModelProperty(notes = "Plant Code", required = true, hidden = false)
	private String businessUnit;

	private String businessUnitName;

	private String businessUnitCode;

	@ApiModelProperty(notes = "Uom Code", required = true)
	private String uom;

	@ApiModelProperty(notes = "Item Type - MATERIAL or SERVICE", required = false, hidden = false)
	private ProductItemType itemType;

	@ApiModelProperty(notes = "Quantity", allowableValues = "range[1, 5]", required = true)
	private BigDecimal quantity;

	@ApiModelProperty(notes = "Balance Quantity", allowableValues = "range[1, 5]", hidden = true)
	private BigDecimal balanceQuantity;

	@ApiModelProperty(notes = "Tax Code", allowableValues = "range[1, 15]", required = false)
	private String taxCode;

	@ApiModelProperty(notes = "Remarks", allowableValues = "range[1, 200]", required = false)
	private String remarks;

	@ApiModelProperty(notes = "Storage Location", required = false)
	private String storageLoc;

	@ApiModelProperty(notes = "Cost Center", required = false)
	private String costCenter;

	private String costCenterName;

	private String costCenterDescription;

	private Date contractEndDate;

	private String productItem;

	private Boolean contractExpire = false;

	private BigDecimal tax;

	private String productCode;

	private String ProductName;

	private String brand;

	private String productCategory;

	private String productCategoryCode;

	private String productCategoryName;

	private Date contractStartDate;

	private String decimal;

	private Boolean freeTextItemEntered = Boolean.FALSE;

	private String description;

	private String quantityStr;

	private Boolean deleted = Boolean.FALSE;

	private String unitPriceStr;

	private String balanceQuantityStr;

	private Boolean erpTransferred = Boolean.FALSE;

	public ProductContractItemsPojo() {

	}

	/**
	 * @param id
	 * @param contractItemNumber
	 * @param unitPrice
	 * @param quantity
	 */
	public ProductContractItemsPojo(String id, String contractItemNumber, String itemName, String itemCode, BigDecimal quantity, BigDecimal balanceQuantity, BigDecimal unitPrice, String storageLocation, String uom, String businessUnit, String costCenter, Date contractEndDate) {
		this.id = id;
		this.contractItemNumber = contractItemNumber;
		this.balanceQuantity = balanceQuantity;
		this.quantity = quantity;
		this.unitPrice = unitPrice;
		this.storageLoc = storageLocation;
		this.uom = uom;
		this.businessUnit = businessUnit;
		this.costCenter = costCenter;
		this.itemCode = itemCode;
		this.itemName = itemName;
		this.contractEndDate = contractEndDate;
	}

	// 2431 - Get Contract Item List in Contract Creation Screen
	public ProductContractItemsPojo(String id, String contractItemNumber, String productName, String productItemCode, BigDecimal quantity, BigDecimal balanceQuantity, BigDecimal unitPrice, String storageLocation, String uom, String businessUnit, String costCenter, Date contractStartDate, BigDecimal tax, String brand, String productCatCode, String productCatName, ProductItemType itemType, String decimal, String itemName, String itemCode, Boolean freeTextItemEntered, String description, Boolean deleted) {
		this.id = id;
		this.contractItemNumber = contractItemNumber;
		this.balanceQuantity = balanceQuantity;
		this.quantity = quantity;
		this.unitPrice = unitPrice;
		this.storageLoc = storageLocation;
		this.uom = uom;
		this.businessUnit = businessUnit;
		this.costCenter = costCenter;
		this.freeTextItemEntered = freeTextItemEntered;
		this.itemName = itemName;
		this.itemCode = itemCode;
		this.contractStartDate = contractStartDate;
		this.tax = tax;
		this.brand = brand;
		this.productCode = productCatCode;
		this.ProductName = productCatName;
		this.itemType = itemType;
		this.decimal = decimal;
		this.description = description;
		this.deleted = deleted;
	}

	public ProductContractItemsPojo(String id, String contractItemNumber, String storageLocation, String uom, String productItem, String costCenter) {
		this.id = id;
		this.contractItemNumber = contractItemNumber;
		this.storageLoc = storageLocation;
		this.uom = uom;
		this.productItem = productItem;
		this.costCenter = costCenter;
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
	 * @return the contractItemNumber
	 */
	public String getContractItemNumber() {
		return contractItemNumber;
	}

	/**
	 * @param contractItemNumber the contractItemNumber to set
	 */
	public void setContractItemNumber(String contractItemNumber) {
		this.contractItemNumber = contractItemNumber;
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
	 * @return the balanceQuantity
	 */
	public BigDecimal getBalanceQuantity() {
		return balanceQuantity;
	}

	/**
	 * @param balanceQuantity the balanceQuantity to set
	 */
	public void setBalanceQuantity(BigDecimal balanceQuantity) {
		this.balanceQuantity = balanceQuantity;
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

	public BigDecimal getPricePerUnit() {
		return pricePerUnit;
	}

	public void setPricePerUnit(BigDecimal pricePerUnit) {
		this.pricePerUnit = pricePerUnit;
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
	 * @return the taxCode
	 */
	public String getTaxCode() {
		return taxCode;
	}

	/**
	 * @param taxCode the taxCode to set
	 */
	public void setTaxCode(String taxCode) {
		this.taxCode = taxCode;
	}

	/**
	 * @return the remarks
	 */
	public String getRemarks() {
		return remarks;
	}

	/**
	 * @param remarks the remarks to set
	 */
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	/**
	 * @return the storageLoc
	 */
	public String getStorageLoc() {
		return storageLoc;
	}

	/**
	 * @param storageLoc the storageLoc to set
	 */
	public void setStorageLoc(String storageLoc) {
		this.storageLoc = storageLoc;
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

	public Date getContractEndDate() {
		return contractEndDate;
	}

	public void setContractEndDate(Date contractEndDate) {
		this.contractEndDate = contractEndDate;
	}

	public String getProductItem() {
		return productItem;
	}

	public void setProductItem(String productItem) {
		this.productItem = productItem;
	}

	public Boolean getContractExpire() {
		return contractExpire;
	}

	public void setContractExpire(Boolean contractExpire) {
		this.contractExpire = contractExpire;
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
	 * @return the productCode
	 */
	public String getProductCode() {
		return productCode;
	}

	/**
	 * @param productCode the productCode to set
	 */
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	/**
	 * @return the productName
	 */
	public String getProductName() {
		return ProductName;
	}

	/**
	 * @param productName the productName to set
	 */
	public void setProductName(String productName) {
		ProductName = productName;
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
	 * @return the contractStartDate
	 */
	public Date getContractStartDate() {
		return contractStartDate;
	}

	/**
	 * @param contractStartDate the contractStartDate to set
	 */
	public void setContractStartDate(Date contractStartDate) {
		this.contractStartDate = contractStartDate;
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

	/**
	 * @return the freeTextItemEntered
	 */
	public Boolean getFreeTextItemEntered() {
		return freeTextItemEntered;
	}

	/**
	 * @param freeTextItemEntered the freeTextItemEntered to set
	 */
	public void setFreeTextItemEntered(Boolean freeTextItemEntered) {
		this.freeTextItemEntered = freeTextItemEntered;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	public String getQuantityStr() {
		return quantityStr;
	}

	public void setQuantityStr(String quantityStr) {
		this.quantityStr = quantityStr;
	}

	/**
	 * @return the deleted
	 */
	public Boolean getDeleted() {
		return deleted;
	}

	/**
	 * @param deleted the deleted to set
	 */
	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public String getUnitPriceStr() {
		return unitPriceStr;
	}

	public void setUnitPriceStr(String unitPriceStr) {
		this.unitPriceStr = unitPriceStr;
	}

	public String getBalanceQuantityStr() {
		return balanceQuantityStr;
	}

	public void setBalanceQuantityStr(String balanceQuantityStr) {
		this.balanceQuantityStr = balanceQuantityStr;
	}

	/**
	 * @return the costCenterName
	 */
	public String getCostCenterName() {
		return costCenterName;
	}

	/**
	 * @param costCenterName the costCenterName to set
	 */
	public void setCostCenterName(String costCenterName) {
		this.costCenterName = costCenterName;
	}

	/**
	 * @return the costCenterDescription
	 */
	public String getCostCenterDescription() {
		return costCenterDescription;
	}

	/**
	 * @param costCenterDescription the costCenterDescription to set
	 */
	public void setCostCenterDescription(String costCenterDescription) {
		this.costCenterDescription = costCenterDescription;
	}

	/**
	 * @return the businessUnitName
	 */
	public String getBusinessUnitName() {
		return businessUnitName;
	}

	/**
	 * @param businessUnitName the businessUnitName to set
	 */
	public void setBusinessUnitName(String businessUnitName) {
		this.businessUnitName = businessUnitName;
	}

	/**
	 * @return the businessUnitCode
	 */
	public String getBusinessUnitCode() {
		return businessUnitCode;
	}

	/**
	 * @param businessUnitCode the businessUnitCode to set
	 */
	public void setBusinessUnitCode(String businessUnitCode) {
		this.businessUnitCode = businessUnitCode;
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
	 * @return the erpTransferred
	 */
	public Boolean getErpTransferred() {
		return erpTransferred;
	}

	/**
	 * @param erpTransferred the erpTransferred to set
	 */
	public void setErpTransferred(Boolean erpTransferred) {
		this.erpTransferred = erpTransferred;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ProductContractItemsPojo [id=" + id + ", contractItemNumber=" + contractItemNumber + ", itemCode=" + itemCode + ", itemName=" + itemName + ", unitPrice=" + unitPrice + ", itemCategory=" + itemCategory + ", businessUnit=" + businessUnit + ", uom=" + uom + ", itemType=" + itemType + ", quantity=" + quantity + ", balanceQuantity=" + balanceQuantity + ", taxCode=" + taxCode + ", remarks=" + remarks + ", storageLoc=" + storageLoc + "]";
	}

}
