/**
 * 
 */
package com.privasia.procurehere.core.pojo;

import java.io.Serializable;
import java.math.BigDecimal;

import com.privasia.procurehere.core.entity.BusinessUnit;
import com.privasia.procurehere.core.entity.CostCenter;
import com.privasia.procurehere.core.entity.Currency;
import com.privasia.procurehere.core.entity.Declaration;
import com.privasia.procurehere.core.entity.GroupCode;
import com.privasia.procurehere.core.entity.IndustryCategory;
import com.privasia.procurehere.core.entity.ProcurementCategories;
import com.privasia.procurehere.core.entity.ProcurementMethod;
import com.privasia.procurehere.core.enums.AuctionConsolePriceVenderType;
import com.privasia.procurehere.core.enums.DurationType;
import com.privasia.procurehere.core.enums.PreBidByType;
import com.privasia.procurehere.core.enums.TimeExtensionType;
import com.privasia.procurehere.core.enums.ValueType;

/**
 * @author Nitin Otageri
 */
public class TemplateFieldPojo implements Serializable {

	private static final long serialVersionUID = 1221536510666186185L;

	private String eventName;
	private Boolean eventNameVisible = true;
	private Boolean eventNameDisabled;
	private Boolean eventNameOptional;

	private Integer submissionValidityDays;
	private Boolean submissionValidityDaysVisible = true;
	private Boolean submissionValidityDaysDisabled;
	private Boolean submissionValidityDaysOptional;

	private IndustryCategory industryCategory;
	private String industryCatArr;
	private Boolean industryCategoryVisible = true;
	private Boolean industryCategoryDisabled;
	private Boolean industryCategoryOptional;

	private BigDecimal participationFees;
	private Boolean participationFeesVisible = true;
	private Boolean participationFeesDisabled;
	private Boolean participationFeesOptional;

	private Currency participationFeeCurrency;
	private Boolean participationFeeCurrencyVisible = true;
	private Boolean participationFeeCurrencyDisabled;
	private Boolean participationFeeCurrencyOptional;

	// private String eventCategory;
	// private Boolean eventCategoryVisible = true;
	// private Boolean eventCategoryDisabled;
	// private Boolean eventCategoryOptional;

	private Currency baseCurrency;
	private Boolean baseCurrencyVisible = true;
	private Boolean baseCurrencyDisabled;
	private Boolean baseCurrencyOptional;

	private Integer decimal;
	private Boolean decimalVisible = true;
	private Boolean decimalDisabled;
	private Boolean decimalOptional;

	private CostCenter costCenter;
	private Boolean costCenterVisible = true;
	private Boolean costCenterDisabled;
	private Boolean costCenterOptional;

	private BusinessUnit businessUnit;
	private Boolean businessUnitDisabled = Boolean.FALSE;

	private BigDecimal budgetAmount;
	private Boolean budgetAmountVisible = true;
	private Boolean budgetAmountDisabled;
	private Boolean budgetAmountOptional;

	private BigDecimal historicAmount;
	private Boolean historicAmountVisible = true;
	private Boolean historicAmountDisabled;
	private Boolean historicAmountOptional = true;

	private String paymentTerms;
	private Boolean paymentTermsVisible = true;
	private Boolean paymentTermsDisabled;
	private Boolean paymentTermsOptional;

	private Boolean documentRequired;
	private Boolean documentRequiredVisible = true;
	private Boolean documentRequiredDisabled;
	private Boolean documentRequiredOptional;

	private Boolean meetingRequired;
	private Boolean meetingRequiredVisible = true;
	private Boolean meetingRequiredDisabled;
	private Boolean meetingRequiredOptional;

	private Boolean questionnaireRequired;
	private Boolean questionnaireRequiredVisible = true;
	private Boolean questionnaireRequiredDisabled;
	private Boolean questionnaireRequiredOptional;

	private Boolean billOfQuantitiesRequired;
	private Boolean billOfQuantitiesRequiredVisible = true;
	private Boolean billOfQuantitiesRequiredDisabled;
	private Boolean billOfQuantitiesRequiredOptional;

	// Auction Rules

	// TODO: it only can be readonly
	private PreBidByType preBidBy;
	private Boolean preBidByDisabled;
	private Boolean preBidByVisible;
	private Boolean preBidByOptional;

	private Boolean isPreBidSameBidPrice;
	private Boolean isPreBidSameBidPriceVisible = true;
	private Boolean isPreBidSameBidPriceDisabled;
	private Boolean isPreBidSameBidPriceOptional;

	private Boolean isPreBidHigherPrice;
	private Boolean isPreBidHigherPriceVisible = true;
	private Boolean isPreBidHigherPriceDisabled;
	private Boolean isPreBidHigherPriceOptional;

	private String biddingType;
	private Boolean biddingTypeDisabled;
	private Boolean biddingTypeVisible;
	private Boolean biddingTypeOptional;

	private Boolean isBiddingMinValueFromPrevious;
	private Boolean isBiddingMinValueFromPreviousVisible = true;
	private Boolean isBiddingMinValueFromPreviousDisabled;
	private Boolean isBiddingMinValueFromPreviousOptional;

	private ValueType biddingMinValueType;

	private BigDecimal biddingMinValue;

	private Boolean isStartGate;
	private Boolean isStartGateVisible = true;
	private Boolean isStartGateDisabled;
	private Boolean isStartGateOptional;

	private Boolean isBiddingPriceHigherLeadingBid;
	private Boolean isBiddingPriceHigherLeadingBidVisible = true;
	private Boolean isBiddingPriceHigherLeadingBidDisabled;
	private Boolean isBiddingPriceHigherLeadingBidOptional;

	private ValueType biddingPriceHigherLeadingBidType;

	private BigDecimal biddingPriceHigherLeadingBidValue;

	private Boolean isBiddingAllowSupplierSameBid;
	private Boolean isBiddingAllowSupplierSameBidVisible = true;
	private Boolean isBiddingAllowSupplierSameBidDisabled;
	private Boolean isBiddingAllowSupplierSameBidOptional;

	private AuctionConsolePriceVenderType auctionConsolePriceType;
	private Boolean auctionConsolePriceTypeDisabled;
	private Boolean auctionConsolePriceTypeVisible;
	private Boolean auctionConsolePriceTypeOptional;

	private AuctionConsolePriceVenderType auctionConsoleVenderType;
	private Boolean auctionConsoleVenderTypeDisabled;
	private Boolean auctionConsoleVenderTypeVisible;
	private Boolean auctionConsoleVenderTypeOptional;

	private AuctionConsolePriceVenderType auctionConsoleRankType;
	private Boolean auctionConsoleRankTypeDisabled;
	private Boolean auctionConsoleRankTypeVisible;
	private Boolean auctionConsoleRankTypeOptional;

	private AuctionConsolePriceVenderType buyerAuctionConsolePriceType;
	private Boolean buyerAuctionConsolePriceTypeDisabled;
	private Boolean buyerAuctionConsolePriceTypeVisible;
	private Boolean buyerAuctionConsolePriceTypeOptional;

	private AuctionConsolePriceVenderType buyerAuctionConsoleVenderType;
	private Boolean buyerAuctionConsoleVenderTypeDisabled;
	private Boolean buyerAuctionConsoleVenderTypeVisible;
	private Boolean buyerAuctionConsoleVenderTypeOptional;

	private AuctionConsolePriceVenderType buyerAuctionConsoleRankType;
	private Boolean buyerAuctionConsoleRankTypeDisabled;
	private Boolean buyerAuctionConsoleRankTypeVisible;
	private Boolean buyerAuctionConsoleRankTypeOptional;

	private TimeExtensionType timeExtensionType;
	private Boolean timeExtensionTypeDisabled;
	private Boolean timeExtensionTypeVisible;
	private Boolean timeExtensionTypeOptional;

	private DurationType timeExtensionDurationType;

	private Integer timeExtensionDuration;

	private DurationType timeExtensionLeadingBidType;

	private Integer timeExtensionLeadingBidValue;

	private Integer extensionCount;

	private Integer bidderDisqualify;

	private Boolean autoDisqualify = Boolean.FALSE;
	private Boolean autoDisqualifyVisible = true;
	private Boolean autoDisqualifyDisabled;
	private Boolean autoDisqualifyOptional;

	private Currency depositCurrency;
	private BigDecimal deposit;
	private Boolean addDepositVisible = true;
	private Boolean addDepositDisabled;
	private Boolean addDepositOptional;

	// This value for only disable poperty on rfx template page
	private Boolean extraForDisAbleField;

	// PH-334
	private Boolean expectedTenderVisible;
	private Boolean expectedTenderDisabled;
	private Boolean expectedTenderOptional = true;
	private Boolean feeVisible;
	private Boolean feeDisabled;
	private Boolean feeOptional = true;

	private String expectedTenderDateTimeRange;
	private String feeDateTimeRange;

	// PH-340
	private String minimumSupplierRating;
	private Boolean minimumSupplierRatingVisible = true;
	private Boolean minimumSupplierRatingDisabled;
	private Boolean minimumSupplierRatingOptional = true;

	// PH-340
	private String maximumSupplierRating;
	private Boolean maximumSupplierRatingVisible = true;
	private Boolean maximumSupplierRatingDisabled;
	private Boolean maximumSupplierRatingOptional = true;

	private Declaration evaluationProcessDeclaration;
	private Boolean evaluationDeclarationVisible;
	private Boolean evaluationDeclarationDisabled;

	private Declaration supplierAcceptanceDeclaration;
	private Boolean supplierDeclarationVisible;
	private Boolean supplierDeclarationDisabled;

	// PH-1043
	private Boolean prebidAsFirstBid;
	private Boolean prebidAsFirstBidVisible = true;
	private Boolean prebidAsFirstBidDisabled;
	private Boolean prebidAsFirstBidOptional;

	// PH-1701
	private Boolean disableTotalAmount = Boolean.FALSE;

	// PH-2167
	private BigDecimal estimatedBudget;
	private Boolean estimatedBudgetVisible = true;
	private Boolean estimatedBudgetDisabled;
	private Boolean estimatedBudgetOptional;

