package com.privasia.procurehere.core.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Email;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.privasia.procurehere.core.entity.Supplier.SupplierSignup;
import com.privasia.procurehere.core.enums.FavouriteSupplierStatus;
import com.privasia.procurehere.core.pojo.SupplierCountPojo;
import com.privasia.procurehere.core.utils.CustomDateTimeSerializer;
import com.privasia.procurehere.core.utils.StringUtils;

@Entity
@Table(name = "PROC_FAVOURITE_SUPPLIER")
@SqlResultSetMapping(name = "currentSupplierCount", classes = { @ConstructorResult(targetClass = SupplierCountPojo.class, columns = { @ColumnResult(name = "supplierCount", type = Integer.class), @ColumnResult(name = "industryCategoryName", type = String.class) }) })
public class FavouriteSupplier implements Serializable {

	public Date getSuspendStartDate() {
		return suspendStartDate;
	}

	public void setSuspendStartDate(Date suspendStartDate) {
		this.suspendStartDate = suspendStartDate;
	}

	public Date getSuspendEndDate() {
		return suspendEndDate;
	}

	public void setSuspendEndDate(Date suspendEndDate) {
		this.suspendEndDate = suspendEndDate;
	}

	private static final long serialVersionUID = 1957927293052336250L;

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "FAV_SUPPLIER_ID", length = 64)
	private String id;

	@NotNull(message = "{common.companyContactNumber.empty}")
	@Size(min = 6, max = 16, message = "{common.companyContactNumber.length}")
	@Column(name = "COMPANY_CONTACT_NUMBER", length = 16)
	private String companyContactNumber;

	@NotNull(message = "{common.fullName.empty}")
	@Size(min = 1, max = 128, message = "{common.fullName.length}")
	@Column(name = "FULL_NAME", length = 128, nullable = false)
	private String fullName;

	@NotNull(message = "{supplier.communicationEmail.empty}")
	@Email(message = "{supplier.communicationEmail.valid}")
	@Size(min = 1, max = 128, message = "{supplier.communicationEmail.length}")
	@Column(name = "COMMUNICATION_EMAIL", length = 128)
	private String communicationEmail;

	@NotNull(message = "{supplier.designation.empty}", groups = { SupplierSignup.class })
	@Size(min = 1, max = 128, message = "{supplier.designation.length}", groups = { SupplierSignup.class })
	@Column(name = "DESIGNATION", length = 128)
	private String designation;

	@ManyToOne(fetch = FetchType.LAZY, optional = true, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "SUPPLIER_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_BUY_FAV_SUPPL_ID"))
	private Supplier supplier;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinTable(name = "PROC_FAV_SUPP_IND_CAT", joinColumns = @JoinColumn(name = "FAV_SUPP_ID"), inverseJoinColumns = @JoinColumn(name = "IND_CAT_ID"))
	private List<IndustryCategory> industryCategory;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinTable(name = "PROC_FAV_SUPP_PRD_CAT", joinColumns = @JoinColumn(name = "FAV_SUPP_ID"), inverseJoinColumns = @JoinColumn(name = "PRODUCT_CAT_ID"))
	private List<ProductCategory> productCategory;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn(name = "BUYER_ID", nullable = true, foreignKey = @ForeignKey(name = "FK_BUY_FAV_BUYER_ID"))
	private Buyer buyer;

	@Enumerated(EnumType.STRING)
	@Column(name = "SUPPLIER_STATUS")
	private FavouriteSupplierStatus status = FavouriteSupplierStatus.ACTIVE;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Column(name = "CREATED_DATE", nullable = true, length = 20)
	private Date createdDate;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@Column(name = "LAST_MODIFIED_TIME", nullable = true, length = 20)
	private Date modifiedDate;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CREATED_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_FAV_SUP_CREATED_BY"))
	private User createdBy;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_LAST_MODIFIED_BY", nullable = true, foreignKey = @ForeignKey(name = "FK_FAV_SUP_MODIFIED_BY"))
	private User modifiedBy;

	@Column(name = "FAV_SUPPLIER_TAX_NO", length = 32)
	private String favouriteSupplierTaxNumber; // this is fax number on screen

	@Column(name = "FAV_VENDOR_CODE", length = 100)
	private String vendorCode;

	@Column(name = "TAX_NO", length = 32)
	private String taxNumber;

	@Column(name = "RATINGS", nullable = true)
	private BigDecimal ratings;

	@Column(name = "BLACKLIST_REMARK", length = 700)
	private String blackListRemark;

	@Column(name = "SUSPEND_REMARK", length = 700)
	private String suspendRemark;

	@Column(name = "SUSPEND_START_DATE", nullable = true)
	private Date suspendStartDate;

	@Column(name = "SUSPEND_END_DATE", nullable = true)
	private Date suspendEndDate;

	@Column(name = "IS_FUTURE_SUSPEND", nullable = true)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean isFutureSuspended = Boolean.FALSE;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinTable(name = "PROC_FAV_SUPPLIER_TAGS", joinColumns = @JoinColumn(name = "FAV_SUPP_ID"), inverseJoinColumns = @JoinColumn(name = "SUPP_TAG_ID"))
	private List<SupplierTags> supplierTags;

	@Column(name = "ASSOCIATED_DATE", nullable = true)
	private Date associatedDate;

	@Column(name = "SUBSIDIARY", nullable = false)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private boolean subsidiary = Boolean.FALSE;

	@Transient
	@Type(type = "org.hibernate.type.NumericBooleanType")
