/**
 * 
 */
package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author Giridhar
 */
public class EvaluationSuppliersBqPojo implements Serializable {

	private static final long serialVersionUID = -9208467047575414782L;
	private String id;
	private String supplierName;
	private BigDecimal totalAfterTax;
	private BigDecimal additionalTax;
	private BigDecimal grandTotal;
	private String bqId;
	private String bqName;
	private String remark;
	private List<EvaluationBqPojo> bqs;
	List<EvaluationBqItemPojo> topSupplierItemList;
	private String supplierName1;
	private BigDecimal totalAfterTax1;
	private BigDecimal additionalTax1;
	private BigDecimal grandTotal1;

	private String supplierName2;
	private BigDecimal totalAfterTax2;
	private BigDecimal additionalTax2;
	private BigDecimal grandTotal2;
	private Boolean revisedBidSubmitted;
	private Boolean revisedBidSubmitted1;
	private Boolean revisedBidSubmitted2;

	public EvaluationSuppliersBqPojo() {

	}

	public EvaluationSuppliersBqPojo(String id, String supplierName, BigDecimal totalAfterTax, BigDecimal additionalTax, BigDecimal grandTotal, String bqId, String bqName, String remark, Boolean revisedBidSubmitted) {
		this.id = id;
		this.supplierName = supplierName;
		this.totalAfterTax = totalAfterTax;
		this.additionalTax = additionalTax;
		this.grandTotal = grandTotal;
		this.bqId = bqId;
		this.bqName = bqName;
		this.remark = remark;
		this.revisedBidSubmitted = revisedBidSubmitted;
	}

	public EvaluationSuppliersBqPojo(String id, String supplierName, BigDecimal totalAfterTax, BigDecimal additionalTax, BigDecimal grandTotal, String bqId, String bqName, String remark) {
		this.id = id;
		this.supplierName = supplierName;
		this.totalAfterTax = totalAfterTax;
		this.additionalTax = additionalTax;
		this.grandTotal = grandTotal;
		this.bqId = bqId;
		this.bqName = bqName;
		this.remark = remark;
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
	 * @return the bqs
	 */
	public List<EvaluationBqPojo> getBqs() {
		return bqs;
	}

	/**
	 * @param bqs the bqs to set
	 */
	public void setBqs(List<EvaluationBqPojo> bqs) {
		this.bqs = bqs;
	}

	public BigDecimal getTotalAfterTax() {
		return totalAfterTax;
	}

	public void setTotalAfterTax(BigDecimal totalAfterTax) {
		this.totalAfterTax = totalAfterTax;
	}

	public BigDecimal getAdditionalTax() {
		return additionalTax;
	}

	public void setAdditionalTax(BigDecimal additionalTax) {
		this.additionalTax = additionalTax;
	}

	public BigDecimal getGrandTotal() {
		return grandTotal;
	}

	public void setGrandTotal(BigDecimal grandTotal) {
		this.grandTotal = grandTotal;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBqId() {
		return bqId;
	}

	public void setBqId(String bqId) {
		this.bqId = bqId;
	}

	public String getBqName() {
		return bqName;
	}

	public void setBqName(String bqName) {
		this.bqName = bqName;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getSupplierName1() {
		return supplierName1;
	}

	public void setSupplierName1(String supplierName1) {
		this.supplierName1 = supplierName1;
	}

	public BigDecimal getTotalAfterTax1() {
		return totalAfterTax1;
	}

	public void setTotalAfterTax1(BigDecimal totalAfterTax1) {
		this.totalAfterTax1 = totalAfterTax1;
	}

	public BigDecimal getAdditionalTax1() {
		return additionalTax1;
	}

	public void setAdditionalTax1(BigDecimal additionalTax1) {
		this.additionalTax1 = additionalTax1;
	}

	public BigDecimal getGrandTotal1() {
		return grandTotal1;
	}

	public void setGrandTotal1(BigDecimal grandTotal1) {
		this.grandTotal1 = grandTotal1;
	}

	public String getSupplierName2() {
		return supplierName2;
	}

	public void setSupplierName2(String supplierName2) {
		this.supplierName2 = supplierName2;
	}

	public BigDecimal getTotalAfterTax2() {
		return totalAfterTax2;
	}

	public void setTotalAfterTax2(BigDecimal totalAfterTax2) {
		this.totalAfterTax2 = totalAfterTax2;
	}

	public BigDecimal getAdditionalTax2() {
		return additionalTax2;
	}

	public void setAdditionalTax2(BigDecimal additionalTax2) {
		this.additionalTax2 = additionalTax2;
	}

	public BigDecimal getGrandTotal2() {
		return grandTotal2;
	}

	public void setGrandTotal2(BigDecimal grandTotal2) {
		this.grandTotal2 = grandTotal2;
	}

	/**
	 * @return the topSupplierItemList
	 */
	public List<EvaluationBqItemPojo> getTopSupplierItemList() {
		return topSupplierItemList;
	}

	/**
	 * @param topSupplierItemList the topSupplierItemList to set
	 */
	public void setTopSupplierItemList(List<EvaluationBqItemPojo> topSupplierItemList) {
		this.topSupplierItemList = topSupplierItemList;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "EvaluationSuppliersBqPojo [supplierName=" + supplierName + ", bqs=" + bqs + "]";
	}

	/**
	 * @return the revisedBidSubmitted
	 */
	public Boolean getRevisedBidSubmitted() {
		return revisedBidSubmitted;
	}

	/**
	 * @param revisedBidSubmitted the revisedBidSubmitted to set
	 */
	public void setRevisedBidSubmitted(Boolean revisedBidSubmitted) {
		this.revisedBidSubmitted = revisedBidSubmitted;
	}

	/**
	 * @return the revisedBidSubmitted1
	 */
	public Boolean getRevisedBidSubmitted1() {
		return revisedBidSubmitted1;
	}

	/**
	 * @param revisedBidSubmitted1 the revisedBidSubmitted1 to set
	 */
	public void setRevisedBidSubmitted1(Boolean revisedBidSubmitted1) {
		this.revisedBidSubmitted1 = revisedBidSubmitted1;
	}

	/**
	 * @return the revisedBidSubmitted2
	 */
	public Boolean getRevisedBidSubmitted2() {
		return revisedBidSubmitted2;
	}

	/**
	 * @param revisedBidSubmitted2 the revisedBidSubmitted2 to set
	 */
	public void setRevisedBidSubmitted2(Boolean revisedBidSubmitted2) {
		this.revisedBidSubmitted2 = revisedBidSubmitted2;
	}

}
