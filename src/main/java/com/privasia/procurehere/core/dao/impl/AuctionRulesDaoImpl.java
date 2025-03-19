package com.privasia.procurehere.core.dao.impl;

import java.math.BigDecimal;

import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import com.privasia.procurehere.core.dao.AuctionRulesDao;
import com.privasia.procurehere.core.entity.AuctionRules;
import com.privasia.procurehere.core.utils.Global;

@Repository
public class AuctionRulesDaoImpl extends GenericDaoImpl<AuctionRules, String> implements AuctionRulesDao {
	private static final Logger LOG = LogManager.getLogger(Global.AUCTION_LOG);

	@Override
	public AuctionRules findAuctionRulesByEventId(String eventId) {
		final Query query = getEntityManager().createQuery("from AuctionRules ar inner join fetch ar.event e where ar.event.id =:eventId");
		query.setParameter("eventId", eventId);
		return (AuctionRules) query.getSingleResult();
	}

	@Override
	public AuctionRules findAuctionRulesWithEventById(String auctionId) {
		final Query query = getEntityManager().createQuery("from AuctionRules ar inner join fetch ar.event e where ar.id =:auctionId");
		query.setParameter("auctionId", auctionId);
		return (AuctionRules) query.getSingleResult();

	}

	@Override
	public BigDecimal findAuctionRulesCurrentPriceForStepNo(Integer currentStep, String auctionId) {
		final Query query = getEntityManager().createQuery("select ar.dutchAuctionCurrentStepAmount from AuctionRules ar where ar.dutchAuctionCurrentStep =:currentStep and ar.id=:auctionId");
		query.setParameter("currentStep", currentStep);
		query.setParameter("auctionId", auctionId);
		return (BigDecimal) query.getSingleResult();

	}

	@Override
	public AuctionRules getAuctionRulesForAuctionConsole(String eventId) {
		StringBuilder hsql = new StringBuilder("select distinct new com.privasia.procurehere.core.entity.AuctionRules(ar.auctionType, ar.fowardAuction, ar.auctionConsolePriceType, ar.auctionConsoleVenderType, ar.auctionConsoleRankType ,ar.buyerAuctionConsolePriceType,ar.buyerAuctionConsoleVenderType, ar.buyerAuctionConsoleRankType) from AuctionRules ar where ar.event.id = :eventId ");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		return (AuctionRules) query.getSingleResult();
	}

	@Override
	public AuctionRules getAuctionRulesForBidSumission(String eventId) {
		StringBuilder hsql = new StringBuilder("select distinct new com.privasia.procurehere.core.entity.AuctionRules(ar.auctionType, ar.fowardAuction, ar.isBiddingMinValueFromPrevious, ar.isBiddingPriceHigherLeadingBid, ar.isBiddingAllowSupplierSameBid) from AuctionRules ar where ar.event.id = :eventId ");
		final Query query = getEntityManager().createQuery(hsql.toString());
		query.setParameter("eventId", eventId);
		return (AuctionRules) query.getSingleResult();
	}

	@Override
	public AuctionRules findLeanAuctionRulesByEventId(String eventId) {
		final Query query = getEntityManager().createQuery("from AuctionRules ar where ar.event.id =:eventId");
		query.setParameter("eventId", eventId);
		return (AuctionRules) query.getSingleResult();
	}

	@Override
	public AuctionRules findPlainAuctionRulesByEventId(String eventId) {
		final Query query = getEntityManager().createQuery("from AuctionRules ar where ar.event.id =:eventId");
		query.setParameter("eventId", eventId);
		return (AuctionRules) query.getSingleResult();
	}

	@Override
	public Boolean findAuctionRulesWithEventId(String eventId) {
		final Query query = getEntityManager().createQuery("select ar.lumsumBiddingWithTax from AuctionRules ar where ar.event.id =:eventId and ar.lumsumBiddingWithTax is not null ");
		query.setParameter("eventId", eventId);
		try {
			return (Boolean) query.getSingleResult();
		} catch (Exception e) {
			LOG.info(e.getMessage(), e);
			return null;
		}

	}

	@Override
	public Boolean findPreSetPredBidAuctionRulesWithEventId(String eventId) {
		final Query query = getEntityManager().createQuery("select ar.preSetSamePreBidForAllSuppliers from AuctionRules ar where ar.event.id =:eventId");
		query.setParameter("eventId", eventId);
		try {
			return (Boolean) query.getSingleResult();
		} catch (Exception e) {
			LOG.info(e.getMessage(), e);
			return null;
		}

	}
}
