/**
 * 
 */
package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Giridhar
 */
public class EvaluationBiddingPricePojo implements Serializable, Comparable<EvaluationBiddingPricePojo> {

	private static final long serialVersionUID = -911574801874308728L;

	private String currencyCode;
	private String bidderName;
	private String remark;
	private String decimal;
	private BigDecimal preAuctionPrice;
	private BigDecimal postAuctionprice;
	private BigDecimal saving;
	private BigDecimal percentage;
	private String auctionType;

	private BigDecimal priceSubmission;
	private BigDecimal priceReduction;
	private String submitedDate;
	private Date submitionDate;
	private String postAuctionStrPrice;
	private String preAuctionStrPrice;

	private Integer bidNumber;
	private String displayValue;
	private String completeness;
	private String totalItem;
	private String compleAndTotalItem;
	private String disqualifyRemarks;
	private String disqualifyBy;
	private Date disqualifiedTime;
	private String ipAddress;

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
	 * @return the bidderName
	 */
	public String getBidderName() {
		return bidderName;
	}

	/**
	 * @param bidderName the bidderName to set
	 */
	public void setBidderName(String bidderName) {
		this.bidderName = bidderName;
	}

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
	 * @return the preAuctionPrice
	 */
	public BigDecimal getPreAuctionPrice() {
		return preAuctionPrice;
	}

	/**
	 * @param preAuctionPrice the preAuctionPrice to set
	 */
	public void setPreAuctionPrice(BigDecimal preAuctionPrice) {
		this.preAuctionPrice = preAuctionPrice;
	}

	/**
	 * @return the postAuctionprice
	 */
	public BigDecimal getPostAuctionprice() {
		return postAuctionprice;
	}

	/**
	 * @param postAuctionprice the postAuctionprice to set
	 */
	public void setPostAuctionprice(BigDecimal postAuctionprice) {
		this.postAuctionprice = postAuctionprice;
	}

	/**
	 * @return the saving
	 */
	public BigDecimal getSaving() {
		return saving;
	}

	/**
	 * @param saving the saving to set
	 */
	public void setSaving(BigDecimal saving) {
		this.saving = saving;
	}

	/**
	 * @return the percentage
	 */
	public BigDecimal getPercentage() {
		return percentage;
	}

	/**
	 * @param percentage the percentage to set
	 */
	public void setPercentage(BigDecimal percentage) {
		this.percentage = percentage;
	}

	/**
	 * @return the auctionType
	 */
	public String getAuctionType() {
		return auctionType;
	}

	/**
	 * @param auctionType the auctionType to set
	 */
	public void setAuctionType(String auctionType) {
		this.auctionType = auctionType;
	}

	/**
	 * @return the priceSubmission
	 */
	public BigDecimal getPriceSubmission() {
		return priceSubmission;
	}

	/**
	 * @param priceSubmission the priceSubmission to set
	 */
	public void setPriceSubmission(BigDecimal priceSubmission) {
		this.priceSubmission = priceSubmission;
	}

	/**
	 * @return the priceReduction
	 */
	public BigDecimal getPriceReduction() {
		return priceReduction;
	}

	/**
	 * @param priceReduction the priceReduction to set
	 */
	public void setPriceReduction(BigDecimal priceReduction) {
		this.priceReduction = priceReduction;
	}

	/**
	 * @return the submitedDate
	 */
	public String getSubmitedDate() {
		return submitedDate;
	}

	/**
	 * @param submitedDate the submitedDate to set
	 */
	public void setSubmitedDate(String submitedDate) {
		this.submitedDate = submitedDate;
	}

	/**
	 * @return the postAuctionStrPrice
	 */
	public String getPostAuctionStrPrice() {
		return postAuctionStrPrice;
	}

	/**
	 * @param postAuctionStrPrice the postAuctionStrPrice to set
	 */
	public void setPostAuctionStrPrice(String postAuctionStrPrice) {
		this.postAuctionStrPrice = postAuctionStrPrice;
	}

	/**
	 * @return the preAuctionStrPrice
	 */
	public String getPreAuctionStrPrice() {
		return preAuctionStrPrice;
	}

	/**
	 * @param preAuctionStrPrice the preAuctionStrPrice to set
	 */
	public void setPreAuctionStrPrice(String preAuctionStrPrice) {
		this.preAuctionStrPrice = preAuctionStrPrice;
	}

	/**
	 * @return the bidNumber
	 */
	public Integer getBidNumber() {
		return bidNumber;
	}

	/**
	 * @param bidNumber the bidNumber to set
	 */
	public void setBidNumber(Integer bidNumber) {
		this.bidNumber = bidNumber;
	}

	/**
	 * @return the displayValue
	 */
	public String getDisplayValue() {
		return displayValue;
	}

	/**
	 * @param displayValue the displayValue to set
	 */
	public void setDisplayValue(String displayValue) {
		this.displayValue = displayValue;
	}

	/**
	 * @return the compleAndTotalItem
	 */
	public String getCompleAndTotalItem() {
		return compleAndTotalItem;
	}

	/**
	 * @param compleAndTotalItem the compleAndTotalItem to set
	 */
	public void setCompleAndTotalItem(String compleAndTotalItem) {
		this.compleAndTotalItem = compleAndTotalItem;
	}

	/**
	 * @return the completeness
	 */
	public String getCompleteness() {
		return completeness;
	}

	/**
	 * @param completeness the completeness to set
	 */
	public void setCompleteness(String completeness) {
		this.completeness = completeness;
	}

	/**
	 * @return the totalItem
	 */
	public String getTotalItem() {
		return totalItem;
	}

	/**
	 * @param totalItem the totalItem to set
	 */
	public void setTotalItem(String totalItem) {
		this.totalItem = totalItem;
	}

	@Override
	public int compareTo(EvaluationBiddingPricePojo candidate) {
		return (this.getBidNumber() < candidate.getBidNumber() ? -1 : (this.getBidNumber() == candidate.getBidNumber() ? 0 : 1));
	}

	/**
	 * @return the disqualifyRemarks
	 */
	public String getDisqualifyRemarks() {
		return disqualifyRemarks;
	}

	/**
	 * @param disqualifyRemarks the disqualifyRemarks to set
	 */
	public void setDisqualifyRemarks(String disqualifyRemarks) {
		this.disqualifyRemarks = disqualifyRemarks;
	}

	/**
	 * @return the disqualifyBy
	 */
	public String getDisqualifyBy() {
		return disqualifyBy;
	}

	/**
	 * @param disqualifyBy the disqualifyBy to set
	 */
	public void setDisqualifyBy(String disqualifyBy) {
		this.disqualifyBy = disqualifyBy;
	}

	/**
	 * @return the disqualifiedTime
	 */
	public Date getDisqualifiedTime() {
		return disqualifiedTime;
	}

	/**
	 * @param disqualifiedTime the disqualifiedTime to set
	 */
	public void setDisqualifiedTime(Date disqualifiedTime) {
		this.disqualifiedTime = disqualifiedTime;
	}

	/**
	 * @return the ipAddress
	 */
	public String getIpAddress() {
		return ipAddress;
	}

	/**
	 * @param ipAddress the ipAddress to set
	 */
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public Date getSubmitionDate() {
		return submitionDate;
	}

	public void setSubmitionDate(Date submitionDate) {
		this.submitionDate = submitionDate;
	}

}