private boolean isSchedule;

	public FavouriteSupplier() {
	}

	public FavouriteSupplier(String id, String companyContactNumber, String communicationEmail, String supplierFaxNumber, String contactNumber, String supplierEmail, String faxNumber, String companyName, String supplierId) {
		super();
		this.id = id;
		this.companyContactNumber = StringUtils.checkString(companyContactNumber).isEmpty() ? contactNumber : companyContactNumber;
		this.communicationEmail = StringUtils.checkString(communicationEmail).isEmpty() ? supplierEmail : communicationEmail;
		this.favouriteSupplierTaxNumber = StringUtils.checkString(supplierFaxNumber).isEmpty() ? faxNumber : supplierFaxNumber;
		Supplier s = new Supplier();
		s.setCompanyName(companyName);
		s.setId(supplierId);
		this.supplier = s;
	}

	public FavouriteSupplier(String id, String companyName) {
		this.id = id;
		Supplier supp = new Supplier();
		supp.setCompanyName(companyName);
	}

	public FavouriteSupplier(String id, FavouriteSupplierStatus status, Date createdDate, Date associatedDate) {
		super();
		this.id = id;
		this.status = status;
		this.createdDate = createdDate;
		this.associatedDate = associatedDate;
	}

	public BigDecimal getRatings() {
		return ratings;
	}

	public void setRatings(BigDecimal ratings) {
		this.ratings = ratings;
	}

	@Transient
	private String suspensionDuration;

	public String getSuspensionDuration() {
		return suspensionDuration;
	}

	public void setSuspensionDuration(String suspensionDuration) {
		this.suspensionDuration = suspensionDuration;
	}

	public boolean isSchedule() {
		return isSchedule;
	}

	public void setSchedule(boolean isSchedule) {
		this.isSchedule = isSchedule;
	}

	public Boolean getIsFutureSuspended() {
		return isFutureSuspended;
	}

	public void setIsFutureSuspended(Boolean isFutureSuspended) {
		this.isFutureSuspended = isFutureSuspended;
	}

	public String getBlackListRemark() {
		return blackListRemark;
	}

	public void setBlackListRemark(String blackListRemark) {
		this.blackListRemark = blackListRemark;
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

	public String getSuspendRemark() {
		return suspendRemark;
	}

	public void setSuspendRemark(String suspendRemark) {
		this.suspendRemark = suspendRemark;
	}

	/**
	 * @return the companyContactNumber
	 */
	public String getCompanyContactNumber() {
		return companyContactNumber;
	}

	/**
	 * @param companyContactNumber the companyContactNumber to set
	 */
	public void setCompanyContactNumber(String companyContactNumber) {
		this.companyContactNumber = companyContactNumber;
	}

	/**
	 * @return the fullName
	 */
	public String getFullName() {
		return fullName;
	}

	/**
	 * @param fullName the fullName to set
	 */
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	/**
	 * @return the communicationEmail
	 */
	public String getCommunicationEmail() {
		return communicationEmail;
	}

	/**
	 * @param communicationEmail the communicationEmail to set
	 */
	public void setCommunicationEmail(String communicationEmail) {
		this.communicationEmail = communicationEmail;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	/**
	 * @return the supplier
	 */
	public Supplier getSupplier() {
		return supplier;
	}

	/**
	 * @param supplier the supplier to set
	 */
	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
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
	 * @return the industryCategory
	 */

	public List<IndustryCategory> getIndustryCategory() {
		return industryCategory;
	}

	/**
	 * @param industryCategory the industryCategory to set
	 */

	public void setIndustryCategory(List<IndustryCategory> industryCategory) {
		this.industryCategory = industryCategory;
	}

	// /**
	// * @return the supplierCompanyName
	// */
	// public String getSupplierCompanyName() {
	// try {
	// return supplier != null ? supplier.getCompanyName() : "";
	// } catch (Exception e) {
	// }
	// return "";
	// }
	//
	// /**
	// * @param supplierCompanyName the supplierCompanyName to set
	// */
	// public void setSupplierCompanyName(String supplierCompanyName) {
	// this.supplierCompanyName = supplierCompanyName;
	// }

	/**
	 * @return the status
	 */
	public FavouriteSupplierStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(FavouriteSupplierStatus status) {
		this.status = status;
	}

	/**
	 * @return the createdDate
	 */
	public Date getCreatedDate() {
		return createdDate;
	}

	/**
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * @return the modifiedDate
	 */
	public Date getModifiedDate() {
		return modifiedDate;
	}

	/**
	 * @param modifiedDate the modifiedDate to set
	 */
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	/**
	 * @return the createdBy
	 */
	public User getCreatedBy() {
		return createdBy;
	}

	/**
	 * @param createdBy the createdBy to set
	 */
	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	/**
	 * @return the modifiedBy
	 */
	public User getModifiedBy() {
		return modifiedBy;
	}

	/**
	 * @param modifiedBy the modifiedBy to set
	 */
	public void setModifiedBy(User modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	/**
	 * @return the favouriteSupplierTaxNumber
	 */
	public String getFavouriteSupplierTaxNumber() {
		return favouriteSupplierTaxNumber;
	}

	/**
	 * @param favouriteSupplierTaxNumber the favouriteSupplierTaxNumber to set
	 */
	public void setFavouriteSupplierTaxNumber(String favouriteSupplierTaxNumber) {
		this.favouriteSupplierTaxNumber = favouriteSupplierTaxNumber;
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

	public List<ProductCategory> getProductCategory() {
		return productCategory;
	}

	public void setProductCategory(List<ProductCategory> productCategory) {
		this.productCategory = productCategory;
	}

	/**
	 * @return the supplierTags
	 */
	public List<SupplierTags> getSupplierTags() {
		return supplierTags;
	}

	/**
	 * @param supplierTags the supplierTags to set
	 */
	public void setSupplierTags(List<SupplierTags> supplierTags) {
		this.supplierTags = supplierTags;
	}

	/**
	 * @return the taxNumber
	 */
	public String getTaxNumber() {
		return taxNumber;
	}

	/**
	 * @param taxNumber the taxNumber to set
	 */
	public void setTaxNumber(String taxNumber) {
		this.taxNumber = taxNumber;
	}

	/**
	 * @return the associatedDate
	 */
	public Date getAssociatedDate() {
		return associatedDate;
	}

	/**
	 * @param associatedDate the associatedDate to set
	 */
	public void setAssociatedDate(Date associatedDate) {
		this.associatedDate = associatedDate;
	}
	
	public boolean isSubsidiary() {
		return subsidiary;
	}

	public void setSubsidiary(boolean subsidiary) {
		this.subsidiary = subsidiary;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((communicationEmail == null) ? 0 : communicationEmail.hashCode());
		result = prime * result + ((companyContactNumber == null) ? 0 : companyContactNumber.hashCode());
		result = prime * result + ((fullName == null) ? 0 : fullName.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FavouriteSupplier other = (FavouriteSupplier) obj;
		if (communicationEmail == null) {
			if (other.communicationEmail != null)
				return false;
		} else if (!communicationEmail.equals(other.communicationEmail))
			return false;
		if (companyContactNumber == null) {
			if (other.companyContactNumber != null)
				return false;
		} else if (!companyContactNumber.equals(other.companyContactNumber))
			return false;
		if (fullName == null) {
			if (other.fullName != null)
				return false;
		} else if (!fullName.equals(other.fullName))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return fullName;
	}

}
