package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.privasia.procurehere.core.entity.Pr;
import com.privasia.procurehere.core.entity.PrItem;
import com.privasia.procurehere.core.entity.RfqBqItem;
import com.privasia.procurehere.core.entity.RftBqItem;
import com.privasia.procurehere.core.entity.SourcingFormRequest;
import com.privasia.procurehere.core.entity.SourcingFormRequestBqItem;
import com.privasia.procurehere.core.enums.ProductItemType;
import com.privasia.procurehere.core.utils.StringUtils;

/**
 * @author Nitin Otageri
 */
public class PrItemErp2Pojo implements Serializable {

	private static final long serialVersionUID = 9202614736960706231L;

	@JsonProperty("PRItemPreqItem")
	private String prItemSeqNo;

	@JsonProperty("PRItemShortText")
	private String itemName;

	@JsonProperty("PRItemtextTextLine")
	private String itemDescription;

	@JsonProperty("PRItemQuantity")
	private BigDecimal quantity;

	@JsonProperty("PRItemUnit")
	private String uom;

	@JsonProperty("PRItemDelivDate")
	@JsonFormat(pattern = "yyyyMMdd", timezone = "GMT+08:00")
	private Date deliveryDate;

	@JsonProperty("PRItemPlant")
	private String businessUnit;

	@JsonProperty("PRItemPurchOrg")
	private String parentBusinessUnit;

	@JsonProperty("PRItemPreqDate")
	@JsonFormat(pattern = "yyyyMMdd", timezone = "GMT+08:00")
	private Date approvedDate;

	@JsonProperty("PRItemPriceUnit")
	private BigDecimal priceUnit; // defaulted to 1 for SAP

	@JsonProperty("PRItemPreqPrice")
	private BigDecimal unitPrice;

	@JsonProperty("PRItemDesVendor")
	private String vendorCode;

	@JsonProperty("PRAccountCostcenter")
	private String costCenter;

	@JsonProperty("PRItemStoreLoc")
	private String storageLocation;

	@JsonProperty("ContractPRItemPreqItem")
	private String contractItemReferenceNumber;

	@JsonProperty("ContractReferenceNumber")
	private String contractReferenceNumber;

	@JsonProperty("PRItemMaterial")
	private String itemCode;

	@JsonProperty("PRItemMatlGroup")
	private String itemCategory;

	@JsonProperty("PRItemMaterialType")
	private ProductItemType productItemType;

	@JsonProperty("PRItemPurGroup")
	private String purchaseGroup;

	@JsonProperty("PRAccountWbsElement")
	private String additionalField1;

	@JsonProperty("PRAccountAssetNo")
	private String additionalField2;

	@JsonProperty("PRAccountBusArea")
	private String additionalField3;

	@JsonProperty("PRAccountOrderId")
	private String additionalField4;

	@JsonProperty("PRAccountNetwork")
	private String additionalField5;

	@JsonProperty("PRAccountActivity")
	private String additionalField6;

	public PrItemErp2Pojo() {
	}

