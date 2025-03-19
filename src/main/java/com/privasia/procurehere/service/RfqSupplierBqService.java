package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.RfqSupplierBq;

public interface RfqSupplierBqService {

	/**
	 * @param id
	 * @param supplierId 
	 * @return
	 */
	RfqSupplierBq getSupplierBqByBqAndSupplierId(String id, String supplierId);

	/**
	 * @param persistSupplier
	 * @return
	 */
	RfqSupplierBq updateSupplierBq(RfqSupplierBq persistSupplier);

	List<RfqSupplierBq> findRfqSupplierBqbyEventId(String eventId);

	List<RfqSupplierBq> findRfqSupplierBqbyEventIdAndSupplierId(String eventId, String loggedInUserTenantId);

	List<RfqSupplierBq> findRfqSummarySupplierBqbyEventId(String id);

	RfqSupplierBq findRfqSupplierBqStatusbyEventIdAndSupplierId(String supplierId, String eventId, String bqId);

}
