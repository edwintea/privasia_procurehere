package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.privasia.procurehere.core.entity.Supplier;

public class EventEvaluationPojo implements Serializable {

	private static final long serialVersionUID = 6913869792979194399L;

	private String name;

	private String envelopId;

	private String eventId;

	private List<Supplier> columns;

	private List<List<String>> data;

	private List<String> scoring;

	private String itemId;

	private List<BigDecimal> amounts;

	private List<EvaluationTotalAmountPojo> totalAmounts;

	private String supplierId;

	private Integer totalScore;

	private String decimal;

	private List<String> addtionalTaxInfo;

	private List<BigDecimal> grandTotals;

	private Boolean withTax = Boolean.TRUE;

	private List<EvaluationTotalAmountPojo> totalAmountList;
	private List<String> supplierRemarks;

	private List<String> levelOrderList;

	private BqNonPriceComprision bqNonPriceComprision;

	private String remark;

	private String dateAnswer;

	public String getDateAnswer() {
		return dateAnswer;
	}

	public void setDateAnswer(String dateAnswer) {
		this.dateAnswer = dateAnswer;
	}

	private List<BigDecimal> totalAmount;

	/**
	 * @return the remark
	 */
	public String getRemark() {
		return remark;
	}

	/**
	 * @param remark the remark to set
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}

	public EventEvaluationPojo() {
		this.withTax = Boolean.TRUE;
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
	 * @return the itemId
	 */
	public String getItemId() {
		return itemId;
	}

	/**
	 * @param itemId the itemId to set
	 */
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	/**
	 * @return the envelopId
	 */
	public String getEnvelopId() {
		return envelopId;
	}

	/**
	 * @param envelopId the envelopId to set
	 */
	public void setEnvelopId(String envelopId) {
		this.envelopId = envelopId;
	}

	/**
	 * @return the eventId
	 */
	public String getEventId() {
		return eventId;
	}

	/**
	 * @param eventId the eventId to set
	 */
	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	/**
	 * @return the columns
	 */
	public List<Supplier> getColumns() {
		return columns;
	}

	/**
	 * @param columns the columns to set
	 */
	public void setColumns(List<Supplier> columns) {
		this.columns = columns;
	}

	/**
	 * @return the data
	 */
	public List<List<String>> getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(List<List<String>> data) {
		this.data = data;
	}

	/**
	 * @return the scoring
	 */
	public List<String> getScoring() {
		return scoring;
	}

	/**
	 * @param scoring the scoring to set
	 */
	public void setScoring(List<String> scoring) {
		this.scoring = scoring;
	}

	/**
	 * @return the totalAmounts
	 */
	public List<EvaluationTotalAmountPojo> getTotalAmounts() {
		return totalAmounts;
	}

	/**
	 * @param totalAmounts the totalAmounts to set
	 */
	public void setTotalAmounts(List<EvaluationTotalAmountPojo> totalAmounts) {
		this.totalAmounts = totalAmounts;
	}

	/**
	 * @return the amounts
	 */
	public List<BigDecimal> getAmounts() {
		return amounts;
	}

	/**
	 * @param amounts the amounts to set
	 */
	public void setAmounts(List<BigDecimal> amounts) {
		this.amounts = amounts;
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
	 * @return the totalScore
	 */
	public Integer getTotalScore() {
		return totalScore;
	}

	/**
	 * @param totalScore the totalScore to set
	 */
	public void setTotalScore(Integer totalScore) {
		this.totalScore = totalScore;
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
	 * @return the addtionalTaxInfo
	 */
	public List<String> getAddtionalTaxInfo() {
		return addtionalTaxInfo;
	}

	/**
	 * @param addtionalTaxInfo the addtionalTaxInfo to set
	 */
	public void setAddtionalTaxInfo(List<String> addtionalTaxInfo) {
		this.addtionalTaxInfo = addtionalTaxInfo;
	}

	/**
	 * @return the grandTotals
	 */
	public List<BigDecimal> getGrandTotals() {
		return grandTotals;
	}

	/**
	 * @param grandTotals the grandTotals to set
	 */
	public void setGrandTotals(List<BigDecimal> grandTotals) {
		this.grandTotals = grandTotals;
	}

	/**
	 * @return the withTax
	 */
	public Boolean getWithTax() {
		return withTax;
	}

	/**
	 * @param withTax the withTax to set
	 */
	public void setWithTax(Boolean withTax) {
		this.withTax = withTax;
	}

	/**
	 * @return the totalAmountList
	 */
	public List<EvaluationTotalAmountPojo> getTotalAmountList() {
		return totalAmountList;
	}

	/**
	 * @param totalAmountList the totalAmountList to set
	 */
	public void setTotalAmountList(List<EvaluationTotalAmountPojo> totalAmountList) {
		this.totalAmountList = totalAmountList;
	}

	/**
	 * @return the levelOrderList
	 */
	public List<String> getLevelOrderList() {
		return levelOrderList;
	}

	/**
	 * @param levelOrderList the levelOrderList to set
	 */
	public void setLevelOrderList(List<String> levelOrderList) {
		this.levelOrderList = levelOrderList;
	}

	public BqNonPriceComprision getBqNonPriceComprision() {
		return bqNonPriceComprision;
	}

	public void setBqNonPriceComprision(BqNonPriceComprision bqNonPriceComprision) {
		this.bqNonPriceComprision = bqNonPriceComprision;
	}

	/**
	 * @return the supplierRemarks
	 */
	public List<String> getSupplierRemarks() {
		return supplierRemarks;
	}

	/**
	 * @param supplierRemarks the supplierRemarks to set
	 */
	public void setSupplierRemarks(List<String> supplierRemarks) {
		this.supplierRemarks = supplierRemarks;
	}

	/**
	 * @return the totalAmount
	 */
	public List<BigDecimal> getTotalAmount() {
		return totalAmount;
	}

	/**
	 * @param totalAmount the totalAmount to set
	 */
	public void setTotalAmount(List<BigDecimal> totalAmount) {
		this.totalAmount = totalAmount;
	}

}
