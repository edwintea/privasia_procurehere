/**
 * 
 */
package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RftSupplierCq;

/**
 * @author jayshree
 */
public interface RftSupplierCqDao extends GenericDao<RftSupplierCq, String> {

	/**
	 * @param eventId
	 * @param cqId
	 * @param supplierId
	 * @return
	 */
	RftSupplierCq findCqByEventIdAndEventCqId(String eventId, String cqId, String supplierId);

	/**
	 * @param supplierId
	 * @param eventId
	 * @param cqId
	 * @return
	 */
	RftSupplierCq findSupplierCqByEventIdAndSupplierId(String supplierId, String eventId, String cqId);

	List<RftSupplierCq> findSupplierCqStatusByEventIdAndSupplierId(String supplierId, String eventId);

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
