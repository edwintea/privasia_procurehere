package com.privasia.procurehere.core.dao;

import java.io.Serializable;
import java.util.List;

import com.privasia.procurehere.core.entity.EventSupplier;
import com.privasia.procurehere.core.pojo.EventSupplierPojo;

/**
 * @author arc
 */
public interface GenericEventSupplierDao<T extends EventSupplier, PK extends Serializable> extends GenericDao<T, PK> {

	/**
	 * @param eventSupplier
	 * @param eventId TODO
	 * @return
	 */
	boolean isExists(T eventSupplier, String eventId);

	/**
	 * @param eventID
	 * @return
	 */
	List<EventSupplier> getAllSuppliersByEventId(String eventID);

	/**
	 * @param eventId
	 * @return
	 */
	T getSupplierById(String eventId);

	/**
	 * @param supplierId
	 * @param eventID
	 * @return
	 */
	T getEventSupplierBySupplierAndEventId(String supplierId, String eventID);

	/**
	 * @param supplierId
	 * @return
	 */
	T getSupplierBySupplierId(String supplierId);

	/**
	 * @param eventId
	 * @param supplierId
	 * @return TODO
	 */
	int updatePrivewTime(String eventId, String supplierId);

	/**
	 * @param eventId
	 */
	void updateSubmiTimeOnEventSuspend(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	List<EventSupplier> getAllSuppliersByEventIdOrderByCompName(String eventId);

	/**
	 * @param eventId
	 * @return
	 */
	boolean checkAnySupplierSubmited(String eventId);

	/**
	 * @param eventID
	 * @return
	 */
	List<EventSupplier> getSubmittedSuppliersByEventId(String eventID);

	T getEventSupplierForAuctionBySupplierAndEventId(String supplierId, String eventID);

	List<EventSupplier> getAllPartiallyCompleteBidsByEventId(String eventID);

	List<EventSupplierPojo> getAllDetailsForSendInvitation(String id);

	void updateEventSuppliersNotificationFlag(String id);

	/**
	 * @param eventID
	 * @return
	 */
	List<EventSupplierPojo> getAllSuppliersWithNoNotificationSentByEventId(String eventID);

	List<EventSupplierPojo> getAllDetailsForSendAllInvitation(String eventID);

	/**
	 * 
	 * @param eventID
	 * @return
	 */
	int deleteAllSuppliersByEventId(String eventID);

	/**
	 * 
	 * @param eventID
	 * @return
	 */
	List<EventSupplierPojo> getSubmitedSuppliers(String eventID);
}