	// PH-2168
	private ProcurementMethod procurementMethod;
	private Boolean procurementMethodVisible = true;
	private Boolean procurementMethodDisabled;
	private Boolean procurementMethodOptional = true;

	private ProcurementCategories procurementCategory;
	private Boolean procurementCategoryVisible = true;
	private Boolean procurementCategoryDisabled;
	private Boolean procurementCategoryOptional = true;

	private GroupCode groupCode;
	private Boolean groupCodeVisible = true;
	private Boolean groupCodeDisabled;
	private Boolean groupCodeOptional;

	private Boolean isPreSetSamePreBidForAllSuppliers;
	private Boolean isPreSetSamePreBidForAllSuppliersVisible = true;
	private Boolean isPreSetSamePreBidForAllSuppliersDisabled;
	private Boolean isPreSetSamePreBidForAllSuppliersOptional;

	private Boolean isTemplateUsed;

	public String getMaximumSupplierRating() {
		return maximumSupplierRating;
	}

	public void setMaximumSupplierRating(String maximumSupplierRating) {
		this.maximumSupplierRating = maximumSupplierRating;
	}

	public Boolean getMaximumSupplierRatingVisible() {
		return maximumSupplierRatingVisible;
	}

	public void setMaximumSupplierRatingVisible(Boolean maximumSupplierRatingVisible) {
		this.maximumSupplierRatingVisible = maximumSupplierRatingVisible;
	}

	public Boolean getMaximumSupplierRatingDisabled() {
		return maximumSupplierRatingDisabled;
	}

	public void setMaximumSupplierRatingDisabled(Boolean maximumSupplierRatingDisabled) {
		this.maximumSupplierRatingDisabled = maximumSupplierRatingDisabled;
	}

	public Boolean getMaximumSupplierRatingOptional() {
		return maximumSupplierRatingOptional;
	}

	public void setMaximumSupplierRatingOptional(Boolean maximumSupplierRatingOptional) {
		this.maximumSupplierRatingOptional = maximumSupplierRatingOptional;
	}

	public String getMinimumSupplierRating() {
		return minimumSupplierRating;
	}

	public void setMinimumSupplierRating(String minimunSupplierRating) {
		this.minimumSupplierRating = minimunSupplierRating;
	}

	public Boolean getMinimumSupplierRatingVisible() {
		return minimumSupplierRatingVisible;
	}

	public void setMinimumSupplierRatingVisible(Boolean minimunSupplierRatingVisible) {
		this.minimumSupplierRatingVisible = minimunSupplierRatingVisible;
	}

	public Boolean getMinimumSupplierRatingDisabled() {
		return minimumSupplierRatingDisabled;
	}

	public void setMinimumSupplierRatingDisabled(Boolean minimunSupplierRatingDisabled) {
		this.minimumSupplierRatingDisabled = minimunSupplierRatingDisabled;
	}

	public Boolean getMinimumSupplierRatingOptional() {
		return minimumSupplierRatingOptional;
	}

	public void setMinimumSupplierRatingOptional(Boolean minimunSupplierRatingOptional) {
		this.minimumSupplierRatingOptional = minimunSupplierRatingOptional;
	}

	public String getExpectedTenderDateTimeRange() {
		return expectedTenderDateTimeRange;
	}

	public void setExpectedTenderDateTimeRange(String expectedTenderDateTimeRange) {
		this.expectedTenderDateTimeRange = expectedTenderDateTimeRange;
	}

	public String getFeeDateTimeRange() {
		return feeDateTimeRange;
	}

	public void setFeeDateTimeRange(String feeDateTimeRange) {
		this.feeDateTimeRange = feeDateTimeRange;
	}

	public Boolean getExpectedTenderVisible() {
		return expectedTenderVisible;
	}

	public void setExpectedTenderVisible(Boolean expectedTenderVisible) {
		this.expectedTenderVisible = expectedTenderVisible;
	}

	public Boolean getExpectedTenderDisabled() {
		return expectedTenderDisabled;
	}

	public void setExpectedTenderDisabled(Boolean expectedTenderDisabled) {
		this.expectedTenderDisabled = expectedTenderDisabled;
	}

	public Boolean getExpectedTenderOptional() {
		return expectedTenderOptional;
	}

	public void setExpectedTenderOptional(Boolean expectedTenderOptional) {
		this.expectedTenderOptional = expectedTenderOptional;
	}

	public Boolean getFeeVisible() {
		return feeVisible;
	}

	public void setFeeVisible(Boolean feeVisible) {
		this.feeVisible = feeVisible;
	}

	public Boolean getFeeDisabled() {
		return feeDisabled;
	}

	public void setFeeDisabled(Boolean feeDisabled) {
		this.feeDisabled = feeDisabled;
	}

	public Boolean getFeeOptional() {
		return feeOptional;
	}

	public void setFeeOptional(Boolean feeOptional) {
		this.feeOptional = feeOptional;
	}

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
	 * @return the eventNameVisible
	 */
	public Boolean getEventNameVisible() {
		return eventNameVisible;
	}

	/**
	 * @param eventNameVisible the eventNameVisible to set
	 */
	public void setEventNameVisible(Boolean eventNameVisible) {
		this.eventNameVisible = eventNameVisible;
	}

	/**
	 * @return the eventNameDisabled
	 */
	public Boolean getEventNameDisabled() {
		return eventNameDisabled;
	}

	/**
	 * @param eventNameDisabled the eventNameDisabled to set
	 */
	public void setEventNameDisabled(Boolean eventNameDisabled) {
		this.eventNameDisabled = eventNameDisabled;
	}

	/**
	 * @return the eventNameOptional
	 */
	public Boolean getEventNameOptional() {
		return eventNameOptional;
	}

	/**
	 * @param eventNameOptional the eventNameOptional to set
	 */
	public void setEventNameOptional(Boolean eventNameOptional) {
		this.eventNameOptional = eventNameOptional;
	}

	/**
	 * @return the submissionValidityDays
	 */
	public Integer getSubmissionValidityDays() {
		return submissionValidityDays;
	}

	/**
	 * @param submissionValidityDays the submissionValidityDays to set
	 */
	public void setSubmissionValidityDays(Integer submissionValidityDays) {
		this.submissionValidityDays = submissionValidityDays;
	}

	/**
	 * @return the submissionValidityDaysVisible
	 */
	public Boolean getSubmissionValidityDaysVisible() {
		return submissionValidityDaysVisible;
	}

	/**
	 * @param submissionValidityDaysVisible the submissionValidityDaysVisible to set
	 */
	public void setSubmissionValidityDaysVisible(Boolean submissionValidityDaysVisible) {
		this.submissionValidityDaysVisible = submissionValidityDaysVisible;
	}

	/**
	 * @return the submissionValidityDaysDisabled
	 */
	public Boolean getSubmissionValidityDaysDisabled() {
		return submissionValidityDaysDisabled;
	}

	/**
	 * @param submissionValidityDaysDisabled the submissionValidityDaysDisabled to set
	 */
	public void setSubmissionValidityDaysDisabled(Boolean submissionValidityDaysDisabled) {
		this.submissionValidityDaysDisabled = submissionValidityDaysDisabled;
	}

	/**
	 * @return the submissionValidityDaysOptional
	 */
	public Boolean getSubmissionValidityDaysOptional() {
		return submissionValidityDaysOptional;
	}

	/**
	 * @param submissionValidityDaysOptional the submissionValidityDaysOptional to set
	 */
	public void setSubmissionValidityDaysOptional(Boolean submissionValidityDaysOptional) {
		this.submissionValidityDaysOptional = submissionValidityDaysOptional;
	}

	/**
	 * @return the industryCategory
	 */
	public IndustryCategory getIndustryCategory() {
		return industryCategory;
	}

	/**
	 * @param industryCategory the industryCategory to set
	 */
	public void setIndustryCategory(IndustryCategory industryCategory) {
		this.industryCategory = industryCategory;
	}

	/**
	 * @return the industryCategoryVisible
	 */
	public Boolean getIndustryCategoryVisible() {
		return industryCategoryVisible;
	}

	/**
	 * @param industryCategoryVisible the industryCategoryVisible to set
	 */
	public void setIndustryCategoryVisible(Boolean industryCategoryVisible) {
		this.industryCategoryVisible = industryCategoryVisible;
	}

	/**
	 * @return the industryCategoryDisabled
	 */
	public Boolean getIndustryCategoryDisabled() {
		return industryCategoryDisabled;
	}

	/**
	 * @param industryCategoryDisabled the industryCategoryDisabled to set
	 */
	public void setIndustryCategoryDisabled(Boolean industryCategoryDisabled) {
		this.industryCategoryDisabled = industryCategoryDisabled;
	}

	/**
	 * @return the industryCategoryOptional
	 */
	public Boolean getIndustryCategoryOptional() {
		return industryCategoryOptional;
	}

	/**
	 * @param industryCategoryOptional the industryCategoryOptional to set
	 */
	public void setIndustryCategoryOptional(Boolean industryCategoryOptional) {
		this.industryCategoryOptional = industryCategoryOptional;
	}

	/**
	 * @return the participationFees
	 */
	public BigDecimal getParticipationFees() {
		return participationFees;
	}

	/**
	 * @param participationFees the participationFees to set
	 */
	public void setParticipationFees(BigDecimal participationFees) {
		this.participationFees = participationFees;
	}

	/**
	 * @return the participationFeesVisible
	 */
	public Boolean getParticipationFeesVisible() {
		return participationFeesVisible;
	}

	/**
	 * @param participationFeesVisible the participationFeesVisible to set
	 */
	public void setParticipationFeesVisible(Boolean participationFeesVisible) {
		this.participationFeesVisible = participationFeesVisible;
	}

	/**
	 * @return the participationFeesDisabled
	 */
	public Boolean getParticipationFeesDisabled() {
		return participationFeesDisabled;
	}

