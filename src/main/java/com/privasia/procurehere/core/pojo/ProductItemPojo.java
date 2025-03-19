package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.OperationType;
import com.privasia.procurehere.core.enums.ProductItemType;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author sarang
 */
@SuppressWarnings("serial")
public class ProductItemPojo implements Serializable {

	@ApiModelProperty(required = false, hidden = true)
	private String id;

	@ApiModelProperty(notes = "Item Code", allowableValues = "range[1, 15]", required = true)
	private String itemCode;

	@ApiModelProperty(notes = "Item Name", allowableValues = "range[1, 100]", required = true)
	private String itemName;

	@ApiModelProperty(notes = "Item Category", required = true)
	private String itemCategory;

	@ApiModelProperty(required = false, hidden = true)
	private String favoriteSupplier;

	@ApiModelProperty(notes = "Vendor Code", required = false, hidden = false)
	private String vendorCode;

	@ApiModelProperty(notes = "Operation", required = true)
	private OperationType operation;

	@ApiModelProperty(notes = "Unit Price", allowableValues = "range[1, 20]", required = true)
	private BigDecimal unitPrice;

	@ApiModelProperty(notes = "Uom Code", required = true)
	private String uom;

	@ApiModelProperty(notes = "Tax", allowableValues = "range[1, 20]", required = false)
	private BigDecimal tax;

	@ApiModelProperty(required = false, hidden = false)
	private Boolean contractItem = Boolean.FALSE;

	@ApiModelProperty(required = false, hidden = true)
	private String itemDescription;

	@ApiModelProperty(required = false, hidden = true)
	private String purchaseGroupCode;

	@ApiModelProperty(required = false)
	private List<PurchaseGroupsPojo> purchaseGroups;

	@ApiModelProperty(required = false, hidden = true)
	private String createBy;

	@ApiModelProperty(required = false, hidden = true)
	private String modifiedBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@ApiModelProperty(required = false, hidden = true)
	private Date createdDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@ApiModelProperty(required = false, hidden = true)
	private Date modifiedDate;

	@ApiModelProperty(required = false, hidden = true)
	private String uomDescription;

	@ApiModelProperty(required = false, hidden = false)
	private String remarks;

	@ApiModelProperty(required = false, hidden = true)
	private String historicPricingRefNo;

	@ApiModelProperty(required = false, hidden = true)
	private String unspscCode;

	@ApiModelProperty(required = false, hidden = false)
	private String brand;

	@ApiModelProperty(required = false, hidden = false)
	private String glCode;

	@ApiModelProperty(required = false, hidden = true)
	private Status status;

	@ApiModelProperty(required = false, hidden = false)
	private String contractReferenceNumber;

	@ApiModelProperty(notes = "date in DD/MM/YYYY format", required = false, hidden = false)
	// @JsonSerialize(using = CustomDateSerializer.class)
	@JsonFormat(pattern = "dd/MM/yyyy")
	private Date validityDate;

	@ApiModelProperty(notes = "Item Type - MATERIAL or SERVICE", required = false, hidden = false)
	private ProductItemType itemType;

	private BigDecimal balanceQuantity;

	private String storageLocation;

	private String interfaceCode;

	private Date startDate;

	private String productCode;

	/**
	 * @return the contractReferenceNumber
	 */
	public String getContractReferenceNumber() {
		return contractReferenceNumber;
	}

	/**
	 * @param contractReferenceNumber the contractReferenceNumber to set
	 */
	public void setContractReferenceNumber(String contractReferenceNumber) {
		this.contractReferenceNumber = contractReferenceNumber;
	}

	/**
	 * @return the validityDate
	 */
	public Date getValidityDate() {
		return validityDate;
	}

	/**
	 * @param validityDate the validityDate to set
	 */
	public void setValidityDate(Date validityDate) {
		this.validityDate = validityDate;
	}

	public ProductItemPojo() {
	}

	public ProductItemPojo(String id, String productName) {
		this.id = id;
		this.itemName = productName;
	}

	public ProductItemPojo(String productName, String productCode, String remarks, String uom) {
		this.itemCode = productCode;
		this.itemName = productName;
		this.itemDescription = remarks;
		this.uom = uom;
	}

	public ProductItemPojo(String id, String productCode, String productName, String productCategory, String groupCode, String favoriteSupplier, String createBy, String modifiedBy, Date createdDate, Date modifiedDate, Status status, String interfaceCode) {
		this.id = id;
		this.itemCode = productCode;
		this.itemName = productName;
		this.itemCategory = productCategory;
		this.purchaseGroupCode = groupCode;
		this.favoriteSupplier = favoriteSupplier;
		this.createBy = createBy;
		this.modifiedBy = modifiedBy;
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
		this.status = status;
		this.interfaceCode = interfaceCode;
	}

