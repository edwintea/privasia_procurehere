package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.RfqSupplierCq;

public interface RfqSupplierCqService {

	/**
	 * @param supplierId
	 * @param eventId
	 * @param id
	 * @return
	 */
	RfqSupplierCq findSupplierCqByEventIdAndSupplierId(String supplierId, String eventId, String cqId);

	/**
	 * @param rfqSupplierCq
	 * @return
	 */
	RfqSupplierCq updateSupplierCq(RfqSupplierCq rfqSupplierCq);

	List<RfqSupplierCq> findSupplierCqStatusByEventIdAndSupplierId(String supplierId, String eventId);

	/**
	 * @param cqId
	 * @param eventId
	 * @param supplierId
	 * @return
	 */
	Integer findCountOfSupplierCqForSupplier(String cqId, String eventId, String supplierId);

}