	public PrItemErp2Pojo(PrItem prItem, Pr pr, Date approvedDate, int sequence) {
		this.prItemSeqNo = StringUtils.lpad(String.valueOf(sequence * 10), 5, '0');
		this.itemName = prItem.getProduct() != null ? prItem.getProduct().getProductName() : prItem.getItemName();
		this.itemDescription = prItem.getItemDescription();
		this.quantity = prItem.getQuantity();
		this.uom = prItem.getUnit() != null ? prItem.getUnit().getUom() : null;
		this.deliveryDate = pr.getDeliveryDate();
		this.businessUnit = pr.getBusinessUnit() != null ? pr.getBusinessUnit().getUnitCode() : null;
		this.parentBusinessUnit = pr.getBusinessUnit() != null ? (pr.getBusinessUnit().getParent() != null ? pr.getBusinessUnit().getParent().getUnitCode() : pr.getBusinessUnit().getUnitCode()) : null;
		this.unitPrice = prItem.getUnitPrice();
		this.itemCode = prItem.getProduct() != null ? (StringUtils.checkString(prItem.getProduct().getInterfaceCode()).length() > 0 ? prItem.getProduct().getInterfaceCode() : prItem.getProduct().getProductCode()) : "Non Item";
		this.itemCategory = prItem.getProductCategory() != null ? prItem.getProductCategory().getProductCode() : (prItem.getProduct() != null ? prItem.getProduct().getProductCategory().getProductCode() : null);
		this.productItemType = prItem.getProduct() != null ? prItem.getProduct().getProductItemType() : null;
		// If Cost Center exists at contract item level then send it... else send the PR Cost Center
		this.costCenter = StringUtils.checkString(prItem.getCostCenter()).length() > 0 ? prItem.getCostCenter() : (pr.getCostCenter() != null ? pr.getCostCenter().getCostCenter() : null);
		this.vendorCode = pr.getSupplier() != null ? pr.getSupplier().getVendorCode() : pr.getSupplierName();
		this.approvedDate = approvedDate;
		this.priceUnit = BigDecimal.ONE; // defaulted to 1 for SAP

		this.storageLocation = prItem.getStorageLocation(); // ?
		this.contractItemReferenceNumber = prItem.getItemContractReferenceNumber(); // ??

		this.contractReferenceNumber = prItem.getContractReferenceNumber(); // ??

		this.purchaseGroup = prItem.getPurchaseGroup(); // Taken from Contract Header Group Code
		this.additionalField1 = prItem.getField1();
		this.additionalField2 = prItem.getField2();
		this.additionalField3 = prItem.getField3();
		this.additionalField4 = prItem.getField4();
		this.additionalField5 = prItem.getField5();
		this.additionalField6 = prItem.getField6();
	}

	public PrItemErp2Pojo(SourcingFormRequestBqItem item, SourcingFormRequest req, Date approvedDate, int sequence) {
		this.prItemSeqNo = StringUtils.lpad(String.valueOf(sequence * 10), 5, '0');
		this.itemName = item.getItemName();
		this.itemDescription = item.getItemDescription();
		this.quantity = item.getQuantity();
		this.uom = item.getUom() != null ? item.getUom().getUom() : null;
		this.deliveryDate = approvedDate != null ? approvedDate : req.getCreatedDate();
		this.businessUnit = req.getBusinessUnit() != null ? req.getBusinessUnit().getUnitCode() : null;
		this.parentBusinessUnit = req.getBusinessUnit() != null ? (req.getBusinessUnit().getParent() != null ? req.getBusinessUnit().getParent().getUnitCode() : req.getBusinessUnit().getUnitCode()) : null;
		this.unitPrice = item.getUnitBudgetPrice() != null ? item.getUnitBudgetPrice() : item.getUnitPrice();
		this.itemCode = item.getField1();

		this.itemCategory = item.getField2();
		if (StringUtils.checkString(item.getField3()).length() > 0) {
			this.productItemType = ProductItemType.valueOf(item.getField3());
		}
		// If Cost Center exists at contract item level then send it... else send the PR Cost Center
		this.costCenter = req.getCostCenter() != null ? req.getCostCenter().getCostCenter() : null;
		this.approvedDate = approvedDate != null ? approvedDate : req.getCreatedDate();
		this.priceUnit = BigDecimal.ONE; // defaulted to 1 for SAP
		this.purchaseGroup = StringUtils.checkString(req.getGroupCodeOld()).length() > 0 ? StringUtils.checkString(req.getGroupCodeOld()) : (req.getGroupCode() != null ?  req.getGroupCode().getGroupCode() : null);
		this.additionalField1 = item.getField4();
		this.additionalField2 = item.getField5();
		this.additionalField3 = item.getField6();
		this.additionalField4 = item.getField7();
		this.additionalField5 = item.getField8();
		this.additionalField6 = item.getField9();
	}