	public ProductItemPojo(String productName, String productCode, String remarks, String uom, String productCategory, ProductItemType productItemType) {
		this.itemCode = productCode;
		this.itemName = productName;
		this.itemDescription = remarks;
		this.uom = uom;
		this.itemCategory = productCategory;
		this.itemType = productItemType;
	}

	public ProductItemPojo(String id, String productCode, String productName, String productCategory, String groupCode, String favoriteSupplier, ProductItemType itemType, String createBy, String modifiedBy, Date createdDate, Date modifiedDate, Status status, String interfaceCode) {
		this.id = id;
		this.itemCode = productCode;
		this.itemName = productName;
		this.itemCategory = productCategory;
		this.purchaseGroupCode = groupCode;
		this.favoriteSupplier = favoriteSupplier;
		this.itemType = itemType;
		this.createBy = createBy;
		this.modifiedBy = modifiedBy;
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
		this.status = status;
		this.interfaceCode = interfaceCode;
	}

	public ProductItemPojo(String interfaceCode, String productCode, String productName, String uom, String productCategory, String supplier, BigDecimal unitPrice, BigDecimal tax, ProductItemType productItemType, String glCode, //
			String unspscCode, Status status, String remarks, String historicPricingRefNo, String purchaseGroupCode, String brand, Date startDate, Date validityDate, String contractReferenceNumber) {
		this.interfaceCode = interfaceCode;
		this.itemName = productName;
		this.uom = uom;
		this.itemCategory = productCategory;
		this.favoriteSupplier = supplier;
		this.unitPrice = unitPrice;
		this.tax = tax;
		this.itemType = productItemType;
		this.glCode = glCode;
		this.unspscCode = unspscCode;
		this.status = status;
		this.remarks = remarks;
		this.historicPricingRefNo = historicPricingRefNo;
		this.purchaseGroupCode = purchaseGroupCode;
		this.brand = brand;
		this.startDate = startDate;
		this.validityDate = validityDate;
		this.contractReferenceNumber = contractReferenceNumber;

	}

	public ProductItemPojo createCopy() {
		ProductItemPojo clone = new ProductItemPojo();
		clone.setBalanceQuantity(this.balanceQuantity);
		clone.setBrand(this.brand);
		clone.setContractItem(this.contractItem);
		clone.setContractReferenceNumber(this.contractReferenceNumber);
		clone.setCreateBy(this.createBy);
		clone.setCreatedDate(this.createdDate);
		clone.setFavoriteSupplier(this.favoriteSupplier);
		clone.setGlCode(this.glCode);
		clone.setHistoricPricingRefNo(this.historicPricingRefNo);
		clone.setId(this.id);
		clone.setInterfaceCode(this.interfaceCode);
		clone.setItemCategory(this.itemCategory);
		clone.setItemCode(this.itemCode);
		clone.setItemDescription(this.itemDescription);
		clone.setItemName(this.itemName);
		clone.setItemType(this.itemType);
		clone.setModifiedBy(this.modifiedBy);
		clone.setModifiedDate(this.modifiedDate);
		clone.setOperation(this.operation);

		// if (this.purchaseGroupCodes != null) {
		// setPurchaseGroupCodes(purchaseGroupCodes.stream().collect(Collectors.toList()));
		// }

		clone.setPurchaseGroups(purchaseGroups);
		clone.setRemarks(this.remarks);
		clone.setStatus(this.status);
		clone.setStorageLocation(this.storageLocation);
		clone.setTax(this.tax);
		clone.setUnitPrice(this.unitPrice);
		clone.setUnspscCode(this.unspscCode);
		clone.setUom(this.uom);
		clone.setUomDescription(this.uomDescription);
		clone.setValidityDate(this.validityDate);
		clone.setVendorCode(this.vendorCode);

		return clone;
	}

	public ProductItemPojo(String id, String productCode, String productName, String productCategory, String groupCode, String favoriteSupplier, ProductItemType itemType, String createBy, String modifiedBy, Date createdDate, Date modifiedDate, Status status) {
		this.id = id;
		this.itemCode = productCode;
		this.itemName = productName;
		this.itemCategory = productCategory;
		this.purchaseGroupCode = groupCode;
		this.favoriteSupplier = favoriteSupplier;
		this.createBy = createBy;
		this.modifiedBy = modifiedBy;
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
		this.status = status;
		this.itemType = itemType;
	}

	public ProductItemPojo(String id, String productCode, String productName, String productCategory, ProductItemType productItemType, String purchaseGroupCode, String interfaceCode, String companyName, Status status, String uom, BigDecimal unitPrice, BigDecimal tax, String remarks, String glCode, String unspscCode, String brand, String contractReferenceNumber, String historicPricingRefNo, Date startDate, Date validityDate) {
		this.id = id;
		this.itemCode = productCode;
		this.itemName = productName;
		this.itemCategory = productCategory;
		this.itemType = productItemType;
		this.purchaseGroupCode = purchaseGroupCode;
		this.interfaceCode = interfaceCode;
		this.favoriteSupplier = companyName;
		this.status = status;
		this.uom = uom;
		this.unitPrice = unitPrice;
		this.tax = tax;
		this.remarks = remarks;
		this.glCode = glCode;
		this.unspscCode = unspscCode;
		this.brand = brand;
		this.contractReferenceNumber = contractReferenceNumber;
		this.historicPricingRefNo = historicPricingRefNo;
		this.startDate = startDate;
		this.validityDate = validityDate;
	}

