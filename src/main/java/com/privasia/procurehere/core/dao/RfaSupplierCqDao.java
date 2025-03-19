/**
 * 
 */
package com.privasia.procurehere.core.dao;

import java.util.List;

import com.privasia.procurehere.core.entity.RfaSupplierCq;
import com.privasia.procurehere.core.enums.SupplierCqStatus;

/**
 * @author jayshree
 */
public interface RfaSupplierCqDao extends GenericDao<RfaSupplierCq, String> {

	/**
	 * @param eventId
	 * @param cqId
	 * @param supplierId
	 * @return
	 */
	RfaSupplierCq findCqByEventIdAndCqId(String eventId, String cqId, String supplierId);

	/**
	 * @param supplierId
	 * @param eventId
	 * @param cqId
	 * @return
	 */
	RfaSupplierCq findSupplierCqByEventIdAndSupplierId(String supplierId, String eventId, String cqId);

	List<RfaSupplierCq> findSupplierCqStatusByEventIdAndSupplierId(String supplierId, String eventId);

	/**
	 * @param supplierId
	 * @param eventId
	 * @param cqId
	 * @return
	 */
	SupplierCqStatus findSupplierCqStatusByCqAndEventId(String supplierId, String eventId, String cqId);

	/**
	 * @param cqId
	 * @param eventId
	 * @param supplierId
	 * @return
	 */
	Integer findCountOfSupplierCqForSupplier(String cqId, String eventId, String supplierId);

	/**
	 * @param eventId
	 * @param supplierId
	 * @return
	 */
	long findPendingCqsByEventIdAndEventCqId(String eventId, String supplierId);

}
