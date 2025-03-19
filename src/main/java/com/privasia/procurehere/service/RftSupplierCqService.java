package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.RftSupplierCq;

public interface RftSupplierCqService {

	/**
	 * @param supplierId
	 * @param eventId
	 * @param cqId
	 * @return
	 */
	RftSupplierCq findSupplierCqByEventIdAndSupplierId(String supplierId, String eventId, String cqId);

	/**
	 * @param rftSupplierCq
	 * @return
	 */
	RftSupplierCq updateSupplierCq(RftSupplierCq rftSupplierCq);

	/**
	 * @param supplierId
	 * @param eventId
	 * @return
	 */
	List<RftSupplierCq> findSupplierCqStatusByEventIdAndSupplierId(String supplierId, String eventId);

	/**
	 * @param cqId
	 * @param eventId
	 * @param supplierId
	 * @return
	 */
	Integer findCountOfSupplierCqForSupplier(String cqId, String eventId, String supplierId);

}
