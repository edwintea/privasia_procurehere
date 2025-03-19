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
public class FinansHereInvoiceRequestPojo implements Serializable {

	private static final long serialVersionUID = 4983605399951091555L;

	private String invoiceTitle;
	private String invoiceNumber;
	private String poNumber;
	private String poId;
	private BigDecimal invoiceAmount;
	private Date invoiceDate;
	private String buyerName;
	private String buyerRoc;
	private String supplierName;
	private String supplierRoc;
	private Integer paymentTerms;
	private String supplierId;
	private String buyerId;
	private String invoiceId;
	private String currencyCode;
	private String decimal;
	private String actionBy;

	/**
	 * @return the invoiceTitle
	 */
	public String getInvoiceTitle() {
		return invoiceTitle;
	}

	/**
	 * @param invoiceTitle the invoiceTitle to set
	 */
	public void setInvoiceTitle(String invoiceTitle) {
		this.invoiceTitle = invoiceTitle;
	}

	/**
	 * @return the invoiceNumber
	 */
	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	/**
	 * @param invoiceNumber the invoiceNumber to set
	 */
	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}

	/**
	 * @return the invoiceAmount
	 */
	public BigDecimal getInvoiceAmount() {
		return invoiceAmount;
	}

	/**
	 * @param invoiceAmount the invoiceAmount to set
	 */
	public void setInvoiceAmount(BigDecimal invoiceAmount) {
		this.invoiceAmount = invoiceAmount;
	}

	/**
	 * @return the invoiceDate
	 */
	public Date getInvoiceDate() {
		return invoiceDate;
	}

	/**
	 * @param invoiceDate the invoiceDate to set
	 */
	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
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
	 * @return the invoiceId
	 */
	public String getInvoiceId() {
		return invoiceId;
	}

	/**
	 * @param invoiceId the invoiceId to set
	 */
	public void setInvoiceId(String invoiceId) {
		this.invoiceId = invoiceId;
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

	public String toLogString() {
		return "FinansHereInvoiceRequestPojo [invoiceTitle=" + invoiceTitle + ", invoiceNumber=" + invoiceNumber + ", poNumber=" + poNumber + ", invoiceAmount=" + invoiceAmount + ", invoiceDate=" + invoiceDate + ", buyerName=" + buyerName + ", buyerRoc=" + buyerRoc + ", supplierName=" + supplierName + ", supplierRoc=" + supplierRoc + ", paymentTerms=" + paymentTerms + ", supplierId=" + supplierId + ", buyerId=" + buyerId + ", invoiceId=" + invoiceId + ", currencyCode=" + currencyCode + ", decimal=" + decimal + ", actionBy=" + actionBy + "]";
	}

}
