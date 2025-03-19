/**
 * 
 */
package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author ravi
 */
public class ErpPoPojo implements Serializable {

	private static final long serialVersionUID = -5293785785202614619L;

	private String businesUnit;

	private String name;

	private BigDecimal grandTotal = BigDecimal.ZERO;

	private String supplierCode;

	private String supplierName;

	private String supplierAddress;

	private String supplierTelNumber;

	private String supplierFaxNumber;

	private String supplierTaxNumber;

	private String poNumber;

	private String poDate;

	private String currencyCode;

	private String paymentTerm;

	private String deliveryAddressTitle;

	private String deliveryAddressLine1;

	private String deliveryAddressLine2;

	private String deliveryAddressCity;

	private String deliveryAddressState;

	private String deliveryAddressZip;

	private String deliveryAddressCountry;

	private String deliveryReceiver;

	private List<ErpPoItemPojo> itemList;

	private String appId;

	/**
	 * @return the businesUnit
	 */
	public String getBusinesUnit() {
		return businesUnit;
	}

	/**
	 * @param businesUnit the businesUnit to set
	 */
	public void setBusinesUnit(String businesUnit) {
		this.businesUnit = businesUnit;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the grandTotal
	 */
	public BigDecimal getGrandTotal() {
		return grandTotal;
	}

	/**
	 * @param grandTotal the grandTotal to set
	 */
	public void setGrandTotal(BigDecimal grandTotal) {
		this.grandTotal = grandTotal;
	}

	/**
	 * @return the supplierCode
	 */
	public String getSupplierCode() {
		return supplierCode;
	}

	/**
	 * @param supplierCode the supplierCode to set
	 */
	public void setSupplierCode(String supplierCode) {
		this.supplierCode = supplierCode;
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
	 * @return the supplierAddress
	 */
	public String getSupplierAddress() {
		return supplierAddress;
	}

	/**
	 * @param supplierAddress the supplierAddress to set
	 */
	public void setSupplierAddress(String supplierAddress) {
		this.supplierAddress = supplierAddress;
	}

	/**
	 * @return the supplierTelNumber
	 */
	public String getSupplierTelNumber() {
		return supplierTelNumber;
	}

	/**
	 * @param supplierTelNumber the supplierTelNumber to set
	 */
	public void setSupplierTelNumber(String supplierTelNumber) {
		this.supplierTelNumber = supplierTelNumber;
	}

	/**
	 * @return the supplierFaxNumber
	 */
	public String getSupplierFaxNumber() {
		return supplierFaxNumber;
	}

	/**
	 * @param supplierFaxNumber the supplierFaxNumber to set
	 */
	public void setSupplierFaxNumber(String supplierFaxNumber) {
		this.supplierFaxNumber = supplierFaxNumber;
	}

	/**
	 * @return the supplierTaxNumber
	 */
	public String getSupplierTaxNumber() {
		return supplierTaxNumber;
	}

	/**
	 * @param supplierTaxNumber the supplierTaxNumber to set
	 */
	public void setSupplierTaxNumber(String supplierTaxNumber) {
		this.supplierTaxNumber = supplierTaxNumber;
	}

	/**
	 * @return the poNumber
	 */
	public String getPoNumber() {
		return poNumber;
	}

	/**
	 * @param poNumber the poNumber to set
	 */
	public void setPoNumber(String poNumber) {
		this.poNumber = poNumber;
	}

	/**
	 * @return the poDate
	 */
	public String getPoDate() {
		return poDate;
	}

	/**
	 * @param poDate the poDate to set
	 */
	public void setPoDate(String poDate) {
		this.poDate = poDate;
	}

	/**
	 * @return the currencyCode
	 */
	public String getCurrencyCode() {
		return currencyCode;
	}

	/**
	 * @param currencyCode the currencyCode to set
	 */
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	/**
	 * @return the paymentTerm
	 */
	public String getPaymentTerm() {
		return paymentTerm;
	}

	/**
	 * @param paymentTerm the paymentTerm to set
	 */
	public void setPaymentTerm(String paymentTerm) {
		this.paymentTerm = paymentTerm;
	}

	/**
	 * @return the deliveryAddressTitle
	 */
	public String getDeliveryAddressTitle() {
		return deliveryAddressTitle;
	}

	/**
	 * @param deliveryAddressTitle the deliveryAddressTitle to set
	 */
	public void setDeliveryAddressTitle(String deliveryAddressTitle) {
		this.deliveryAddressTitle = deliveryAddressTitle;
	}

	/**
	 * @return the deliveryAddressLine1
	 */
	public String getDeliveryAddressLine1() {
		return deliveryAddressLine1;
	}

	/**
	 * @param deliveryAddressLine1 the deliveryAddressLine1 to set
	 */
	public void setDeliveryAddressLine1(String deliveryAddressLine1) {
		this.deliveryAddressLine1 = deliveryAddressLine1;
	}

	/**
	 * @return the deliveryAddressLine2
	 */
	public String getDeliveryAddressLine2() {
		return deliveryAddressLine2;
	}

	/**
	 * @param deliveryAddressLine2 the deliveryAddressLine2 to set
	 */
	public void setDeliveryAddressLine2(String deliveryAddressLine2) {
		this.deliveryAddressLine2 = deliveryAddressLine2;
	}

	/**
	 * @return the deliveryAddressCity
	 */
	public String getDeliveryAddressCity() {
		return deliveryAddressCity;
	}

	/**
	 * @param deliveryAddressCity the deliveryAddressCity to set
	 */
	public void setDeliveryAddressCity(String deliveryAddressCity) {
		this.deliveryAddressCity = deliveryAddressCity;
	}

	/**
	 * @return the deliveryAddressState
	 */
	public String getDeliveryAddressState() {
		return deliveryAddressState;
	}

	/**
	 * @param deliveryAddressState the deliveryAddressState to set
	 */
	public void setDeliveryAddressState(String deliveryAddressState) {
		this.deliveryAddressState = deliveryAddressState;
	}

	/**
	 * @return the deliveryAddressZip
	 */
	public String getDeliveryAddressZip() {
		return deliveryAddressZip;
	}

	/**
	 * @param deliveryAddressZip the deliveryAddressZip to set
	 */
	public void setDeliveryAddressZip(String deliveryAddressZip) {
		this.deliveryAddressZip = deliveryAddressZip;
	}

	/**
	 * @return the deliveryAddressCountry
	 */
	public String getDeliveryAddressCountry() {
		return deliveryAddressCountry;
	}

	/**
	 * @param deliveryAddressCountry the deliveryAddressCountry to set
	 */
	public void setDeliveryAddressCountry(String deliveryAddressCountry) {
		this.deliveryAddressCountry = deliveryAddressCountry;
	}

	/**
	 * @return the deliveryReceiver
	 */
	public String getDeliveryReceiver() {
		return deliveryReceiver;
	}

	/**
	 * @param deliveryReceiver the deliveryReceiver to set
	 */
	public void setDeliveryReceiver(String deliveryReceiver) {
		this.deliveryReceiver = deliveryReceiver;
	}

	/**
	 * @return the itemList
	 */
	public List<ErpPoItemPojo> getItemList() {
		return itemList;
	}

	/**
	 * @param itemList the itemList to set
	 */
	public void setItemList(List<ErpPoItemPojo> itemList) {
		this.itemList = itemList;
	}

	/**
	 * @return the appId
	 */
	public String getAppId() {
		return appId;
	}

	/**
	 * @param appId the appId to set
	 */
	public void setAppId(String appId) {
		this.appId = appId;
	}

}
