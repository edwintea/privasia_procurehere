package com.privasia.procurehere.core.entity;

/**
 * @author RT-Kapil
 */

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Digits;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.privasia.procurehere.core.enums.AuctionConsolePriceVenderType;
import com.privasia.procurehere.core.enums.AuctionType;
import com.privasia.procurehere.core.enums.DurationMinSecType;
import com.privasia.procurehere.core.enums.PreBidByType;
import com.privasia.procurehere.core.enums.ValueType;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "PROC_AUCTION_RULES")
public class AuctionRules implements Serializable {

	private static final long serialVersionUID = 3378689263672920719L;

	public interface AuctionCreate {
	}

	@Id
	@GenericGenerator(name = "idGen", strategy = "uuid.hex")
	@GeneratedValue(generator = "idGen")
	@Column(name = "ID", length = 64)
	private String id;

	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.REFRESH }, optional = false)
	@JoinColumn(name = "EVENT_ID", foreignKey = @ForeignKey(name = "FK_RFA_AUCTION_RULE"))
	private RfaEvent event;

	@Enumerated(EnumType.STRING)
	@Column(name = "AUCTION_TYPE")
	private AuctionType auctionType;

	@Column(name = "FORWARD_AUCTION")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean fowardAuction;

	@Enumerated(EnumType.STRING)
	@Column(name = "PRE_BID_BY")
	private PreBidByType preBidBy;

	@Column(name = "PRE_BID_HIGHER_PRICE")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean isPreBidHigherPrice;

	@Column(name = "PRE_SAME_BID_PRICE")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean isPreBidSameBidPrice;

	@Column(name = "IS_BID_MIN_VALUE_PREVIOUS")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean isBiddingMinValueFromPrevious;

	@Enumerated(EnumType.STRING)
	@Column(name = "BID_MIN_VALUE_TYPE")
	private ValueType biddingMinValueType;

	@Column(name = "BID_MIN_VALUE", precision = 22, scale = 6)
	private BigDecimal biddingMinValue;

	@Column(name = "IS_START_GATE")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean isStartGate;

	@Column(name = "IS_PRICE_HIGHER_LEAD_BID")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean isBiddingPriceHigherLeadingBid;

	@Enumerated(EnumType.STRING)
	@Column(name = "HIGHER_PRICE_LEAD_BID_TYPE")
	private ValueType biddingPriceHigherLeadingBidType;

	@Column(name = "PRICE_HIGHER_LEAD_BID_VAL", precision = 22, scale = 6)
	private BigDecimal biddingPriceHigherLeadingBidValue;

	@Column(name = "IS_BID_SUPPLIER_SAME_PRICE")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean isBiddingAllowSupplierSameBid;

	@Enumerated(EnumType.STRING)
	@Column(name = "AUCTION_CON_PRICE_TYPE")
	private AuctionConsolePriceVenderType auctionConsolePriceType;

	@Enumerated(EnumType.STRING)
	@Column(name = "AUCTION_CON_VENDER_TYPE")
	private AuctionConsolePriceVenderType auctionConsoleVenderType;

	@Enumerated(EnumType.STRING)
	@Column(name = "AUCTION_CON_RANK_TYPE")
	private AuctionConsolePriceVenderType auctionConsoleRankType;

	@Enumerated(EnumType.STRING)
	@Column(name = "AUCTION_BUY_CON_PRICE_TYPE")
	private AuctionConsolePriceVenderType buyerAuctionConsolePriceType;

	@Enumerated(EnumType.STRING)
	@Column(name = "AUCTION_BUY_CON_VENDER_TYPE")
	private AuctionConsolePriceVenderType buyerAuctionConsoleVenderType;

	@Enumerated(EnumType.STRING)
	@Column(name = "AUCTION_BUY_CON_RANK_TYPE")
	private AuctionConsolePriceVenderType buyerAuctionConsoleRankType;

	@Column(name = "DUTCH_START_PRICE", precision = 22, scale = 6)
	private BigDecimal dutchStartPrice;

	@Column(name = "DUTCH_MINIMUM_PRICE", precision = 22, scale = 6)
	private BigDecimal dutchMinimumPrice;

	@Column(name = "DUTCH_AMOUNT_DIFFERENCE", precision = 22, scale = 6)
	private BigDecimal amountPerIncrementDecrement;

	@Enumerated(EnumType.STRING)
	@Column(name = "INTERVAL_TYPE")
	private DurationMinSecType intervalType;

	@Column(name = "AUCTION_INTERVAL", length = 3)
	@Digits(integer = 3, fraction = 0, message = "{event.days}", groups = { AuctionCreate.class })
	private Integer interval;

	@Column(name = "DUTCH_AUCTION_TOTAL_STEP", length = 6)
	@Digits(integer = 6, fraction = 0)
	private Integer dutchAuctionTotalStep;

	@Column(name = "DUTCH_AUCTION_CURRENT_STEP", length = 6)
	@Digits(integer = 6, fraction = 0)
	private Integer dutchAuctionCurrentStep;

	@Column(name = "DUTCH_AUCTION_CURRENT_AMNT", precision = 22, scale = 6)
	private BigDecimal dutchAuctionCurrentStepAmount;

	@Column(name = "AUCTION_STARTED")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean auctionStarted = Boolean.FALSE;

	@Column(name = "ITEMIZED_BIDDING_TAX")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean itemizedBiddingWithTax;

	@Column(name = "LUMSUM_BIDDING_TAX")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean lumsumBiddingWithTax;

	@Column(name = "PREBID_AS_FIRSTBID")
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean prebidAsFirstBid;

	@Column(name = "PRE_SET_SAME_PRE_BID_SUPP", nullable = false)
	@Type(type = "org.hibernate.type.NumericBooleanType")
