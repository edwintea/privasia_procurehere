package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;

import com.privasia.procurehere.core.enums.FavouriteSupplierStatus;

/**
 * @author Javed Ahmed
 */
public class ImportSupplierPojo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3893420226352349654L;
	private String id;
	private String contactNumber;
	private String communicaionEmail;
	private String fullName;
	private String designation;
	private String indCat[];
	private FavouriteSupplierStatus status;
	private String saveOrUpdate;
	private String favouriteSupplierTaxNumber;
	private String vendorCode;
	private String[] productCategory;
	private String[] supplierTags;
	private Boolean subsidiary;

	private BigDecimal ratings;

	public BigDecimal getRatings() {
		return ratings;
	}

	public void setRatings(BigDecimal ratings) {
		this.ratings = ratings;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public String getCommunicaionEmail() {
		return communicaionEmail;
	}

	public void setCommunicaionEmail(String communicaionEmail) {
		this.communicaionEmail = communicaionEmail;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public String[] getIndCat() {
		return indCat;
	}

	public void setIndCat(String[] indCat) {
		this.indCat = indCat;
	}

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

	public String getSaveOrUpdate() {
		return saveOrUpdate;
	}

	public void setSaveOrUpdate(String saveOrUpdate) {
		this.saveOrUpdate = saveOrUpdate;
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

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toLogString() {
		return "ImportSupplierPojo [id=" + id + ", contactNumber=" + contactNumber + ", communicaionEmail=" + communicaionEmail + ", fullName=" + fullName + ", designation=" + designation + ", indCat=" + Arrays.toString(indCat) + ", status=" + status + ", saveOrUpdate=" + saveOrUpdate + "]";
	}

	public String[] getProductCategory() {
		return productCategory;
	}

	public void setProductCategory(String[] productCategory) {
		this.productCategory = productCategory;
	}

	/**
	 * @return the supplierTags
	 */
	public String[] getSupplierTags() {
		return supplierTags;
	}

	/**
	 * @param supplierTags the supplierTags to set
	 */
	public void setSupplierTags(String[] supplierTags) {
		this.supplierTags = supplierTags;
	}

	public Boolean getSubsidiary() {
		return subsidiary;
	}

	public void setSubsidiary(Boolean subsidiary) {
		this.subsidiary = subsidiary;
	}

}
