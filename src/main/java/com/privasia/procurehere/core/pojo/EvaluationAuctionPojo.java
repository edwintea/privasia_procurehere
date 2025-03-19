package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class EvaluationAuctionPojo implements Serializable {

	private static final long serialVersionUID = -7102584578297096257L;

	private String buyerName;
	private String auctionType;
	private String auctionName;
	private String auctionId;
	private String referenceNo;
	private String auctionPublishDate;
	private String auctionStartDate;
	private String auctionEndDate;
	private String currencyCode;
	private String auctionExtension;
	private Integer totalExtension;
	private String auctionStatus;
	private Integer supplierInvited;
	private Integer supplierParticipated;
	private Integer totalBilds;
	private String owner;
	private String dateTime;
	private List<EvaluationCancelDisqualifyList> cancelBildList;
	private List<EvaluationCancelDisqualifyList> disqualifyList;
	private List<EvaluationBidHistoryPojo> bidHistory;
	private List<EvaluationBiddingPricePojo> biddingPrice;
	private List<EvaluationBidderContactPojo> bidContacts;
	private List<EvaluationSupplierBidsPojo> supplierBidsList;
	private List<EvaluationBiddingPricePojo> totallyCompleteBidsPriceList;
	private String auctionTitle;
	private String netSavingTitle;
	private String savingBudgetTitle;
	private String priceBidTitle;
	private String auctionCompletionDate;
	private Boolean isBuyer;

	// Dutch Related fields
	private String decimal;
	private BigDecimal startPrice;
	private BigDecimal ductchPrice;
	private String intervalType;
	private Integer interval;
	private String winner;
	private BigDecimal winningPrice;
	private String winningDate;

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
	 * @return the auctionName
	 */
	public String getAuctionName() {
		return auctionName;
	}

	/**
	 * @param auctionName the auctionName to set
	 */
	public void setAuctionName(String auctionName) {
		this.auctionName = auctionName;
	}

	/**
	 * @return the auctionId
	 */
	public String getAuctionId() {
		return auctionId;
	}

	/**
	 * @param auctionId the auctionId to set
	 */
	public void setAuctionId(String auctionId) {
		this.auctionId = auctionId;
	}

	/**
	 * @return the referenceNo
	 */
	public String getReferenceNo() {
		return referenceNo;
	}

	/**
	 * @param referenceNo the referenceNo to set
	 */
	public void setReferenceNo(String referenceNo) {
		this.referenceNo = referenceNo;
	}

	/**
	 * @return the auctionPublishDate
	 */
	public String getAuctionPublishDate() {
		return auctionPublishDate;
	}

	/**
	 * @param auctionPublishDate the auctionPublishDate to set
	 */
	public void setAuctionPublishDate(String auctionPublishDate) {
		this.auctionPublishDate = auctionPublishDate;
	}

	/**
	 * @return the auctionStartDate
	 */
	public String getAuctionStartDate() {
		return auctionStartDate;
	}

	/**
	 * @param auctionStartDate the auctionStartDate to set
	 */
	public void setAuctionStartDate(String auctionStartDate) {
		this.auctionStartDate = auctionStartDate;
	}

	/**
	 * @return the auctionEndDate
	 */
	public String getAuctionEndDate() {
		return auctionEndDate;
	}

	/**
	 * @param auctionEndDate the auctionEndDate to set
	 */
	public void setAuctionEndDate(String auctionEndDate) {
		this.auctionEndDate = auctionEndDate;
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
	 * @return the auctionExtension
	 */
	public String getAuctionExtension() {
		return auctionExtension;
	}

	/**
	 * @param auctionExtension the auctionExtension to set
	 */
	public void setAuctionExtension(String auctionExtension) {
		this.auctionExtension = auctionExtension;
	}

	/**
	 * @return the totalExtension
	 */
	public Integer getTotalExtension() {
		return totalExtension;
	}

	/**
	 * @param totalExtension the totalExtension to set
	 */
	public void setTotalExtension(Integer totalExtension) {
		this.totalExtension = totalExtension;
	}

	/**
	 * @return the auctionStatus
	 */
	public String getAuctionStatus() {
		return auctionStatus;
	}

	/**
	 * @param auctionStatus the auctionStatus to set
	 */
	public void setAuctionStatus(String auctionStatus) {
		this.auctionStatus = auctionStatus;
	}

	/**
	 * @return the supplierInvited
	 */
	public Integer getSupplierInvited() {
		return supplierInvited;
	}

	/**
	 * @param supplierInvited the supplierInvited to set
	 */
	public void setSupplierInvited(Integer supplierInvited) {
		this.supplierInvited = supplierInvited;
	}

	/**
	 * @return the supplierParticipated
	 */
	public Integer getSupplierParticipated() {
		return supplierParticipated;
	}

	/**
	 * @param supplierParticipated the supplierParticipated to set
	 */
	public void setSupplierParticipated(Integer supplierParticipated) {
		this.supplierParticipated = supplierParticipated;
	}

	/**
	 * @return the totalBilds
	 */
	public Integer getTotalBilds() {
		return totalBilds;
	}

	/**
	 * @param totalBilds the totalBilds to set
	 */
	public void setTotalBilds(Integer totalBilds) {
		this.totalBilds = totalBilds;
	}

	/**
	 * @return the owner
	 */
	public String getOwner() {
		return owner;
	}

	/**
	 * @param owner the owner to set
	 */
	public void setOwner(String owner) {
		this.owner = owner;
	}

	/**
	 * @return the dateTime
	 */
	public String getDateTime() {
		return dateTime;
	}

	/**
	 * @param dateTime the dateTime to set
	 */
	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	/**
	 * @return the cancelBildList
	 */
	public List<EvaluationCancelDisqualifyList> getCancelBildList() {
		return cancelBildList;
	}

	/**
	 * @param cancelBildList the cancelBildList to set
	 */
	public void setCancelBildList(List<EvaluationCancelDisqualifyList> cancelBildList) {
		this.cancelBildList = cancelBildList;
	}

	/**
	 * @return the disqualifyList
	 */
	public List<EvaluationCancelDisqualifyList> getDisqualifyList() {
		return disqualifyList;
	}

	/**
	 * @param disqualifyList the disqualifyList to set
	 */
	public void setDisqualifyList(List<EvaluationCancelDisqualifyList> disqualifyList) {
		this.disqualifyList = disqualifyList;
	}

	/**
	 * @return the bidHistory
	 */
	public List<EvaluationBidHistoryPojo> getBidHistory() {
		return bidHistory;
	}

	/**
	 * @param bidHistory the bidHistory to set
	 */
	public void setBidHistory(List<EvaluationBidHistoryPojo> bidHistory) {
		this.bidHistory = bidHistory;
	}

	/**
	 * @return the biddingPrice
	 */
	public List<EvaluationBiddingPricePojo> getBiddingPrice() {
		return biddingPrice;
	}

	/**
	 * @param biddingPrice the biddingPrice to set
	 */
	public void setBiddingPrice(List<EvaluationBiddingPricePojo> biddingPrice) {
		this.biddingPrice = biddingPrice;
	}

	/**
	 * @return the bidContacts
	 */
	public List<EvaluationBidderContactPojo> getBidContacts() {
		return bidContacts;
	}

	/**
	 * @param bidContacts the bidContacts to set
	 */
	public void setBidContacts(List<EvaluationBidderContactPojo> bidContacts) {
		this.bidContacts = bidContacts;
	}

	/**
	 * @return the supplierBidsList
	 */
	public List<EvaluationSupplierBidsPojo> getSupplierBidsList() {
		return supplierBidsList;
	}

	/**
	 * @param supplierBidsList the supplierBidsList to set
	 */
	public void setSupplierBidsList(List<EvaluationSupplierBidsPojo> supplierBidsList) {
		this.supplierBidsList = supplierBidsList;
	}

	/**
	 * @return the auctionTitle
	 */
	public String getAuctionTitle() {
		return auctionTitle;
	}

	/**
	 * @param auctionTitle the auctionTitle to set
	 */
	public void setAuctionTitle(String auctionTitle) {
		this.auctionTitle = auctionTitle;
	}

	/**
	 * @return the netSavingTitle
	 */
	public String getNetSavingTitle() {
		return netSavingTitle;
	}

	/**
	 * @param netSavingTitle the netSavingTitle to set
	 */
	public void setNetSavingTitle(String netSavingTitle) {
		this.netSavingTitle = netSavingTitle;
	}

	/**
	 * @return the savingBudgetTitle
	 */
	public String getSavingBudgetTitle() {
		return savingBudgetTitle;
	}

	/**
	 * @param savingBudgetTitle the savingBudgetTitle to set
	 */
	public void setSavingBudgetTitle(String savingBudgetTitle) {
		this.savingBudgetTitle = savingBudgetTitle;
	}

	/**
	 * @return the priceBidTitle
	 */
	public String getPriceBidTitle() {
		return priceBidTitle;
	}

	/**
	 * @param priceBidTitle the priceBidTitle to set
	 */
	public void setPriceBidTitle(String priceBidTitle) {
		this.priceBidTitle = priceBidTitle;
	}

	/**
	 * @return the auctionCompletionDate
	 */
	public String getAuctionCompletionDate() {
		return auctionCompletionDate;
	}

	/**
	 * @param auctionCompletionDate the auctionCompletionDate to set
	 */
	public void setAuctionCompletionDate(String auctionCompletionDate) {
		this.auctionCompletionDate = auctionCompletionDate;
	}

	public Boolean getIsBuyer() {
		return isBuyer;
	}

	public void setIsBuyer(Boolean isBuyer) {
		this.isBuyer = isBuyer;
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
	 * @return the startPrice
	 */
	public BigDecimal getStartPrice() {
		return startPrice;
	}

	/**
	 * @param startPrice the startPrice to set
	 */
	public void setStartPrice(BigDecimal startPrice) {
		this.startPrice = startPrice;
	}

	/**
	 * @return the ductchPrice
	 */
	public BigDecimal getDuctchPrice() {
		return ductchPrice;
	}

	/**
	 * @param ductchPrice the ductchPrice to set
	 */
	public void setDuctchPrice(BigDecimal ductchPrice) {
		this.ductchPrice = ductchPrice;
	}

	/**
	 * @return the intervalType
	 */
	public String getIntervalType() {
		return intervalType;
	}

	/**
	 * @param intervalType the intervalType to set
	 */
	public void setIntervalType(String intervalType) {
		this.intervalType = intervalType;
	}

	/**
	 * @return the interval
	 */
	public Integer getInterval() {
		return interval;
	}

	/**
	 * @param interval the interval to set
	 */
	public void setInterval(Integer interval) {
		this.interval = interval;
	}

	/**
	 * @return the winner
	 */
	public String getWinner() {
		return winner;
	}

	/**
	 * @param winner the winner to set
	 */
	public void setWinner(String winner) {
		this.winner = winner;
	}

	/**
	 * @return the winningPrice
	 */
	public BigDecimal getWinningPrice() {
		return winningPrice;
	}

	/**
	 * @param winningPrice the winningPrice to set
	 */
	public void setWinningPrice(BigDecimal winningPrice) {
		this.winningPrice = winningPrice;
	}

	/**
	 * @return the winningDate
	 */
	public String getWinningDate() {
		return winningDate;
	}

	/**
	 * @param winningDate the winningDate to set
	 */
	public void setWinningDate(String winningDate) {
		this.winningDate = winningDate;
	}

	/**
	 * @return the totallyCompleteBidsPriceList
	 */
	public List<EvaluationBiddingPricePojo> getTotallyCompleteBidsPriceList() {
		return totallyCompleteBidsPriceList;
	}

	/**
	 * @param totallyCompleteBidsPriceList the totallyCompleteBidsPriceList to set
	 */
	public void setTotallyCompleteBidsPriceList(List<EvaluationBiddingPricePojo> totallyCompleteBidsPriceList) {
		this.totallyCompleteBidsPriceList = totallyCompleteBidsPriceList;
	}

}
