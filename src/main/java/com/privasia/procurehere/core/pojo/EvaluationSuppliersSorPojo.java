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
public class EvaluationSuppliersSorPojo implements Serializable {

	private static final long serialVersionUID = -9208467047575414782L;
	private String id;
	private String supplierName;
	private String sorId;
	private String sorName;
	private String remark;
	private List<EvaluationSorPojo> sors;
	List<EvaluationSorItemPojo> topSupplierItemList;
	private String supplierName1;
	private String supplierName2;
	private BigDecimal rate1;
	private BigDecimal rate2;
	private BigDecimal rate3;
	private Boolean revisedBidSubmitted;
	private Boolean revisedBidSubmitted1;
	private Boolean revisedBidSubmitted2;

	public EvaluationSuppliersSorPojo() {

	}

	public EvaluationSuppliersSorPojo(String id, String companyName, String sorId , String sorName, String remark) {
		this.id = id;
		this.supplierName = companyName;
		this.sorId = sorId;
		this.sorName = sorName;
		this.remark = remark;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public String getSorId() {
		return sorId;
	}

	public void setSorId(String sorId) {
		this.sorId = sorId;
	}

	public String getSorName() {
		return sorName;
	}

	public void setSorName(String sorName) {
		this.sorName = sorName;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public List<EvaluationSorPojo> getSors() {
		return sors;
	}

	public void setSors(List<EvaluationSorPojo> sors) {
		this.sors = sors;
	}

	public List<EvaluationSorItemPojo> getTopSupplierItemList() {
		return topSupplierItemList;
	}

	public void setTopSupplierItemList(List<EvaluationSorItemPojo> topSupplierItemList) {
		this.topSupplierItemList = topSupplierItemList;
	}

	public String getSupplierName1() {
		return supplierName1;
	}

	public void setSupplierName1(String supplierName1) {
		this.supplierName1 = supplierName1;
	}

	public String getSupplierName2() {
		return supplierName2;
	}

	public void setSupplierName2(String supplierName2) {
		this.supplierName2 = supplierName2;
	}

	public BigDecimal getRate1() {
		return rate1;
	}

	public void setRate1(BigDecimal rate1) {
		this.rate1 = rate1;
	}

	public BigDecimal getRate2() {
		return rate2;
	}

	public void setRate2(BigDecimal rate2) {
		this.rate2 = rate2;
	}

	public BigDecimal getRate3() {
		return rate3;
	}

	public void setRate3(BigDecimal rate3) {
		this.rate3 = rate3;
	}

	public Boolean getRevisedBidSubmitted() {
		return revisedBidSubmitted;
	}

	public void setRevisedBidSubmitted(Boolean revisedBidSubmitted) {
		this.revisedBidSubmitted = revisedBidSubmitted;
	}

	public Boolean getRevisedBidSubmitted1() {
		return revisedBidSubmitted1;
	}

	public void setRevisedBidSubmitted1(Boolean revisedBidSubmitted1) {
		this.revisedBidSubmitted1 = revisedBidSubmitted1;
	}

	public Boolean getRevisedBidSubmitted2() {
		return revisedBidSubmitted2;
	}

	public void setRevisedBidSubmitted2(Boolean revisedBidSubmitted2) {
		this.revisedBidSubmitted2 = revisedBidSubmitted2;
	}
}