	/**
	 * @param participationFeesDisabled the participationFeesDisabled to set
	 */
	public void setParticipationFeesDisabled(Boolean participationFeesDisabled) {
		this.participationFeesDisabled = participationFeesDisabled;
	}

	/**
	 * @return the participationFeesOptional
	 */
	public Boolean getParticipationFeesOptional() {
		return participationFeesOptional;
	}

	/**
	 * @param participationFeesOptional the participationFeesOptional to set
	 */
	public void setParticipationFeesOptional(Boolean participationFeesOptional) {
		this.participationFeesOptional = participationFeesOptional;
	}

	/**
	 * @return the participationFeeCurrency
	 */
	public Currency getParticipationFeeCurrency() {
		return participationFeeCurrency;
	}

	/**
	 * @param participationFeeCurrency the participationFeeCurrency to set
	 */
	public void setParticipationFeeCurrency(Currency participationFeeCurrency) {
		this.participationFeeCurrency = participationFeeCurrency;
	}

	/**
	 * @return the participationFeeCurrencyVisible
	 */
	public Boolean getParticipationFeeCurrencyVisible() {
		return participationFeeCurrencyVisible;
	}

	/**
	 * @param participationFeeCurrencyVisible the participationFeeCurrencyVisible to set
	 */
	public void setParticipationFeeCurrencyVisible(Boolean participationFeeCurrencyVisible) {
		this.participationFeeCurrencyVisible = participationFeeCurrencyVisible;
	}

	/**
	 * @return the participationFeeCurrencyDisabled
	 */
	public Boolean getParticipationFeeCurrencyDisabled() {
		return participationFeeCurrencyDisabled;
	}

	/**
	 * @param participationFeeCurrencyDisabled the participationFeeCurrencyDisabled to set
	 */
	public void setParticipationFeeCurrencyDisabled(Boolean participationFeeCurrencyDisabled) {
		this.participationFeeCurrencyDisabled = participationFeeCurrencyDisabled;
	}

	/**
	 * @return the participationFeeCurrencyOptional
	 */
	public Boolean getParticipationFeeCurrencyOptional() {
		return participationFeeCurrencyOptional;
	}

	/**
	 * @param participationFeeCurrencyOptional the participationFeeCurrencyOptional to set
	 */
	public void setParticipationFeeCurrencyOptional(Boolean participationFeeCurrencyOptional) {
		this.participationFeeCurrencyOptional = participationFeeCurrencyOptional;
	}

	// /**
	// * @return the eventCategory
	// */
	// public String getEventCategory() {
	// return eventCategory;
	// }
	//
	// /**
	// * @param eventCategory the eventCategory to set
	// */
	// public void setEventCategory(String eventCategory) {
	// this.eventCategory = eventCategory;
	// }
	//
	// /**
	// * @return the eventCategoryVisible
	// */
	// public Boolean getEventCategoryVisible() {
	// return eventCategoryVisible;
	// }
	//
	// /**
	// * @param eventCategoryVisible the eventCategoryVisible to set
	// */
	// public void setEventCategoryVisible(Boolean eventCategoryVisible) {
	// this.eventCategoryVisible = eventCategoryVisible;
	// }
	//
	// /**
	// * @return the eventCategoryDisabled
	// */
	// public Boolean getEventCategoryDisabled() {
	// return eventCategoryDisabled;
	// }
	//
	// /**
	// * @param eventCategoryDisabled the eventCategoryDisabled to set
	// */
	// public void setEventCategoryDisabled(Boolean eventCategoryDisabled) {
	// this.eventCategoryDisabled = eventCategoryDisabled;
	// }
	//
	// /**
	// * @return the eventCategoryOptional
	// */
	// public Boolean getEventCategoryOptional() {
	// return eventCategoryOptional;
	// }
	//
	// /**
	// * @param eventCategoryOptional the eventCategoryOptional to set
	// */
	// public void setEventCategoryOptional(Boolean eventCategoryOptional) {
	// this.eventCategoryOptional = eventCategoryOptional;
	// }

	/**
	 * @return the baseCurrency
	 */
	public Currency getBaseCurrency() {
		return baseCurrency;
	}

	/**
	 * @param baseCurrency the baseCurrency to set
	 */
	public void setBaseCurrency(Currency baseCurrency) {
		this.baseCurrency = baseCurrency;
	}

	/**
	 * @return the baseCurrencyVisible
	 */
	public Boolean getBaseCurrencyVisible() {
		return baseCurrencyVisible;
	}

	/**
	 * @param baseCurrencyVisible the baseCurrencyVisible to set
	 */
	public void setBaseCurrencyVisible(Boolean baseCurrencyVisible) {
		this.baseCurrencyVisible = baseCurrencyVisible;
	}

	/**
	 * @return the baseCurrencyDisabled
	 */
	public Boolean getBaseCurrencyDisabled() {
		return baseCurrencyDisabled;
	}

	/**
	 * @param baseCurrencyDisabled the baseCurrencyDisabled to set
	 */
	public void setBaseCurrencyDisabled(Boolean baseCurrencyDisabled) {
		this.baseCurrencyDisabled = baseCurrencyDisabled;
	}

	/**
	 * @return the baseCurrencyOptional
	 */
	public Boolean getBaseCurrencyOptional() {
		return baseCurrencyOptional;
	}

	/**
	 * @param baseCurrencyOptional the baseCurrencyOptional to set
	 */
	public void setBaseCurrencyOptional(Boolean baseCurrencyOptional) {
		this.baseCurrencyOptional = baseCurrencyOptional;
	}

	/**
	 * @return the decimal
	 */
	public Integer getDecimal() {
		return decimal;
	}

	/**
	 * @param decimal the decimal to set
	 */
	public void setDecimal(Integer decimal) {
		this.decimal = decimal;
	}

	/**
	 * @return the decimalVisible
	 */
	public Boolean getDecimalVisible() {
		return decimalVisible;
	}

	/**
	 * @param decimalVisible the decimalVisible to set
	 */
	public void setDecimalVisible(Boolean decimalVisible) {
		this.decimalVisible = decimalVisible;
	}

	/**
	 * @return the decimalDisabled
	 */
	public Boolean getDecimalDisabled() {
		return decimalDisabled;
	}

	/**
	 * @param decimalDisabled the decimalDisabled to set
	 */
	public void setDecimalDisabled(Boolean decimalDisabled) {
		this.decimalDisabled = decimalDisabled;
	}

	/**
	 * @return the decimalOptional
	 */
	public Boolean getDecimalOptional() {
		return decimalOptional;
	}

	/**
	 * @param decimalOptional the decimalOptional to set
	 */
	public void setDecimalOptional(Boolean decimalOptional) {
		this.decimalOptional = decimalOptional;
	}

	/**
	 * @return the costCenter
	 */
	public CostCenter getCostCenter() {
		return costCenter;
	}

	/**
	 * @param costCenter the costCenter to set
	 */
	public void setCostCenter(CostCenter costCenter) {
		this.costCenter = costCenter;
	}

	/**
	 * @return the costCenterVisible
	 */
	public Boolean getCostCenterVisible() {
		return costCenterVisible;
	}

	/**
	 * @param costCenterVisible the costCenterVisible to set
	 */
	public void setCostCenterVisible(Boolean costCenterVisible) {
		this.costCenterVisible = costCenterVisible;
	}

	/**
	 * @return the costCenterDisabled
	 */
	public Boolean getCostCenterDisabled() {
		return costCenterDisabled;
	}

	/**
	 * @param costCenterDisabled the costCenterDisabled to set
	 */
	public void setCostCenterDisabled(Boolean costCenterDisabled) {
		this.costCenterDisabled = costCenterDisabled;
	}

	/**
	 * @return the costCenterOptional
	 */
	public Boolean getCostCenterOptional() {
		return costCenterOptional;
	}

	/**
	 * @param costCenterOptional the costCenterOptional to set
	 */
	public void setCostCenterOptional(Boolean costCenterOptional) {
		this.costCenterOptional = costCenterOptional;
	}

	/**
	 * @return the budgetAmount
	 */
	public BigDecimal getBudgetAmount() {
		return budgetAmount;
	}

	/**
	 * @param budgetAmount the budgetAmount to set
	 */
	public void setBudgetAmount(BigDecimal budgetAmount) {
		this.budgetAmount = budgetAmount;
	}

	/**
	 * @return the historicAmount
	 */
	public BigDecimal getHistoricAmount() {
		return historicAmount;
	}

	/**
	 * @param historicAmount the historicAmount to set
	 */
	public void setHistoricAmount(BigDecimal historicAmount) {
		this.historicAmount = historicAmount;
	}

	/**
	 * @return the budgetAmountVisible
	 */
	public Boolean getBudgetAmountVisible() {
		return budgetAmountVisible;
	}

	/**
	 * @param budgetAmountVisible the budgetAmountVisible to set
	 */
	public void setBudgetAmountVisible(Boolean budgetAmountVisible) {
		this.budgetAmountVisible = budgetAmountVisible;
	}

	/**
	 * @return the budgetAmountDisabled
	 */
	public Boolean getBudgetAmountDisabled() {
		return budgetAmountDisabled;
	}

	/**
	 * @param budgetAmountDisabled the budgetAmountDisabled to set
	 */
	public void setBudgetAmountDisabled(Boolean budgetAmountDisabled) {
		this.budgetAmountDisabled = budgetAmountDisabled;
	}

	/**
	 * @return the budgetAmountOptional
	 */
	public Boolean getBudgetAmountOptional() {
		return budgetAmountOptional;
	}

	/**
	 * @param budgetAmountOptional the budgetAmountOptional to set
	 */
	public void setBudgetAmountOptional(Boolean budgetAmountOptional) {
		this.budgetAmountOptional = budgetAmountOptional;
	}

	/**
	 * @return the historicAmountVisible
	 */
	public Boolean getHistoricAmountVisible() {
		return historicAmountVisible;
	}

	/**
	 * @param historicAmountVisible the historicAmountVisible to set
	 */
	public void setHistoricAmountVisible(Boolean historicAmountVisible) {
		this.historicAmountVisible = historicAmountVisible;
	}

