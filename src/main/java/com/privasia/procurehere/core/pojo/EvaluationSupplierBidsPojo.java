package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public class EvaluationSupplierBidsPojo implements Serializable {

	private static final long serialVersionUID = 2825518219112891948L;

	private String supplierName;

	// BQ Details
	private String bqDescription;
	private BigDecimal estimatedUnitPrice;
	private String uom;
	private BigInteger quantity;
	private BigDecimal unitPrice;
	private BigDecimal Amount;

	private BigDecimal changeFromOwnPrevious;
	private BigDecimal changeForPreviousLeadingBid;
	private List<EvaluationBiddingPricePojo> priceSubmissionList;
	private List<EvaluationBqItemPojo> bqItems;
	private BigDecimal initialPrice;
	private String decimals;
	private String currencyCode;
	private String remark;
	private String supplierRemark;
	private String time;

	/**
	 * @return the remark
	 */
	public String getRemark() {
		return remark;
	}

	/**
	 * @param remark
	 *            the remark to set
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}

	/**
	 * @return the supplierName
	 */
	public String getSupplierName() {
		return supplierName;
	}

	/**
	 * @param supplierName
	 *            the supplierName to set
	 */
	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	/**
	 * @return the bqDescription
	 */
	public String getBqDescription() {
		return bqDescription;
	}

	/**
	 * @param bqDescription
	 *            the bqDescription to set
	 */
	public void setBqDescription(String bqDescription) {
		this.bqDescription = bqDescription;
	}

	/**
	 * @return the estimatedUnitPrice
	 */
	public BigDecimal getEstimatedUnitPrice() {
		return estimatedUnitPrice;
	}

	/**
	 * @param estimatedUnitPrice
	 *            the estimatedUnitPrice to set
	 */
	public void setEstimatedUnitPrice(BigDecimal estimatedUnitPrice) {
		this.estimatedUnitPrice = estimatedUnitPrice;
	}

	/**
	 * @return the uom
	 */
	public String getUom() {
		return uom;
	}

	/**
	 * @param uom
	 *            the uom to set
	 */
	public void setUom(String uom) {
		this.uom = uom;
	}

	/**
	 * @return the quantity
	 */
	public BigInteger getQuantity() {
		return quantity;
	}

	/**
	 * @param quantity
	 *            the quantity to set
	 */
	public void setQuantity(BigInteger quantity) {
		this.quantity = quantity;
	}

	/**
	 * @return the unitPrice
	 */
	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	/**
	 * @param unitPrice
	 *            the unitPrice to set
	 */
	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}

	/**
	 * @return the amount
	 */
	public BigDecimal getAmount() {
		return Amount;
	}

	/**
	 * @param amount
	 *            the amount to set
	 */
	public void setAmount(BigDecimal amount) {
		Amount = amount;
	}

	/**
	 * @return the priceSubmissionList
	 */
	public List<EvaluationBiddingPricePojo> getPriceSubmissionList() {
		return priceSubmissionList;
	}

	/**
	 * @param priceSubmissionList
	 *            the priceSubmissionList to set
	 */
	public void setPriceSubmissionList(List<EvaluationBiddingPricePojo> priceSubmissionList) {
		this.priceSubmissionList = priceSubmissionList;
	}

	/**
	 * @return the initialPrice
	 */
	public BigDecimal getInitialPrice() {
		return initialPrice;
	}

	/**
	 * @param initialPrice
	 *            the initialPrice to set
	 */
	public void setInitialPrice(BigDecimal initialPrice) {
		this.initialPrice = initialPrice;
	}

	/**
	 * @return the decimal
	 */
	public String getDecimals() {
		return decimals;
	}

	/**
	 * @param decimal
	 *            the decimal to set
	 */
	public void setDecimals(String decimals) {
		this.decimals = decimals;
	}

	/**
	 * @return the currencyCode
	 */
	public String getCurrencyCode() {
		return currencyCode;
	}

	/**
	 * @param currencyCode
	 *            the currencyCode to set
	 */
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	/**
	 * @return the bqItems
	 */
	public List<EvaluationBqItemPojo> getBqItems() {
		return bqItems;
	}

	/**
	 * @param bqItems
	 *            the bqItems to set
	 */
	public void setBqItems(List<EvaluationBqItemPojo> bqItems) {
		this.bqItems = bqItems;
	}

	/**
	 * @return the supplierRemark
	 */
	public String getSupplierRemark() {
		return supplierRemark;
	}

	/**
	 * @param supplierRemark
	 *            the supplierRemark to set
	 */
	public void setSupplierRemark(String supplierRemark) {
		this.supplierRemark = supplierRemark;
	}

	/**
	 * @return the changeFromOwnPrevious
	 */
	public BigDecimal getChangeFromOwnPrevious() {
		return changeFromOwnPrevious;
	}

	/**
	 * @param changeFromOwnPrevious
	 *            the changeFromOwnPrevious to set
	 */
	public void setChangeFromOwnPrevious(BigDecimal changeFromOwnPrevious) {
		this.changeFromOwnPrevious = changeFromOwnPrevious;
	}

	/**
	 * @return the changeForPreviousLeadingBid
	 */
	public BigDecimal getChangeForPreviousLeadingBid() {
		return changeForPreviousLeadingBid;
	}

	/**
	 * @param changeForPreviousLeadingBid
	 *            the changeForPreviousLeadingBid to set
	 */
	public void setChangeForPreviousLeadingBid(BigDecimal changeForPreviousLeadingBid) {
		this.changeForPreviousLeadingBid = changeForPreviousLeadingBid;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

}
