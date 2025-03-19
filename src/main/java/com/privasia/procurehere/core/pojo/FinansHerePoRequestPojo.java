/**
 * 
 */
package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Nitin Otageri
 */
public class FinansHerePoRequestPojo implements Serializable {

	private static final long serialVersionUID = 4983605399951091555L;

	String poName;
	String poNumber;
	BigDecimal poAmount;
	Date poDate;
	String buyerName;
	String buyerRoc;
	String supplierName;
	String supplierRoc;
	Integer paymentTerms;
	String supplierId;
	String buyerId;
	String poId;
	String currencyCode;
	String decimal;
	String actionBy;

	/**
	 * @return the poName
	 */
	public String getPoName() {
		return poName;
	}

	/**
	 * @param poName the poName to set
	 */
	public void setPoName(String poName) {
		this.poName = poName;
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
	 * @return the poAmount
	 */
	public BigDecimal getPoAmount() {
		return poAmount;
	}

	/**
	 * @param poAmount the poAmount to set
	 */
	public void setPoAmount(BigDecimal poAmount) {
		this.poAmount = poAmount;
	}

	/**
	 * @return the poDate
	 */
	public Date getPoDate() {
		return poDate;
	}

	/**
	 * @param poDate the poDate to set
	 */
	public void setPoDate(Date poDate) {
		this.poDate = poDate;
	}

	/**
	 * @return the buyerName
	 */
	public String getBuyerName() {
		return buyerName;
	}

	/**
	 * @param buyerName the buyerName to set
	 */
	public void setBuyerName(String buyerName) {
		this.buyerName = buyerName;
	}

	/**
	 * @return the buyerRoc
	 */
	public String getBuyerRoc() {
		return buyerRoc;
	}

	/**
	 * @param buyerRoc the buyerRoc to set
	 */
	public void setBuyerRoc(String buyerRoc) {
		this.buyerRoc = buyerRoc;
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
	 * @return the supplierRoc
	 */
	public String getSupplierRoc() {
		return supplierRoc;
	}

	/**
	 * @param supplierRoc the supplierRoc to set
	 */
	public void setSupplierRoc(String supplierRoc) {
		this.supplierRoc = supplierRoc;
	}

	/**
	 * @return the paymentTerms
	 */
	public Integer getPaymentTerms() {
		return paymentTerms;
	}

	/**
	 * @param paymentTerms the paymentTerms to set
	 */
	public void setPaymentTerms(Integer paymentTerms) {
		this.paymentTerms = paymentTerms;
	}

	/**
	 * @return the supplierId
	 */
	public String getSupplierId() {
		return supplierId;
	}

	/**
	 * @param supplierId the supplierId to set
	 */
	public void setSupplierId(String supplierId) {
		this.supplierId = supplierId;
	}

	/**
	 * @return the buyerId
	 */
	public String getBuyerId() {
		return buyerId;
	}

	/**
	 * @param buyerId the buyerId to set
	 */
	public void setBuyerId(String buyerId) {
		this.buyerId = buyerId;
	}

	/**
	 * @return the poId
	 */
	public String getPoId() {
		return poId;
	}

	/**
	 * @param poId the poId to set
	 */
	public void setPoId(String poId) {
		this.poId = poId;
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
	 * @return the actionBy
	 */
	public String getActionBy() {
		return actionBy;
	}

	/**
	 * @param actionBy the actionBy to set
	 */
	public void setActionBy(String actionBy) {
		this.actionBy = actionBy;
	}

	public String toLogString() {
		return "FinansHerePoRequestPojo [poName=" + poName + ", poNumber=" + poNumber + ", poAmount=" + poAmount + ", poDate=" + poDate + ", buyerName=" + buyerName + ", buyerRoc=" + buyerRoc + ", supplierName=" + supplierName + ", supplierRoc=" + supplierRoc + ", paymentTerms=" + paymentTerms + ", supplierId=" + supplierId + ", buyerId=" + buyerId + ", poId=" + poId + ", currencyCode=" + currencyCode + ", decimal=" + decimal + "]";
	}

}
