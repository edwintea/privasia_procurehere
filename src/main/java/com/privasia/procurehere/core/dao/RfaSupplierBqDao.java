package com.privasia.procurehere.core.dao;

import java.math.BigDecimal;
import java.util.List;

import com.privasia.procurehere.core.entity.RfaSupplierBq;
import com.privasia.procurehere.core.enums.AuctionType;
import com.privasia.procurehere.core.pojo.EvaluationSuppliersBqPojo;
import com.privasia.procurehere.core.pojo.RfaSupplierBqPojo;

public interface RfaSupplierBqDao extends GenericDao<RfaSupplierBq, String> {

	/**
	 * @param id
	 * @param name
	 * @return
	 */
	RfaSupplierBq findBqByEventIdAndSupplierId(String id, String bqId, String supplierId);

	/**
	 * @param id
	 * @param supplierId TODO
	 * @return
	 */
	RfaSupplierBq findBqByBqId(String id, String supplierId);

	List<RfaSupplierBq> findSupplierBqbyEventId(String eventId);

	List<RfaSupplierBq> findRfaSupplierBqBySupplierIds(List<String> supplierIds, String eventId, Integer limitSupplier);

	RfaSupplierBq findSupplierBqByBqId(String bqId);

	BigDecimal getMaxBidFromAllBidsOfSuppliers(String bqId);

	Integer getCountsOfSameBidBySupliers(String bqId, String eventId, BigDecimal bidAmount);

	Integer getCountsOfRfaSupplierBqBySuplierIdAndBqId(String eventId, String supplierId);

	/**
	 * @param eventId
	 * @param limitSupplier
	 * @return
	 */
	List<RfaSupplierBqPojo> getSupplierListForAuctionConsole(String eventId, Integer limitSupplier);

	/**
	 * @param supplierId
	 * @return
	 */
	List<RfaSupplierBq> getAllBqsBySupplierId(String eventId, String supplierId);

	void updateLumsumAuction(String eventId, String supplierId, String bqId, BigDecimal totalAfterTax, BigDecimal differencePercentage);

	RfaSupplierBq getRfaSupplierBqForLumsumBid(String supplierId, String bqId);

	/**
	 * @param eventId
	 */
	void discardSupplierBqforEventId(String eventId);

	List<RfaSupplierBq> findRfaSummarySupplierBqbyEventId(String eventId);

	List<RfaSupplierBq> findRfaSupplierBqbyEventIdAndSupplierId(String eventId, String supplierId);

	/**
	 * @param eventId
	 * @param supplierId
	 * @return
	 */
	List<BigDecimal> grandTotalOfBqByEventIdAndSupplierId(String eventId, String supplierId);

	BigDecimal getMinBidFromAllBidsOfSuppliers(String bqId);

	/**
	 * @param eventId
	 * @param supplierId
	 * @return
	 */
	List<RfaSupplierBq> rfaSupplierBqAnswerdByEventIdAndSupplierId(String eventId, String supplierId);

	/**
	 * @param eventId
	 * @param supplierId
	 * @param bidPrice
	 */
	void updateDuctionAuctionBidForSupplier(String eventId, String supplierId, BigDecimal bidPrice);

	RfaSupplierBq getRfaSupplierBqByEventIdAndSupplierId(String eventId, String supplierId);

	void discardSupplierBqforSupplierId(String eventId, String supplierId);

	BigDecimal getLastTotalBqAmountBySupplierId(String eventId, String supplierId);

	Integer getCountsOfSamePreBidBySupliers(String eventId, BigDecimal bidAmount, String supplierId);

	List<RfaSupplierBqPojo> getSupplierListForDutchAuctionConsole(String eventId, Integer limitSupplier);

	List<RfaSupplierBqPojo> findRfaSupplierBqBySupplierIdsOdrByRank(String eventId, Integer limitSupplier);

	List<RfaSupplierBqPojo> findRfaSupplierBqCompleteNessBySupplierIdsOdrByRank(String eventId, Integer limitSupplier);

	List<RfaSupplierBqPojo> findRfaSupplierBqByDisqualifiedSupplierIdsOdrByRank(String eventId, Integer limitSupplier);

	List<RfaSupplierBqPojo> findRfaTopSupplierBqBySupplierIdsOdrByRank(String eventId, Integer limitSupplier);

	List<RfaSupplierBqPojo> getAllRfaSuppliersIdByEventId(String eventId);

	BigDecimal findMinLeadingPrice(String id, String id2, String id3);

	BigDecimal findMaxLeadingPrice(String id, String id2, String id3);

	List<RfaSupplierBq> findBqListByBqIds(List<String> bqIdlist);

	List<EvaluationSuppliersBqPojo> getAllBqsByBqIdsAndEventId(String bqid, String id, AuctionType auctionType);

	List<RfaSupplierBqPojo> getAllRfaTopEventSuppliersIdByEventId(String id, Integer i, String id2);

	List<RfaSupplierBqPojo> findRfaSupplierBqCompleteNessBySupplierIds(String eventId);

	RfaSupplierBq findBqByEventIdAndSupplierIdOfQualifiedSupplier(String id, String bqId, String supplierId);

	BigDecimal findInitialMinPrice(String eventId, AuctionType auctionType);

	BigDecimal findPostMinPrice(String id, AuctionType auctionType);

	Long getRfaSupplierBqCountForEvent(String eventId);

	RfaSupplierBq findRfaSupplierBqStatusbyEventIdAndSupplierId(String supplierId, String eventId, String id);

	/**
	 * @param eventId
	 * @param supplierId
	 * @return
	 */
	long findPendingBqAnswerdByEventIdAndSupplierId(String eventId, String supplierId);

	Boolean isBqPreBidPricingExistForSuppliers(String eventId);

}
