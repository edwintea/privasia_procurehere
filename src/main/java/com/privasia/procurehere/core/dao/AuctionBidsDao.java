package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.AuctionBids;
import com.privasia.procurehere.core.pojo.AuctionSupplierBidPojo;

/**
 * @author RT-Kapil
 */
public interface AuctionBidsDao extends GenericDao<AuctionBids, String> {

	/**
	 * @param supplierId
	 * @param eventId
	 * @return
	 */
	List<AuctionSupplierBidPojo> getAuctionBidsListBySupplierIdAndEventId(String supplierId, String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<AuctionBids> getAuctionBidsListByEventId(String eventId);

	/**
	 * @param supplierId
	 * @param eventId
	 * @return
	 */
	List<AuctionBids> getAuctionBidsForSupplier(String supplierId, String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<AuctionBids> getAuctionBidsListByEventIdForReport(String eventId);

	List<AuctionBids> getAuctionBidsForSupplierForReport(String supplierId, String eventId);

	/***
	 * @param supplierId
	 * @param id
	 * @return only last 2 bids for supplier
	 */
	List<AuctionBids> getAuctionTopPreviousBidsForSupplierForReport(String supplierId, String id);

	/***
	 * @param supplierId
	 * @param id
	 * @return only last 2 bids for supplier
	 */
	List<AuctionBids> getAuctionPreviousBidsForSupplierForReportOrderByDate(String supplierId, String id);

	List<AuctionBids> getRevertAuctionBidsForSupplierForReport(String supplierId, String eventId);

}
