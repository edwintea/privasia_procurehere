/**
 * 
 */
package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfiSupplierCq;

/**
 * @author jayshree
 */
public interface RfiSupplierCqDao extends GenericDao<RfiSupplierCq, String> {

	/**
	 * @param eventId
	 * @param cqId
	 * @param supplierId
	 * @return
	 */
	RfiSupplierCq findCqByEventIdAndEventCqId(String eventId, String cqId, String supplierId);

	/**
	 * @param supplierId
	 * @param eventId
	 * @param cqId
	 * @return
	 */
	RfiSupplierCq findSupplierCqByEventIdAndSupplierId(String supplierId, String eventId, String cqId);

	/**
	 * @param supplierId
	 * @param eventId
	 * @return
	 */
	List<RfiSupplierCq> findSupplierCqStatusByEventIdAndSupplierId(String supplierId, String eventId);

	/**
	 * @param cqId
	 * @param eventId
	 * @param supplierId
	 * @return
	 */
	Integer findCountOfSupplierCqForSupplier(String cqId, String eventId, String supplierId);

	/**
	 * @param id
	 */
	void deleteSupplierCqForEvent(String id);

	/**
	 * @param eventId
	 * @param supplierId
	 * @return
	 */
	long findPendingCqsByEventIdAndEventCqId(String eventId, String supplierId);

}
