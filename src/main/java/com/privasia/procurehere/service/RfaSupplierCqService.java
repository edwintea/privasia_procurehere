/**
 * 
 */
package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.RfaSupplierCq;

/**
 * @author jayshree
 *
 */
public interface RfaSupplierCqService {

	/**
	 * @param supplierId
	 * @param eventId
	 * @return
	 */
	RfaSupplierCq findSupplierCqByEventIdAndSupplierId(String supplierId, String eventId, String cqId);

	/**
	 * @param persistSupplierCq
	 * @return
	 */
	RfaSupplierCq updateSupplierCq(RfaSupplierCq persistSupplierCq);

	/**
	 * @param supplierId
	 * @param eventId
	 * @return
	 */
	List<RfaSupplierCq> findSupplierCqStatusByEventIdAndSupplierId(String supplierId, String eventId);

	/**
	 * @param cqId
	 * @param eventId
	 * @param supplierId
	 * @return
	 */
	Integer findCountOfSupplierCqForSupplier(String cqId, String eventId, String supplierId);

}