	/**
	 * @return the historicAmountDisabled
	 */
	public Boolean getHistoricAmountDisabled() {
		return historicAmountDisabled;
	}

	/**
	 * @param historicAmountDisabled the historicAmountDisabled to set
	 */
	public void setHistoricAmountDisabled(Boolean historicAmountDisabled) {
		this.historicAmountDisabled = historicAmountDisabled;
	}

	/**
	 * @return the historicAmountOptional
	 */
	public Boolean getHistoricAmountOptional() {
		return historicAmountOptional;
	}

	/**
	 * @param historicAmountOptional the historicAmountOptional to set
	 */
	public void setHistoricAmountOptional(Boolean historicAmountOptional) {
		this.historicAmountOptional = historicAmountOptional;
	}

	/**
	 * @return the paymentTerms
	 */
	public String getPaymentTerms() {
		return paymentTerms;
	}

	/**
	 * @param paymentTerms the paymentTerms to set
	 */
	public void setPaymentTerms(String paymentTerms) {
		this.paymentTerms = paymentTerms;
	}

	/**
	 * @return the paymentTermsVisible
	 */
	public Boolean getPaymentTermsVisible() {
		return paymentTermsVisible;
	}

	/**
	 * @param paymentTermsVisible the paymentTermsVisible to set
	 */
	public void setPaymentTermsVisible(Boolean paymentTermsVisible) {
		this.paymentTermsVisible = paymentTermsVisible;
	}

	/**
	 * @return the paymentTermsDisabled
	 */
	public Boolean getPaymentTermsDisabled() {
		return paymentTermsDisabled;
	}

	/**
	 * @param paymentTermsDisabled the paymentTermsDisabled to set
	 */
	public void setPaymentTermsDisabled(Boolean paymentTermsDisabled) {
		this.paymentTermsDisabled = paymentTermsDisabled;
	}

	/**
	 * @return the paymentTermsOptional
	 */
	public Boolean getPaymentTermsOptional() {
		return paymentTermsOptional;
	}

	/**
	 * @param paymentTermsOptional the paymentTermsOptional to set
	 */
	public void setPaymentTermsOptional(Boolean paymentTermsOptional) {
		this.paymentTermsOptional = paymentTermsOptional;
	}

	/**
	 * @return the documentRequired
	 */
	public Boolean getDocumentRequired() {
		return documentRequired;
	}

	/**
	 * @param documentRequired the documentRequired to set
	 */
	public void setDocumentRequired(Boolean documentRequired) {
		this.documentRequired = documentRequired;
	}

	/**
	 * @return the documentRequiredVisible
	 */
	public Boolean getDocumentRequiredVisible() {
		return documentRequiredVisible;
	}

	/**
	 * @param documentRequiredVisible the documentRequiredVisible to set
	 */
	public void setDocumentRequiredVisible(Boolean documentRequiredVisible) {
		this.documentRequiredVisible = documentRequiredVisible;
	}

	/**
	 * @return the documentRequiredDisabled
	 */
	public Boolean getDocumentRequiredDisabled() {
		return documentRequiredDisabled;
	}

	/**
	 * @param documentRequiredDisabled the documentRequiredDisabled to set
	 */
	public void setDocumentRequiredDisabled(Boolean documentRequiredDisabled) {
		this.documentRequiredDisabled = documentRequiredDisabled;
	}

	/**
	 * @return the documentRequiredOptional
	 */
	public Boolean getDocumentRequiredOptional() {
		return documentRequiredOptional;
	}

	/**
	 * @param documentRequiredOptional the documentRequiredOptional to set
	 */
	public void setDocumentRequiredOptional(Boolean documentRequiredOptional) {
		this.documentRequiredOptional = documentRequiredOptional;
	}

	/**
	 * @return the meetingRequired
	 */
	public Boolean getMeetingRequired() {
		return meetingRequired;
	}

	/**
	 * @param meetingRequired the meetingRequired to set
	 */
	public void setMeetingRequired(Boolean meetingRequired) {
		this.meetingRequired = meetingRequired;
	}

	/**
	 * @return the meetingRequiredVisible
	 */
	public Boolean getMeetingRequiredVisible() {
		return meetingRequiredVisible;
	}

	/**
	 * @param meetingRequiredVisible the meetingRequiredVisible to set
	 */
	public void setMeetingRequiredVisible(Boolean meetingRequiredVisible) {
		this.meetingRequiredVisible = meetingRequiredVisible;
	}

	/**
	 * @return the meetingRequiredDisabled
	 */
	public Boolean getMeetingRequiredDisabled() {
		return meetingRequiredDisabled;
	}

	/**
	 * @param meetingRequiredDisabled the meetingRequiredDisabled to set
	 */
	public void setMeetingRequiredDisabled(Boolean meetingRequiredDisabled) {
		this.meetingRequiredDisabled = meetingRequiredDisabled;
	}

	/**
	 * @return the meetingRequiredOptional
	 */
	public Boolean getMeetingRequiredOptional() {
		return meetingRequiredOptional;
	}

	/**
	 * @param meetingRequiredOptional the meetingRequiredOptional to set
	 */
	public void setMeetingRequiredOptional(Boolean meetingRequiredOptional) {
		this.meetingRequiredOptional = meetingRequiredOptional;
	}

	/**
	 * @return the questionnaireRequired
	 */
	public Boolean getQuestionnaireRequired() {
		return questionnaireRequired;
	}

	/**
	 * @param questionnaireRequired the questionnaireRequired to set
	 */
	public void setQuestionnaireRequired(Boolean questionnaireRequired) {
		this.questionnaireRequired = questionnaireRequired;
	}

	/**
	 * @return the questionnaireRequiredVisible
	 */
	public Boolean getQuestionnaireRequiredVisible() {
		return questionnaireRequiredVisible;
	}

	/**
	 * @param questionnaireRequiredVisible the questionnaireRequiredVisible to set
	 */
	public void setQuestionnaireRequiredVisible(Boolean questionnaireRequiredVisible) {
		this.questionnaireRequiredVisible = questionnaireRequiredVisible;
	}

	/**
	 * @return the questionnaireRequiredDisabled
	 */
	public Boolean getQuestionnaireRequiredDisabled() {
		return questionnaireRequiredDisabled;
	}

	/**
	 * @param questionnaireRequiredDisabled the questionnaireRequiredDisabled to set
	 */
	public void setQuestionnaireRequiredDisabled(Boolean questionnaireRequiredDisabled) {
		this.questionnaireRequiredDisabled = questionnaireRequiredDisabled;
	}

	/**
	 * @return the questionnaireRequiredOptional
	 */
	public Boolean getQuestionnaireRequiredOptional() {
		return questionnaireRequiredOptional;
	}

	/**
	 * @param questionnaireRequiredOptional the questionnaireRequiredOptional to set
	 */
	public void setQuestionnaireRequiredOptional(Boolean questionnaireRequiredOptional) {
		this.questionnaireRequiredOptional = questionnaireRequiredOptional;
	}

	/**
	 * @return the billOfQuantitiesRequired
	 */
	public Boolean getBillOfQuantitiesRequired() {
		return billOfQuantitiesRequired;
	}

	/**
	 * @param billOfQuantitiesRequired the billOfQuantitiesRequired to set
	 */
	public void setBillOfQuantitiesRequired(Boolean billOfQuantitiesRequired) {
		this.billOfQuantitiesRequired = billOfQuantitiesRequired;
	}

	/**
	 * @return the billOfQuantitiesRequiredVisible
	 */
	public Boolean getBillOfQuantitiesRequiredVisible() {
		return billOfQuantitiesRequiredVisible;
	}

	/**
	 * @param billOfQuantitiesRequiredVisible the billOfQuantitiesRequiredVisible to set
	 */
	public void setBillOfQuantitiesRequiredVisible(Boolean billOfQuantitiesRequiredVisible) {
		this.billOfQuantitiesRequiredVisible = billOfQuantitiesRequiredVisible;
	}

	/**
	 * @return the billOfQuantitiesRequiredDisabled
	 */
	public Boolean getBillOfQuantitiesRequiredDisabled() {
		return billOfQuantitiesRequiredDisabled;
	}

	/**
	 * @param billOfQuantitiesRequiredDisabled the billOfQuantitiesRequiredDisabled to set
	 */
	public void setBillOfQuantitiesRequiredDisabled(Boolean billOfQuantitiesRequiredDisabled) {
		this.billOfQuantitiesRequiredDisabled = billOfQuantitiesRequiredDisabled;
	}

	/**
	 * @return the billOfQuantitiesRequiredOptional
	 */
	public Boolean getBillOfQuantitiesRequiredOptional() {
		return billOfQuantitiesRequiredOptional;
	}

