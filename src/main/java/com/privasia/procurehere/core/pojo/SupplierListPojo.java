package com.privasia.procurehere.core.pojo;

import java.math.BigDecimal;

public class SupplierListPojo {

	private String supplierName;
	private BigDecimal submissionPrice;
	private int numberOfBids;
	private boolean revisedBidSubmitted;
	private BigDecimal totalAfterTax;
	private String supplierName_NotRevisedBq;
	private boolean showNote = false;

	public SupplierListPojo() {
		super();
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
	 * @return the submissionPrice
	 */
	public BigDecimal getSubmissionPrice() {
		return submissionPrice;
	}

	/**
	 * @param submissionPrice the submissionPrice to set
	 */
	public void setSubmissionPrice(BigDecimal submissionPrice) {
		this.submissionPrice = submissionPrice;
	}

	/**
	 * @return the numberOfBids
	 */
	public int getNumberOfBids() {
		return numberOfBids;
	}

	/**
	 * @param numberOfBids the numberOfBids to set
	 */
	public void setNumberOfBids(int numberOfBids) {
		this.numberOfBids = numberOfBids;
	}

	/**
	 * @return the revisedBidSubmitted
	 */
	public boolean isRevisedBidSubmitted() {
		return revisedBidSubmitted;
	}

	/**
	 * @param revisedBidSubmitted the revisedBidSubmitted to set
	 */
	public void setRevisedBidSubmitted(boolean revisedBidSubmitted) {
		this.revisedBidSubmitted = revisedBidSubmitted;
	}

	/**
	 * @return the totalAfterTax
	 */
	public BigDecimal getTotalAfterTax() {
		return totalAfterTax;
	}

	/**
	 * @param totalAfterTax the totalAfterTax to set
	 */
	public void setTotalAfterTax(BigDecimal totalAfterTax) {
		this.totalAfterTax = totalAfterTax;
	}

	/**
	 * @return the supplierName_NotRevisedBq
	 */
	public String getSupplierName_NotRevisedBq() {
		return supplierName_NotRevisedBq;
	}

	/**
	 * @param supplierName_NotRevisedBq the supplierName_NotRevisedBq to set
	 */
	public void setSupplierName_NotRevisedBq(String supplierName_NotRevisedBq) {
		this.supplierName_NotRevisedBq = supplierName_NotRevisedBq;
	}

	public boolean isShowNote() {
		return showNote;
	}

	public void setShowNote(boolean showNote) {
		this.showNote = showNote;
	}

}
