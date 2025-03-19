package com.privasia.procurehere.core.pojo;

import java.io.Serializable;

/**
 * @author Priyanka
 */
public class RftEventAwardDetailsPojo implements Serializable {

	private static final long serialVersionUID = -1399044888943281514L;
	private String title;
	private String itemSeq;
	private String itemName;
	private String supplierName;
	private String supplierPrice;
	private String totalSupplierPrice;
	private String awardedPrice;
	private String totalAwardPrice;
	private String tax;
	private String taxType;
	private String refNo;
	private String awardRemarks;
	private String grandTotalPrice;
	private String totalPrice;
	private String currentDate;
	private String eventId;
	
	
	

	
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}



	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}



	/**
	 * @return the itemSeq
	 */
	public String getItemSeq() {
		return itemSeq;
	}



	/**
	 * @param itemSeq the itemSeq to set
	 */
	public void setItemSeq(String itemSeq) {
		this.itemSeq = itemSeq;
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
	 * @return the supplierName
	 */
	public String getSupplierName() {
		return supplierName;
	}



	/**
	 * @param supplierName the supplierName to set
	 */
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}



	/**
	 * @return the supplierPrice
	 */
	public String getSupplierPrice() {
		return supplierPrice;
	}



	/**
	 * @param supplierPrice the supplierPrice to set
	 */
	public void setSupplierPrice(String supplierPrice) {
		this.supplierPrice = supplierPrice;
	}

	

	/**
	 * @return the totalSupplierPrice
	 */
	public String getTotalSupplierPrice() {
		return totalSupplierPrice;
	}



	/**
	 * @param totalSupplierPrice the totalSupplierPrice to set
	 */
	public void setTotalSupplierPrice(String totalSupplierPrice) {
		this.totalSupplierPrice = totalSupplierPrice;
	}



	/**
	 * @return the awardedPrice
	 */
	public String getAwardedPrice() {
		return awardedPrice;
	}



	/**
	 * @param awardedPrice the awardedPrice to set
	 */
	public void setAwardedPrice(String awardedPrice) {
		this.awardedPrice = awardedPrice;
	}



	/**
	 * @return the totalAwardPrice
	 */
	public String getTotalAwardPrice() {
		return totalAwardPrice;
	}



	/**
	 * @param totalAwardPrice the totalAwardPrice to set
	 */
	public void setTotalAwardPrice(String totalAwardPrice) {
		this.totalAwardPrice = totalAwardPrice;
	}



	/**
	 * @return the tax
	 */
	public String getTax() {
		return tax;
	}



	/**
	 * @param tax the tax to set
	 */
	public void setTax(String tax) {
		this.tax = tax;
	}



	/**
	 * @param grandTotalPrice the grandTotalPrice to set
	 */
	public void setGrandTotalPrice(String grandTotalPrice) {
		this.grandTotalPrice = grandTotalPrice;
	}



	/**
	 * @return the taxType
	 */
	public String getTaxType() {
		return taxType;
	}



	/**
	 * @param taxType the taxType to set
	 */
	public void setTaxType(String taxType) {
		this.taxType = taxType;
	}



	/**
	 * @return the awardRemarks
	 */
	public String getAwardRemarks() {
		return awardRemarks;
	}



	/**
	 * @param awardRemarks the awardRemarks to set
	 */
	public void setAwardRemarks(String awardRemarks) {
		this.awardRemarks = awardRemarks;
	}



		

	/**
	 * @return the grandTotalPrice
	 */
	public String getGrandTotalPrice() {
		return grandTotalPrice;
	}



	/**
	 * @return the currentDate
	 */
	public String getCurrentDate() {
		return currentDate;
	}



	/**
	 * @param currentDate the currentDate to set
	 */
	public void setCurrentDate(String currentDate) {
		this.currentDate = currentDate;		
	}



	public String getTotalPrice() {
		return totalPrice;
	}



	public void setTotalPrice(String totalPrice) {
		this.totalPrice = totalPrice;
	}

	public String getEventId() {
		return eventId;
	}



	public void setEventId(String eventId) {
		this.eventId = eventId;
	}
	
	public String getRefNo() {
		return refNo;
	}



	public void setRefNo(String refNo) {
		this.refNo = refNo;
	}


	@Override
	public String toString() {
		return "RftEventAwardDetailsPojo [ title=" + title +
				", itemSeq=" + itemSeq + 
				", itemName=" + itemName +
				", supplierName=" + supplierName +
				", supplierPrice=" + supplierPrice +
				", totalSupplierPrice=" + totalSupplierPrice +
				", awardedPrice=" + awardedPrice +
				", totalAwardPrice=" + totalAwardPrice +
				", tax=" + tax +
				", taxType=" + taxType +
				", refNo=" + refNo +
				", awardRemarks=" + awardRemarks +
				", grandTotalPrice=" + grandTotalPrice +
				", totalPrice=" + totalPrice +
				", currentDate=" + currentDate +    "]"  ;
	}








}
