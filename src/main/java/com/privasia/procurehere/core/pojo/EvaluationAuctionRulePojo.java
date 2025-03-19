package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Giridhar
 */
public class EvaluationAuctionRulePojo implements Serializable {

	private static final long serialVersionUID = 1280730118056320681L;

	private String eventName;
	private String auctionType;
	private BigDecimal dutchStartPrice;
	private BigDecimal minPrice;
	private BigDecimal maxPrice;
	private String supplierMustProvide;
	private Boolean fowardAuction;
	private BigDecimal amountPerIncrementDecrement;
	private Integer interval;
	private String intervalType;
	private String preBidBy;
	private Boolean isPreBidHigherPrice;
	private Boolean isPreBidSameBidPrice;
	private Boolean itemizedBiddingWithTax;
	private String biddingType;
	private String biddingMinValueType;
	private String biddingMinValue;
	private Boolean isBiddingPriceHigherLeadingBid;
	private String biddingPriceHigherLeadingBidType;
	private Boolean lumsumBiddingWithTax;
	private String biddingPriceHigherLeadingBidValue;
	private Boolean isBiddingAllowSupplierSameBid;
	private Boolean isBiddingMinValueFromPrevious;
	private String auctionConsolePriceType;
	private String auctionConsoleVenderType;
	private String auctionConsoleRankType;
	private String isStartGate;
	private String decimal;
	private String ownPrevious;
	private String leadBid;
	private String prebidByTitle;
	private String buyerAuctionConsolePriceType;
	private String buyerAuctionConsoleVenderType;
	private String buyerAuctionConsoleRankType;
	private String supplierAuctionSetting;
	private String prebidAsFirstBid;
	private String preBidSameBidPriceValue;
	private String preSetSamePreBidForAllSuppValue;
	private Boolean isPreSetSamePreBidForAllSupp;

	/**
	 * @return the eventName
	 */
	public String getEventName() {
		return eventName;
	}