	public ProductItemPojo(String id, String productName, String productCode) {
		this.id = id;
		this.itemName = productName;
		this.productCode = productCode;
	}

	public String getItemDescription() {
		return itemDescription;
	}

	public void setItemDescription(String itemDescription) {
		this.itemDescription = itemDescription;
	}

	/**
	 * @return the operation
	 */
	public OperationType getOperation() {
		return operation;
	}

	/**
	 * @param operation the operation to set
	 */
	public void setOperation(OperationType operation) {
		this.operation = operation;
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getItemCode() {
		return itemCode;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	public String getItemCategory() {
		return itemCategory;
	}

	public void setItemCategory(String itemCategory) {
		this.itemCategory = itemCategory;
	}

	public String getFavoriteSupplier() {
		return favoriteSupplier;
	}

	public void setFavoriteSupplier(String favoriteSupplier) {
		this.favoriteSupplier = favoriteSupplier;
	}

	public String getUom() {
		return uom;
	}

	public void setUom(String uom) {
		this.uom = uom;
	}

	/**
	 * @return the contractItem
	 */
	public Boolean getContractItem() {
		return contractItem;
	}

	/**
	 * @param contractItem the contractItem to set
	 */
	public void setContractItem(Boolean contractItem) {
		this.contractItem = contractItem;
	}

	public String getPurchaseGroupCode() {
		return purchaseGroupCode;
	}

	public void setPurchaseGroupCode(String purchaseGroupCode) {
		this.purchaseGroupCode = purchaseGroupCode;
	}

	public String getCreateBy() {
		return createBy;
	}

	/**
	 * @return the purchaseGroups
	 */
	public List<PurchaseGroupsPojo> getPurchaseGroups() {
		return purchaseGroups;
	}

	/**
	 * @param purchaseGroups the purchaseGroups to set
	 */
	public void setPurchaseGroups(List<PurchaseGroupsPojo> purchaseGroups) {
		this.purchaseGroups = purchaseGroups;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getUomDescription() {
		return uomDescription;
	}

	public void setUomDescription(String uomDescription) {
		this.uomDescription = uomDescription;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getHistoricPricingRefNo() {
		return historicPricingRefNo;
	}

	public void setHistoricPricingRefNo(String historicPricingRefNo) {
		this.historicPricingRefNo = historicPricingRefNo;
	}

	public String getUnspscCode() {
		return unspscCode;
	}

	public void setUnspscCode(String unspscCode) {
		this.unspscCode = unspscCode;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getGlCode() {
		return glCode;
	}

	public void setGlCode(String glCode) {
		this.glCode = glCode;
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

	public BigDecimal getBalanceQuantity() {
		return balanceQuantity;
	}

	public void setBalanceQuantity(BigDecimal balanceQuantity) {
		this.balanceQuantity = balanceQuantity;
	}

	public String getStorageLocation() {
		return storageLocation;
	}

	public void setStorageLocation(String storageLocation) {
		this.storageLocation = storageLocation;
	}

	/**
	 * @return the interfaceCode
	 */
	public String getInterfaceCode() {
		return interfaceCode;
	}

	/**
	 * @param interfaceCode the interfaceCode to set
	 */
	public void setInterfaceCode(String interfaceCode) {
		this.interfaceCode = interfaceCode;
	}

	/**
	 * @return the startDate
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((itemName == null) ? 0 : itemName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProductItemPojo other = (ProductItemPojo) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (itemName == null) {
			if (other.itemName != null)
				return false;
		} else if (!itemName.equals(other.itemName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ProductItemPojo [itemCode=" + itemCode + ", itemName=" + itemName + ", itemCategory=" + itemCategory + ", vendorCode=" + vendorCode + ", operation=" + operation + ", unitPrice=" + unitPrice + ", uom=" + uom + ", tax=" + tax + ", itemDescription=" + itemDescription + ", purchaseGroupCode=" + purchaseGroupCode + ", createdDate=" + createdDate + ", modifiedDate=" + modifiedDate + ", uomDescription=" + uomDescription + ", remarks=" + remarks + ", glCode=" + glCode + ", status=" + status + ", contractReferenceNumber=" + contractReferenceNumber + ", validityDate=" + validityDate + ", itemType=" + itemType + ", storageLocation=" + storageLocation + ", interfaceCode=" + interfaceCode + "]";
	}

}
