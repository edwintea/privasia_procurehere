package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.enums.ProductItemType;
import com.privasia.procurehere.core.enums.Status;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;

@Entity
@Table(name = "PROC_PRODUCT_ITEM")
@SqlResultSetMapping(name = "winnerSupplierResult", classes = { @ConstructorResult(targetClass = Supplier.class, columns = { @ColumnResult(name = "id"), @ColumnResult(name = "totalAfterTax"), @ColumnResult(name = "createdDate") }) })
public class ProductItem implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -651372841187980055L;

	public interface ProductItemInt {
	}

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@NotEmpty(message = "{productitem.productCode.empty}", groups = ProductItemInt.class)
	@Size(min = 1, max = 50, message = "{productitem.productCode.length}", groups = ProductItemInt.class)
	@Column(name = "PRODUCT_CODE", length = 50, nullable = false)
	private String productCode;

	@NotEmpty(message = "{product.productName.empty}", groups = ProductItemInt.class)
	@Size(min = 1, max = 64, message = "{product.productName.length}", groups = ProductItemInt.class)
	@Column(name = "PRODUCT_NAME", length = 100, nullable = false)
	private String productName;

	// @JsonIgnore
	@NotNull(message = "{uom.empty}", groups = ProductItemInt.class)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UOM", nullable = false, foreignKey = @ForeignKey(name = "PRD_LST_UOM"))
	private Uom uom;

	@Digits(integer = 16, fraction = 4, message = "{product.unitPrice.length}", groups = ProductItemInt.class)
	@Column(name = "UNIT_PRICE", length = 20, nullable = true)
	private BigDecimal unitPrice;

	@Digits(integer = 16, fraction = 4, message = "{product.tax.length}", groups = ProductItemInt.class)
	@Column(name = "TAX", length = 20, nullable = true)
	private BigDecimal tax;

	@Size(min = 0, max = 300, message = "{remarks.length}", groups = ProductItemInt.class)
	@Column(name = "REMARKS", length = 300, nullable = true)
	private String remarks;

	@Temporal(TemporalType.DATE)
	@Column(name = "VALIDITY_DATE")
	private Date validityDate;

	@Size(min = 0, max = 20, message = "{glCode.length}", groups = ProductItemInt.class)
	@Column(name = "GL_CODE", length = 20, nullable = true)
	private String glCode;

	@Size(min = 0, max = 10, message = "{unspscCode.length}", groups = ProductItemInt.class)
	@Column(name = "UNSPSC_CODE", length = 10, nullable = true)
	private String unspscCode;

	@NotNull(message = "{productCategory.empty}", groups = ProductItemInt.class)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PRODUCT_CATEGORY", nullable = true, foreignKey = @ForeignKey(name = "PRD_ITM_CATEGORY"))
	private ProductCategory productCategory;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FAVORITE_SUPPLIER", nullable = true, foreignKey = @ForeignKey(name = "PRD_ITM_FAV_SUP"))
	private FavouriteSupplier favoriteSupplier;

	@Lob
	@Type(type = "org.hibernate.type.BinaryType")
	@Column(name = "FILE_DATA", nullable = true)
	private byte[] fileAttatchment;

	@Column(name = "FILE_NAME", length = 500, nullable = true)
	private String fileName;

	@Column(name = "CONTENT_TYPE", length = 160, nullable = true)
	private String contentType;

	@Enumerated(EnumType.STRING)
	@Column(name = "PRODUCT_ACTIVE", nullable = false, length = 32)
	private Status status;

	@Enumerated(EnumType.STRING)
	@Column(name = "PRODUCT_ITEM_TYPE", nullable = true, length = 32)
	private ProductItemType productItemType;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "CREATED_BY", nullable = true, foreignKey = @ForeignKey(name = "PRD_ITM_CREATED_BY"))
	private User createdBy;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Column(name = "CREATION_DATE", nullable = true, length = 20)
	private Date createdDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Column(name = "MODIFIED_TIME", nullable = true, length = 20)
	private Date modifiedDate;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "MODIFIED_BY", nullable = true, foreignKey = @ForeignKey(name = "PRD_ITM_MODIFIED_BY"))
	private User modifiedBy;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "TENANT_ID", nullable = true, foreignKey = @ForeignKey(name = "PRD_ITM_BUYER_ID"))
	private Buyer buyer;

	@Column(name = "CONTRACT_ITEM")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private boolean contractItem = false;

	@Size(min = 0, max = 30, message = "{historicPricingRefNo.length}", groups = ProductItemInt.class)
	@Column(name = "HISTORIC_PRICING_REF_NO", length = 30)
	private String historicPricingRefNo;

	@Size(min = 0, max = 30, message = "{purchaseGroupCode.length}", groups = ProductItemInt.class)
	@Column(name = "PURCHASE_GROUP_CODE", length = 30)
	private String purchaseGroupCode;

	@Size(min = 0, max = 500, message = "{brand.length}", groups = ProductItemInt.class)
	@Column(name = "BRAND", length = 500)
	private String brand;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Column(name = "START_DATE", nullable = true)
	@Temporal(TemporalType.DATE)
	private Date startDate;

	@Column(name = "CONTRACT_REFERENCE_NUMBER", nullable = true, length = 50)
	private String contractReferenceNumber;

	@NotEmpty(message = "{product.interfaceCode.empty}", groups = ProductItemInt.class)
	@Size(min = 1, max = 20, message = "{product.interfaceCode.length}", groups = ProductItemInt.class)
	@Column(name = "INTERFACE_CODE", length = 20, nullable = false)
	private String interfaceCode;

	@Transient
	private String dateTimeRange;

	@Transient
	// used only for SAP Integration
	private String itemContractReferenceNumber;

	@Transient
	// used only for SAP Integration
	private String storageLocation;

	public String getContractReferenceNumber() {
		return contractReferenceNumber;
	}

	public void setContractReferenceNumber(String contractReferenceNumber) {
		this.contractReferenceNumber = contractReferenceNumber;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public String getDateTimeRange() {
		return dateTimeRange;
	}

	public void setDateTimeRange(String dateTimeRange) {
		this.dateTimeRange = dateTimeRange;
	}

	public ProductItem() {

	}

	public ProductItem(String id) {
		this.id = id;
	}

	public ProductItem(String id, String productCode, String productName, Uom uom, BigDecimal unitPrice, BigDecimal tax, ProductCategory productCategory, FavouriteSupplier favoriteSupplier, Status status) {
		super();
		this.id = id;
		this.productCode = productCode;
		this.productName = productName;
		this.uom = uom;
		this.unitPrice = unitPrice;
		this.tax = tax;
		this.productCategory = productCategory;
		this.favoriteSupplier = favoriteSupplier;
		this.status = status;
	}

	public String getSupplierName() {
		try {
			if (this.favoriteSupplier != null && this.favoriteSupplier.getSupplier() != null) {
				return this.favoriteSupplier.getSupplier().getCompanyName();
			}
		} catch (Exception e) {
		}
		return null;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Uom getUom() {
		return uom;
	}

	public void setUom(Uom uom) {
		this.uom = uom;
	}

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
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

	public User getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(User modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Date getValidityDate() {
		return validityDate;
	}

	public void setValidityDate(Date validityDate) {
		this.validityDate = validityDate;
	}

	public String getGlCode() {
		return glCode;
	}

	public void setGlCode(String glCode) {
		this.glCode = glCode;
	}

	public String getUnspscCode() {
		return unspscCode;
	}

	public void setUnspscCode(String unspscCode) {
		this.unspscCode = unspscCode;
	}

	public ProductCategory getProductCategory() {
		return productCategory;
	}

	public void setProductCategory(ProductCategory productCategory) {
		this.productCategory = productCategory;
	}

	public byte[] getFileAttatchment() {
		return fileAttatchment;
	}

	public void setFileAttatchment(byte[] fileAttatchment) {
		this.fileAttatchment = fileAttatchment;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public FavouriteSupplier getFavoriteSupplier() {
		return favoriteSupplier;
	}

	public void setFavoriteSupplier(FavouriteSupplier favoriteSupplier) {
		this.favoriteSupplier = favoriteSupplier;
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
	 * @return the buyer
	 */
	public Buyer getBuyer() {
		return buyer;
	}

	/**
	 * @param buyer the buyer to set
	 */
	public void setBuyer(Buyer buyer) {
		this.buyer = buyer;
	}

	/**
	 * @return the contractItem
	 */
	public boolean isContractItem() {
		return contractItem;
	}

	/**
	 * @param contractItem the contractItem to set
	 */
	public void setContractItem(boolean contractItem) {
		this.contractItem = contractItem;
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

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
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
	 * @return the itemContractReferenceNumber
	 */
	public String getItemContractReferenceNumber() {
		return itemContractReferenceNumber;
	}

	/**
	 * @param itemContractReferenceNumber the itemContractReferenceNumber to set
	 */
	public void setItemContractReferenceNumber(String itemContractReferenceNumber) {
		this.itemContractReferenceNumber = itemContractReferenceNumber;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((productName == null) ? 0 : productName.hashCode());
		result = prime * result + ((productCode == null) ? 0 : productCode.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
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
		ProductItem other = (ProductItem) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (productCode == null) {
			if (other.productCode != null)
				return false;
		} else if (!productCode.equals(other.productCode))
			return false;
		if (productName == null) {
			if (other.productName != null)
				return false;
		} else if (!productName.equals(other.productName))
			return false;
		if (status != other.status)
			return false;
		return true;
	}

	public ProductItem createShallowCopy() {
		ProductItem ic = new ProductItem();
		ic.setInterfaceCode(getInterfaceCode());
		ic.setId(getId());
		ic.setUom(getUom().createShallowCopy());
		ic.setProductCode(getProductCode());
		ic.setProductName(getProductName());
		ic.setUnitPrice(getUnitPrice());
		ic.setFileName(getFileName());
		ic.setContentType(getContentType());
		ic.setRemarks(getRemarks());
		ic.setGlCode(getGlCode());
		ic.setUnspscCode(getUnspscCode());
		ic.setStatus(getStatus());
		ProductCategory test = getProductCategory();
		if (getProductCategory() != null) {
			ic.setProductCategory(getProductCategory().createShallowCopy());
		}
		ic.setPurchaseGroupCode(getPurchaseGroupCode());
		ic.setProductItemType(getProductItemType());
		try {
			if (this.favoriteSupplier != null && this.favoriteSupplier.getSupplier() != null) {
				ic.setFavoriteSupplier(favoriteSupplier);
			}
		} catch (Exception e) {
		}

		if (ic.getCreatedBy() != null) {
			ic.getCreatedBy().setPrfPicName(null);
			ic.getCreatedBy().setId(null);
			ic.getCreatedBy().setLastLoginTime(null);
			ic.getCreatedBy().setTenantId(null);
			ic.getCreatedBy().setTenantType(null);
			ic.getCreatedBy().setDesignation(null);
			ic.getCreatedBy().setCreatedDate(null);
			ic.getCreatedBy().setCommunicationEmail(null);
			ic.getCreatedBy().setFailedAttempts(null);
			ic.getCreatedBy().setLastFailedLoginTime(null);
			ic.getCreatedBy().setLastPasswordChangedDate(null);
			ic.getCreatedBy().setPrfPicAttatchment(null);
			ic.getCreatedBy().setModifiedBy(null);

		}
		ic.setCreatedBy(getCreatedBy());
		ic.setCreatedDate(getCreatedDate());
		if (ic.getModifiedBy() != null) {
			ic.getModifiedBy().setPrfPicName(null);
			ic.getModifiedBy().setTenantId(null);
			ic.getModifiedBy().setTenantType(null);
			ic.getModifiedBy().setId(null);
			ic.getModifiedBy().setDesignation(null);
			ic.getModifiedBy().setCreatedDate(null);
			ic.getModifiedBy().setCommunicationEmail(null);
			ic.getModifiedBy().setFailedAttempts(null);
			ic.getModifiedBy().setLastLoginTime(null);
			ic.getModifiedBy().setLastFailedLoginTime(null);
			ic.getModifiedBy().setLastPasswordChangedDate(null);
			ic.getModifiedBy().setPrfPicAttatchment(null);
			ic.getModifiedBy().setModifiedBy(null);

		}
		ic.setModifiedBy(getModifiedBy());
		ic.setModifiedDate(getModifiedDate());
		ic.setContractItem(isContractItem());
		return ic;
	}

	public ProductItem createShallowCopyForAttachment() {
		ProductItem ic = new ProductItem();
		ic.setId(getId());
		ic.setUom(getUom().createShallowCopy());
		ic.setInterfaceCode(getInterfaceCode());
		ic.setProductCode(getProductCode());
		ic.setProductName(getProductName());
		ic.setUnitPrice(getUnitPrice());
		ic.setFileName(getFileName());
		ic.setContentType(getContentType());
		ic.setFileAttatchment(getFileAttatchment());
		ic.setRemarks(getRemarks());
		ic.setGlCode(getGlCode());
		ic.setUnspscCode(getUnspscCode());
		ic.setStatus(getStatus());
		ic.setProductCategory(getProductCategory());
		ic.setProductItemType(getProductItemType());

		try {
			if (this.favoriteSupplier != null && this.favoriteSupplier.getSupplier() != null) {
				ic.setFavoriteSupplier(favoriteSupplier);
			}
		} catch (Exception e) {
		}

		if (ic.getCreatedBy() != null) {
			ic.getCreatedBy().setPrfPicName(null);
			ic.getCreatedBy().setId(null);
			ic.getCreatedBy().setLastLoginTime(null);
			ic.getCreatedBy().setTenantId(null);
			ic.getCreatedBy().setTenantType(null);
			ic.getCreatedBy().setDesignation(null);
			ic.getCreatedBy().setCreatedDate(null);
			ic.getCreatedBy().setCommunicationEmail(null);
			ic.getCreatedBy().setFailedAttempts(null);
			ic.getCreatedBy().setLastFailedLoginTime(null);
			ic.getCreatedBy().setLastPasswordChangedDate(null);
			ic.getCreatedBy().setPrfPicAttatchment(null);
			ic.getCreatedBy().setModifiedBy(null);

		}
		ic.setCreatedBy(getCreatedBy());
		ic.setCreatedDate(getCreatedDate());
		if (ic.getModifiedBy() != null) {
			ic.getModifiedBy().setPrfPicName(null);
			ic.getModifiedBy().setTenantId(null);
			ic.getModifiedBy().setTenantType(null);
			ic.getModifiedBy().setId(null);
			ic.getModifiedBy().setDesignation(null);
			ic.getModifiedBy().setCreatedDate(null);
			ic.getModifiedBy().setCommunicationEmail(null);
			ic.getModifiedBy().setFailedAttempts(null);
			ic.getModifiedBy().setLastLoginTime(null);
			ic.getModifiedBy().setLastFailedLoginTime(null);
			ic.getModifiedBy().setLastPasswordChangedDate(null);
			ic.getModifiedBy().setPrfPicAttatchment(null);
			ic.getModifiedBy().setModifiedBy(null);

		}
		ic.setModifiedBy(getModifiedBy());
		ic.setModifiedDate(getModifiedDate());
		ic.setContractItem(isContractItem());
		return ic;
	}

	public String toLogString() {
		return "ProductListMaintenance [productCode=" + productCode + ", productName=" + productName + ", unitPrice=" + unitPrice + ", tax=" + tax + ", remarks=" + remarks + ", validityDate=" + validityDate + ", glCode=" + glCode + ", unspscCode=" + unspscCode + "]";
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return productCode + "-" + productName;
	}

}
