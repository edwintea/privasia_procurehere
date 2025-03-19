package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;

import com.privasia.procurehere.core.entity.PrItem.PurchaseItem;
import com.privasia.procurehere.core.entity.ProductItem.ProductItemInt;
import com.privasia.procurehere.core.enums.ProductItemType;

@Entity
@Table(name = "PROC_PRODUCT_CONTRACT_ITEM")
public class ProductContractItems implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5660896686718692837L;

	public interface ProductContractItemInt {
	}

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "CONTRACT_ID", foreignKey = @ForeignKey(name = "FK_CONT_ITM_CONTRACT_ID"), nullable = false)
	private ProductContract productContract;

	@NotEmpty(message = "{product.contract.itemnumber.empty}", groups = ProductContractItemInt.class)
	@Size(min = 1, max = 16, message = "{product.contract.itemnumber.length}", groups = ProductContractItemInt.class)
	@Column(name = "CONTRACT_ITEM_NUMBER", length = 16, nullable = false)
	private String contractItemNumber;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "PRODUCT_ITEM_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_CON_ITM_CON_ID"))
	private ProductItem productItem;

	@Digits(integer = 16, fraction = 6, message = "{product.quantity.length}", groups = ProductContractItemInt.class)
	@Column(name = "QUANTITY", length = 20, nullable = true)
	private BigDecimal quantity;

	@Digits(integer = 22, fraction = 4, message = "{product.balance.quantity.length}", groups = ProductContractItemInt.class)
	@Column(name = "BALANCE_QUANTITY", length = 20, precision = 22, scale = 6, nullable = true)
	private BigDecimal balanceQuantity;

	/*
	 * @NotNull(message = "{uom.empty}", groups = ProductContractItemInt.class)
	 */
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "UOM_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_CON_ITM_UOM_ID"))
	private Uom uom;

	@Digits(integer = 22, fraction = 6, message = "{product.unitPrice.length}", groups = ProductContractItemInt.class)
	@Column(name = "UNIT_PRICE", length = 20, precision = 22, scale = 6, nullable = false)
	private BigDecimal unitPrice;

	/*
	 * @NotNull(message = "{productCategory.empty}", groups = ProductItemInt.class)
	 */
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "PRODUCT_CATEGORY_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_CON_ITM_ITEM_CAT_ID"))
	private ProductCategory productCategory;

	/*
	 * @NotNull(message = "{business.unit.empty}", groups = ProductItemInt.class)
	 */
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "BUSINESS_UNIT_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_CON_ITM_BUS_UNIT_ID"))
	private BusinessUnit businessUnit;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "COST_CENTER_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_CON_ITM_COST_CENTR_ID"))
	private CostCenter costCenter;

	@Size(min = 0, max = 500, message = "{brand.length}", groups = ProductItemInt.class)
	@Column(name = "BRAND", length = 64)
	private String brand;

	@Digits(integer = 16, fraction = 4, message = "{product.tax.length}", groups = ProductItemInt.class)
	@Column(name = "TAX", length = 20, nullable = true)
	private BigDecimal tax;

	@Column(name = "STORAGE_LOCATION", length = 32, nullable = true)
	private String storageLocation;

	@Enumerated(EnumType.STRING)
	@Column(name = "PRODUCT_ITEM_TYPE", nullable = true, length = 32)
	private ProductItemType productItemType;

	@Column(name = "ITEM_NAME", nullable = false, length = 250)
	private String itemName;

	@Column(name = "ITEM_CODE", nullable = true, length = 50)
	private String itemCode;

	@Column(name = "ITEM_DESCRIPTION", length = 1050)
	private String itemDescription;

	// to check item is free text or selected
	@Column(name = "FREE_ITEM_TEXT_ENTERED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean freeTextItemEntered = Boolean.FALSE;

	@Digits(integer = 16, fraction = 8, message = "{totalamount.length.error}", groups = { PurchaseItem.class })
	@Column(name = "TOTAL_AMOUNT", nullable = true, precision = 22, scale = 6)
	private BigDecimal totalAmount;

	@Column(name = "TAX_AMOUNT", nullable = true, precision = 22, scale = 6)
	private BigDecimal taxAmount;

	@Column(name = "TOTAL_AMT_WITH_TAX", nullable = true, precision = 22, scale = 6)
	private BigDecimal totalAmountWithTax;

	@Column(name = "IS_DELETED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean deleted = Boolean.FALSE;

	@Column(name = "OLD_QUANTITY", length = 20, nullable = true)
	private BigDecimal oldQuantity;

	@Digits(integer = 22, fraction = 6, message = "{product.unitPrice.length}", groups = ProductContractItemInt.class)
	@Column(name = "PRICE_PER_UNIT", length = 20, precision = 22, scale = 6, nullable = true)
	private BigDecimal pricePerUnit;

	@Column(name = "IS_ERP_TRANSFERRED", nullable = false)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean erpTransferred = Boolean.FALSE;

	@Transient
	private String productName;

	@Transient
	private FavouriteSupplier supplier;

	public ProductContractItems() {
		super();
	}

	public ProductContractItems(String id, String itemId, String productName, BigDecimal unitPrice, Uom uom, BigDecimal balanceQuantity, String storageLocation, FavouriteSupplier favouriteSupplier, ProductCategory productCategory, BusinessUnit businessUnit) {
		super();
		ProductItem user = new ProductItem();
		user.setProductName(productName);
		this.productName = user.getProductName();
		this.id = id;
		this.unitPrice = unitPrice;
		this.uom = uom;
		this.balanceQuantity = balanceQuantity;
		this.supplier = favouriteSupplier;
		this.productCategory = productCategory;
		this.storageLocation = storageLocation;
		this.businessUnit = businessUnit;
		this.productItem = new ProductItem(itemId);
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the productContract
	 */
	public ProductContract getProductContract() {
		return productContract;
	}

	/**
	 * @param productContract the productContract to set
	 */
	public void setProductContract(ProductContract productContract) {
		this.productContract = productContract;
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
	 * @return the uom
	 */
	public Uom getUom() {
		return uom;
	}

	/**
	 * @param uom the uom to set
	 */
	public void setUom(Uom uom) {
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
	 * @return the productCategory
	 */
	public ProductCategory getProductCategory() {
		return productCategory;
	}

	/**
	 * @param productCategory the productCategory to set
	 */
	public void setProductCategory(ProductCategory productCategory) {
		this.productCategory = productCategory;
	}

	/**
	 * @return the businessUnit
	 */
	public BusinessUnit getBusinessUnit() {
		return businessUnit;
	}

	/**
	 * @param businessUnit the businessUnit to set
	 */
	public void setBusinessUnit(BusinessUnit businessUnit) {
		this.businessUnit = businessUnit;
	}

	/**
	 * @return the productItem
	 */
	public ProductItem getProductItem() {
		return productItem;
	}

	/**
	 * @param productItem the productItem to set
	 */
	public void setProductItem(ProductItem productItem) {
		this.productItem = productItem;
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

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public FavouriteSupplier getSupplier() {
		return supplier;
	}

	public void setSupplier(FavouriteSupplier supplier) {
		this.supplier = supplier;
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
	 * @return the costCenter
	 */
	public CostCenter getCostCenter() {
		return costCenter;
	}

	/**
	 * @param costCenter the costCenter to set
	 */
	public void setCostCenter(CostCenter costCenter) {
		this.costCenter = costCenter;
	}

	@Override
	public String toString() {
		return "ProductContractItems [contractItemNumber=" + contractItemNumber + ", quantity=" + quantity + ", balanceQuantity=" + balanceQuantity + ", uom=" + uom + ", unitPrice=" + unitPrice + ", productName=" + productName + "]";
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
	 * @return the pricePerUnit
	 */
	public BigDecimal getPricePerUnit() {
		return pricePerUnit;
	}

	/**
	 * @param pricePerUnit the pricePerUnit to set
	 */
	public void setPricePerUnit(BigDecimal pricePerUnit) {
		this.pricePerUnit = pricePerUnit;
	}

	/**
	 * @param totalAmountWithTax the totalAmountWithTax to set
	 */
	public void setTotalAmountWithTax(BigDecimal totalAmountWithTax) {
		this.totalAmountWithTax = totalAmountWithTax;
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

	public BigDecimal getOldQuantity() {
		return oldQuantity;
	}

	public void setOldQuantity(BigDecimal oldQuantity) {
		this.oldQuantity = oldQuantity;
	}

	public Boolean getErpTransferred() {
		return erpTransferred;
	}

	public void setErpTransferred(Boolean erpTransferred) {
		this.erpTransferred = erpTransferred;
	}

}
