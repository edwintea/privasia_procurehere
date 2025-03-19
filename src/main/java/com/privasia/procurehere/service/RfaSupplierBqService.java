package com.privasia.procurehere.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.privasia.procurehere.core.entity.RfaEventSupplier;
import com.privasia.procurehere.core.entity.RfaSupplierBq;
import com.privasia.procurehere.core.entity.User;
import com.privasia.procurehere.core.exceptions.ApplicationException;
import com.privasia.procurehere.core.exceptions.NotAllowedException;
import com.privasia.procurehere.core.pojo.RfaSupplierBqPojo;

public interface RfaSupplierBqService {

	/**
	 * @param id
	 * @return
	 */
	RfaSupplierBq getSupplierBqByBqAndSupplierId(String id, String supplierId);

	/**
	 * @param persistSupplier
	 * @return
	 */
	RfaSupplierBq updateSupplierBq(RfaSupplierBq persistSupplier);

	RfaSupplierBq saveOrUpdateSupplierBq(RfaSupplierBq persistSupplier, String supplierId, String eventId, String ipAddress) throws ApplicationException;

	List<RfaSupplierBq> findRfaSupplierBqBySupplierIds(List<String> supplierIds, String eventId);

	RfaSupplierBq findSupplierBqByBqId(String bqId);

	List<RfaSupplierBqPojo> getSupplierListForAuctionConsole(String eventId, Integer limitSupplier);

	BigDecimal getMaxBidFromAllBidsOfSuppliers(String bqId);

	Integer getCountsOfSameBidBySupliers(String bqId, String eventId, BigDecimal bidAmount);

	Integer getCountsOfRfaSupplierBqBySuplierIdAndBqId(String eventId, String supplierId);

	Boolean checkMandatoryToFinishEvent(String supplierId, String eventId) throws NotAllowedException, Exception;

	RfaSupplierBq getRfaSupplierBqForLumsumBid(String supplierId, String bqId);

	Long getRfaSupplierBqCountForEvent(String eventId);

	/**
	 * @param id
	 * @return
	 */
	RfaSupplierBq findById(String id);

	/**
	 * @param supplierId
	 * @param eventId
	 * @param bidId
	 * @param ipAddress TODO
	 * @return TODO
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	String revertOnAuctionBid(String supplierId, String eventId, String bidId, String ipAddress) throws JsonParseException, JsonMappingException, IOException;

	/**
	 * @param eventId
	 */
	void discardSupplierBqforEventId(String eventId);

	List<RfaSupplierBq> findRfaSummarySupplierBqbyEventId(String eventId);

	List<RfaSupplierBq> findRfaSupplierBqbyEventIdAndSupplierId(String eventId, String loggedInUserTenantId);

	BigDecimal getMinBidFromAllBidsOfSuppliers(String bqId);

	RfaSupplierBq getRfaSupplierBqByEventIdAndSupplierId(String eventId, String supplierId);

	void deleteRfaSupplierBq(String eventId, String supplierId);

	BigDecimal getLastTotalBqAmountBySupplierId(String eventId, String supplierId);

	Integer getCountsOfSamePreBidBySupliers(String eventId, BigDecimal bidAmount, String supplierId);

	List<RfaSupplierBqPojo> getSupplierListForDutchAuctionConsole(String eventId, Integer limitSupplier);

	Integer updateSupplierAuctionRank(String eventId, Boolean true1, String id);

	String revertOnAuctionBid(String supplierId, String eventId, String auctionBidId, String ipAddress, String revertReason, User getLoggedInUser) throws JsonParseException, JsonMappingException, IOException;

	RfaSupplierBq saveOrUpdateSupplierBq(RfaSupplierBq rfaSupplierBqCopy);

	RfaSupplierBq findRfaSupplierBqStatusbyEventIdAndSupplierId(String supplierId, String eventId, String id);

	Boolean isBqPreBidPricingExistForSuppliers(String eventId);

	void saveSupplierPreBidBqDetails(String rfaSupplierBqId, RfaEventSupplier eventSupplierDetails);
}
