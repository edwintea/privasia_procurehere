package com.privasia.procurehere.core.entity;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.privasia.procurehere.core.enums.ProductItemType;

@Entity
@Table(name = "PROC_RFT_EVENT_AWARD_DETAILS")
public class RftEventAwardDetails extends AwardDetails implements Serializable {

	private static final long serialVersionUID = 2380243016865784373L;

	@ManyToOne(optional = true, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "EVENT_AWARD", nullable = true, foreignKey = @ForeignKey(name = "FK_RFT_EVNT_AWD_DT"))
	private RftEventAward eventAward;

	@ManyToOne(optional = false, cascade = { CascadeType.REFRESH })
	@JoinColumn(name = "BQ_ITEM_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_RFT_EVNT_AWD_BQ_ITEM"))
	private RftBqItem bqItem;

	@Transient
	private String selectItem;

	@Transient
	private String productCategory;

	@Transient
	private String productCode;
	
	@Transient
	private String brand;
	
	@Transient
	private String costCenter;
	
	@Transient
	private String businessUnit;
	
	@Transient
	private ProductItemType productType;

	/**
	 * @return the eventAward
	 */
	public RftEventAward getEventAward() {
		return eventAward;
	}

	/**
	 * @param eventAward the eventAward to set
	 */
	public void setEventAward(RftEventAward eventAward) {
		this.eventAward = eventAward;
	}

	/**
	 * @return the bqItem
	 */
	public RftBqItem getBqItem() {
		return bqItem;
	}

	/**
	 * @param bqItem the bqItem to set
	 */
	public void setBqItem(RftBqItem bqItem) {
		this.bqItem = bqItem;
	}

	/**
	 * @return the selectItem
	 */
	public String getSelectItem() {
		return selectItem;
	}

	/**
	 * @param selectItem the selectItem to set
	 */
	public void setSelectItem(String selectItem) {
		this.selectItem = selectItem;
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
	 * @return the productType
	 */
	public ProductItemType getProductType() {
		return productType;
	}

	/**
	 * @param productType the productType to set
	 */
	public void setProductType(ProductItemType productType) {
		this.productType = productType;
	}
	
	/*
	 * @Override public String toString() { return "RftEventAwardDetails [eventAward=" + eventAward + ", bqItem=" +
	 * bqItem + "]"; }
	 */

}
