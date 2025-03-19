package com.privasia.procurehere.service;

import java.util.List;

import com.privasia.procurehere.core.entity.RfpSupplierBq;

public interface RfpSupplierBqService {

	/**
	 * @param id
	 * @param supplierId
	 * @return
	 */
	RfpSupplierBq getSupplierBqByBqAndSupplierId(String id, String supplierId);

	/**
	 * @param persistSupplier
	 * @return
	 */
	RfpSupplierBq updateSupplierBq(RfpSupplierBq persistSupplier);

	List<RfpSupplierBq> findRfpSupplierBqbyEventId(String eventId);

	List<RfpSupplierBq> findRfpSupplierBqbyEventIdAndSupplierId(String eventId, String loggedInUserTenantId);

	List<RfpSupplierBq> findRfpSummarySupplierBqbyEventId(String eventId);

	RfpSupplierBq findRfpSupplierBqStatusbyEventIdAndSupplierId(String loggedInUserTenantId, String eventId, String bqId);

}
