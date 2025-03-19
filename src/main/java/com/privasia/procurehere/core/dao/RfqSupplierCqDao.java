/**
 * 
 */
package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfqSupplierCq;

/**
 * @author jayshree
 */
public interface RfqSupplierCqDao extends GenericDao<RfqSupplierCq, String> {

	/**
	 * @param eventId
	 * @param cqId
	 * @param supplierId
	 * @return
	 */
	RfqSupplierCq findCqByEventIdAndEventCqId(String eventId, String cqId, String supplierId);

	/**
	 * @param supplierId
	 * @param eventId
	 * @param cqId
	 * @return
	 */
	RfqSupplierCq findSupplierCqByEventIdAndSupplierId(String supplierId, String eventId, String cqId);

	/**
	 * @param supplierId
	 * @param eventId
	 * @return
	 */
	List<RfqSupplierCq> findSupplierCqStatusByEventIdAndSupplierId(String supplierId, String eventId);

	/**
	 * @param cqId
	 * @param eventId
	 * @param supplierId
	 * @return
	 */
	Integer findCountOfSupplierCqForSupplier(String cqId, String eventId, String supplierId);

	/**
	 * @param eventId
	 */
	void deleteSupplierCqForEvent(String eventId);

	/**
	 * @param eventId
	 * @param supplierId
	 * @return
	 */
	long findPendingCqsByEventIdAndEventCqId(String eventId, String supplierId);

}