	/**
	 * @param eventName the eventName to set
	 */
	public void setEventName(String eventName) {
		this.eventName = eventName;
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
	 * @return the dutchStartPrice
	 */
	public BigDecimal getDutchStartPrice() {
		return dutchStartPrice;
	}

	/**
	 * @param dutchStartPrice the dutchStartPrice to set
	 */
	public void setDutchStartPrice(BigDecimal dutchStartPrice) {
		this.dutchStartPrice = dutchStartPrice;
	}

	/**
	 * @return the minPrice
	 */
	public BigDecimal getMinPrice() {
		return minPrice;
	}

	/**
	 * @param minPrice the minPrice to set
	 */
	public void setMinPrice(BigDecimal minPrice) {
		this.minPrice = minPrice;
	}

	/**
	 * @return the maxPrice
	 */
	public BigDecimal getMaxPrice() {
		return maxPrice;
	}

	/**
	 * @param maxPrice the maxPrice to set
	 */
	public void setMaxPrice(BigDecimal maxPrice) {
		this.maxPrice = maxPrice;
	}

	/**
	 * @return the supplierMustProvide
	 */
	public String getSupplierMustProvide() {
		return supplierMustProvide;
	}

	/**
	 * @param supplierMustProvide the supplierMustProvide to set
	 */
	public void setSupplierMustProvide(String supplierMustProvide) {
		this.supplierMustProvide = supplierMustProvide;
	}

	/**
	 * @return the fowardAuction
	 */
	public Boolean getFowardAuction() {
		return fowardAuction;
	}

	/**
	 * @param fowardAuction the fowardAuction to set
	 */
	public void setFowardAuction(Boolean fowardAuction) {
		this.fowardAuction = fowardAuction;
	}

	/**
	 * @return the amountPerIncrementDecrement
	 */
	public BigDecimal getAmountPerIncrementDecrement() {
		return amountPerIncrementDecrement;
	}

	/**
	 * @param amountPerIncrementDecrement the amountPerIncrementDecrement to set
	 */
	public void setAmountPerIncrementDecrement(BigDecimal amountPerIncrementDecrement) {
		this.amountPerIncrementDecrement = amountPerIncrementDecrement;
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
	 * @return the preBidBy
	 */
	public String getPreBidBy() {
		return preBidBy;
	}

	/**
	 * @param preBidBy the preBidBy to set
	 */
	public void setPreBidBy(String preBidBy) {
		this.preBidBy = preBidBy;
	}

	/**
	 * @return the isPreBidHigherPrice
	 */
	public Boolean getIsPreBidHigherPrice() {
		return isPreBidHigherPrice;
	}

	/**
	 * @param isPreBidHigherPrice the isPreBidHigherPrice to set
	 */
	public void setIsPreBidHigherPrice(Boolean isPreBidHigherPrice) {
		this.isPreBidHigherPrice = isPreBidHigherPrice;
	}

	/**
	 * @return the isPreBidSameBidPrice
	 */
	public Boolean getIsPreBidSameBidPrice() {
		return isPreBidSameBidPrice;
	}

	/**
	 * @param isPreBidSameBidPrice the isPreBidSameBidPrice to set
	 */
	public void setIsPreBidSameBidPrice(Boolean isPreBidSameBidPrice) {
		this.isPreBidSameBidPrice = isPreBidSameBidPrice;
	}

	/**
	 * @return the itemizedBiddingWithTax
	 */
	public Boolean getItemizedBiddingWithTax() {
		return itemizedBiddingWithTax;
	}

	/**
	 * @param itemizedBiddingWithTax the itemizedBiddingWithTax to set
	 */
	public void setItemizedBiddingWithTax(Boolean itemizedBiddingWithTax) {
		this.itemizedBiddingWithTax = itemizedBiddingWithTax;
	}

	/**
	 * @return the biddingType
	 */
	public String getBiddingType() {
		return biddingType;
	}

	/**
	 * @param biddingType the biddingType to set
	 */
	public void setBiddingType(String biddingType) {
		this.biddingType = biddingType;
	}

	/**
	 * @return the biddingMinValueType
	 */
	public String getBiddingMinValueType() {
		return biddingMinValueType;
	}

	/**
	 * @param biddingMinValueType the biddingMinValueType to set
	 */
	public void setBiddingMinValueType(String biddingMinValueType) {
		this.biddingMinValueType = biddingMinValueType;
	}

	/**
	 * @return the biddingMinValue
	 */
	public String getBiddingMinValue() {
		return biddingMinValue;
	}

	/**
	 * @param biddingMinValue the biddingMinValue to set
	 */
	public void setBiddingMinValue(String biddingMinValue) {
		this.biddingMinValue = biddingMinValue;
	}

	/**
	 * @return the isBiddingPriceHigherLeadingBid
	 */
	public Boolean getIsBiddingPriceHigherLeadingBid() {
		return isBiddingPriceHigherLeadingBid;
	}

	/**
	 * @param isBiddingPriceHigherLeadingBid the isBiddingPriceHigherLeadingBid to set
	 */
	public void setIsBiddingPriceHigherLeadingBid(Boolean isBiddingPriceHigherLeadingBid) {
		this.isBiddingPriceHigherLeadingBid = isBiddingPriceHigherLeadingBid;
	}

	/**
	 * @return the biddingPriceHigherLeadingBidType
	 */
	public String getBiddingPriceHigherLeadingBidType() {
		return biddingPriceHigherLeadingBidType;
	}

	/**
	 * @param biddingPriceHigherLeadingBidType the biddingPriceHigherLeadingBidType to set
	 */
	public void setBiddingPriceHigherLeadingBidType(String biddingPriceHigherLeadingBidType) {
		this.biddingPriceHigherLeadingBidType = biddingPriceHigherLeadingBidType;
	}

	/**
	 * @return the lumsumBiddingWithTax
	 */
	public Boolean getLumsumBiddingWithTax() {
		return lumsumBiddingWithTax;
	}

	/**
	 * @param lumsumBiddingWithTax the lumsumBiddingWithTax to set
	 */
	public void setLumsumBiddingWithTax(Boolean lumsumBiddingWithTax) {
		this.lumsumBiddingWithTax = lumsumBiddingWithTax;
	}

	/**
	 * @return the biddingPriceHigherLeadingBidValue
	 */
	public String getBiddingPriceHigherLeadingBidValue() {
		return biddingPriceHigherLeadingBidValue;
	}

	/**
	 * @param biddingPriceHigherLeadingBidValue the biddingPriceHigherLeadingBidValue to set
	 */
	public void setBiddingPriceHigherLeadingBidValue(String biddingPriceHigherLeadingBidValue) {
		this.biddingPriceHigherLeadingBidValue = biddingPriceHigherLeadingBidValue;
	}

	/**
	 * @return the isBiddingAllowSupplierSameBid
	 */
	public Boolean getIsBiddingAllowSupplierSameBid() {
		return isBiddingAllowSupplierSameBid;
	}

	/**
	 * @param isBiddingAllowSupplierSameBid the isBiddingAllowSupplierSameBid to set
	 */
	public void setIsBiddingAllowSupplierSameBid(Boolean isBiddingAllowSupplierSameBid) {
		this.isBiddingAllowSupplierSameBid = isBiddingAllowSupplierSameBid;
	}

	/**
	 * @return the isBiddingMinValueFromPrevious
	 */
	public Boolean getIsBiddingMinValueFromPrevious() {
		return isBiddingMinValueFromPrevious;
	}

	/**
	 * @param isBiddingMinValueFromPrevious the isBiddingMinValueFromPrevious to set
	 */
	public void setIsBiddingMinValueFromPrevious(Boolean isBiddingMinValueFromPrevious) {
		this.isBiddingMinValueFromPrevious = isBiddingMinValueFromPrevious;
	}

	/**
	 * @return the auctionConsolePriceType
	 */
	public String getAuctionConsolePriceType() {
		return auctionConsolePriceType;
	}

	/**
	 * @param auctionConsolePriceType the auctionConsolePriceType to set
	 */
	public void setAuctionConsolePriceType(String auctionConsolePriceType) {
		this.auctionConsolePriceType = auctionConsolePriceType;
	}

	/**
	 * @return the auctionConsoleVenderType
	 */
	public String getAuctionConsoleVenderType() {
		return auctionConsoleVenderType;
	}

	/**
	 * @param auctionConsoleVenderType the auctionConsoleVenderType to set
	 */
	public void setAuctionConsoleVenderType(String auctionConsoleVenderType) {
		this.auctionConsoleVenderType = auctionConsoleVenderType;
	}

	/**
	 * @return the auctionConsoleRankType
	 */
	public String getAuctionConsoleRankType() {
		return auctionConsoleRankType;
	}

	/**
	 * @param auctionConsoleRankType the auctionConsoleRankType to set
	 */
	public void setAuctionConsoleRankType(String auctionConsoleRankType) {
		this.auctionConsoleRankType = auctionConsoleRankType;
	}

	/**
	 * @return the isStartGate
	 */
	public String getIsStartGate() {
		return isStartGate;
	}

	/**
	 * @param isStartGate the isStartGate to set
	 */
	public void setIsStartGate(String isStartGate) {
		this.isStartGate = isStartGate;
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
	 * @return the ownPrevious
	 */
	public String getOwnPrevious() {
		return ownPrevious;
	}

	/**
	 * @param ownPrevious the ownPrevious to set
	 */
	public void setOwnPrevious(String ownPrevious) {
		this.ownPrevious = ownPrevious;
	}

	/**
	 * @return the leadBid
	 */
	public String getLeadBid() {
		return leadBid;
	}

	/**
	 * @param leadBid the leadBid to set
	 */
	public void setLeadBid(String leadBid) {
		this.leadBid = leadBid;
	}

	/**
	 * @return the prebidByTitle
	 */
	public String getPrebidByTitle() {
		return prebidByTitle;
	}

	/**
	 * @param prebidByTitle the prebidByTitle to set
	 */
	public void setPrebidByTitle(String prebidByTitle) {
		this.prebidByTitle = prebidByTitle;
	}

	/**
	 * @return the buyerAuctionConsolePriceType
	 */
	public String getBuyerAuctionConsolePriceType() {
		return buyerAuctionConsolePriceType;
	}

	/**
	 * @param buyerAuctionConsolePriceType the buyerAuctionConsolePriceType to set
	 */
	public void setBuyerAuctionConsolePriceType(String buyerAuctionConsolePriceType) {
		this.buyerAuctionConsolePriceType = buyerAuctionConsolePriceType;
	}

	/**
	 * @return the buyerAuctionConsoleVenderType
	 */
	public String getBuyerAuctionConsoleVenderType() {
		return buyerAuctionConsoleVenderType;
	}

	/**
	 * @param buyerAuctionConsoleVenderType the buyerAuctionConsoleVenderType to set
	 */
	public void setBuyerAuctionConsoleVenderType(String buyerAuctionConsoleVenderType) {
		this.buyerAuctionConsoleVenderType = buyerAuctionConsoleVenderType;
	}

	/**
	 * @return the buyerAuctionConsoleRankType
	 */
	public String getBuyerAuctionConsoleRankType() {
		return buyerAuctionConsoleRankType;
	}

	/**
	 * @param buyerAuctionConsoleRankType the buyerAuctionConsoleRankType to set
	 */
	public void setBuyerAuctionConsoleRankType(String buyerAuctionConsoleRankType) {
		this.buyerAuctionConsoleRankType = buyerAuctionConsoleRankType;
	}

	/**
	 * @return the supplierAuctionSetting
	 */
	public String getSupplierAuctionSetting() {
		return supplierAuctionSetting;
	}

	/**
	 * @param supplierAuctionSetting the supplierAuctionSetting to set
	 */
	public void setSupplierAuctionSetting(String supplierAuctionSetting) {
		this.supplierAuctionSetting = supplierAuctionSetting;
	}

	public String getPrebidAsFirstBid() {
		return prebidAsFirstBid;
	}

	public void setPrebidAsFirstBid(String prebidAsFirstBid) {
		this.prebidAsFirstBid = prebidAsFirstBid;
	}

	/**
	 * @return the preBidSameBidPriceValue
	 */
	public String getPreBidSameBidPriceValue() {
		return preBidSameBidPriceValue;
	}

	/**
	 * @param preBidSameBidPriceValue the preBidSameBidPriceValue to set
	 */
	public void setPreBidSameBidPriceValue(String preBidSameBidPriceValue) {
		this.preBidSameBidPriceValue = preBidSameBidPriceValue;
	}

	/**
	 * @return the preSetSamePreBidForAllSuppValue
	 */
	public String getPreSetSamePreBidForAllSuppValue() {
		return preSetSamePreBidForAllSuppValue;
	}

	/**
	 * @param preSetSamePreBidForAllSuppValue the preSetSamePreBidForAllSuppValue to set
	 */
	public void setPreSetSamePreBidForAllSuppValue(String preSetSamePreBidForAllSuppValue) {
		this.preSetSamePreBidForAllSuppValue = preSetSamePreBidForAllSuppValue;
	}

	/**
	 * @return the isPreSetSamePreBidForAllSupp
	 */
	public Boolean getIsPreSetSamePreBidForAllSupp() {
		return isPreSetSamePreBidForAllSupp;
	}

	/**
	 * @param isPreSetSamePreBidForAllSupp the isPreSetSamePreBidForAllSupp to set
	 */
	public void setIsPreSetSamePreBidForAllSupp(Boolean isPreSetSamePreBidForAllSupp) {
		this.isPreSetSamePreBidForAllSupp = isPreSetSamePreBidForAllSupp;
	}

}