	/**
	 * @param billOfQuantitiesRequiredOptional the billOfQuantitiesRequiredOptional to set
	 */
	public void setBillOfQuantitiesRequiredOptional(Boolean billOfQuantitiesRequiredOptional) {
		this.billOfQuantitiesRequiredOptional = billOfQuantitiesRequiredOptional;
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
	 * @return the preBidByDisabled
	 */
	public Boolean getPreBidByDisabled() {
		return preBidByDisabled;
	}

	/**
	 * @param preBidByDisabled the preBidByDisabled to set
	 */
	public void setPreBidByDisabled(Boolean preBidByDisabled) {
		this.preBidByDisabled = preBidByDisabled;
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
	 * @return the isPreBidSameBidPriceVisible
	 */
	public Boolean getIsPreBidSameBidPriceVisible() {
		return isPreBidSameBidPriceVisible;
	}

	/**
	 * @param isPreBidSameBidPriceVisible the isPreBidSameBidPriceVisible to set
	 */
	public void setIsPreBidSameBidPriceVisible(Boolean isPreBidSameBidPriceVisible) {
		this.isPreBidSameBidPriceVisible = isPreBidSameBidPriceVisible;
	}

	/**
	 * @return the isPreBidSameBidPriceDisabled
	 */
	public Boolean getIsPreBidSameBidPriceDisabled() {
		return isPreBidSameBidPriceDisabled;
	}

	/**
	 * @param isPreBidSameBidPriceDisabled the isPreBidSameBidPriceDisabled to set
	 */
	public void setIsPreBidSameBidPriceDisabled(Boolean isPreBidSameBidPriceDisabled) {
		this.isPreBidSameBidPriceDisabled = isPreBidSameBidPriceDisabled;
	}

	/**
	 * @return the isPreBidSameBidPriceOptional
	 */
	public Boolean getIsPreBidSameBidPriceOptional() {
		return isPreBidSameBidPriceOptional;
	}

	/**
	 * @param isPreBidSameBidPriceOptional the isPreBidSameBidPriceOptional to set
	 */
	public void setIsPreBidSameBidPriceOptional(Boolean isPreBidSameBidPriceOptional) {
		this.isPreBidSameBidPriceOptional = isPreBidSameBidPriceOptional;
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
	 * @return the isPreBidHigherPriceVisible
	 */
	public Boolean getIsPreBidHigherPriceVisible() {
		return isPreBidHigherPriceVisible;
	}

	/**
	 * @param isPreBidHigherPriceVisible the isPreBidHigherPriceVisible to set
	 */
	public void setIsPreBidHigherPriceVisible(Boolean isPreBidHigherPriceVisible) {
		this.isPreBidHigherPriceVisible = isPreBidHigherPriceVisible;
	}

	/**
	 * @return the isPreBidHigherPriceDisabled
	 */
	public Boolean getIsPreBidHigherPriceDisabled() {
		return isPreBidHigherPriceDisabled;
	}

	/**
	 * @param isPreBidHigherPriceDisabled the isPreBidHigherPriceDisabled to set
	 */
	public void setIsPreBidHigherPriceDisabled(Boolean isPreBidHigherPriceDisabled) {
		this.isPreBidHigherPriceDisabled = isPreBidHigherPriceDisabled;
	}

	/**
	 * @return the isPreBidHigherPriceOptional
	 */
	public Boolean getIsPreBidHigherPriceOptional() {
		return isPreBidHigherPriceOptional;
	}

	/**
	 * @param isPreBidHigherPriceOptional the isPreBidHigherPriceOptional to set
	 */
	public void setIsPreBidHigherPriceOptional(Boolean isPreBidHigherPriceOptional) {
		this.isPreBidHigherPriceOptional = isPreBidHigherPriceOptional;
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
	 * @return the biddingTypeDisabled
	 */
	public Boolean getBiddingTypeDisabled() {
		return biddingTypeDisabled;
	}

	/**
	 * @param biddingTypeDisabled the biddingTypeDisabled to set
	 */
	public void setBiddingTypeDisabled(Boolean biddingTypeDisabled) {
		this.biddingTypeDisabled = biddingTypeDisabled;
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
	 * @return the isBiddingMinValueFromPreviousVisible
	 */
	public Boolean getIsBiddingMinValueFromPreviousVisible() {
		return isBiddingMinValueFromPreviousVisible;
	}

	/**
	 * @param isBiddingMinValueFromPreviousVisible the isBiddingMinValueFromPreviousVisible to set
	 */
	public void setIsBiddingMinValueFromPreviousVisible(Boolean isBiddingMinValueFromPreviousVisible) {
		this.isBiddingMinValueFromPreviousVisible = isBiddingMinValueFromPreviousVisible;
	}

	/**
	 * @return the isBiddingMinValueFromPreviousDisabled
	 */
	public Boolean getIsBiddingMinValueFromPreviousDisabled() {
		return isBiddingMinValueFromPreviousDisabled;
	}

	/**
	 * @param isBiddingMinValueFromPreviousDisabled the isBiddingMinValueFromPreviousDisabled to set
	 */
	public void setIsBiddingMinValueFromPreviousDisabled(Boolean isBiddingMinValueFromPreviousDisabled) {
		this.isBiddingMinValueFromPreviousDisabled = isBiddingMinValueFromPreviousDisabled;
	}

	/**
	 * @return the isBiddingMinValueFromPreviousOptional
	 */
	public Boolean getIsBiddingMinValueFromPreviousOptional() {
		return isBiddingMinValueFromPreviousOptional;
	}

	/**
	 * @param isBiddingMinValueFromPreviousOptional the isBiddingMinValueFromPreviousOptional to set
	 */
	public void setIsBiddingMinValueFromPreviousOptional(Boolean isBiddingMinValueFromPreviousOptional) {
		this.isBiddingMinValueFromPreviousOptional = isBiddingMinValueFromPreviousOptional;
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
	 * @return the isStartGateVisible
	 */
	public Boolean getIsStartGateVisible() {
		return isStartGateVisible;
	}

	/**
	 * @param isStartGateVisible the isStartGateVisible to set
	 */
	public void setIsStartGateVisible(Boolean isStartGateVisible) {
		this.isStartGateVisible = isStartGateVisible;
	}

	/**
	 * @return the isStartGateDisabled
	 */
	public Boolean getIsStartGateDisabled() {
		return isStartGateDisabled;
	}

	/**
	 * @param isStartGateDisabled the isStartGateDisabled to set
	 */
	public void setIsStartGateDisabled(Boolean isStartGateDisabled) {
		this.isStartGateDisabled = isStartGateDisabled;
	}

	/**
	 * @return the isStartGateOptional
	 */
	public Boolean getIsStartGateOptional() {
		return isStartGateOptional;
	}

	/**
	 * @param isStartGateOptional the isStartGateOptional to set
	 */
	public void setIsStartGateOptional(Boolean isStartGateOptional) {
		this.isStartGateOptional = isStartGateOptional;
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
	 * @return the isBiddingPriceHigherLeadingBidVisible
	 */
	public Boolean getIsBiddingPriceHigherLeadingBidVisible() {
		return isBiddingPriceHigherLeadingBidVisible;
	}

	/**
	 * @param isBiddingPriceHigherLeadingBidVisible the isBiddingPriceHigherLeadingBidVisible to set
	 */
	public void setIsBiddingPriceHigherLeadingBidVisible(Boolean isBiddingPriceHigherLeadingBidVisible) {
		this.isBiddingPriceHigherLeadingBidVisible = isBiddingPriceHigherLeadingBidVisible;
	}

	/**
	 * @return the isBiddingPriceHigherLeadingBidDisabled
	 */
	public Boolean getIsBiddingPriceHigherLeadingBidDisabled() {
		return isBiddingPriceHigherLeadingBidDisabled;
	}

	/**
	 * @param isBiddingPriceHigherLeadingBidDisabled the isBiddingPriceHigherLeadingBidDisabled to set
	 */
	public void setIsBiddingPriceHigherLeadingBidDisabled(Boolean isBiddingPriceHigherLeadingBidDisabled) {
		this.isBiddingPriceHigherLeadingBidDisabled = isBiddingPriceHigherLeadingBidDisabled;
	}

	/**
	 * @return the isBiddingPriceHigherLeadingBidOptional
	 */
	public Boolean getIsBiddingPriceHigherLeadingBidOptional() {
		return isBiddingPriceHigherLeadingBidOptional;
	}

	/**
	 * @param isBiddingPriceHigherLeadingBidOptional the isBiddingPriceHigherLeadingBidOptional to set
	 */
	public void setIsBiddingPriceHigherLeadingBidOptional(Boolean isBiddingPriceHigherLeadingBidOptional) {
		this.isBiddingPriceHigherLeadingBidOptional = isBiddingPriceHigherLeadingBidOptional;
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
	 * @return the isBiddingAllowSupplierSameBidVisible
	 */
	public Boolean getIsBiddingAllowSupplierSameBidVisible() {
		return isBiddingAllowSupplierSameBidVisible;
	}

	/**
	 * @param isBiddingAllowSupplierSameBidVisible the isBiddingAllowSupplierSameBidVisible to set
	 */
	public void setIsBiddingAllowSupplierSameBidVisible(Boolean isBiddingAllowSupplierSameBidVisible) {
		this.isBiddingAllowSupplierSameBidVisible = isBiddingAllowSupplierSameBidVisible;
	}

	/**
	 * @return the isBiddingAllowSupplierSameBidDisabled
	 */
	public Boolean getIsBiddingAllowSupplierSameBidDisabled() {
		return isBiddingAllowSupplierSameBidDisabled;
	}

	/**
	 * @param isBiddingAllowSupplierSameBidDisabled the isBiddingAllowSupplierSameBidDisabled to set
	 */
	public void setIsBiddingAllowSupplierSameBidDisabled(Boolean isBiddingAllowSupplierSameBidDisabled) {
		this.isBiddingAllowSupplierSameBidDisabled = isBiddingAllowSupplierSameBidDisabled;
	}

	/**
	 * @return the isBiddingAllowSupplierSameBidOptional
	 */
	public Boolean getIsBiddingAllowSupplierSameBidOptional() {
		return isBiddingAllowSupplierSameBidOptional;
	}

	/**
	 * @param isBiddingAllowSupplierSameBidOptional the isBiddingAllowSupplierSameBidOptional to set
	 */
	public void setIsBiddingAllowSupplierSameBidOptional(Boolean isBiddingAllowSupplierSameBidOptional) {
		this.isBiddingAllowSupplierSameBidOptional = isBiddingAllowSupplierSameBidOptional;
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
	 * @return the auctionConsolePriceTypeDisabled
	 */
	public Boolean getAuctionConsolePriceTypeDisabled() {
		return auctionConsolePriceTypeDisabled;
	}

	/**
	 * @param auctionConsolePriceTypeDisabled the auctionConsolePriceTypeDisabled to set
	 */
	public void setAuctionConsolePriceTypeDisabled(Boolean auctionConsolePriceTypeDisabled) {
		this.auctionConsolePriceTypeDisabled = auctionConsolePriceTypeDisabled;
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
	 * @return the auctionConsoleVenderTypeDisabled
	 */
	public Boolean getAuctionConsoleVenderTypeDisabled() {
		return auctionConsoleVenderTypeDisabled;
	}

	/**
	 * @param auctionConsoleVenderTypeDisabled the auctionConsoleVenderTypeDisabled to set
	 */
	public void setAuctionConsoleVenderTypeDisabled(Boolean auctionConsoleVenderTypeDisabled) {
		this.auctionConsoleVenderTypeDisabled = auctionConsoleVenderTypeDisabled;
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
	 * @return the auctionConsoleRankTypeDisabled
	 */
	public Boolean getAuctionConsoleRankTypeDisabled() {
		return auctionConsoleRankTypeDisabled;
	}

	/**
	 * @param auctionConsoleRankTypeDisabled the auctionConsoleRankTypeDisabled to set
	 */
	public void setAuctionConsoleRankTypeDisabled(Boolean auctionConsoleRankTypeDisabled) {
		this.auctionConsoleRankTypeDisabled = auctionConsoleRankTypeDisabled;
	}

	/**
	 * @return the timeExtensionType
	 */
	public TimeExtensionType getTimeExtensionType() {
		return timeExtensionType;
	}

	/**
	 * @param timeExtensionType the timeExtensionType to set
	 */
	public void setTimeExtensionType(TimeExtensionType timeExtensionType) {
		this.timeExtensionType = timeExtensionType;
	}

	/**
	 * @return the timeExtensionTypeDisabled
	 */
	public Boolean getTimeExtensionTypeDisabled() {
		return timeExtensionTypeDisabled;
	}

	/**
	 * @param timeExtensionTypeDisabled the timeExtensionTypeDisabled to set
	 */
	public void setTimeExtensionTypeDisabled(Boolean timeExtensionTypeDisabled) {
		this.timeExtensionTypeDisabled = timeExtensionTypeDisabled;
	}

	/**
	 * @return the timeExtensionDurationType
	 */
	public DurationType getTimeExtensionDurationType() {
		return timeExtensionDurationType;
	}

	/**
	 * @param timeExtensionDurationType the timeExtensionDurationType to set
	 */
	public void setTimeExtensionDurationType(DurationType timeExtensionDurationType) {
		this.timeExtensionDurationType = timeExtensionDurationType;
	}

	/**
	 * @return the timeExtensionDuration
	 */
	public Integer getTimeExtensionDuration() {
		return timeExtensionDuration;
	}

	/**
	 * @param timeExtensionDuration the timeExtensionDuration to set
	 */
	public void setTimeExtensionDuration(Integer timeExtensionDuration) {
		this.timeExtensionDuration = timeExtensionDuration;
	}

	/**
	 * @return the timeExtensionLeadingBidType
	 */
	public DurationType getTimeExtensionLeadingBidType() {
		return timeExtensionLeadingBidType;
	}

	/**
	 * @param timeExtensionLeadingBidType the timeExtensionLeadingBidType to set
	 */
	public void setTimeExtensionLeadingBidType(DurationType timeExtensionLeadingBidType) {
		this.timeExtensionLeadingBidType = timeExtensionLeadingBidType;
	}

	/**
	 * @return the timeExtensionLeadingBidValue
	 */
	public Integer getTimeExtensionLeadingBidValue() {
		return timeExtensionLeadingBidValue;
	}

	/**
	 * @param timeExtensionLeadingBidValue the timeExtensionLeadingBidValue to set
	 */
	public void setTimeExtensionLeadingBidValue(Integer timeExtensionLeadingBidValue) {
		this.timeExtensionLeadingBidValue = timeExtensionLeadingBidValue;
	}

	/**
	 * @return the extensionCount
	 */
	public Integer getExtensionCount() {
		return extensionCount;
	}

	/**
	 * @param extensionCount the extensionCount to set
	 */
	public void setExtensionCount(Integer extensionCount) {
		this.extensionCount = extensionCount;
	}

	/**
	 * @return the bidderDisqualify
	 */
	public Integer getBidderDisqualify() {
		return bidderDisqualify;
	}

	/**
	 * @param bidderDisqualify the bidderDisqualify to set
	 */
	public void setBidderDisqualify(Integer bidderDisqualify) {
		this.bidderDisqualify = bidderDisqualify;
	}

	/**
	 * @return the autoDisqualifyVisible
	 */
	public Boolean getAutoDisqualifyVisible() {
		return autoDisqualifyVisible;
	}

	/**
	 * @param autoDisqualifyVisible the autoDisqualifyVisible to set
	 */
	public void setAutoDisqualifyVisible(Boolean autoDisqualifyVisible) {
		this.autoDisqualifyVisible = autoDisqualifyVisible;
	}

	/**
	 * @return the autoDisqualifyDisabled
	 */
	public Boolean getAutoDisqualifyDisabled() {
		return autoDisqualifyDisabled;
	}

	/**
	 * @param autoDisqualifyDisabled the autoDisqualifyDisabled to set
	 */
	public void setAutoDisqualifyDisabled(Boolean autoDisqualifyDisabled) {
		this.autoDisqualifyDisabled = autoDisqualifyDisabled;
	}

	/**
	 * @return the autoDisqualifyOptional
	 */
	public Boolean getAutoDisqualifyOptional() {
		return autoDisqualifyOptional;
	}

	/**
	 * @param autoDisqualifyOptional the autoDisqualifyOptional to set
	 */
	public void setAutoDisqualifyOptional(Boolean autoDisqualifyOptional) {
		this.autoDisqualifyOptional = autoDisqualifyOptional;
	}

	/**
	 * @return the autoDisqualify
	 */
	public Boolean getAutoDisqualify() {
		return autoDisqualify;
	}

	/**
	 * @param autoDisqualify the autoDisqualify to set
	 */
	public void setAutoDisqualify(Boolean autoDisqualify) {
		this.autoDisqualify = autoDisqualify;
	}

	/**
	 * @return the biddingTypeVisible
	 */
	public Boolean getBiddingTypeVisible() {
		return biddingTypeVisible;
	}

	/**
	 * @param biddingTypeVisible the biddingTypeVisible to set
	 */
	public void setBiddingTypeVisible(Boolean biddingTypeVisible) {
		this.biddingTypeVisible = biddingTypeVisible;
	}

	/**
	 * @return the biddingTypeOptional
	 */
	public Boolean getBiddingTypeOptional() {
		return biddingTypeOptional;
	}

	/**
	 * @param biddingTypeOptional the biddingTypeOptional to set
	 */
	public void setBiddingTypeOptional(Boolean biddingTypeOptional) {
		this.biddingTypeOptional = biddingTypeOptional;
	}

	/**
	 * @return the auctionConsolePriceTypeVisible
	 */
	public Boolean getAuctionConsolePriceTypeVisible() {
		return auctionConsolePriceTypeVisible;
	}

	/**
	 * @param auctionConsolePriceTypeVisible the auctionConsolePriceTypeVisible to set
	 */
	public void setAuctionConsolePriceTypeVisible(Boolean auctionConsolePriceTypeVisible) {
		this.auctionConsolePriceTypeVisible = auctionConsolePriceTypeVisible;
	}

	/**
	 * @return the auctionConsolePriceTypeOptional
	 */
	public Boolean getAuctionConsolePriceTypeOptional() {
		return auctionConsolePriceTypeOptional;
	}

	/**
	 * @param auctionConsolePriceTypeOptional the auctionConsolePriceTypeOptional to set
	 */
	public void setAuctionConsolePriceTypeOptional(Boolean auctionConsolePriceTypeOptional) {
		this.auctionConsolePriceTypeOptional = auctionConsolePriceTypeOptional;
	}

	/**
	 * @return the auctionConsoleVenderTypeVisible
	 */
	public Boolean getAuctionConsoleVenderTypeVisible() {
		return auctionConsoleVenderTypeVisible;
	}

	/**
	 * @param auctionConsoleVenderTypeVisible the auctionConsoleVenderTypeVisible to set
	 */
	public void setAuctionConsoleVenderTypeVisible(Boolean auctionConsoleVenderTypeVisible) {
		this.auctionConsoleVenderTypeVisible = auctionConsoleVenderTypeVisible;
	}

	/**
	 * @return the auctionConsoleVenderTypeOptional
	 */
	public Boolean getAuctionConsoleVenderTypeOptional() {
		return auctionConsoleVenderTypeOptional;
	}

	/**
	 * @param auctionConsoleVenderTypeOptional the auctionConsoleVenderTypeOptional to set
	 */
	public void setAuctionConsoleVenderTypeOptional(Boolean auctionConsoleVenderTypeOptional) {
		this.auctionConsoleVenderTypeOptional = auctionConsoleVenderTypeOptional;
	}

	/**
	 * @return the auctionConsoleRankTypeVisible
	 */
	public Boolean getAuctionConsoleRankTypeVisible() {
		return auctionConsoleRankTypeVisible;
	}

	/**
	 * @param auctionConsoleRankTypeVisible the auctionConsoleRankTypeVisible to set
	 */
	public void setAuctionConsoleRankTypeVisible(Boolean auctionConsoleRankTypeVisible) {
		this.auctionConsoleRankTypeVisible = auctionConsoleRankTypeVisible;
	}

	/**
	 * @return the auctionConsoleRankTypeOptional
	 */
	public Boolean getAuctionConsoleRankTypeOptional() {
		return auctionConsoleRankTypeOptional;
	}

	/**
	 * @param auctionConsoleRankTypeOptional the auctionConsoleRankTypeOptional to set
	 */
	public void setAuctionConsoleRankTypeOptional(Boolean auctionConsoleRankTypeOptional) {
		this.auctionConsoleRankTypeOptional = auctionConsoleRankTypeOptional;
	}

	/**
	 * @return the timeExtensionTypeVisible
	 */
	public Boolean getTimeExtensionTypeVisible() {
		return timeExtensionTypeVisible;
	}

	/**
	 * @param timeExtensionTypeVisible the timeExtensionTypeVisible to set
	 */
	public void setTimeExtensionTypeVisible(Boolean timeExtensionTypeVisible) {
		this.timeExtensionTypeVisible = timeExtensionTypeVisible;
	}

	/**
	 * @return the timeExtensionTypeOptional
	 */
	public Boolean getTimeExtensionTypeOptional() {
		return timeExtensionTypeOptional;
	}

	/**
	 * @param timeExtensionTypeOptional the timeExtensionTypeOptional to set
	 */
	public void setTimeExtensionTypeOptional(Boolean timeExtensionTypeOptional) {
		this.timeExtensionTypeOptional = timeExtensionTypeOptional;
	}

	/**
	 * @return the preBidByVisible
	 */
	public Boolean getPreBidByVisible() {
		return preBidByVisible;
	}

	/**
	 * @param preBidByVisible the preBidByVisible to set
	 */
	public void setPreBidByVisible(Boolean preBidByVisible) {
		this.preBidByVisible = preBidByVisible;
	}

	/**
	 * @return the preBidByOptional
	 */
	public Boolean getPreBidByOptional() {
		return preBidByOptional;
	}

	/**
	 * @param preBidByOptional the preBidByOptional to set
	 */
	public void setPreBidByOptional(Boolean preBidByOptional) {
		this.preBidByOptional = preBidByOptional;
	}

	/**
	 * @return the extraForDisAbleField
	 */
	public Boolean getExtraForDisAbleField() {
		return extraForDisAbleField;
	}

	/**
	 * @param extraForDisAbleField the extraForDisAbleField to set
	 */
	public void setExtraForDisAbleField(Boolean extraForDisAbleField) {
		this.extraForDisAbleField = extraForDisAbleField;
	}

	/**
	 * @return the businessUnit
	 */
	public BusinessUnit getBusinessUnit() {
		return businessUnit;
	}

	/**
	 * @param businessUnit the businessUnit to set
	 */
	public void setBusinessUnit(BusinessUnit businessUnit) {
		this.businessUnit = businessUnit;
	}

	/**
	 * @return the businessUnitDisabled
	 */
	public Boolean getBusinessUnitDisabled() {
		return businessUnitDisabled;
	}

	/**
	 * @param businessUnitDisabled the businessUnitDisabled to set
	 */
	public void setBusinessUnitDisabled(Boolean businessUnitDisabled) {
		this.businessUnitDisabled = businessUnitDisabled;
	}

	/**
	 * @return the industryCatArr
	 */
	public String getIndustryCatArr() {
		return industryCatArr;
	}

	/**
	 * @param industryCatArr the industryCatArr to set
	 */
	public void setIndustryCatArr(String industryCatArr) {
		this.industryCatArr = industryCatArr;
	}

	/**
	 * @return the depositCurrency
	 */
	public Currency getDepositCurrency() {
		return depositCurrency;
	}

	/**
	 * @param depositCurrency the depositCurrency to set
	 */
	public void setDepositCurrency(Currency depositCurrency) {
		this.depositCurrency = depositCurrency;
	}

	/**
	 * @return the deposit
	 */
	public BigDecimal getDeposit() {
		return deposit;
	}

	/**
	 * @param deposit the deposit to set
	 */
	public void setDeposit(BigDecimal deposit) {
		this.deposit = deposit;
	}

	/**
	 * @return the addDepositVisible
	 */
	public Boolean getAddDepositVisible() {
		return addDepositVisible;
	}

	/**
	 * @param addDepositVisible the addDepositVisible to set
	 */
	public void setAddDepositVisible(Boolean addDepositVisible) {
		this.addDepositVisible = addDepositVisible;
	}

	/**
	 * @return the addDepositDisabled
	 */
	public Boolean getAddDepositDisabled() {
		return addDepositDisabled;
	}

	/**
	 * @param addDepositDisabled the addDepositDisabled to set
	 */
	public void setAddDepositDisabled(Boolean addDepositDisabled) {
		this.addDepositDisabled = addDepositDisabled;
	}

	/**
	 * @return the addDepositOptional
	 */
	public Boolean getAddDepositOptional() {
		return addDepositOptional;
	}

	/**
	 * @param addDepositOptional the addDepositOptional to set
	 */
	public void setAddDepositOptional(Boolean addDepositOptional) {
		this.addDepositOptional = addDepositOptional;
	}

	public AuctionConsolePriceVenderType getBuyerAuctionConsolePriceType() {
		return buyerAuctionConsolePriceType;
	}

	public void setBuyerAuctionConsolePriceType(AuctionConsolePriceVenderType buyerAuctionConsolePriceType) {
		this.buyerAuctionConsolePriceType = buyerAuctionConsolePriceType;
	}

	public Boolean getBuyerAuctionConsolePriceTypeDisabled() {
		return buyerAuctionConsolePriceTypeDisabled;
	}

	public void setBuyerAuctionConsolePriceTypeDisabled(Boolean buyerAuctionConsolePriceTypeDisabled) {
		this.buyerAuctionConsolePriceTypeDisabled = buyerAuctionConsolePriceTypeDisabled;
	}

	public Boolean getBuyerAuctionConsolePriceTypeVisible() {
		return buyerAuctionConsolePriceTypeVisible;
	}

	public void setBuyerAuctionConsolePriceTypeVisible(Boolean buyerAuctionConsolePriceTypeVisible) {
		this.buyerAuctionConsolePriceTypeVisible = buyerAuctionConsolePriceTypeVisible;
	}

	public Boolean getBuyerAuctionConsolePriceTypeOptional() {
		return buyerAuctionConsolePriceTypeOptional;
	}

	public void setBuyerAuctionConsolePriceTypeOptional(Boolean buyerAuctionConsolePriceTypeOptional) {
		this.buyerAuctionConsolePriceTypeOptional = buyerAuctionConsolePriceTypeOptional;
	}

	public AuctionConsolePriceVenderType getBuyerAuctionConsoleVenderType() {
		return buyerAuctionConsoleVenderType;
	}

	public void setBuyerAuctionConsoleVenderType(AuctionConsolePriceVenderType buyerAuctionConsoleVenderType) {
		this.buyerAuctionConsoleVenderType = buyerAuctionConsoleVenderType;
	}

	public Boolean getBuyerAuctionConsoleVenderTypeDisabled() {
		return buyerAuctionConsoleVenderTypeDisabled;
	}

	public void setBuyerAuctionConsoleVenderTypeDisabled(Boolean buyerAuctionConsoleVenderTypeDisabled) {
		this.buyerAuctionConsoleVenderTypeDisabled = buyerAuctionConsoleVenderTypeDisabled;
	}

	public Boolean getBuyerAuctionConsoleVenderTypeVisible() {
		return buyerAuctionConsoleVenderTypeVisible;
	}

	public void setBuyerAuctionConsoleVenderTypeVisible(Boolean buyerAuctionConsoleVenderTypeVisible) {
		this.buyerAuctionConsoleVenderTypeVisible = buyerAuctionConsoleVenderTypeVisible;
	}

	public Boolean getBuyerAuctionConsoleVenderTypeOptional() {
		return buyerAuctionConsoleVenderTypeOptional;
	}

	public void setBuyerAuctionConsoleVenderTypeOptional(Boolean buyerAuctionConsoleVenderTypeOptional) {
		this.buyerAuctionConsoleVenderTypeOptional = buyerAuctionConsoleVenderTypeOptional;
	}

	public AuctionConsolePriceVenderType getBuyerAuctionConsoleRankType() {
		return buyerAuctionConsoleRankType;
	}

	public void setBuyerAuctionConsoleRankType(AuctionConsolePriceVenderType buyerAuctionConsoleRankType) {
		this.buyerAuctionConsoleRankType = buyerAuctionConsoleRankType;
	}

	public Boolean getBuyerAuctionConsoleRankTypeDisabled() {
		return buyerAuctionConsoleRankTypeDisabled;
	}

	public void setBuyerAuctionConsoleRankTypeDisabled(Boolean buyerAuctionConsoleRankTypeDisabled) {
		this.buyerAuctionConsoleRankTypeDisabled = buyerAuctionConsoleRankTypeDisabled;
	}

	public Boolean getBuyerAuctionConsoleRankTypeVisible() {
		return buyerAuctionConsoleRankTypeVisible;
	}

	public void setBuyerAuctionConsoleRankTypeVisible(Boolean buyerAuctionConsoleRankTypeVisible) {
		this.buyerAuctionConsoleRankTypeVisible = buyerAuctionConsoleRankTypeVisible;
	}

	public Boolean getBuyerAuctionConsoleRankTypeOptional() {
		return buyerAuctionConsoleRankTypeOptional;
	}

	public void setBuyerAuctionConsoleRankTypeOptional(Boolean buyerAuctionConsoleRankTypeOptional) {
		this.buyerAuctionConsoleRankTypeOptional = buyerAuctionConsoleRankTypeOptional;
	}

	/**
	 * @return the evaluationProcessDeclaration
	 */
	public Declaration getEvaluationProcessDeclaration() {
		return evaluationProcessDeclaration;
	}

	/**
	 * @param evaluationProcessDeclaration the evaluationProcessDeclaration to set
	 */
	public void setEvaluationProcessDeclaration(Declaration evaluationProcessDeclaration) {
		this.evaluationProcessDeclaration = evaluationProcessDeclaration;
	}

	/**
	 * @return the evaluationDeclarationVisible
	 */
	public Boolean getEvaluationDeclarationVisible() {
		return evaluationDeclarationVisible;
	}

	/**
	 * @param evaluationDeclarationVisible the evaluationDeclarationVisible to set
	 */
	public void setEvaluationDeclarationVisible(Boolean evaluationDeclarationVisible) {
		this.evaluationDeclarationVisible = evaluationDeclarationVisible;
	}

	/**
	 * @return the evaluationDeclarationDisabled
	 */
	public Boolean getEvaluationDeclarationDisabled() {
		return evaluationDeclarationDisabled;
	}

	/**
	 * @param evaluationDeclarationDisabled the evaluationDeclarationDisabled to set
	 */
	public void setEvaluationDeclarationDisabled(Boolean evaluationDeclarationDisabled) {
		this.evaluationDeclarationDisabled = evaluationDeclarationDisabled;
	}

	/**
	 * @return the supplierAcceptanceDeclaration
	 */
	public Declaration getSupplierAcceptanceDeclaration() {
		return supplierAcceptanceDeclaration;
	}

	/**
	 * @param supplierAcceptanceDeclaration the supplierAcceptanceDeclaration to set
	 */
	public void setSupplierAcceptanceDeclaration(Declaration supplierAcceptanceDeclaration) {
		this.supplierAcceptanceDeclaration = supplierAcceptanceDeclaration;
	}

	/**
	 * @return the supplierDeclarationVisible
	 */
	public Boolean getSupplierDeclarationVisible() {
		return supplierDeclarationVisible;
	}

	/**
	 * @param supplierDeclarationVisible the supplierDeclarationVisible to set
	 */
	public void setSupplierDeclarationVisible(Boolean supplierDeclarationVisible) {
		this.supplierDeclarationVisible = supplierDeclarationVisible;
	}

	/**
	 * @return the supplierDeclarationDisabled
	 */
	public Boolean getSupplierDeclarationDisabled() {
		return supplierDeclarationDisabled;
	}

	/**
	 * @param supplierDeclarationDisabled the supplierDeclarationDisabled to set
	 */
	public void setSupplierDeclarationDisabled(Boolean supplierDeclarationDisabled) {
		this.supplierDeclarationDisabled = supplierDeclarationDisabled;
	}

	public Boolean getPrebidAsFirstBid() {
		return prebidAsFirstBid;
	}

	public void setPrebidAsFirstBid(Boolean prebidAsFirstBid) {
		this.prebidAsFirstBid = prebidAsFirstBid;
	}

	public Boolean getPrebidAsFirstBidVisible() {
		return prebidAsFirstBidVisible;
	}

	public void setPrebidAsFirstBidVisible(Boolean prebidAsFirstBidVisible) {
		this.prebidAsFirstBidVisible = prebidAsFirstBidVisible;
	}

	public Boolean getPrebidAsFirstBidDisabled() {
		return prebidAsFirstBidDisabled;
	}

	public void setPrebidAsFirstBidDisabled(Boolean prebidAsFirstBidDisabled) {
		this.prebidAsFirstBidDisabled = prebidAsFirstBidDisabled;
	}

	public Boolean getPrebidAsFirstBidOptional() {
		return prebidAsFirstBidOptional;
	}

	public void setPrebidAsFirstBidOptional(Boolean prebidAsFirstBidOptional) {
		this.prebidAsFirstBidOptional = prebidAsFirstBidOptional;
	}

	/**
	 * @return the disableTotalAmount
	 */
	public Boolean getDisableTotalAmount() {
		return disableTotalAmount;
	}

	/**
	 * @param disableTotalAmount the disableTotalAmount to set
	 */
	public void setDisableTotalAmount(Boolean disableTotalAmount) {
		this.disableTotalAmount = disableTotalAmount;
	}

	public BigDecimal getEstimatedBudget() {
		return estimatedBudget;
	}

	public void setEstimatedBudget(BigDecimal estimatedBudget) {
		this.estimatedBudget = estimatedBudget;
	}

	public Boolean getEstimatedBudgetVisible() {
		return estimatedBudgetVisible;
	}

	public void setEstimatedBudgetVisible(Boolean estimatedBudgetVisible) {
		this.estimatedBudgetVisible = estimatedBudgetVisible;
	}

	public Boolean getEstimatedBudgetDisabled() {
		return estimatedBudgetDisabled;
	}

	public void setEstimatedBudgetDisabled(Boolean estimatedBudgetDisabled) {
		this.estimatedBudgetDisabled = estimatedBudgetDisabled;
	}

	public Boolean getEstimatedBudgetOptional() {
		return estimatedBudgetOptional;
	}

	public void setEstimatedBudgetOptional(Boolean estimatedBudgetOptional) {
		this.estimatedBudgetOptional = estimatedBudgetOptional;
	}

	public ProcurementMethod getProcurementMethod() {
		return procurementMethod;
	}

	public void setProcurementMethod(ProcurementMethod procurementMethod) {
		this.procurementMethod = procurementMethod;
	}

	public Boolean getProcurementMethodVisible() {
		return procurementMethodVisible;
	}

	public void setProcurementMethodVisible(Boolean procurementMethodVisible) {
		this.procurementMethodVisible = procurementMethodVisible;
	}

	public Boolean getProcurementMethodDisabled() {
		return procurementMethodDisabled;
	}

	public void setProcurementMethodDisabled(Boolean procurementMethodDisabled) {
		this.procurementMethodDisabled = procurementMethodDisabled;
	}

	public Boolean getProcurementMethodOptional() {
		return procurementMethodOptional;
	}

	public void setProcurementMethodOptional(Boolean procurementMethodOptional) {
		this.procurementMethodOptional = procurementMethodOptional;
	}

	public ProcurementCategories getProcurementCategory() {
		return procurementCategory;
	}

	public void setProcurementCategory(ProcurementCategories procurementCategory) {
		this.procurementCategory = procurementCategory;
	}

	public Boolean getProcurementCategoryVisible() {
		return procurementCategoryVisible;
	}

	public void setProcurementCategoryVisible(Boolean procurementCategoryVisible) {
		this.procurementCategoryVisible = procurementCategoryVisible;
	}

	public Boolean getProcurementCategoryDisabled() {
		return procurementCategoryDisabled;
	}

	public void setProcurementCategoryDisabled(Boolean procurementCategoryDisabled) {
		this.procurementCategoryDisabled = procurementCategoryDisabled;
	}

	public Boolean getProcurementCategoryOptional() {
		return procurementCategoryOptional;
	}

	public void setProcurementCategoryOptional(Boolean procurementCategoryOptional) {
		this.procurementCategoryOptional = procurementCategoryOptional;
	}

	/**
	 * @return the groupCode
	 */
	public GroupCode getGroupCode() {
		return groupCode;
	}

	/**
	 * @param groupCode the groupCode to set
	 */
	public void setGroupCode(GroupCode groupCode) {
		this.groupCode = groupCode;
	}

	/**
	 * @return the groupCodeVisible
	 */
	public Boolean getGroupCodeVisible() {
		return groupCodeVisible;
	}

	/**
	 * @param groupCodeVisible the groupCodeVisible to set
	 */
	public void setGroupCodeVisible(Boolean groupCodeVisible) {
		this.groupCodeVisible = groupCodeVisible;
	}

	/**
	 * @return the groupCodeDisabled
	 */
	public Boolean getGroupCodeDisabled() {
		return groupCodeDisabled;
	}

	/**
	 * @param groupCodeDisabled the groupCodeDisabled to set
	 */
	public void setGroupCodeDisabled(Boolean groupCodeDisabled) {
		this.groupCodeDisabled = groupCodeDisabled;
	}

	/**
	 * @return the groupCodeOptional
	 */
	public Boolean getGroupCodeOptional() {
		return groupCodeOptional;
	}

	/**
	 * @param groupCodeOptional the groupCodeOptional to set
	 */
	public void setGroupCodeOptional(Boolean groupCodeOptional) {
		this.groupCodeOptional = groupCodeOptional;
	}

	/**
	 * @return the isPreSetSamePreBidForAllSuppliers
	 */
	public Boolean getIsPreSetSamePreBidForAllSuppliers() {
		return isPreSetSamePreBidForAllSuppliers;
	}

	/**
	 * @param isPreSetSamePreBidForAllSuppliers the isPreSetSamePreBidForAllSuppliers to set
	 */
	public void setIsPreSetSamePreBidForAllSuppliers(Boolean isPreSetSamePreBidForAllSuppliers) {
		this.isPreSetSamePreBidForAllSuppliers = isPreSetSamePreBidForAllSuppliers;
	}

	/**
	 * @return the isPreSetSamePreBidForAllSuppliersVisible
	 */
	public Boolean getIsPreSetSamePreBidForAllSuppliersVisible() {
		return isPreSetSamePreBidForAllSuppliersVisible;
	}

	/**
	 * @param isPreSetSamePreBidForAllSuppliersVisible the isPreSetSamePreBidForAllSuppliersVisible to set
	 */
	public void setIsPreSetSamePreBidForAllSuppliersVisible(Boolean isPreSetSamePreBidForAllSuppliersVisible) {
		this.isPreSetSamePreBidForAllSuppliersVisible = isPreSetSamePreBidForAllSuppliersVisible;
	}

	/**
	 * @return the isPreSetSamePreBidForAllSuppliersDisabled
	 */
	public Boolean getIsPreSetSamePreBidForAllSuppliersDisabled() {
		return isPreSetSamePreBidForAllSuppliersDisabled;
	}

	/**
	 * @param isPreSetSamePreBidForAllSuppliersDisabled the isPreSetSamePreBidForAllSuppliersDisabled to set
	 */
	public void setIsPreSetSamePreBidForAllSuppliersDisabled(Boolean isPreSetSamePreBidForAllSuppliersDisabled) {
		this.isPreSetSamePreBidForAllSuppliersDisabled = isPreSetSamePreBidForAllSuppliersDisabled;
	}

	/**
	 * @return the isPreSetSamePreBidForAllSuppliersOptional
	 */
	public Boolean getIsPreSetSamePreBidForAllSuppliersOptional() {
		return isPreSetSamePreBidForAllSuppliersOptional;
	}

	/**
	 * @param isPreSetSamePreBidForAllSuppliersOptional the isPreSetSamePreBidForAllSuppliersOptional to set
	 */
	public void setIsPreSetSamePreBidForAllSuppliersOptional(Boolean isPreSetSamePreBidForAllSuppliersOptional) {
		this.isPreSetSamePreBidForAllSuppliersOptional = isPreSetSamePreBidForAllSuppliersOptional;
	}

	public Boolean getTemplateUsed() {
		return isTemplateUsed;
	}

	public void setTemplateUsed(Boolean templateUsed) {
		isTemplateUsed = templateUsed;
	}
}