private Boolean preSetSamePreBidForAllSuppliers = Boolean.FALSE;

	public AuctionRules() {
		this.auctionStarted = Boolean.FALSE;
		this.fowardAuction = Boolean.TRUE;
		this.isBiddingAllowSupplierSameBid = Boolean.FALSE;
		this.isBiddingMinValueFromPrevious = Boolean.FALSE;
		this.isStartGate = Boolean.FALSE;
		this.isBiddingPriceHigherLeadingBid = Boolean.FALSE;
		this.isPreBidHigherPrice = Boolean.FALSE;
		this.isPreBidSameBidPrice = Boolean.FALSE;
		// this.itemizedBiddingWithTax = Boolean.TRUE;
		this.auctionConsolePriceType = AuctionConsolePriceVenderType.SHOW_ALL;
		this.auctionConsoleRankType = AuctionConsolePriceVenderType.SHOW_ALL;
		this.auctionConsoleVenderType = AuctionConsolePriceVenderType.SHOW_ALL;

		this.buyerAuctionConsolePriceType = AuctionConsolePriceVenderType.SHOW_ALL;
		this.buyerAuctionConsoleRankType = AuctionConsolePriceVenderType.SHOW_ALL;
		this.buyerAuctionConsoleVenderType = AuctionConsolePriceVenderType.SHOW_ALL;
		this.prebidAsFirstBid = Boolean.FALSE;
		this.preSetSamePreBidForAllSuppliers = Boolean.FALSE;
	}

	public AuctionRules copyFrom(AuctionRules auctionRules) {
		AuctionRules ret = new AuctionRules();

		ret.setAmountPerIncrementDecrement(auctionRules.getAmountPerIncrementDecrement());
		ret.setAuctionConsolePriceType(auctionRules.getAuctionConsolePriceType());
		ret.setAuctionConsoleRankType(auctionRules.getAuctionConsoleRankType());
		ret.setAuctionConsoleVenderType(auctionRules.getAuctionConsoleVenderType());

		ret.setBuyerAuctionConsolePriceType(auctionRules.getBuyerAuctionConsolePriceType());
		ret.setBuyerAuctionConsoleRankType(auctionRules.getBuyerAuctionConsoleRankType());
		ret.setBuyerAuctionConsoleVenderType(auctionRules.getBuyerAuctionConsoleVenderType());

		ret.setIsBiddingPriceHigherLeadingBid(auctionRules.getIsBiddingPriceHigherLeadingBid());
		ret.setBiddingPriceHigherLeadingBidType(auctionRules.getBiddingPriceHigherLeadingBidType());
		ret.setBiddingPriceHigherLeadingBidValue(auctionRules.getBiddingPriceHigherLeadingBidValue());
		ret.setAuctionType(auctionRules.getAuctionType());
		ret.setBiddingMinValue(auctionRules.getBiddingMinValue());
		ret.setBiddingMinValueType(auctionRules.getBiddingMinValueType());
		ret.setBiddingPriceHigherLeadingBidType(auctionRules.getBiddingPriceHigherLeadingBidType());
		ret.setBiddingPriceHigherLeadingBidValue(auctionRules.getBiddingPriceHigherLeadingBidValue());
		ret.setDutchMinimumPrice(auctionRules.getDutchMinimumPrice());
		ret.setDutchStartPrice(auctionRules.getDutchStartPrice());
		ret.setFowardAuction(auctionRules.getFowardAuction());
		ret.setInterval(auctionRules.getInterval());
		ret.setIntervalType(auctionRules.getIntervalType());
		ret.setIsBiddingAllowSupplierSameBid(auctionRules.getIsBiddingAllowSupplierSameBid());
		ret.setIsBiddingMinValueFromPrevious(auctionRules.getIsBiddingMinValueFromPrevious());
		ret.setIsPreBidSameBidPrice(auctionRules.getIsPreBidSameBidPrice());
		ret.setIsStartGate(auctionRules.getIsStartGate());
		ret.setIsPreBidHigherPrice(auctionRules.getIsPreBidHigherPrice());
		ret.setPreBidBy(auctionRules.getPreBidBy());
		ret.setLumsumBiddingWithTax(auctionRules.getLumsumBiddingWithTax());
		ret.setItemizedBiddingWithTax(auctionRules.getItemizedBiddingWithTax());
		ret.setPrebidAsFirstBid(auctionRules.getPrebidAsFirstBid());
		ret.setPreSetSamePreBidForAllSuppliers(auctionRules.getPreSetSamePreBidForAllSuppliers());
		return ret;
	}

	public AuctionRules(AuctionType auctionType, Boolean fowardAuction, AuctionConsolePriceVenderType auctionConsolePriceType, AuctionConsolePriceVenderType auctionConsoleVenderType, AuctionConsolePriceVenderType auctionConsoleRankType) {
		super();
		this.auctionType = auctionType;
		this.fowardAuction = fowardAuction;
		this.auctionConsolePriceType = auctionConsolePriceType;
		this.auctionConsoleVenderType = auctionConsoleVenderType;
		this.auctionConsoleRankType = auctionConsoleRankType;
	}

	public AuctionRules(AuctionType auctionType, Boolean fowardAuction, AuctionConsolePriceVenderType auctionConsolePriceType, AuctionConsolePriceVenderType auctionConsoleVenderType, AuctionConsolePriceVenderType auctionConsoleRankType, AuctionConsolePriceVenderType buyerAuctionConsolePriceType, AuctionConsolePriceVenderType buyerAuctionConsoleVenderType, AuctionConsolePriceVenderType buyerAuctionConsoleRankType) {
		super();
		this.auctionType = auctionType;
		this.fowardAuction = fowardAuction;
		this.auctionConsolePriceType = auctionConsolePriceType;
		this.auctionConsoleVenderType = auctionConsoleVenderType;
		this.auctionConsoleRankType = auctionConsoleRankType;
		this.buyerAuctionConsolePriceType = buyerAuctionConsolePriceType;
		this.buyerAuctionConsoleVenderType = buyerAuctionConsoleVenderType;
		this.buyerAuctionConsoleRankType = buyerAuctionConsoleRankType;
	}

	/**
	 * @param auctionType
	 * @param fowardAuction
	 * @param isBiddingMinValueFromPrevious
	 * @param isBiddingPriceHigherLeadingBid
	 * @param isBiddingAllowSupplierSameBid
	 */
	public AuctionRules(AuctionType auctionType, Boolean fowardAuction, Boolean isBiddingMinValueFromPrevious, Boolean isBiddingPriceHigherLeadingBid, Boolean isBiddingAllowSupplierSameBid) {
		super();
		this.auctionType = auctionType;
		this.fowardAuction = fowardAuction;
		this.isBiddingMinValueFromPrevious = isBiddingMinValueFromPrevious;
		this.isBiddingPriceHigherLeadingBid = isBiddingPriceHigherLeadingBid;
		this.isBiddingAllowSupplierSameBid = isBiddingAllowSupplierSameBid;
	}

	public AuctionConsolePriceVenderType getBuyerAuctionConsolePriceType() {
		return buyerAuctionConsolePriceType;
	}

	public void setBuyerAuctionConsolePriceType(AuctionConsolePriceVenderType buyerAuctionConsolePriceType) {
		this.buyerAuctionConsolePriceType = buyerAuctionConsolePriceType;
	}

	public AuctionConsolePriceVenderType getBuyerAuctionConsoleVenderType() {
		return buyerAuctionConsoleVenderType;
	}

	public void setBuyerAuctionConsoleVenderType(AuctionConsolePriceVenderType buyerAuctionConsoleVenderType) {
		this.buyerAuctionConsoleVenderType = buyerAuctionConsoleVenderType;
	}

	public AuctionConsolePriceVenderType getBuyerAuctionConsoleRankType() {
		return buyerAuctionConsoleRankType;
	}

	public void setBuyerAuctionConsoleRankType(AuctionConsolePriceVenderType buyerAuctionConsoleRankType) {
		this.buyerAuctionConsoleRankType = buyerAuctionConsoleRankType;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the event
	 */
	public RfaEvent getEvent() {
		return event;
	}

	/**
	 * @param event the event to set
	 */
	public void setEvent(RfaEvent event) {
		this.event = event;
	}

	/**
	 * @return the auctionType
	 */
	public AuctionType getAuctionType() {
		return auctionType;
	}

	/**
	 * @param auctionType the auctionType to set
	 */
	public void setAuctionType(AuctionType auctionType) {
		this.auctionType = auctionType;
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
	 * @return the preBidBy
	 */
	public PreBidByType getPreBidBy() {
		return preBidBy;
	}

	/**
	 * @param preBidBy the preBidBy to set
	 */
	public void setPreBidBy(PreBidByType preBidBy) {
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
	 * @return the biddingMinValueType
	 */
	public ValueType getBiddingMinValueType() {
		return biddingMinValueType;
	}

	/**
	 * @param biddingMinValueType the biddingMinValueType to set
	 */
	public void setBiddingMinValueType(ValueType biddingMinValueType) {
		this.biddingMinValueType = biddingMinValueType;
	}

	/**
	 * @return the biddingMinValue
	 */
	public BigDecimal getBiddingMinValue() {
		return biddingMinValue;
	}

	/**
	 * @param biddingMinValue the biddingMinValue to set
	 */
	public void setBiddingMinValue(BigDecimal biddingMinValue) {
		this.biddingMinValue = biddingMinValue;
	}

	/**
	 * @return the isStartGate
	 */
	public Boolean getIsStartGate() {
		return isStartGate;
	}

	/**
	 * @param isStartGate the isStartGate to set
	 */
	public void setIsStartGate(Boolean isStartGate) {
		this.isStartGate = isStartGate;
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
	public ValueType getBiddingPriceHigherLeadingBidType() {
		return biddingPriceHigherLeadingBidType;
	}

	/**
	 * @param biddingPriceHigherLeadingBidType the biddingPriceHigherLeadingBidType to set
	 */
	public void setBiddingPriceHigherLeadingBidType(ValueType biddingPriceHigherLeadingBidType) {
		this.biddingPriceHigherLeadingBidType = biddingPriceHigherLeadingBidType;
	}

	/**
	 * @return the biddingPriceHigherLeadingBidValue
	 */
	public BigDecimal getBiddingPriceHigherLeadingBidValue() {
		return biddingPriceHigherLeadingBidValue;
	}

	/**
	 * @param biddingPriceHigherLeadingBidValue the biddingPriceHigherLeadingBidValue to set
	 */
	public void setBiddingPriceHigherLeadingBidValue(BigDecimal biddingPriceHigherLeadingBidValue) {
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
	 * @return the auctionConsolePriceType
	 */
	public AuctionConsolePriceVenderType getAuctionConsolePriceType() {
		return auctionConsolePriceType;
	}

	/**
	 * @param auctionConsolePriceType the auctionConsolePriceType to set
	 */
	public void setAuctionConsolePriceType(AuctionConsolePriceVenderType auctionConsolePriceType) {
		this.auctionConsolePriceType = auctionConsolePriceType;
	}

	/**
	 * @return the auctionConsoleVenderType
	 */
	public AuctionConsolePriceVenderType getAuctionConsoleVenderType() {
		return auctionConsoleVenderType;
	}

	/**
	 * @param auctionConsoleVenderType the auctionConsoleVenderType to set
	 */
	public void setAuctionConsoleVenderType(AuctionConsolePriceVenderType auctionConsoleVenderType) {
		this.auctionConsoleVenderType = auctionConsoleVenderType;
	}

	/**
	 * @return the auctionConsoleRankType
	 */
	public AuctionConsolePriceVenderType getAuctionConsoleRankType() {
		return auctionConsoleRankType;
	}

	/**
	 * @param auctionConsoleRankType the auctionConsoleRankType to set
	 */
	public void setAuctionConsoleRankType(AuctionConsolePriceVenderType auctionConsoleRankType) {
		this.auctionConsoleRankType = auctionConsoleRankType;
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
	 * @return the dutchMinimumPrice
	 */
	public BigDecimal getDutchMinimumPrice() {
		return dutchMinimumPrice;
	}

	/**
	 * @param dutchMinimumPrice the dutchMinimumPrice to set
	 */
	public void setDutchMinimumPrice(BigDecimal dutchMinimumPrice) {
		this.dutchMinimumPrice = dutchMinimumPrice;
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
	 * @return the intervalType
	 */
	public DurationMinSecType getIntervalType() {
		return intervalType;
	}

	/**
	 * @param intervalType the intervalType to set
	 */
	public void setIntervalType(DurationMinSecType intervalType) {
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
	 * @return the dutchAuctionTotalStep
	 */
	public Integer getDutchAuctionTotalStep() {
		return dutchAuctionTotalStep;
	}

	/**
	 * @param dutchAuctionTotalStep the dutchAuctionTotalStep to set
	 */
	public void setDutchAuctionTotalStep(Integer dutchAuctionTotalStep) {
		this.dutchAuctionTotalStep = dutchAuctionTotalStep;
	}

	/**
	 * @return the dutchAuctionCurrentStep
	 */
	public Integer getDutchAuctionCurrentStep() {
		return dutchAuctionCurrentStep;
	}

	/**
	 * @param dutchAuctionCurrentStep the dutchAuctionCurrentStep to set
	 */
	public void setDutchAuctionCurrentStep(Integer dutchAuctionCurrentStep) {
		this.dutchAuctionCurrentStep = dutchAuctionCurrentStep;
	}

	/**
	 * @return the dutchAuctionCurrentStepAmount
	 */
	public BigDecimal getDutchAuctionCurrentStepAmount() {
		return dutchAuctionCurrentStepAmount;
	}

	/**
	 * @param dutchAuctionCurrentStepAmount the dutchAuctionCurrentStepAmount to set
	 */
	public void setDutchAuctionCurrentStepAmount(BigDecimal dutchAuctionCurrentStepAmount) {
		this.dutchAuctionCurrentStepAmount = dutchAuctionCurrentStepAmount;
	}

	/**
	 * @return the auctionStarted
	 */
	public Boolean getAuctionStarted() {
		return auctionStarted;
	}

	/**
	 * @param auctionStarted the auctionStarted to set
	 */
	public void setAuctionStarted(Boolean auctionStarted) {
		this.auctionStarted = auctionStarted;
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

	public Boolean getPrebidAsFirstBid() {
		return prebidAsFirstBid;
	}

	public void setPrebidAsFirstBid(Boolean prebidAsFirstBid) {
		this.prebidAsFirstBid = prebidAsFirstBid;
	}

	/**
	 * @return the preSetSamePreBidForAllSuppliers
	 */
	public Boolean getPreSetSamePreBidForAllSuppliers() {
		return preSetSamePreBidForAllSuppliers;
	}

	/**
	 * @param preSetSamePreBidForAllSuppliers the preSetSamePreBidForAllSuppliers to set
	 */
	public void setPreSetSamePreBidForAllSuppliers(Boolean preSetSamePreBidForAllSuppliers) {
		this.preSetSamePreBidForAllSuppliers = preSetSamePreBidForAllSuppliers;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((amountPerIncrementDecrement == null) ? 0 : amountPerIncrementDecrement.hashCode());
		result = prime * result + ((biddingMinValue == null) ? 0 : biddingMinValue.hashCode());
		result = prime * result + ((biddingPriceHigherLeadingBidValue == null) ? 0 : biddingPriceHigherLeadingBidValue.hashCode());
		result = prime * result + ((dutchMinimumPrice == null) ? 0 : dutchMinimumPrice.hashCode());
		result = prime * result + ((dutchStartPrice == null) ? 0 : dutchStartPrice.hashCode());
		result = prime * result + ((interval == null) ? 0 : interval.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AuctionRules other = (AuctionRules) obj;
		if (amountPerIncrementDecrement == null) {
			if (other.amountPerIncrementDecrement != null)
				return false;
		} else if (!amountPerIncrementDecrement.equals(other.amountPerIncrementDecrement))
			return false;
		if (biddingMinValue == null) {
			if (other.biddingMinValue != null)
				return false;
		} else if (!biddingMinValue.equals(other.biddingMinValue))
			return false;
		if (biddingPriceHigherLeadingBidValue == null) {
			if (other.biddingPriceHigherLeadingBidValue != null)
				return false;
		} else if (!biddingPriceHigherLeadingBidValue.equals(other.biddingPriceHigherLeadingBidValue))
			return false;
		if (dutchMinimumPrice == null) {
			if (other.dutchMinimumPrice != null)
				return false;
		} else if (!dutchMinimumPrice.equals(other.dutchMinimumPrice))
			return false;
		if (dutchStartPrice == null) {
			if (other.dutchStartPrice != null)
				return false;
		} else if (!dutchStartPrice.equals(other.dutchStartPrice))
			return false;
		if (interval == null) {
			if (other.interval != null)
				return false;
		} else if (!interval.equals(other.interval))
			return false;
		return true;
	}

	public String toLogString() {
		try {
			return "AuctionRules [id=" + id + ", auctionType=" + auctionType + ", fowardAuction=" + fowardAuction + ", preBidBy=" + preBidBy + ", isPreBidHigherPrice=" + isPreBidHigherPrice + ", isPreBidSameBidPrice=" + isPreBidSameBidPrice + ", isBiddingMinValueFromPrevious=" + isBiddingMinValueFromPrevious + ", biddingMinValueType=" + biddingMinValueType + ", biddingMinValue=" + biddingMinValue + ", isStartGate=" + isStartGate + ", isBiddingPriceHigherLeadingBid=" + isBiddingPriceHigherLeadingBid + ", biddingPriceHigherLeadingBidType=" + biddingPriceHigherLeadingBidType + ", biddingPriceHigherLeadingBidValue=" + biddingPriceHigherLeadingBidValue + ", isBiddingAllowSupplierSameBid=" + isBiddingAllowSupplierSameBid + ", auctionConsolePriceType=" + auctionConsolePriceType + ", auctionConsoleVenderType=" + auctionConsoleVenderType + ", auctionConsoleRankType=" + auctionConsoleRankType + ", dutchStartPrice=" + dutchStartPrice + ", dutchMinimumPrice=" + dutchMinimumPrice + ", amountPerIncrementDecrement=" + amountPerIncrementDecrement + ", intervalType=" + intervalType + ", interval=" + interval + ", dutchAuctionTotalStep=" + dutchAuctionTotalStep + ", dutchAuctionCurrentStep=" + dutchAuctionCurrentStep + ", dutchAuctionCurrentStepAmount=" + dutchAuctionCurrentStepAmount + ", auctionStarted=" + auctionStarted + ", itemizedBiddingWithTax=" + itemizedBiddingWithTax + ", lumsumBiddingWithTax=" + lumsumBiddingWithTax + "]";
		} catch (Exception e) {
			return "some exception in LogString : " + e.getMessage();
		}
	}
}