	public PrItemErp2Pojo(SourcingFormRequestBqItem item, int sequence, boolean isDelete) {
		this.prItemSeqNo = StringUtils.lpad(String.valueOf(sequence * 10), 5, '0');
		this.itemCode = item.getField1();
	}

	public PrItemErp2Pojo(RfqBqItem item, int sequence) {
		this.prItemSeqNo = StringUtils.lpad(String.valueOf(sequence * 10), 5, '0');
		this.itemCode = item.getField1();
	}

	public PrItemErp2Pojo(RftBqItem item, int sequence) {
		this.prItemSeqNo = StringUtils.lpad(String.valueOf(sequence * 10), 5, '0');
		this.itemCode = item.getField1();
	}

	/**
	 * @return the prItemSeqNo
	 */
	public String getPrItemSeqNo() {
		return prItemSeqNo;
	}

	/**
	 * @param prItemSeqNo the prItemSeqNo to set
	 */
	public void setPrItemSeqNo(String prItemSeqNo) {
		this.prItemSeqNo = prItemSeqNo;
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
	 * @return the storageLocation
	 */
	public String getStorageLocation() {
		return storageLocation;
	}

	/**
	 * @param storageLocation the storageLocation to set
	 */
	public void setStorageLocation(String storageLocation) {
		this.storageLocation = storageLocation;
	}

	/**
	 * @return the parentBusinessUnit
	 */
	public String getParentBusinessUnit() {
		return parentBusinessUnit;
	}

	/**
	 * @param parentBusinessUnit the parentBusinessUnit to set
	 */
	public void setParentBusinessUnit(String parentBusinessUnit) {
		this.parentBusinessUnit = parentBusinessUnit;
	}

	/**
	 * @return the approvedDate
	 */
	public Date getApprovedDate() {
		return approvedDate;
	}

	/**
	 * @param approvedDate the approvedDate to set
	 */
	public void setApprovedDate(Date approvedDate) {
		this.approvedDate = approvedDate;
	}

	/**
	 * @return the priceUnit
	 */
	public BigDecimal getPriceUnit() {
		return priceUnit;
	}

	/**
	 * @param priceUnit the priceUnit to set
	 */
	public void setPriceUnit(BigDecimal priceUnit) {
		this.priceUnit = priceUnit;
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
	 * @return the additionalField1
	 */
	public String getAdditionalField1() {
		return additionalField1;
	}

	/**
	 * @param additionalField1 the additionalField1 to set
	 */
	public void setAdditionalField1(String additionalField1) {
		this.additionalField1 = additionalField1;
	}

	/**
	 * @return the additionalField2
	 */
	public String getAdditionalField2() {
		return additionalField2;
	}

	/**
	 * @param additionalField2 the additionalField2 to set
	 */
	public void setAdditionalField2(String additionalField2) {
		this.additionalField2 = additionalField2;
	}

	/**
	 * @return the additionalField3
	 */
	public String getAdditionalField3() {
		return additionalField3;
	}

	/**
	 * @param additionalField3 the additionalField3 to set
	 */
	public void setAdditionalField3(String additionalField3) {
		this.additionalField3 = additionalField3;
	}

	/**
	 * @return the additionalField4
	 */
	public String getAdditionalField4() {
		return additionalField4;
	}

	/**
	 * @param additionalField4 the additionalField4 to set
	 */
	public void setAdditionalField4(String additionalField4) {
		this.additionalField4 = additionalField4;
	}

	/**
	 * @return the additionalField5
	 */
	public String getAdditionalField5() {
		return additionalField5;
	}

	/**
	 * @param additionalField5 the additionalField5 to set
	 */
	public void setAdditionalField5(String additionalField5) {
		this.additionalField5 = additionalField5;
	}

	/**
	 * @return the additionalField6
	 */
	public String getAdditionalField6() {
		return additionalField6;
	}

	/**
	 * @param additionalField6 the additionalField6 to set
	 */
	public void setAdditionalField6(String additionalField6) {
		this.additionalField6 = additionalField6;
	}

}
