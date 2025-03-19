package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class EvaluationAuctionBiddingPojo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8650204823048365415L;
	private String buyerName;
	private String auctionType;
	private String eventType;
	private String ownerWithContact;
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
	private List<EvaluationAuctionBiddingPojo> header;
	private String headerBqName;
	private List<EvaluationCancelDisqualifyList> cancelBildList;
	private List<EvaluationCancelDisqualifyList> disqualifyList;
	private List<EvaluationBidHistoryPojo> bidHistory;
	private List<EvaluationBiddingPricePojo> biddingPrice;
	private List<EvaluationBidderContactPojo> bidContacts;
	private List<EvaluationBidderContactPojo> supplierActivitySummary;
	private List<EvaluationBidderContactPojo> supplierAcceptedBids;
	private List<EvaluationBidderContactPojo> supplierRejectedBids;
	private List<EvaluationBidderContactPojo> supplierInvitedBids;
	private List<EvaluationBiddingPricePojo> bidderPartiallyCompleteBidsList;
	private List<EvaluationBiddingPricePojo> netSavingList;
	private List<EvaluationBiddingPricePojo> bidderTotallyCompleteBidsList;
	private List<EvaluationBiddingPricePojo> bidderDisqualifiedCompleteBidsList;
	private List<EvaluationSupplierBidsPojo> supplierBidsList;
	private List<EvaluationSupplierBidsPojo> supplierTopBidsList;
	private List<EvaluationSupplierBidsPojo> supplierBidHistoryList;
	private List<EvaluationSupplierBidsPojo> suppBidHistoryList;
	private List<EvaluationBqItemPojo> topSupplierbqItem;
	private List<EvaluationBiddingIpAddressPojo> ipAddressList;
	private List<AuctionEvaluationDocumentPojo> auctionEvaluationDocument;
	private String leadingSupplierBqRemak;
	private String auctionTitle;
	private String netSavingTitle;
	private String savingBudgetTitle;
	private String priceBidTitle;
	private String auctionCompletionDate;
	private Boolean isBuyer;
	private List<EvaluationBqItemComments> evaluationSummary;
	private String eventDescription;
	// Dutch Related fields
	private String decimal;
	private BigDecimal startPrice;
	private BigDecimal ductchPrice;
	private String intervalType;
	private Integer interval;
	private String winner;
	private BigDecimal winningPrice;
	private String winningDate;

	private String auctionCreatorDate;
	private String envelopTitle;
	private String leadEvaluatorSummary;
	private String leadEvaluater;

	private RfaSupplierBqPojo supplier;
	private List<EnvelopeBqPojo> envelopeBq;
	private List<EnvelopeSorPojo> envelopeSor;
	private List<EnvelopeCqPojo> envelopeCq;
	private List<EvaluationSuppliersPojo> disQualifiedSuppliers;
	private List<EvaluationSuppliersPojo> reverToBidSuppliers;

	// this is added for the report CR PH 200
	private String supplierCompanyName;
	private String leadingSecondComparison;
	private String leadSupplierCompanyName;
	private String remarkCompareLowestPrice;
	private String remarkComparerBudgetPrice;
	private String remarkCompareHistoricPrice;
	private String leadSuppliergrandTotal;
	private BigDecimal revisedGrandTotal;
	private String remark;
	private String supplierRemark;
	private List<EvaluationBiddingPricePojo> evaluationSupplierBidsPojoList;
	private List<EvaluationBiddingPricePojo> evaluationTopSupplierBidsPojoList;
	private List<EvaluationBiddingPricePojo> evaluationSupplierBidsLineChartPojoList;
	private List<EvaluationBiddingPricePojo> evaluationSupplierBidsLineTimeChartPojoList;
	List<EvaluationSupplierBidsPojo> supplierBidHistoryTable;
	private String showSingleDisQaulify;
	private String showSingleIpAdd;
	private String bqName;
	private Boolean revisedBidSubmitted;
	private Boolean isMask = true;
	private List<EvaluationDeclarationAcceptancePojo> evaluationDeclarationAcceptList;
	private String fileName;

	public Boolean getIsMask() {
		return isMask;
	}

	public void setIsMask(Boolean isMask) {
		this.isMask = isMask;
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
	 * @return the auctionCreatorDate
	 */
	public String getAuctionCreatorDate() {
		return auctionCreatorDate;
	}

	/**
	 * @param auctionCreatorDate the auctionCreatorDate to set
	 */
	public void setAuctionCreatorDate(String auctionCreatorDate) {
		this.auctionCreatorDate = auctionCreatorDate;
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
	 * @return the envelopTitle
	 */
	public String getEnvelopTitle() {
		return envelopTitle;
	}

	/**
	 * @param envelopTitle the envelopTitle to set
	 */
	public void setEnvelopTitle(String envelopTitle) {
		this.envelopTitle = envelopTitle;
	}

	/**
	 * @return the supplier
	 */
	public RfaSupplierBqPojo getSupplier() {
		return supplier;
	}

	/**
	 * @param supplier the supplier to set
	 */
	public void setSupplier(RfaSupplierBqPojo supplier) {
		this.supplier = supplier;
	}

	/**
	 * @return the disQualifiedSuppliers
	 */
	public List<EvaluationSuppliersPojo> getDisQualifiedSuppliers() {
		return disQualifiedSuppliers;
	}

	/**
	 * @param disQualifiedSuppliers the disQualifiedSuppliers to set
	 */
	public void setDisQualifiedSuppliers(List<EvaluationSuppliersPojo> disQualifiedSuppliers) {
		this.disQualifiedSuppliers = disQualifiedSuppliers;
	}

	/**
	 * @return the supplierTopBidsList
	 */
	public List<EvaluationSupplierBidsPojo> getSupplierTopBidsList() {
		return supplierTopBidsList;
	}

	/**
	 * @param supplierTopBidsList the supplierTopBidsList to set
	 */
	public void setSupplierTopBidsList(List<EvaluationSupplierBidsPojo> supplierTopBidsList) {
		this.supplierTopBidsList = supplierTopBidsList;
	}

	/**
	 * @return the supplierActivitySummary
	 */
	public List<EvaluationBidderContactPojo> getSupplierActivitySummary() {
		return supplierActivitySummary;
	}

	/**
	 * @param supplierActivitySummary the supplierActivitySummary to set
	 */
	public void setSupplierActivitySummary(List<EvaluationBidderContactPojo> supplierActivitySummary) {
		this.supplierActivitySummary = supplierActivitySummary;
	}

	/**
	 * @return the bidderTotallyCompleteBidsList
	 */
	public List<EvaluationBiddingPricePojo> getBidderTotallyCompleteBidsList() {
		return bidderTotallyCompleteBidsList;
	}

	/**
	 * @param bidderTotallyCompleteBidsList the bidderTotallyCompleteBidsList to set
	 */
	public void setBidderTotallyCompleteBidsList(List<EvaluationBiddingPricePojo> bidderTotallyCompleteBidsList) {
		this.bidderTotallyCompleteBidsList = bidderTotallyCompleteBidsList;
	}

	/**
	 * @return the supplierAcceptedBids
	 */
	public List<EvaluationBidderContactPojo> getSupplierAcceptedBids() {
		return supplierAcceptedBids;
	}

	/**
	 * @param supplierAcceptedBids the supplierAcceptedBids to set
	 */
	public void setSupplierAcceptedBids(List<EvaluationBidderContactPojo> supplierAcceptedBids) {
		this.supplierAcceptedBids = supplierAcceptedBids;
	}

	/**
	 * @return the supplierRejectedBids
	 */
	public List<EvaluationBidderContactPojo> getSupplierRejectedBids() {
		return supplierRejectedBids;
	}

	/**
	 * @param supplierRejectedBids the supplierRejectedBids to set
	 */
	public void setSupplierRejectedBids(List<EvaluationBidderContactPojo> supplierRejectedBids) {
		this.supplierRejectedBids = supplierRejectedBids;
	}

	/**
	 * @return the supplierInvitedBids
	 */
	public List<EvaluationBidderContactPojo> getSupplierInvitedBids() {
		return supplierInvitedBids;
	}

	/**
	 * @param supplierInvitedBids the supplierInvitedBids to set
	 */
	public void setSupplierInvitedBids(List<EvaluationBidderContactPojo> supplierInvitedBids) {
		this.supplierInvitedBids = supplierInvitedBids;
	}

	/**
	 * @return the bidderPartiallyCompleteBidsList
	 */
	public List<EvaluationBiddingPricePojo> getBidderPartiallyCompleteBidsList() {
		return bidderPartiallyCompleteBidsList;
	}

	/**
	 * @param bidderPartiallyCompleteBidsList the bidderPartiallyCompleteBidsList to set
	 */
	public void setBidderPartiallyCompleteBidsList(List<EvaluationBiddingPricePojo> bidderPartiallyCompleteBidsList) {
		this.bidderPartiallyCompleteBidsList = bidderPartiallyCompleteBidsList;
	}

	/**
	 * @return the bidderDisqualifiedCompleteBidsList
	 */
	public List<EvaluationBiddingPricePojo> getBidderDisqualifiedCompleteBidsList() {
		return bidderDisqualifiedCompleteBidsList;
	}

	/**
	 * @param bidderDisqualifiedCompleteBidsList the bidderDisqualifiedCompleteBidsList to set
	 */
	public void setBidderDisqualifiedCompleteBidsList(List<EvaluationBiddingPricePojo> bidderDisqualifiedCompleteBidsList) {
		this.bidderDisqualifiedCompleteBidsList = bidderDisqualifiedCompleteBidsList;
	}

	/**
	 * @return the topSupplierbqItem
	 */
	public List<EvaluationBqItemPojo> getTopSupplierbqItem() {
		return topSupplierbqItem;
	}

	/**
	 * @param topSupplierbqItem the topSupplierbqItem to set
	 */
	public void setTopSupplierbqItem(List<EvaluationBqItemPojo> topSupplierbqItem) {
		this.topSupplierbqItem = topSupplierbqItem;
	}

	/**
	 * @return the supplierCompanyName
	 */
	public String getSupplierCompanyName() {
		return supplierCompanyName;
	}

	/**
	 * @param supplierCompanyName the supplierCompanyName to set
	 */
	public void setSupplierCompanyName(String supplierCompanyName) {
		this.supplierCompanyName = supplierCompanyName;
	}

	/**
	 * @return the remarkCompareLowestPrice
	 */
	public String getRemarkCompareLowestPrice() {
		return remarkCompareLowestPrice;
	}

	/**
	 * @param remarkCompareLowestPrice the remarkCompareLowestPrice to set
	 */
	public void setRemarkCompareLowestPrice(String remarkCompareLowestPrice) {
		this.remarkCompareLowestPrice = remarkCompareLowestPrice;
	}

	/**
	 * @return the remarkComparerBudgetPrice
	 */
	public String getRemarkComparerBudgetPrice() {
		return remarkComparerBudgetPrice;
	}

	/**
	 * @param remarkComparerBudgetPrice the remarkComparerBudgetPrice to set
	 */
	public void setRemarkComparerBudgetPrice(String remarkComparerBudgetPrice) {
		this.remarkComparerBudgetPrice = remarkComparerBudgetPrice;
	}

	/**
	 * @return the remarkCompareHistoricPrice
	 */
	public String getRemarkCompareHistoricPrice() {
		return remarkCompareHistoricPrice;
	}

	/**
	 * @param remarkCompareHistoricPrice the remarkCompareHistoricPrice to set
	 */
	public void setRemarkCompareHistoricPrice(String remarkCompareHistoricPrice) {
		this.remarkCompareHistoricPrice = remarkCompareHistoricPrice;
	}

	/**
	 * @return the revisedGrandTotal
	 */
	public BigDecimal getRevisedGrandTotal() {
		return revisedGrandTotal;
	}

	/**
	 * @param revisedGrandTotal the revisedGrandTotal to set
	 */
	public void setRevisedGrandTotal(BigDecimal revisedGrandTotal) {
		this.revisedGrandTotal = revisedGrandTotal;
	}

	/**
	 * @return the leadingSupplierBqRemak
	 */
	public String getLeadingSupplierBqRemak() {
		return leadingSupplierBqRemak;
	}

	/**
	 * @param leadingSupplierBqRemak the leadingSupplierBqRemak to set
	 */
	public void setLeadingSupplierBqRemak(String leadingSupplierBqRemak) {
		this.leadingSupplierBqRemak = leadingSupplierBqRemak;
	}

	/**
	 * @return the supplierBidHistoryList
	 */
	public List<EvaluationSupplierBidsPojo> getSupplierBidHistoryList() {
		return supplierBidHistoryList;
	}

	/**
	 * @param supplierBidHistoryList the supplierBidHistoryList to set
	 */
	public void setSupplierBidHistoryList(List<EvaluationSupplierBidsPojo> supplierBidHistoryList) {
		this.supplierBidHistoryList = supplierBidHistoryList;
	}

	/**
	 * @return the supplierRemark
	 */
	public String getSupplierRemark() {
		return supplierRemark;
	}

	/**
	 * @param supplierRemark the supplierRemark to set
	 */
	public void setSupplierRemark(String supplierRemark) {
		this.supplierRemark = supplierRemark;
	}

	public List<EvaluationBiddingPricePojo> getEvaluationSupplierBidsPojoList() {
		return evaluationSupplierBidsPojoList;
	}

	public void setEvaluationSupplierBidsPojoList(List<EvaluationBiddingPricePojo> evaluationSupplierBidsPojoList) {
		this.evaluationSupplierBidsPojoList = evaluationSupplierBidsPojoList;
	}

	/**
	 * @return the ipAddressList
	 */
	public List<EvaluationBiddingIpAddressPojo> getIpAddressList() {
		return ipAddressList;
	}

	/**
	 * @param ipAddressList the ipAddressList to set
	 */
	public void setIpAddressList(List<EvaluationBiddingIpAddressPojo> ipAddressList) {
		this.ipAddressList = ipAddressList;
	}

	/**
	 * @return the evaluationTopSupplierBidsPojoList
	 */
	public List<EvaluationBiddingPricePojo> getEvaluationTopSupplierBidsPojoList() {
		return evaluationTopSupplierBidsPojoList;
	}

	/**
	 * @param evaluationTopSupplierBidsPojoList the evaluationTopSupplierBidsPojoList to set
	 */
	public void setEvaluationTopSupplierBidsPojoList(List<EvaluationBiddingPricePojo> evaluationTopSupplierBidsPojoList) {
		this.evaluationTopSupplierBidsPojoList = evaluationTopSupplierBidsPojoList;
	}

	/**
	 * @return the suppBidHistoryList
	 */
	public List<EvaluationSupplierBidsPojo> getSuppBidHistoryList() {
		return suppBidHistoryList;
	}

	/**
	 * @param suppBidHistoryList the suppBidHistoryList to set
	 */
	public void setSuppBidHistoryList(List<EvaluationSupplierBidsPojo> suppBidHistoryList) {
		this.suppBidHistoryList = suppBidHistoryList;
	}

	/**
	 * @return the evaluationSupplierBidsLineChartPojoList
	 */
	public List<EvaluationBiddingPricePojo> getEvaluationSupplierBidsLineChartPojoList() {
		return evaluationSupplierBidsLineChartPojoList;
	}

	/**
	 * @param evaluationSupplierBidsLineChartPojoList the evaluationSupplierBidsLineChartPojoList to set
	 */
	public void setEvaluationSupplierBidsLineChartPojoList(List<EvaluationBiddingPricePojo> evaluationSupplierBidsLineChartPojoList) {
		this.evaluationSupplierBidsLineChartPojoList = evaluationSupplierBidsLineChartPojoList;
	}

	/**
	 * @return the leadSupplierCompanyName
	 */
	public String getLeadSupplierCompanyName() {
		return leadSupplierCompanyName;
	}

	/**
	 * @param leadSupplierCompanyName the leadSupplierCompanyName to set
	 */
	public void setLeadSupplierCompanyName(String leadSupplierCompanyName) {
		this.leadSupplierCompanyName = leadSupplierCompanyName;
	}

	/**
	 * @return the leadingSecondComparison
	 */
	public String getLeadingSecondComparison() {
		return leadingSecondComparison;
	}

	/**
	 * @param leadingSecondComparison the leadingSecondComparison to set
	 */
	public void setLeadingSecondComparison(String leadingSecondComparison) {
		this.leadingSecondComparison = leadingSecondComparison;
	}

	/**
	 * @return the leadEvaluatorSummary
	 */
	public String getLeadEvaluatorSummary() {
		return leadEvaluatorSummary;
	}

	/**
	 * @param leadEvaluatorSummary the leadEvaluatorSummary to set
	 */
	public void setLeadEvaluatorSummary(String leadEvaluatorSummary) {
		this.leadEvaluatorSummary = leadEvaluatorSummary;
	}

	/**
	 * @return the leadEvaluater
	 */
	public String getLeadEvaluater() {
		return leadEvaluater;
	}

	/**
	 * @param leadEvaluater the leadEvaluater to set
	 */
	public void setLeadEvaluater(String leadEvaluater) {
		this.leadEvaluater = leadEvaluater;
	}

	/**
	 * @return the evaluationSummary
	 */
	public List<EvaluationBqItemComments> getEvaluationSummary() {
		return evaluationSummary;
	}

	/**
	 * @param evaluationSummary the evaluationSummary to set
	 */
	public void setEvaluationSummary(List<EvaluationBqItemComments> evaluationSummary) {
		this.evaluationSummary = evaluationSummary;
	}

	/**
	 * @return the auctionEvaluationDocument
	 */
	public List<AuctionEvaluationDocumentPojo> getAuctionEvaluationDocument() {
		return auctionEvaluationDocument;
	}

	/**
	 * @param auctionEvaluationDocument the auctionEvaluationDocument to set
	 */
	public void setAuctionEvaluationDocument(List<AuctionEvaluationDocumentPojo> auctionEvaluationDocument) {
		this.auctionEvaluationDocument = auctionEvaluationDocument;
	}

	/**
	 * @return the envelopeBq
	 */
	public List<EnvelopeBqPojo> getEnvelopeBq() {
		return envelopeBq;
	}

	/**
	 * @param envelopeBq the envelopeBq to set
	 */
	public void setEnvelopeBq(List<EnvelopeBqPojo> envelopeBq) {
		this.envelopeBq = envelopeBq;
	}

	public List<EvaluationAuctionBiddingPojo> getHeader() {
		return header;
	}

	public void setHeader(List<EvaluationAuctionBiddingPojo> header) {
		this.header = header;
	}

	public String getHeaderBqName() {
		return headerBqName;
	}

	public void setHeaderBqName(String headerBqName) {
		this.headerBqName = headerBqName;
	}

	/**
	 * @return the envelopeCq
	 */
	public List<EnvelopeCqPojo> getEnvelopeCq() {
		return envelopeCq;
	}

	/**
	 * @param envelopeCq the envelopeCq to set
	 */
	public void setEnvelopeCq(List<EnvelopeCqPojo> envelopeCq) {
		this.envelopeCq = envelopeCq;
	}

	public String getShowSingleDisQaulify() {
		return showSingleDisQaulify;
	}

	public void setShowSingleDisQaulify(String showSingleDisQaulify) {
		this.showSingleDisQaulify = showSingleDisQaulify;
	}

	public String getShowSingleIpAdd() {
		return showSingleIpAdd;
	}

	public void setShowSingleIpAdd(String showSingleIpAdd) {
		this.showSingleIpAdd = showSingleIpAdd;
	}

	/**
	 * @return the eventDescription
	 */
	public String getEventDescription() {
		return eventDescription;
	}

	/**
	 * @param eventDescription the eventDescription to set
	 */
	public void setEventDescription(String eventDescription) {
		this.eventDescription = eventDescription;
	}

	/**
	 * @return the supplierBidHistoryTable
	 */
	public List<EvaluationSupplierBidsPojo> getSupplierBidHistoryTable() {
		return supplierBidHistoryTable;
	}

	/**
	 * @param supplierBidHistoryTable the supplierBidHistoryTable to set
	 */
	public void setSupplierBidHistoryTable(List<EvaluationSupplierBidsPojo> supplierBidHistoryTable) {
		this.supplierBidHistoryTable = supplierBidHistoryTable;
	}

	/**
	 * @return the reverToBidSuppliers
	 */
	public List<EvaluationSuppliersPojo> getReverToBidSuppliers() {
		return reverToBidSuppliers;
	}

	/**
	 * @param reverToBidSuppliers the reverToBidSuppliers to set
	 */
	public void setReverToBidSuppliers(List<EvaluationSuppliersPojo> reverToBidSuppliers) {
		this.reverToBidSuppliers = reverToBidSuppliers;
	}

	public List<EvaluationBiddingPricePojo> getEvaluationSupplierBidsLineTimeChartPojoList() {
		return evaluationSupplierBidsLineTimeChartPojoList;
	}

	public void setEvaluationSupplierBidsLineTimeChartPojoList(List<EvaluationBiddingPricePojo> evaluationSupplierBidsLineTimeChartPojoList) {
		this.evaluationSupplierBidsLineTimeChartPojoList = evaluationSupplierBidsLineTimeChartPojoList;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public String getOwnerWithContact() {
		return ownerWithContact;
	}

	public void setOwnerWithContact(String ownerWithContact) {
		this.ownerWithContact = ownerWithContact;
	}

	public List<EvaluationBiddingPricePojo> getNetSavingList() {
		return netSavingList;
	}

	public void setNetSavingList(List<EvaluationBiddingPricePojo> netSavingList) {
		this.netSavingList = netSavingList;
	}

	/**
	 * @return the bqName
	 */
	public String getBqName() {
		return bqName;
	}

	/**
	 * @param bqName the bqName to set
	 */
	public void setBqName(String bqName) {
		this.bqName = bqName;
	}

	public String getLeadSuppliergrandTotal() {
		return leadSuppliergrandTotal;
	}

	public void setLeadSuppliergrandTotal(String leadSuppliergrandTotal) {
		this.leadSuppliergrandTotal = leadSuppliergrandTotal;
	}

	public Boolean getRevisedBidSubmitted() {
		return revisedBidSubmitted;
	}

	public void setRevisedBidSubmitted(Boolean revisedBidSubmitted) {
		this.revisedBidSubmitted = revisedBidSubmitted;
	}

	/**
	 * @return the evaluationDeclarationAcceptList
	 */
	public List<EvaluationDeclarationAcceptancePojo> getEvaluationDeclarationAcceptList() {
		return evaluationDeclarationAcceptList;
	}

	/**
	 * @param evaluationDeclarationAcceptList the evaluationDeclarationAcceptList to set
	 */
	public void setEvaluationDeclarationAcceptList(List<EvaluationDeclarationAcceptancePojo> evaluationDeclarationAcceptList) {
		this.evaluationDeclarationAcceptList = evaluationDeclarationAcceptList;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public List<EnvelopeSorPojo> getEnvelopeSor() {
		return envelopeSor;
	}

	public void setEnvelopeSor(List<EnvelopeSorPojo> envelopeSor) {
		this.envelopeSor = envelopeSor;
	}
}
