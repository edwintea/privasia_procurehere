package com.privasia.procurehere.core.dao;

import java.math.BigDecimal;

import com.privasia.procurehere.core.entity.AuctionRules;

public interface AuctionRulesDao extends GenericDao<AuctionRules, String> {

	AuctionRules findAuctionRulesByEventId(String eventId);

	AuctionRules findAuctionRulesWithEventById(String auctionId);

	BigDecimal findAuctionRulesCurrentPriceForStepNo(Integer currentStep, String auctionId);

	AuctionRules getAuctionRulesForAuctionConsole(String eventId);

	AuctionRules getAuctionRulesForBidSumission(String eventId);

	AuctionRules findLeanAuctionRulesByEventId(String eventId);

	AuctionRules findPlainAuctionRulesByEventId(String id);

	Boolean findAuctionRulesWithEventId(String eventId);

	Boolean findPreSetPredBidAuctionRulesWithEventId(String eventId);

}
